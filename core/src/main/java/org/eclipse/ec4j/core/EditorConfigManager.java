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
package org.eclipse.ec4j.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import org.eclipse.ec4j.core.model.optiontypes.OptionTypeRegistry;

/**
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
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
