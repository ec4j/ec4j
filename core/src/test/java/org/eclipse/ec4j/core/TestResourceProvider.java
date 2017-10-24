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

import java.io.IOException;
import java.io.Reader;

/**
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
public class TestResourceProvider implements ResourceProvider<TestResource> {

    @Override
    public TestResource getParent(TestResource file) {
        return file.getParent();
    }

    @Override
    public TestResource getResource(TestResource parent, String child) {
        return ((TestFolder) parent).getResource(child);
    }

    @Override
    public boolean exists(TestResource configFile) {
        return configFile != null && configFile.exists();
    }

    @Override
    public String getPath(TestResource file) {
        return file.getPath();
    }

    @Override
    public Reader getContent(TestResource configFile) throws IOException {
        return ((TestFile) configFile).getReader();
    }

}
