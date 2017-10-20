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

import java.util.HashMap;
import java.util.Map;

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
