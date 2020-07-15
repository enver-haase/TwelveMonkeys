/*
 * Copyright (c) 2020, Enver Haase
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.twelvemonkeys.imageio.plugins.i64;

import com.twelvemonkeys.imageio.ImageReaderBase;
import com.twelvemonkeys.imageio.util.ImageTypeSpecifiers;

import javax.imageio.ImageReadParam;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * ImageReader for Bally/Williams Pinball 2000 i64
 */
public final class I64ImageReader extends ImageReaderBase {

    final static boolean DEBUG = "true".equalsIgnoreCase(System.getProperty("com.twelvemonkeys.imageio.plugins.i64.debug"));


    private static final int BUFFERED_IMAGE_TYPE_RGB555 = BufferedImage.TYPE_USHORT_555_RGB;
    /**
     * 8 bit ImageTypeSpecifier used for reading bitplane images.
     */
    private static final ImageTypeSpecifier RGB555 = ImageTypeSpecifiers.createFromBufferedImageType(BUFFERED_IMAGE_TYPE_RGB555);

    public I64ImageReader(final ImageReaderSpi provider) {
        super(provider);
    }

    @Override
    protected void resetMembers() {
    }

    @Override
    public int getWidth(final int imageIndex) throws IOException {
        return 100;
    }

    @Override
    public int getHeight(final int imageIndex) throws IOException {
        return 50;
    }

    @Override
    public Iterator<ImageTypeSpecifier> getImageTypes(final int imageIndex) throws IOException {
        ImageTypeSpecifier rawType = getRawImageType(imageIndex);
        List<ImageTypeSpecifier> specifiers = new ArrayList<>();
        specifiers.add(rawType);
        return specifiers.iterator();
    }

    @Override
    public ImageTypeSpecifier getRawImageType(final int imageIndex) throws IOException {
        return RGB555;
    }


    @Override
    public BufferedImage read(final int imageIndex, final ImageReadParam param) throws IOException {
        assertInput();
        if (imageIndex != 0) {
            throw new IndexOutOfBoundsException("Only one image expected.");
        }
        processImageStarted(imageIndex);

        BufferedImage bufferedImage = new BufferedImage(getWidth(imageIndex), getHeight(imageIndex), BUFFERED_IMAGE_TYPE_RGB555);

        int[] colorPalette = new int[64]; // really only 16-bit are used per int
        for (int i = 0; i < colorPalette.length; i++) {
            colorPalette[i] = read(imageInput) + read(imageInput) * 256;
        }

        // This is a high-score table of the most frequently occurring pixel
        // pairs. We store the 15bit RGB value of each pixel. So, if the third most
        // popular pair is {black,red} we'd store [2][0]=0x0000 [2][1]=0x7c00.
        int[][] dictionary = new int[64][2]; // really only 16-bit are used in the resulting int, directly a color of a pixel
        for (int i = 0; i < dictionary.length; i++) {
            dictionary[i][0] = read(imageInput) + read(imageInput) * 256;
            dictionary[i][1] = read(imageInput) + read(imageInput) * 256;
        }

        int flipTableSize = read(imageInput) + read(imageInput) * 256;
        if (DEBUG) {
            System.err.println("Flip table size is: " + flipTableSize);
        }
        int[] flipTable = new int[flipTableSize-2]; // subtract the two size bytes
        for (int i = 0; i < flipTable.length; i++) {
            flipTable[i] = read(imageInput);
        }

        // Now we're finally at the compressed image data.
        // The encoding is like this:
        //  00cccccc        Single pixel, colour of index C (0-63)
        //  01cccccc        Two pixels, colour of index C (0-63)
        //  10nnnnnn        Two pixels, dictionary lookup entry N (0-63)
        //  11cccccc        Run of pixels, colour of index C (0-63)
        //  rrrrrrrr        Run length is R+3 (thus 3 to 258)


        int[][] pixels = new int[getHeight(imageIndex)][getWidth(imageIndex)];

        int countFlips=0;
        for (int line = 0; line < getHeight(imageIndex); line++) {
            if (DEBUG){
                System.err.println("Line: "+line);
            }
            int pixelCount = 0; // per line
            while (pixelCount < getWidth(imageIndex)) {
                int value = read(imageInput);
                int opcode = value & 192;
                value &= 63;
                switch (opcode) {
                    case 0:
                        pixels[line][pixelCount++] = colorPalette[value];
                        break;
                    case 64:
                        pixels[line][pixelCount++] = colorPalette[value];
                        pixels[line][pixelCount++] = colorPalette[value];
                        break;
                    case 128:
                        if (DEBUG){
                            System.err.println("Flip number "+countFlips);
                        }
                        int flipByte = countFlips / 8;
                        int flipBit = countFlips % 8;
                        boolean flip = (flipTable[flipByte] & (1 << flipBit)) != 0;

                        if (!flip) {
                            pixels[line][pixelCount++] = dictionary[value][0];
                            pixels[line][pixelCount++] = dictionary[value][1];
                        }
                        else {
                            pixels[line][pixelCount++] = dictionary[value][1];
                            pixels[line][pixelCount++] = dictionary[value][0];
                        }
                        countFlips++;
                        break;
                    case 192:
                        int len = read(imageInput);
                        if (DEBUG){
                            System.err.println("Run of pixels, number: "+(len+3));
                        }
                        for (int i = 0; i < len + 3; i++) {
                            pixels[line][pixelCount++] = colorPalette[value];
                        }
                        break;
                    default:
                        throw new IOException("Internal error.");
                }
            }
        }

        if (DEBUG) {
            int c;
            while ((c = imageInput.read()) != -1) {
                System.err.println("Trailing garbage: " + c);
            }
        }

        copyPixels(imageIndex, pixels, bufferedImage);

        processImageComplete();
        return bufferedImage;
    }

    private void copyPixels(int imageIndex, int[][] source, BufferedImage dest) throws IOException {
        WritableRaster raster = dest.getRaster();
        for (int line = 0; line < getHeight(imageIndex); line++) {
            for (int col = 0; col < getWidth(imageIndex); col++) {
                //int red = dest.getColorModel().getRed(source[line][col]);
                int red = (source[line][col] & 0x7c00) >> 10;
                //int green = dest.getColorModel().getGreen(source[line][col]);
                int green = (source[line][col] & 0x03e0) >> 5;
                //int blue = dest.getColorModel().getBlue(source[line][col]);
                int blue = (source[line][col] & 0x001f);
                raster.setPixel(col, line, new int[]{red, green, blue});
            }
        }
    }

    private int read(ImageInputStream is) throws IOException {
        int val = is.read();
        if (val == -1) {
            throw new IOException("Short read.");
        }
        return val;
    }


    @Override
    public IIOMetadata getImageMetadata(final int imageIndex) throws IOException {
        return null; // no metadata available.
    }

}
