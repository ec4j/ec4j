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

public class LoggerEditorConfigHandler<Section, Option> extends AbstractEditorConfigHandler<Section, Option> {

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
	public void startMultiPatternSection(Section section) {
		System.err.println("Start multi pattern at " + getLocation());
	}

	@Override
	public void endMultiPatternSection(Section section) {
		System.err.println("End multi pattern at " + getLocation());
	}

	@Override
	public void startPattern(Section section, int i) {
		System.err.println("Start pattern [" + i + "] at " + getLocation());
	}

	@Override
	public void endPattern(Section section, String pattern, int i) {
		System.err.println("End pattern [" + i + "] at " + getLocation() + ", pattern=" + pattern);
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
