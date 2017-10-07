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

import java.io.IOException;
import java.io.Reader;

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
