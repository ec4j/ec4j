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
package org.eclipse.ec4e.services.dom;

import java.io.IOException;
import java.io.StringReader;

import org.eclipse.ec4e.services.model.EditorConfig;
import org.junit.Assert;
import org.junit.Test;

public class EditorConfigTest {

	@Test
	public void load() throws IOException {
		String s = "# EditorConfig is awesome: http://EditorConfig.org\n";
		s += "\n";
		s += "# top-most EditorConfig file\n";
		s += "root = true\n";
		s += "\n";
		s += "# Unix-style newlines with a newline ending every file\n";
		s += "[*]\n";
		s += "end_of_line = lf\n";
		s += "insert_final_newline = true\n\n";
		s += "# Matches multiple files with brace expansion notation\n";
		s += "# Set default charset\n";
		s += "[*.{js,py}]\n";
		s += "charset = utf-8";
		EditorConfig config = EditorConfig.load(new StringReader(s));
		Assert.assertEquals("root = true\n" + "\n" + "[*]\n" + "end_of_line = lf\n" + "insert_final_newline = true\n" + "\n"
				+ "[*.{js,py}]\n" + "charset = utf-8", config.toString());
	}
}
