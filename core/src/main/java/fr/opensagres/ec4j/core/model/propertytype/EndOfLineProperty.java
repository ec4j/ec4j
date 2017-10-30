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
package fr.opensagres.ec4j.core.model.propertytype;

/**
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
public enum EndOfLineProperty {

    LF("Line Feed", "\n"),

    CR("Carriage Return", "\r"),

    CRLF("Carriage Return + Line Feed", "\r\n");

    private final String displayValue;

    private String eolString;

    EndOfLineProperty(final String displayValue, final String eolString) {
        this.displayValue = displayValue;
        this.eolString = eolString;
    }

    public String getEndOfLineString() {
        return eolString;
    }

}
