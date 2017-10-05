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

public interface ISeverityProvider {

	public static final ISeverityProvider DEFAULT = new ISeverityProvider() {

		@Override
		public Severity getSeverity(ErrorType errorType) {
			return errorType.isSyntaxError() ? Severity.error : Severity.warning;
		}
	};

	Severity getSeverity(ErrorType errorType);
}
