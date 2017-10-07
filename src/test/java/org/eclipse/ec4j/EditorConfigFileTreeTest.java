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
 * https://github.com/editorconfig/editorconfig-core-test/tree/master/filetree
 *
 */
public class EditorConfigFileTreeTest {

	@Test
	public void windows_separator() throws IOException, EditorConfigException {
		String content = "; test for path separator\r\n" + 
				"\r\n" + 
				"root=true\r\n" + 
				"\r\n" + 
				"[path/separator]\r\n" + 
				"key=value\r\n" + 
				"\r\n" + 
				"[/top/of/path]\r\n" + 
				"key=value\r\n" + 
				"\r\n" + 
				"[windows\\separator]\r\n" + 
				"key=value\r\n" + 
				"\r\n" + 
				"[windows\\\\separator2]\r\n" + 
				"key=value\r\n" + 
				"";
		
		TestEditorConfigManager manager = new TestEditorConfigManager();
		
		TestFolder root = new TestFolder("root");
		root.addFile(".editorconfig", content);
		TestFolder windows = root.addFolder("windows");
		TestFile file = windows.addFile("separator");

		Collection<Option> options = manager.getOptions(file, null);
		Assert.assertTrue(options.isEmpty());
	}

}
