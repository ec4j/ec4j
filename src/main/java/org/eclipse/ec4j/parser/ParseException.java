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