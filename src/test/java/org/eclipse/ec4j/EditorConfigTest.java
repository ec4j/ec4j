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
import java.io.StringReader;

import org.eclipse.ec4j.model.EditorConfig;
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
