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
package org.ec4j.core.model;

import java.util.List;

import org.ec4j.core.model.PropertyType.PropertyValue;

/**
 * A key value pair in a {@link Section}.
 *
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public class Property extends Adaptable {

    /**
     * A {@link Property} builder.
     */
    public static class Builder extends Adaptable.Builder<Builder> {

        String name;
        private final Section.Builder parentBuilder;
        PropertyType<?> type;
        PropertyValue<?> value;

        public Builder(org.ec4j.core.model.Section.Builder parentBuilder) {
            super();
            this.parentBuilder = parentBuilder;
        }

        /**
         * @return a new {@link Property}
         */
        public Property build() {
            return new Property(sealAdapters(), type, name, value);
        }

        /**
         * Adds this {@link Builder} to the parent {@link Section.Builder} using parent
         * {@link Section.Builder#property(Property.Builder)} and returns the parent {@link Section.Builder}
         *
         * @return the parent {@link Section.Builder}
         */
        public Section.Builder closeProperty() {
            return parentBuilder.property(this);
        }

        /**
         * Sets the {@link #name}. Note that you should prefer to use {@link #type(PropertyType)} if the type is known,
         * because {@link #type(PropertyType)} sets both {@link #name} and {@link #type} based on the
         * {@link PropertyType}.
         *
         * @param name
         *        the key of this key value pair
         * @return this {@link Builder}
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets {@link #type} and also sets {@link #name} to {@code type.getName}.
         *
         * @param type
         *        the {@link PropertyType} to set
         * @return this {@link Builder}
         */
        public Builder type(PropertyType<?> type) {
            this.name = type.getName();
            this.type = type;
            return this;
        }

        /**
         * Sets the {@link #value}.
         *
         * @param value
         *        the value of this key value pair
         * @return this {@link Builder}
         */
        public Builder value(PropertyValue<?> value) {
            this.value = value;
            return this;
        }

        /**
         * Sets the {@link #value}.
         *
         * @param value
         *        the value of this key value pair
         * @return this {@link Builder}
         */
        public Builder value(String value) {
            if (this.type == null) {
                value(PropertyValue.valid(value, value));
            } else {
                value(type.parse(value));
            }
            return this;
        }

    }

    /**
     * @return a new {@link Builder} with no parent bulder set
     */
    public static Builder builder() {
        return new Builder(null);
    }

    private final String name;

    private final PropertyType<?> type;

    private final PropertyValue<?> value;

    /**
     * Use the {@link Builder} if you cannot access this constructor
     *
     * @param adapters
     * @param type
     * @param name
     * @param value
     */
    Property(List<Object> adapters, PropertyType<?> type, String name, PropertyValue<?> value) {
        super(adapters);
        this.type = type;
        this.name = name;
        this.value = value;
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
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
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
        return value.getSource();
    }

    /**
     * @return the {@link PropertyType} associated with the {@link #name} or {@code null} if no {@link PropertyType} is
     *         associated with the {@link #getName()}
     */
    public PropertyType<?> getType() {
        return type;
    }

    /**
     * @param <T>
     *        the type of the value
     * @return the parsed value
     * @throws RuntimeException
     *         if the {@link #value} is not a valid value for the associated {@link PropertyType}
     */
    @SuppressWarnings("unchecked")
    public <T> T getValueAs() {
        if (value.isValid()) {
            return (T) value.getParsed();
        } else {
            throw new RuntimeException(value.getErrorMessage());
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    /**
     * @return {@code true} if the source value is {@code "unset"}; otherwise {@code false}
     */
    public boolean isUnset() {
        return value.isUnset();
    }

    /**
     * @return {@code true} if {@link #type} is {@code null} or {@link #value} is a valid value for the associated
     *         {@link PropertyType}; {@code false} otherwise
     */
    public boolean isValid() {
        return value.isValid();
    }

    @Override
    public String toString() {
        return new StringBuilder(name).append(" = ").append(value.getSource()).toString();
    }

}
