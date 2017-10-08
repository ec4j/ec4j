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
	
	@Test
	public void brackets_close_inside() throws IOException, EditorConfigException {
		String content = "; test [ and ]\r\n" + 
				"\r\n" + 
				"root=true\r\n" + 
				"\r\n" + 
				"; Character choice\r\n" + 
				"[[ab].a]\r\n" + 
				"choice=true\r\n" + 
				"\r\n" + 
				"; Negative character choice\r\n" + 
				"[[!ab].b]\r\n" + 
				"choice=false\r\n" + 
				"\r\n" + 
				"; Character range\r\n" + 
				"[[d-g].c]\r\n" + 
				"range=true\r\n" + 
				"\r\n" + 
				"; Negative character range\r\n" + 
				"[[!d-g].d]\r\n" + 
				"range=false\r\n" + 
				"\r\n" + 
				"; Range and choice\r\n" + 
				"[[abd-g].e]\r\n" + 
				"range_and_choice=true\r\n" + 
				"\r\n" + 
				"; Choice with dash\r\n" + 
				"[[-ab].f]\r\n" + 
				"choice_with_dash=true\r\n" + 
				"\r\n" + 
				"; Close bracket inside\r\n" + 
				"[[\\]ab].g]\r\n" + 
				"close_inside=true\r\n" + 
				"\r\n" + 
				"; Close bracket outside\r\n" + 
				"[[ab]].g]\r\n" + 
				"close_outside=true\r\n" + 
				"\r\n" + 
				"; Negative close bracket inside\r\n" + 
				"[[!\\]ab].g]\r\n" + 
				"close_inside=false\r\n" + 
				"\r\n" + 
				"; Negative¬close bracket outside\r\n" + 
				"[[!ab]].g]\r\n" + 
				"close_outside=false\r\n" + 
				"\r\n" + 
				"; Slash inside brackets\r\n" + 
				"[ab[e/]cd.i]\r\n" + 
				"slash_inside=true\r\n" + 
				"\r\n" + 
				"; Slash after an half-open bracket\r\n" + 
				"[ab[/c]\r\n" + 
				"slash_half_open=true\r\n" + 
				"";
		
		TestEditorConfigManager manager = new TestEditorConfigManager();
		
		TestFolder root = new TestFolder("root");
		root.addFile(".editorconfig", content);
		TestFolder bar = root.addFolder("Bar");
		TestFile file = bar.addFile("foo.txt");

		Collection<Option> options = manager.getOptions(file, null);
		Assert.assertEquals(1, options.size());
		Assert.assertEquals("close_inside = true", options.iterator().next().toString());
	}	
}
