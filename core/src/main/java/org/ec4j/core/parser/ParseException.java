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
package org.ec4j.core.parser;

import org.ec4j.core.model.Glob;
import org.ec4j.core.model.PropertyType;

/**
 * An unchecked exception to indicate that an input does not qualify as valid .editorconfig.
 *
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public class ParseException extends RuntimeException {

    /**  */
    private static final long serialVersionUID = -3857553902141819279L;
    private final Location location;
    private final boolean syntaxError;

    public ParseException(String message, boolean syntaxError, Location location) {
        super(message + " at " + location);
        this.location = location;
        this.syntaxError = syntaxError;
    }

    /**
     * Returns the location at which the error occurred.
     *
     * @return the error location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Syntax errors are such ones that have to do with the {@code .editorconfig} file structure, such a as property
     * without value, or property name not followed by equal sign. Non-syntax errors that may happen with the current
     * implementation are only of two kinds: (i) broken {@link Glob} pattern or (ii) invalid value for the given
     * registered {@link PropertyType}.
     *
     * @return {@code true} is this is a syntax error; {@code false} otherwise
     */
    public boolean isSyntaxError() {
        return syntaxError;
    }

}