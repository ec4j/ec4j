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
package org.eclipse.ec4j.parser.handlers;

import org.eclipse.ec4j.parser.ParseException;

public class EditorConfigHandlerAdapter<S, O> extends AbstractEditorConfigHandler<S, O> {

	@Override
	public void startDocument() {

	}

	@Override
	public void endDocument() {

	}

	@Override
	public S startSection() {
		return null;
	}

	@Override
	public void endSection(S section) {

	}

	@Override
	public void startPattern(S section) {

	}

	@Override
	public void endPattern(S section, String pattern) {

	}

	@Override
	public void startOption() {

	}

	@Override
	public void endOption(O option, S section) {

	}

	@Override
	public void startOptionName() {

	}

	@Override
	public O endOptionName(String name) {
		return null;
	}

	@Override
	public void startOptionValue(O option, String name) {

	}

	@Override
	public void endOptionValue(O option, String value, String name) {

	}

	@Override
	public void error(ParseException e) {

	}
}
