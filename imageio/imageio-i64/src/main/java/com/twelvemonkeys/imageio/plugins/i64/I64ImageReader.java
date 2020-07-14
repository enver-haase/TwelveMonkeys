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
import com.twelvemonkeys.imageio.util.IIOUtil;
import com.twelvemonkeys.imageio.util.ImageTypeSpecifiers;
import com.twelvemonkeys.io.enc.DecoderStream;
import com.twelvemonkeys.xml.XMLSerializer;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

/**
 * ImageReader for ZSoft PC Paintbrush (PCX) format.
 *
 * @see <a href="http://www.drdobbs.com/pcx-graphics/184402396">PCX Graphics</a>
 */
public final class I64ImageReader extends ImageReaderBase {

    final static boolean DEBUG = "true".equalsIgnoreCase(System.getProperty("com.twelvemonkeys.imageio.plugins.i64.debug"));

    /** 8 bit ImageTypeSpecifer used for reading bitplane images. */
    private static final ImageTypeSpecifier GRAYSCALE = ImageTypeSpecifiers.createGrayscale(8, DataBuffer.TYPE_BYTE);

    private boolean readPalette;
    private IndexColorModel vgaPalette;

    public I64ImageReader(final ImageReaderSpi provider) {
        super(provider);
    }

    @Override
    protected void resetMembers() {

    }

    @Override
    public int getWidth(final int imageIndex) throws IOException {
        return 150;
    }

    @Override
    public int getHeight(final int imageIndex) throws IOException {
        return 100;
    }

    @Override
    public Iterator<ImageTypeSpecifier> getImageTypes(final int imageIndex) throws IOException {
        ImageTypeSpecifier rawType = getRawImageType(imageIndex);

        List<ImageTypeSpecifier> specifiers = new ArrayList<>();

        // TODO: Implement
        specifiers.add(rawType);

        return specifiers.iterator();
    }

    @Override
    public ImageTypeSpecifier getRawImageType(final int imageIndex) throws IOException {

        return GRAYSCALE;

//        int channels = header.getChannels();
//
//        switch (header.getBitsPerPixel()) {
//            case 1:
//            case 2:
//            case 4:
//                // TODO: If there's a VGA palette here, use it?
//
//                return ImageTypeSpecifiers.createFromIndexColorModel(header.getEGAPalette());
//            case 8:
//                if (channels == 1) {
//                    // We may have IndexColorModel here for 1 channel images
//                    IndexColorModel palette = getVGAPalette();
//
//                    if (palette != null) {
//                        return ImageTypeSpecifiers.createFromIndexColorModel(palette);
//                    }
//                    else {
//                        // PCX Gray has 1 channel and no palette
//                        return ImageTypeSpecifiers.createGrayscale(8, DataBuffer.TYPE_BYTE);
//                    }
//                }
//
//                // PCX RGB has channels for 24 bit RGB, will be validated by ImageTypeSpecifier
//                return ImageTypeSpecifiers.createBanded(ColorSpace.getInstance(ColorSpace.CS_sRGB), createIndices(channels, 1), createIndices(channels, 0), DataBuffer.TYPE_BYTE, channels == 4, false);
//            case 24:
//                // Some sources says this is possible...
//                return ImageTypeSpecifiers.createFromBufferedImageType(BufferedImage.TYPE_3BYTE_BGR);
//            case 32:
//                // Some sources says this is possible...
//                return ImageTypeSpecifiers.createFromBufferedImageType(BufferedImage.TYPE_4BYTE_ABGR);
//            default:
//                throw new IIOException("Unknown number of bytes per pixel: " + header.getBitsPerPixel());
//        }
    }



