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
import org.eclipse.ec4j.core.model.optiontypes.OptionNames;
import org.eclipse.ec4j.core.model.optiontypes.OptionType;

/**
 * A key value pair in a {@link Section}.
 *
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public class Option {

    public static class Builder {

        static boolean isValid(OptionType<?> type, String value) {
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

        /**
         * Return the lowercased option value for certain options.
         *
         * @param name
         * @param value
         * @return the lowercased option value for certain options.
         */
        private static String preprocessOptionValue(OptionNames option, String value) {
            // According test "lowercase_values1" a "lowercase_values2": test that same
            // property values are lowercased (v0.9.0 properties)
            switch (option) {
            case end_of_line:
            case indent_style:
            case indent_size:
            case insert_final_newline:
            case trim_trailing_whitespace:
            case charset:
                return value.toLowerCase();
            default:
                return value;
            }
        }

        private String name;
        private final Section.Builder parentBuilder;
        private Object parsedValue;
        private OptionType<?> type;
        private boolean valid;
        private String value;

        public Builder(org.eclipse.ec4j.core.model.Section.Builder parentBuilder) {
            super();
            this.parentBuilder = parentBuilder;
        }

        boolean checkMax() {
            if (name != null && name.length() > 50) {
                return false;
            }
            if (value != null && value.length() > 255) {
                return false;
            }
            return true;
        }

        /**
         * Creates a new {@link Option} instance, adds it to the parent {@link Section.Builder} using parent
         * {@link Section.Builder#option(Option)} and returns the parent {@link Section.Builder}
         *
         * @return the parent {@link Section.Builder}
         */
        public Section.Builder closeOption() {
            if (checkMax()) {
                Option option = new Option(type, name, value, parsedValue, valid);
                parentBuilder.option(option);
            }
            return parentBuilder;
        }

        /**
         * Sets the {@link #name}.
         *
         * @param name
         *            the key of this key value pair
         * @return this {@link Builder}
         */
        public Builder name(String name) {
            this.name = name;
            type = parentBuilder.parentBuilder.registry.getType(name);
            return this;
        }

        /**
         * Sets the {@link #value}.
         *
         * @param name
         *            the value of this key value pair
         * @return this {@link Builder}
         */
        public Builder value(String value) {
            this.value = preprocessOptionValue(OptionNames.get(name), value);
            this.valid = isValid(type, value);
            if (valid) {
                this.parsedValue = type.parse(value);
            }
            return this;
        }
    }

    private final String name;

    private final Object parsedValue;

    private final String sourceValue;
    private final OptionType<?> type;
    private final boolean valid;
    /**
     * Use the {@link Builder} if you cannot access this constructor
     * @param type
     * @param name
     * @param sourceValue
     * @param parsedValue
     * @param valid
     */
    Option(OptionType<?> type, String name, String sourceValue, Object parsedValue, boolean valid) {
        super();
        this.type = type;
        this.name = name;
        this.sourceValue = sourceValue;
        this.parsedValue = parsedValue;
        this.valid = valid;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Option other = (Option) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (sourceValue == null) {
            if (other.sourceValue != null)
                return false;
        } else if (!sourceValue.equals(other.sourceValue))
            return false;
        return true;
    }

    /**
     * @return the key of this key value pair
     */
    public String getName() {
        return name;
    }

    /**
     * @return the string value of this key value pair
     */
    public String getSourceValue() {
        return sourceValue;
    }

    /**
     * @return the {@link OptionType} associated with the {@link #name} or {@code null} if no {@link OptionType} is
     *         associated with the {@link #getName()}
     */
    public OptionType<?> getType() {
        return type;
    }

    /**
     * @return the parsed value
     */
    @SuppressWarnings("unchecked")
    public <T> T getValueAs() {
        return (T) parsedValue;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((sourceValue == null) ? 0 : sourceValue.hashCode());
        return result;
    }

    /**
     * @return {@code true} if {@link #sourceValue} is a valid value for the associated {@link OptionType};
     *         {@code false} otherwise
     */
    public boolean isValid() {
        return valid;
    }

    @Override
    public String toString() {
        return new StringBuilder(name).append(" = ").append(sourceValue).toString();
    }

}
