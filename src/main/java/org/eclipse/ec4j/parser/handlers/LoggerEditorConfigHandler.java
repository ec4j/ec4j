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

public class LoggerEditorConfigHandler<Section, Option> extends AbstractEditorConfigHandler<Section, Option> {

	@Override
	public void startDocument() {
		
	}
	
	@Override
	public void endDocument() {
		
	}
	
	@Override
	public Section startSection() {
		System.err.println("Start section at " + getLocation());
		return null;
	}

	@Override
	public void endSection(Section section) {
		System.err.println("End section at " + getLocation());
	}

	@Override
	public void startPattern(Section section) {
		System.err.println("Start pattern at " + getLocation());
	}

	@Override
	public void endPattern(Section section, String pattern) {
		System.err.println("End pattern at " + getLocation() + ", pattern=" + pattern);
	}

	@Override
	public void startOption() {
		System.err.println("Start option at " + getLocation());
	}

	@Override
	public void endOption(Option option, Section section) {
		System.err.println("End option at " + getLocation());
	}

	@Override
	public void startOptionName() {
		System.err.println("Start option name at " + getLocation());
	}

	@Override
	public Option endOptionName(String name) {
		System.err.println("End option name at " + getLocation() + ", name=" + name);
		return null;
	}

	@Override
	public void startOptionValue(Option option, String name) {
		System.err.println("Start option value of '" + name + "' at " + getLocation());
	}

	@Override
	public void endOptionValue(Option option, String value, String name) {
		System.err.println("End option value of '" + name + "', value=" + value + " at " + getLocation());
	}

	@Override
	public void error(ParseException e) {
		e.printStackTrace();
	}
}
