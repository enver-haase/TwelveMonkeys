/*
 * Copyright (c) 2014, Harald Kuhr
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

import com.twelvemonkeys.imageio.util.ImageReaderAbstractTest;
import org.junit.Ignore;

import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;

import java.awt.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


/**
 * PCXImageReaderTest
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: PCXImageReaderTest.java,v 1.0 03.07.14 22:28 haraldk Exp$
 */
public class I64ImageReaderTest extends ImageReaderAbstractTest<I64ImageReader> {
    @Override
    protected List<TestData> getTestData() {
        return Arrays.asList(
                new TestData(getClassLoaderResource("/i64/CircleW.i64"), new Dimension(100, 50)),
                new TestData(getClassLoaderResource("/i64/pic.i64"), new Dimension(100, 50)),
                new TestData(getClassLoaderResource("/i64/coffee.i64"), new Dimension(100, 50))
        );
    }

    @Override
    protected ImageReaderSpi createProvider() {
        return new com.twelvemonkeys.imageio.plugins.i64.I64ImageReaderSpi();
    }

    @Override
    protected Class<com.twelvemonkeys.imageio.plugins.i64.I64ImageReader> getReaderClass() {
        return com.twelvemonkeys.imageio.plugins.i64.I64ImageReader.class;
    }

    @Override
    protected com.twelvemonkeys.imageio.plugins.i64.I64ImageReader createReader() {
        return new com.twelvemonkeys.imageio.plugins.i64.I64ImageReader(createProvider());
    }

    @Override
    protected List<String> getFormatNames() {
        return Arrays.asList("I64", "i64");
    }

    @Override
    protected List<String> getSuffixes() {
        return Collections.singletonList("i64");
    }

    @Override
    protected List<String> getMIMETypes() {
        return Arrays.asList(
                "image/i64", "image/x-i64"
        );
    }

    @Override
    @Ignore("TODO - Under Development")
    public void testReRead() throws IOException {
    }

    @Override
    @Ignore("I64 supports only one size: 100x50")
    public void testReadWithSubsampleParamDimensions() {
    }

    @Override
    @Ignore("TODO - Under Development")
    public void testReadWithSubsampleParamPixels() throws IOException {
    }

    @Override
    @Ignore("TODO - Under Development")
    public void testReadWithNewParam() {
    }

    @Override
    @Ignore("TODO - Under Development")
    public void testReadWithDefaultParam() {
    }

    @Override
    @Ignore("TODO - Under Development")
    public void testReadWithSourceRegionParam() {
    }

    @Override
    @Ignore("TODO - Under Development")
    public void testReadWithSourceRegionParamEqualImage() throws IOException {
    }

    @Override
    public void testGetWidthNoInput() {
        ImageReader reader = createReader();

        int width = 0;
        try {
            width = reader.getWidth(0);
        }
        catch (IllegalStateException ignore) {
        }
        catch (IOException e) {
            fail("Could not read image width: " + e);
        }
        assertEquals("Wrong width reported", 100, width);
        reader.dispose();
    }

    @Override
    public void testGetHeightNoInput() {
        ImageReader reader = createReader();

        int height = 0;
        try {
            height = reader.getHeight(0);
        }
        catch (IllegalStateException ignore) {
        }
        catch (IOException e) {
            fail("Could not read image height: " + e);
        }
        assertEquals("Wrong height reported", 50, height);
        reader.dispose();
    }


    @Override
    public void testGetAspectRatioNoInput() {
        ImageReader reader = createReader();

        float aspectRatio = 0f;
        try {
            aspectRatio = reader.getAspectRatio(0);
        }
        catch (IllegalStateException ignore) {
        }
        catch (IOException e) {
            fail("Could not read image aspect ratio" + e);
        }
        assertEquals("Wrong aspect aspect ratio", 2f, aspectRatio, 0.01f);
        reader.dispose();
    }

    @Override
    @Ignore("TODO - Under Development")
    public void testReadWithSubsampleAndSourceRegionParam() {
    }

    @Override
    @Ignore("TODO - Under Development")
    public void testReadAsRenderedImageWithDefaultParam() {
    }

    @Override
    @Ignore("TODO - Under Development")
    public void testAbort() {
    }

    @Override
    @Ignore("TODO - Under Development")
    public void testSetDestination() throws IOException {
    }

    @Override
    @Ignore("TODO - Under Development")
    public void testNotBadCaching() throws IOException {
    }

    @Override
    @Ignore("TODO - Under Development")
    public void testSetDestinationRaw() throws IOException {
    }

    @Override
    @Ignore("TODO - Under Development")
    public void testSetDestinationType() throws IOException {
    }

    @Override
    @Ignore("TODO - Under Development")
    public void testSetDestinationIllegal() throws IOException {
    }

    @Override
    @Ignore("TODO - Under Development")
    public void testSetDestinationOffset() throws IOException {
    }

    @Override
    @Ignore("TODO - Under Development")
    public void testSetDestinationTypeIllegal() throws IOException {
    }
}

