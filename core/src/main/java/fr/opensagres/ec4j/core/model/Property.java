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
package fr.opensagres.ec4j.core.model;

import fr.opensagres.ec4j.core.model.propertytype.PropertyException;
import fr.opensagres.ec4j.core.model.propertytype.PropertyName;
import fr.opensagres.ec4j.core.model.propertytype.PropertyType;

/**
 * A key value pair in a {@link Section}.
 *
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public class Property {

    public static class Builder {

        static boolean isValid(PropertyType<?> type, String value) {
            if (type == null) {
                return false;
            }
            try {
                type.validate(value);
            } catch (PropertyException e) {
                return false;
            }
            return true;
        }

        /**
         * Return the lowercased value for certain properties.
         *
         * @param propertyName the {@link PropertyName}
         * @param value the value to transform
         * @return the transformed value or the same {@code value} as was passed in if no transformation was necessary
         */
        private static String preprocessValue(PropertyName propertyName, String value) {
            // According test "lowercase_values1" a "lowercase_values2": test that same
            // property values are lowercased (v0.9.0 properties)
            switch (propertyName) {
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
        private PropertyType<?> type;
        private boolean valid;
        private String value;

        public Builder(fr.opensagres.ec4j.core.model.Section.Builder parentBuilder) {
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
         * Creates a new {@link Property} instance, adds it to the parent {@link Section.Builder} using parent
         * {@link Section.Builder#property(Property)} and returns the parent {@link Section.Builder}
         *
         * @return the parent {@link Section.Builder}
         */
        public Section.Builder closeProperty() {
            if (checkMax()) {
                Property property = new Property(type, name, value, parsedValue, valid);
                parentBuilder.property(property);
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
            this.value = preprocessValue(PropertyName.get(name), value);
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
    private final PropertyType<?> type;
    private final boolean valid;
    /**
     * Use the {@link Builder} if you cannot access this constructor
     * @param type
     * @param name
     * @param sourceValue
     * @param parsedValue
     * @param valid
     */
    Property(PropertyType<?> type, String name, String sourceValue, Object parsedValue, boolean valid) {
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
        Property other = (Property) obj;
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
     * @return the {@link PropertyType} associated with the {@link #name} or {@code null} if no {@link PropertyType} is
     *         associated with the {@link #getName()}
     */
    public PropertyType<?> getType() {
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
     * @return {@code true} if {@link #sourceValue} is a valid value for the associated {@link PropertyType};
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
