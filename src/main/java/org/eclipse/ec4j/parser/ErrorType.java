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
package org.eclipse.ec4j.parser;

public enum ErrorType {

	ParsingError(true), SectionNotClosed(true), MultiPatternNotClosed(true), OptionAssignementMissing(
			true), OptionValueMissing(true), OptionNameNotExists(false), OptionValueType(false);

	private final boolean syntaxError;

	private ErrorType(boolean syntaxError) {
		this.syntaxError = syntaxError;
	}

	public boolean isSyntaxError() {
		return syntaxError;
	}
}
