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
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.ec4j.model.Option;
import org.junit.Assert;
import org.junit.Test;

/**
 * Some glob test of
 * https://github.com/editorconfig/editorconfig-core-test/tree/master/properties
 *
 */
public class EditorConfigPropertiesTest {

	@Test
	public void indent_size_default() throws IOException, EditorConfigException {
		String content = "root = true\r\n" + 
				"\r\n" + 
				"[test.c]\r\n" + 
				"indent_style = tab\r\n" + 
				"\r\n" + 
				"[test2.c]\r\n" + 
				"indent_style = space\r\n" + 
				"\r\n" + 
				"[test3.c]\r\n" + 
				"indent_style = tab\r\n" + 
				"tab_width = 2\r\n" + 
				"";
		
		TestEditorConfigManager manager = new TestEditorConfigManager();
		
		TestFolder root = new TestFolder("root");
		root.addFile(".editorconfig", content);
		TestFile file = root.addFile("test.c");

		Collection<Option> options = manager.getOptions(file, null);
		Assert.assertEquals(2, options.size());
		Iterator<Option> iter = options.iterator();
		Assert.assertEquals("indent_style = tab", iter.next().toString());
		Assert.assertEquals("indent_size = tab", iter.next().toString());
	}

}
