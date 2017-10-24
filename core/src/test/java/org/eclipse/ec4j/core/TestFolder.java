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

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
public class TestFolder extends TestResource {

    private final Map<String, TestResource> children;

    public TestFolder(String name) {
        this(name, null);
    }

    public TestFolder(String name, TestFolder parent) {
        super(name, parent);
        children = new HashMap<>();
    }

    public TestFile addFile(String name, String content) {
        TestFile file = new TestFile(name, content, this);
        children.put(file.getName(), file);
        return file;
    }

    public TestFolder addFolder(String name) {
        TestFolder folder = new TestFolder(name, this);
        children.put(folder.getName(), folder);
        return folder;
    }

    public TestFile addFile(String name) {
        return addFile(name, null);
    }

    public TestResource getResource(String child) {
        return children.get(child);
    }

}
