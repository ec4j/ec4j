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
import org.junit.Ignore;
import org.junit.Test;

/**
 * Some glob test of
 * https://github.com/editorconfig/editorconfig-core-test/tree/master/parser
 *
 */
public class EditorConfigParserTest {

	private static final String WHITESPACE_DOT_IN = "; test whitespace usage\r\n" + 
			"\r\n" + 
			"root = true\r\n" + 
			"\r\n" + 
			"; no whitespace\r\n" + 
			"[test1.c]\r\n" + 
			"key=value\r\n" + 
			"\r\n" + 
			"; spaces around equals\r\n" + 
			"[test2.c]\r\n" + 
			"key = value\r\n" + 
			"\r\n" + 
			"; lots of space after equals\r\n" + 
			"[test3.c]\r\n" + 
			"key  =   value\r\n" + 
			"\r\n" + 
			"; spaces before property name\r\n" + 
			"[test4.c]\r\n" + 
			"  key=value\r\n" + 
			"\r\n" + 
			"; spaces after property value\r\n" + 
			"[test5.c]\r\n" + 
			"key=value  \r\n" + 
			"\r\n" + 
			"; blank lines between properties\r\n" + 
			"[test6.c]\r\n" + 
			"\r\n" + 
			"key1=value1\r\n" + 
			"\r\n" + 
			"key2=value2\r\n" + 
			"\r\n" + 
			"; spaces in section name\r\n" + 
			"[ test 7 ]\r\n" + 
			"key=value\r\n" + 
			"\r\n" + 
			"; spaces before section name\r\n" + 
			"  [test8.c]\r\n" + 
			"key=value\r\n" + 
			"\r\n" + 
			"; spaces after section name\r\n" + 
			"[test9.c]  \r\n" + 
			"key=value\r\n" + 
			"\r\n" + 
			"; spacing before middle property\r\n" + 
			"[test10.c]\r\n" + 
			"key1=value1\r\n" + 
			"  key2=value2\r\n" + 
			"key3=value3\r\n" + 
			"\r\n" + 
			"; colon separator with no spaces\r\n" + 
			"[test1.d]\r\n" + 
			"key:value\r\n" + 
			"\r\n" + 
			"; colon separator with space after\r\n" + 
			"[test2.d]\r\n" + 
			"key: value\r\n" + 
			"\r\n" + 
			"; colon separator with space before and after\r\n" + 
			"[test3.d]\r\n" + 
			"key : value\r\n" + 
			"\r\n" + 
			"; colon separator with spaces befor\r\n" + 
			"[test4.d]\r\n" + 
			"  key:value\r\n" + 
			"\r\n" + 
			"; colon seperator with spaces after property value\r\n" + 
			"[test5.d]\r\n" + 
			"key:value  \r\n" + 
			"";

	@Test
	public void spaces_in_section_name() throws IOException, EditorConfigException {
		
		TestEditorConfigManager manager = new TestEditorConfigManager();
		
		TestFolder root = new TestFolder("root");
		root.addFile(".editorconfig", WHITESPACE_DOT_IN);
		TestFile file = root.addFile(" test 7 ");

		Collection<Option> options = manager.getOptions(file, null);
		Assert.assertEquals(1, options.size());
		Assert.assertEquals("key = value", options.iterator().next().toString());
	}

	private static final String COMMENTS_DOT_IN = "; test comments\r\n" + 
			"\r\n" + 
			"root = true\r\n" + 
			"\r\n" + 
			"[test1.c]\r\n" + 
			"key=value ; Comment after property is ignored\r\n" + 
			"\r\n" + 
			"[test2.c] ; Comment ignored, even with ] character\r\n" + 
			"key=value\r\n" + 
			"\r\n" + 
			"[test3.c]\r\n" + 
			"; Comment before properties ignored\r\n" + 
			"key=value\r\n" + 
			"\r\n" + 
			"[test4.c]\r\n" + 
			"key1=value1\r\n" + 
			"; Comment between properties ignored\r\n" + 
			"key2=value2\r\n" + 
			"\r\n" + 
			"; Semicolon at end of value read as part of value\r\n" + 
			"[test5.c]\r\n" + 
			"key=value; not comment\r\n" + 
			"\r\n" + 
			"; Escaped semicolon in value\r\n" + 
			"[test6.c]\r\n" + 
			"key=value \\; not comment\r\n" + 
			"\r\n" + 
			"; Escaped semicolon in section name\r\n" + 
			"[test\\;.c]\r\n" + 
			"key=value\r\n" + 
			"\r\n" + 
			"[test7.c]\r\n" + 
			"key=value # Comment after property is ignored\r\n" + 
			"\r\n" + 
			"[test8.c] # Comment ignored, even with ] character\r\n" + 
			"key=value\r\n" + 
			"\r\n" + 
			"[test9.c]\r\n" + 
			"# Comment before properties ignored\r\n" + 
			"key=value\r\n" + 
			"\r\n" + 
			"[test10.c]\r\n" + 
			"key1=value1\r\n" + 
			"# Comment between properties ignored\r\n" + 
			"key2=value2\r\n" + 
			"\r\n" + 
			"# Semicolon at end of value read as part of value\r\n" + 
			"[test11.c]\r\n" + 
			"key=value# not comment\r\n" + 
			"\r\n" + 
			"# Escaped semicolon in value\r\n" + 
			"[test12.c]\r\n" + 
			"key=value \\# not comment\r\n" + 
			"\r\n" + 
			"# Escaped semicolon in section name\r\n" + 
			"[test\\#.c]\r\n" + 
			"key=value\r\n" + 
			"\r\n" + 
			"";
	
	@Test
	public void comments_after_property() throws IOException, EditorConfigException {
		
		TestEditorConfigManager manager = new TestEditorConfigManager();
		
		TestFolder root = new TestFolder("root");
		root.addFile(".editorconfig", COMMENTS_DOT_IN);
		TestFile file = root.addFile("test7.c");

		Collection<Option> options = manager.getOptions(file, null);
		Assert.assertEquals(1, options.size());
		Assert.assertEquals("key = value ", options.iterator().next().toString());
	}
	
}
