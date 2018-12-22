/**
 * Copyright (c) 2017 Angelo Zerr and other contributors as
 * indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ec4j.core;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.ec4j.core.Resource.Charsets;
import org.ec4j.core.Resource.RandomReader;
import org.ec4j.core.Resource.Resources;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public class BomTest {

    @Test
    public void openRandomReaderUtf8BomBad() throws IOException {
        final Path path = Paths.get("src/test/resources/bom/utf-8-bom-bad.txt");
        final Resource resource = Resources.ofPath(path, Charsets.forName("utf-8-bom"));
        try {
            resource.openRandomReader();
        } catch (IllegalStateException e) {
            Assert.assertEquals(
                    "Input expected to start with Byte Order Mark (BOM) [0xEF, 0xBB, 0xBF], found [0x68] at offset [2]",
                    e.getMessage());
        }
    }

    @Test
    public void openRandomReaderUtf8BomGood() throws IOException {
        final Path path = Paths.get("src/test/resources/bom/utf-8-bom-good.txt");
        final Resource resource = Resources.ofPath(path, Charsets.forName("utf-8-bom"));
        final StringBuilder sb = new StringBuilder();
        final RandomReader r = resource.openRandomReader();
        for (int i = 0; i < r.getLength(); i++) {
            sb.append(r.read(i));
        }
        Assert.assertEquals("hello world", sb.toString());
    }

    @Test
    public void openRandomReaderUtf8BomZeroLength() throws IOException {
        final Path path = Paths.get("src/test/resources/bom/zero-length.txt");
        final Resource resource = Resources.ofPath(path, Charsets.forName("utf-8-bom"));
        final StringBuilder sb = new StringBuilder();
        final RandomReader r = resource.openRandomReader();
        for (int i = 0; i < r.getLength(); i++) {
            sb.append(r.read(i));
        }
        Assert.assertEquals("", sb.toString());
    }

    @Test
    public void openReaderUtf8BomBad() throws IOException {
        final Path path = Paths.get("src/test/resources/bom/utf-8-bom-bad.txt");
        final Resource resource = Resources.ofPath(path, Charsets.forName("utf-8-bom"));
        try (Reader r = resource.openReader()) {
            Assert.fail(IllegalStateException.class.getSimpleName() + " expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals(
                    "Stream expected to start with Byte Order Mark (BOM) [0xEF, 0xBB, 0xBF], found [0x68] at offset [2]",
                    e.getMessage());
        }
    }

    @Test
    public void openReaderUtf8BomGood() throws IOException {
        final Path path = Paths.get("src/test/resources/bom/utf-8-bom-good.txt");
        final Resource resource = Resources.ofPath(path, Charsets.forName("utf-8-bom"));
        final StringBuilder sb = new StringBuilder();
        try (Reader r = resource.openReader()) {
            int c;
            while ((c = r.read()) >= 0) {
                sb.append((char) c);
            }
        }
        Assert.assertEquals("hello world", sb.toString());
    }

    @Test
    public void openReaderUtf8BomZeroLength() throws IOException {
        final Path path = Paths.get("src/test/resources/bom/zero-length.txt");
        final Resource resource = Resources.ofPath(path, Charsets.forName("utf-8-bom"));
        final StringBuilder sb = new StringBuilder();
        try (Reader r = resource.openReader()) {
            int c;
            while ((c = r.read()) >= 0) {
                sb.append((char) c);
            }
        }
        Assert.assertEquals("", sb.toString());
    }
}
