/**
 *  Copyright (c) 2017 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse  License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.ec4j.parser.handlers;

import org.eclipse.ec4j.parser.EditorConfigParser;
import org.eclipse.ec4j.parser.ParseException;

public interface IEditorConfigHandler<Section, Option> {

	void setParser(EditorConfigParser<Section, Option> parser);

	Section startSection();

	void endSection(Section section);

	void startMultiPatternSection(Section section);

	void endMultiPatternSection(Section section);

	void startPattern(Section section, int i);

	void endPattern(Section section, String pattern, int i);

	void startOption();

	void startOptionName();

	Option endOptionName(String name);

	void endOption(Option option, Section section);

	void startOptionValue(Option option, String name);

	void endOptionValue(Option option, String value, String name);

	void error(ParseException e);

}