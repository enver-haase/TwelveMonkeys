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

import com.twelvemonkeys.imageio.ImageWriterBase;
import com.twelvemonkeys.imageio.util.IIOUtil;
import com.twelvemonkeys.io.FastByteArrayOutputStream;
import com.twelvemonkeys.io.enc.EncoderStream;
import com.twelvemonkeys.io.enc.PackBitsEncoder;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageWriterSpi;
import java.awt.*;
import java.awt.image.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

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

        // Color table
        for (int i=0; i<64; i++){
            imageOutput.writeShort(0); // TODO
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
        for (int line=0; line<50; line++){
            for (int col=0; col<100; col++){
                imageOutput.writeByte(0x00); // single pixel, color palette entry 0. TODO
            }
        }

        processImageComplete();
    }
}
