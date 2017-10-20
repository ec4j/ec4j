/**
 * The MIT License
 * Copyright © 2017 Angelo Zerr and other contributors as
 * indicated by the @author tags.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.eclipse.ec4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import org.eclipse.ec4j.model.optiontypes.OptionTypeRegistry;

public class EditorConfigManager extends AbstractEditorConfigManager<File> {

    public static final ResourceProvider<File> FILE_RESOURCE_PROVIDER = new ResourceProvider<File>() {

        @Override
        public File getParent(File file) {
            return file.getParentFile();
        }

        @Override
        public File getResource(File parent, String child) {
            return new File(parent, child);
        }

        @Override
        public boolean exists(File file) {
            return file.exists();
        }

        @Override
        public String getPath(File file) {
            return file.toString().replaceAll("[\\\\]", "/");
        }

        @Override
        public Reader getContent(File configFile) throws IOException {
            return new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8);
        }
    };

    public EditorConfigManager() {
        super(FILE_RESOURCE_PROVIDER);
    }

    public EditorConfigManager(OptionTypeRegistry registry, String configFilename, String version) {
        super(registry, FILE_RESOURCE_PROVIDER, configFilename, version);
    }
}
