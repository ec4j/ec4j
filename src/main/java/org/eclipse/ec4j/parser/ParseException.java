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

/**
 * An unchecked exception to indicate that an input does not qualify as valid
 * .editorconfig.
 */
@SuppressWarnings("serial") // use default serial UID
public class ParseException extends RuntimeException {

	private final Location location;
	private final ErrorType errorType;

	ParseException(String message, Location location) {
		this(message, location, ErrorType.ParsingError);
	}

	ParseException(String message, Location location, ErrorType errorType) {
		super(message + " at " + location);
		this.location = location;
		this.errorType = errorType;
	}

	/**
	 * Returns the location at which the error occurred.
	 *
	 * @return the error location
	 */
	public Location getLocation() {
		return location;
	}

	public ErrorType getErrorType() {
		return errorType;
	}
}