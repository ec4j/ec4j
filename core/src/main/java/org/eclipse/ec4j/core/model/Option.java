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
package org.eclipse.ec4j.core.model;

import org.eclipse.ec4j.core.model.optiontypes.OptionException;
import org.eclipse.ec4j.core.model.optiontypes.OptionType;

/**
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
public class Option {

    private final String name;
    private final EditorConfig editorConfig;
    private final OptionType<?> type;
    private Boolean valid;
    private String value;

    public Option(String name, EditorConfig editorConfig) {
        this.name = name;
        this.editorConfig = editorConfig;
        this.type = editorConfig.getRegistry().getType(name);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(String value) {
        this.value = value;
        this.valid = null;
    }

    @Override
    public String toString() {
        return new StringBuilder(name).append(" = ").append(value).toString();
    }

    public <T> T getValueAs() {
        // TODO: call type.validate
        return (T) type.parse(getValue());
    }

    public boolean isValid() {
        if (valid == null) {
            valid = computeValid();
        }
        return valid;
    }

    private Boolean computeValid() {
        OptionType<?> type = getType();
        if (type == null) {
            return false;
        }
        try {
            type.validate(value);
        } catch (OptionException e) {
            return false;
        }
        return true;
    }

    public OptionType<?> getType() {
        return type;
    }

    public boolean checkMax() {
        if (name != null && name.length() > 50) {
            return false;
        }
        if (value != null && value.length() > 255) {
            return false;
        }
        return true;
    }

}
