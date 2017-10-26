/**
 * Copyright (c) 2017 Angelo Zerr and other contributors as
 * indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eclipse.ec4j.services.validation;

import org.eclipse.ec4j.core.parser.ErrorType;
import org.eclipse.ec4j.core.parser.Location;

/**
 * EditorConfig reporter.
 *
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
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
