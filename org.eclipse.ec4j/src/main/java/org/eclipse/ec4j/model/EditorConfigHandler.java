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
package org.eclipse.ec4j.model;

import org.eclipse.ec4j.model.optiontypes.OptionTypeRegistry;
import org.eclipse.ec4j.parser.ParseException;
import org.eclipse.ec4j.parser.handlers.AbstractEditorConfigHandler;

class EditorConfigHandler extends AbstractEditorConfigHandler<Section, Option> {

	private final EditorConfig editorConfig;

	public EditorConfigHandler(OptionTypeRegistry registry) {
		this.editorConfig = new EditorConfig(registry);
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
	public void startMultiPatternSection(Section section) {

	}

	@Override
	public void endMultiPatternSection(Section section) {

	}

	@Override
	public void startPattern(Section section, int i) {

	}

	@Override
	public void endPattern(Section section, String pattern, int i) {
		section.addPattern(pattern);
	}

	@Override
	public void startOption() {

	}

	@Override
	public void endOption(Option option, Section section) {
		if (section != null) {
			section.addOption(option);
		} else if ("root".equals(option.getName())) {
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