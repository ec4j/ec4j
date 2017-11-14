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

import org.ec4j.core.PropertyTypeRegistry;
import org.ec4j.core.model.Property;
import org.ec4j.core.model.PropertyType;

/**
 * Thrown when an {@link EditorConfigParser} encounters an invalid {@link Property} value. Validity of property values
 * is decided by {@link PropertyType} registered for the given property name in {@link PropertyTypeRegistry}.
 *
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public class InvalidPropertyValueException extends ParseException {

    private static final long serialVersionUID = 111772326725147819L;

    InvalidPropertyValueException(String message, Location location) {
        super(message, location);
    }

}
