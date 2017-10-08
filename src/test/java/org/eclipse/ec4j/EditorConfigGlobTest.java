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

import org.eclipse.ec4j.model.Option;
import org.junit.Assert;
import org.junit.Test;

/**
 * Some glob test of
 * https://github.com/editorconfig/editorconfig-core-test/tree/master/glob
 *
 */
public class EditorConfigGlobTest {

	@Test
	public void star_after_slash() throws IOException, EditorConfigException {
		String content = "; test *\r\n" + 
				"\r\n" + 
				"root=true\r\n" + 
				"\r\n" + 
				"[a*e.c]\r\n" + 
				"key=value\r\n" + 
				"\r\n" + 
				"[Bar/*]\r\n" + 
				"keyb=valueb\r\n" + 
				"";
		
		TestEditorConfigManager manager = new TestEditorConfigManager();
		
		TestFolder root = new TestFolder("root");
		root.addFile(".editorconfig", content);
		TestFolder bar = root.addFolder("Bar");
		TestFile file = bar.addFile("foo.txt");

		Collection<Option> options = manager.getOptions(file, null);
		Assert.assertEquals(1, options.size());
		Assert.assertEquals("keyb = valueb", options.iterator().next().toString());
	}
	
	@Test
	public void utf_8_char() throws IOException, EditorConfigException {
		String content = "; test EditorConfig files with UTF-8 characters larger than 127\r\n" + 
				"\r\n" + 
				"root = true\r\n" + 
				"\r\n" + 
				"[中文.txt]\r\n" + 
				"key = value\r\n" + 
				"";
		
		TestEditorConfigManager manager = new TestEditorConfigManager();
		
		TestFolder root = new TestFolder("root");
		root.addFile(".editorconfig", content);
		TestFile file = root.addFile("中文.txt");

		Collection<Option> options = manager.getOptions(file, null);
		Assert.assertEquals(1, options.size());
		Assert.assertEquals("key = value", options.iterator().next().toString());
	}
}
