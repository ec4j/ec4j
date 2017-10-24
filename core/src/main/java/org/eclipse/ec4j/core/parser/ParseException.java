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
package org.eclipse.ec4j.core.parser;

/**
 * An unchecked exception to indicate that an input does not qualify as valid
 * .editorconfig.
 *
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
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