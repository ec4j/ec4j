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
package org.eclipse.ec4j.core.model.propertytype;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
public enum EndOfLineValue {

    lf("Line Feed", "\n"),

    cr("Carriage Return", "\r"),

    crlf("Carriage Return + Line Feed", "\r\n");

    private static final Set<String> VALUE_SET;
    static {
        Set<String> s = new LinkedHashSet<>();
        for (EndOfLineValue v : values()) {
            s.add(v.name());
        }
        VALUE_SET = Collections.unmodifiableSet(s);
    }

    private final String displayValue;

    private final String eolString;

    EndOfLineValue(final String displayValue, final String eolString) {
        this.displayValue = displayValue;
        this.eolString = eolString;
    }

    public String getEndOfLineString() {
        return eolString;
    }

    public static Set<String> valueSet() {
        return VALUE_SET;
    }
}
