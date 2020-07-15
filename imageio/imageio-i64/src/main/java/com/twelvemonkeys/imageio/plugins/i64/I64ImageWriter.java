/*
 * Copyright (c) 2008, Harald Kuhr
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

import com.twelvemonkeys.image.ImageUtil;
import com.twelvemonkeys.image.ResampleOp;
import com.twelvemonkeys.imageio.ImageWriterBase;


import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageWriterSpi;
import java.awt.*;
import java.awt.image.*;

import java.io.IOException;

/**
 * Writer for Bally/Williams Pinball 2000 i64
 */
public class I64ImageWriter extends ImageWriterBase {

    final static boolean DEBUG = "true".equalsIgnoreCase(System.getProperty("com.twelvemonkeys.imageio.plugins.i64.debug"));

    public I64ImageWriter() {
        this(null);
    }

    protected I64ImageWriter(ImageWriterSpi pProvider) {
        super(pProvider);
    }

    public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier imageType, ImageWriteParam param) {
        throw new UnsupportedOperationException("Method getDefaultImageMetadata not implemented");// TODO: Implement
    }

    public IIOMetadata convertImageMetadata(IIOMetadata inData, ImageTypeSpecifier imageType, ImageWriteParam param) {
        throw new UnsupportedOperationException("Method convertImageMetadata not implemented");// TODO: Implement
    }

    public void write(IIOMetadata pStreamMetadata, IIOImage pImage, ImageWriteParam pParam) throws IOException {
        assertOutput();
        processImageStarted(0);

        if (DEBUG){
            System.err.println("Writing *.i64 file.");
        }
        if (pImage.hasRaster()) {
            throw new UnsupportedOperationException("Cannot write raster");
        }

        // i64 file format mandates 100x50 images
        BufferedImage bufferedImage = ImageUtil.toBuffered((Image) pImage.getRenderedImage());
        Raster raster = pImage.getRenderedImage().getData();
        if (raster.getWidth() != 100 || raster.getHeight() != 50){
            BufferedImageOp resampler = new ResampleOp(100, 50, ResampleOp.FILTER_LANCZOS); // A good default filter, see class documentation for more info
            bufferedImage = resampler.filter(bufferedImage, null);
        }

        BufferedImage indexedImage = ImageUtil.createIndexed(bufferedImage, 64, Color.BLACK, ImageUtil.DITHER_DIFFUSION);
        IndexColorModel model = (IndexColorModel) indexedImage.getColorModel();

        final int length = model.getMapSize();
        if (DEBUG){
            System.err.println("Number of colors in color map: "+length);
        }

        // color table
        for (int i = 0; i < length; i++) {
            int red = model.getRed(i);
            red = red >> 3; // only five bits are used

            int green = model.getGreen(i);
            green = green >> 3; // only five bits are used

            int blue = model.getGreen(i);
            blue = blue >> 3; // only five bits are used

            int color = (red << 10) | (green << 5) | blue;
            imageOutput.writeByte(color % 256);
            imageOutput.writeByte(color / 256);
        }
        // Color table padding
        for (int i=length; i<64; i++){
            imageOutput.writeShort(0);
        }
        // Dictionary (unused here as we don't compress)
        for (int i=0; i<64; i++){
            imageOutput.writeShort(0); // 'left' pixel
            imageOutput.writeShort(0); // 'right' pixel
        }
        // Length of double-pixel-fliparound table (we don't use it as we don't compress)
        imageOutput.writeByte(2); // just the two bytes of ourselves
        imageOutput.writeByte(0); // TODO: this whole byte-order thing should be understood better
        // Now writing the image data, always 100 x 50 image size!
        // The encoding is like this:
        //  00cccccc        Single pixel, colour of index C (0-63)
        //  01cccccc        Two pixels, colour of index C (0-63)
        //  10nnnnnn        Two pixels, dictionary lookup entry N (0-63)
        //  11cccccc        Run of pixels, colour of index C (0-63)
        //  rrrrrrrr        Run length is R+3 (thus 3 to 258)
        if (DEBUG){
            System.err.println("Data type in color map buffer: "+indexedImage.getData().getDataBuffer().getDataType());
            System.err.println("Number of banks: "+indexedImage.getData().getDataBuffer().getNumBanks());
            System.err.println("Size: "+indexedImage.getData().getDataBuffer().getSize());
        }
        if (indexedImage.getData().getDataBuffer().getSize() != 100*50){
            throw new IOException("Internal error.");
        }

        for (int line=0; line<50; line++){
            for (int col=0; col<100; col++){
                int element = indexedImage.getData().getDataBuffer().getElem(line*100+col);
                element &= 0x3f; // make absolutely sure the upper two bits are cleared
                imageOutput.writeByte(element); // single pixel, element is color palette entry
            }
        }

        processImageComplete();
    }
}
