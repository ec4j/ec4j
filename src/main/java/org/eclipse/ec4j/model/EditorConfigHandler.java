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
package org.eclipse.ec4j.model;

import org.eclipse.ec4j.model.optiontypes.OptionNames;
import org.eclipse.ec4j.model.optiontypes.OptionTypeRegistry;
import org.eclipse.ec4j.parser.ParseException;
import org.eclipse.ec4j.parser.handlers.AbstractEditorConfigHandler;

class EditorConfigHandler extends AbstractEditorConfigHandler<Section, Option> {

	private final EditorConfig editorConfig;

	public EditorConfigHandler(OptionTypeRegistry registry, String version) {
		this.editorConfig = new EditorConfig(registry, version);
	}

	@Override
	public void startDocument() {
		
	}
	
	@Override
	public void endDocument() {
		for (Section section : editorConfig.getSections()) {
			section.preprocessOptions();
		}
	}
	
	@Override
	public Section startSection() {
		return new Section(editorConfig);
	}

	@Override
	public void endSection(Section section) {
		editorConfig.addSection(section);		
	}

	@Override
	public void startPattern(Section section) {

	}

	@Override
	public void endPattern(Section section, String pattern) {
		section.setPattern(pattern);
	}

	@Override
	public void startOption() {

	}

	@Override
	public void endOption(Option option, Section section) {
		if (section != null) {
			section.addOption(option);
		} else if (OptionNames.get(option.getName()) == OptionNames.root) {
			editorConfig.setRoot("true".equals(option.getValue()));
		}
	}

	@Override
	public void startOptionName() {

	}

	@Override
	public Option endOptionName(String name) {
		return new Option(name, editorConfig);
	}

	@Override
	public void startOptionValue(Option option, String name) {

	}

	@Override
	public void endOptionValue(Option option, String value, String name) {
		option.setValue(value);
	}

	@Override
	public void error(ParseException e) {
		e.printStackTrace();
	}

	public EditorConfig getEditorConfig() {
		return editorConfig;
	}

}