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

/**
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
public class TestResource {

    private final String name;
    private final TestResource parent;

    public TestResource(String name, TestFolder parent) {
        this.name = name;
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        StringBuilder path = new StringBuilder(name);
        TestResource p = parent;
        while (p != null) {
            path.insert(0, p.getName() + "/");
            p = p.getParent();
        }
        return path.toString();
    }

    public boolean exists() {
        return true;
    }

    public TestResource getParent() {
        return parent;
    }
}