    @Override
    public BufferedImage read(final int imageIndex, final ImageReadParam param) throws IOException {

        return new BufferedImage(getWidth(imageIndex), getHeight(imageIndex), TYPE_INT_RGB); // TODO

//        Iterator<ImageTypeSpecifier> imageTypes = getImageTypes(imageIndex);
//        ImageTypeSpecifier rawType = getRawImageType(imageIndex);
//
//        int width = getWidth(imageIndex);
//        int height = getHeight(imageIndex);
//
//        BufferedImage destination = getDestination(param, imageTypes, width, height);
//
//        Rectangle srcRegion = new Rectangle();
//        Rectangle destRegion = new Rectangle();
//        computeRegions(param, width, height, destination, srcRegion, destRegion);
//
//        WritableRaster destRaster = clipToRect(destination.getRaster(), destRegion, param != null ? param.getDestinationBands() : null);
//        checkReadParamBandSettings(param, rawType.getNumBands(), destRaster.getNumBands());
//
//        int compression = header.getCompression();
//
//        // Wrap input (COMPRESSION_RLE is really the only value allowed)
//        DataInput input = compression == PCX.COMPRESSION_RLE
//                          ? new DataInputStream(new DecoderStream(IIOUtil.createStreamAdapter(imageInput), new RLEDecoder()))
//                          : imageInput;
//
//        int xSub = param != null ? param.getSourceXSubsampling() : 1;
//        int ySub = param != null ? param.getSourceYSubsampling() : 1;
//
//        processImageStarted(imageIndex);
//
//        if (rawType.getColorModel() instanceof IndexColorModel && header.getChannels() > 1) {
//            // Bit planes!
//            // Create raster from a default 8 bit layout
//            int planeWidth = header.getBytesPerLine();
//            int rowWidth = planeWidth * 8; // bitsPerPixel == 1
//            WritableRaster rowRaster = GRAYSCALE.createBufferedImage(rowWidth, 1).getRaster();
//
//            // Clip to source region
//            Raster clippedRow = clipRowToRect(rowRaster, srcRegion,
//                    param != null ? param.getSourceBands() : null,
//                    param != null ? param.getSourceXSubsampling() : 1);
//
//            byte[] planeData = new byte[rowWidth];
//            byte[] rowDataByte = ((DataBufferByte) rowRaster.getDataBuffer()).getData();
//
//            for (int y = 0; y < height; y++) {
//                switch (header.getBitsPerPixel()) {
//                    case 1:
//                        readRowByte(input, srcRegion, xSub, ySub, planeData, 0, planeWidth * header.getChannels(), destRaster, clippedRow, y);
//                        break;
//                    default:
//                        throw new AssertionError();
//                }
//
//                int pixelPos = 0;
//                for (int planePos = 0; planePos < planeWidth; planePos++) {
//                    BitRotator.bitRotateCW(planeData, planePos, planeWidth, rowDataByte, pixelPos, 1);
//                    pixelPos += 8;
//                }
//
//                processImageProgress(100f * y / height);
//
//                if (y >= srcRegion.y + srcRegion.height) {
//                    break;
//                }
//
//                if (abortRequested()) {
//                    processReadAborted();
//                    break;
//                }
//            }
//        }
//        else if (header.getBitsPerPixel() == 24 || header.getBitsPerPixel() == 32) {
//            // Can't use width here, as we need to take bytesPerLine into account, and re-create a width based on this
//            int rowWidth = (header.getBytesPerLine() * 8) / header.getBitsPerPixel();
//            WritableRaster rowRaster = rawType.createBufferedImage(rowWidth, 1).getRaster();
//
//            // Clip to source region
//            Raster clippedRow = clipRowToRect(rowRaster, srcRegion,
//                    param != null ? param.getSourceBands() : null,
//                    param != null ? param.getSourceXSubsampling() : 1);
//
//            for (int y = 0; y < height; y++) {
//                byte[] rowDataByte = ((DataBufferByte) rowRaster.getDataBuffer()).getData();
//                readRowByte(input, srcRegion, xSub, ySub, rowDataByte, 0, rowDataByte.length, destRaster, clippedRow, y);
//
//                processImageProgress(100f * y / height);
//
//                if (y >= srcRegion.y + srcRegion.height) {
//                    break;
//                }
//
//                if (abortRequested()) {
//                    processReadAborted();
//                    break;
//                }
//            }
//        }
//        else {
//            // Can't use width here, as we need to take bytesPerLine into account, and re-create a width based on this
//            int rowWidth = (header.getBytesPerLine() * 8) / header.getBitsPerPixel();
//            WritableRaster rowRaster = rawType.createBufferedImage(rowWidth, 1).getRaster();
//
//            // Clip to source region
//            Raster clippedRow = clipRowToRect(rowRaster, srcRegion,
//                    param != null ? param.getSourceBands() : null,
//                    param != null ? param.getSourceXSubsampling() : 1);
//
//            for (int y = 0; y < height; y++) {
//                for (int c = 0; c < header.getChannels(); c++) {
//                    WritableRaster destChannel = destRaster.createWritableChild(destRaster.getMinX(), destRaster.getMinY(), destRaster.getWidth(), destRaster.getHeight(), 0, 0, new int[] {c});
//                    Raster srcChannel = clippedRow.createChild(clippedRow.getMinX(), 0, clippedRow.getWidth(), 1, 0, 0, new int[] {c});
//
//                    switch (header.getBitsPerPixel()) {
//                        case 1:
//                        case 2:
//                        case 4:
//                        case 8:
//                            byte[] rowDataByte = ((DataBufferByte) rowRaster.getDataBuffer()).getData(c);
//                            readRowByte(input, srcRegion, xSub, ySub, rowDataByte, 0, rowDataByte.length, destChannel, srcChannel, y);
//                            break;
//                        default:
//                            throw new AssertionError();
//                    }
//
//                    if (abortRequested()) {
//                        break;
//                    }
//                }
//
//                processImageProgress(100f * y / height);
//
//                if (y >= srcRegion.y + srcRegion.height) {
//                    break;
//                }
//
//                if (abortRequested()) {
//                    processReadAborted();
//                    break;
//                }
//            }
//        }
//
//        processImageComplete();
//
//        return destination;
    }



    @Override
    public IIOMetadata getImageMetadata(final int imageIndex) throws IOException {
        return null;
    }

}
