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
import org.junit.Test;

public class EditorConfigTest2 {

	@Test
	public void load() throws IOException, EditorConfigException {
		String s = "; test EditorConfig files with UTF-8 characters larger than 127\r\n" + 
				"\r\n" + 
				"root = true\r\n" + 
				"\r\n" + 
				"[中文.txt]\r\n" + 
				"key = value\r\n" + 
				"";
		
		TestEditorConfigManager manager = new TestEditorConfigManager();
		
		TestFolder root = new TestFolder("root");
		root.addFile(".editorconfig", s);
		TestFile file = root.addFile("中文.txt");

		Collection<Option> options = manager.getOptions(file, null);
		System.err.println(options);
	}
}
