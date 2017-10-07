/**
 *  Copyright (c) 2017 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
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
