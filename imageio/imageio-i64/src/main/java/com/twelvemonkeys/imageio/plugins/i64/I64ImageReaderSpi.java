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

import com.twelvemonkeys.imageio.spi.ImageReaderSpiBase;

import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.util.Locale;

public final class I64ImageReaderSpi extends ImageReaderSpiBase {

    /**
     * Creates a {@code PCXImageReaderSpi}.
     */
    public I64ImageReaderSpi() {
        super(new I64ProviderInfo());
    }

    @Override
    public boolean canDecodeInput(final Object source) throws IOException {
        if (!(source instanceof ImageInputStream)) {
            return false;
        }

        ImageInputStream stream = (ImageInputStream) source;

        stream.mark();
        try {
            for (int i = 0; i < 64; i++) { // read color map
                stream.readByte();
                stream.readByte();
            }
            for (int i = 0; i < 64; i++) { // read color map
                stream.readByte();
                stream.readByte();
                stream.readByte();
                stream.readByte();
            }
            int lo = stream.read();
            int hi = stream.read();
            int dictLength = hi * 256 + lo;
            for (int i = 0; i < dictLength - 2; i++) {
                stream.readByte();
            }
            // we're now at the beginning of the compressed dict/rle data
            // The encoding is like this:
            //  00cccccc        Single pixel, colour of index C (0-63)
            //  01cccccc        Two pixels, colour of index C (0-63)
            //  10nnnnnn        Two pixels, dictionary lookup entry N (0-63)
            //  11cccccc        Run of pixels, colour of index C (0-63)
            //  rrrrrrrr        Run length is R+3 (thus 3 to 258)
            int countPixels = 0;
            do {
                int value = stream.read();
                int opcode = value & 192;
                switch (opcode) {
                    case 0:
                        countPixels++;
                        break;
                    case 64:
                    case 128:
                        countPixels += 2;
                        break;
                    case 192:
                        int run = stream.read();
                        countPixels += run+3;
                        break;
                    default:
                        throw new IOException("Internal error.");
                }
            } while (countPixels < 100 * 50) ;
            if (countPixels != 100 * 50){
                return false;
            }
            if (stream.read() != -1){
                return false;
            }

            return true;
        } finally {
            stream.reset();
        }
    }

    @Override
    public ImageReader createReaderInstance(final Object extension) throws IOException {
        return new com.twelvemonkeys.imageio.plugins.i64.I64ImageReader(this);
    }

    @Override
    public String getDescription(final Locale locale) {
        return "Bally/Williams Pinball 2000 i64 image reader";
    }
}

