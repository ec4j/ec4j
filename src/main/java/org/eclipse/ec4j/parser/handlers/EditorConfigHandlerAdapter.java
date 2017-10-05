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
package org.eclipse.ec4j.parser.handlers;

import org.eclipse.ec4j.parser.ParseException;

public class EditorConfigHandlerAdapter<Section, Option> extends AbstractEditorConfigHandler<Section, Option> {

	@Override
	public Section startSection() {
		return null;
	}

	@Override
	public void endSection(Section section) {

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

	}

	@Override
	public void startOption() {

	}

	@Override
	public void endOption(Option option, Section section) {

	}

	@Override
	public void startOptionName() {

	}

	@Override
	public Option endOptionName(String name) {
		return null;
	}

	@Override
	public void startOptionValue(Object option, String name) {

	}

	@Override
	public void endOptionValue(Object option, String value, String name) {

	}

	@Override
	public void error(ParseException e) {

	}
}
