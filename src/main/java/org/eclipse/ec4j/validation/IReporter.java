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
package org.eclipse.ec4j.validation;

import org.eclipse.ec4j.parser.ErrorType;
import org.eclipse.ec4j.parser.Location;

/**
 * EditorConfig reporter.
 *
 */
public interface IReporter {

	/**
	 * Add error.
	 * 
	 * @param message
	 *            the message error.
	 * @param start
	 *            the start location of the error.
	 * @param end
	 *            the end location of the error (can be null).
	 * @param type
	 *            the type of the error.
	 * @param severity
	 *            the severity of the error.
	 */
	void addError(String message, Location start, Location end, ErrorType type, Severity severity);

}
