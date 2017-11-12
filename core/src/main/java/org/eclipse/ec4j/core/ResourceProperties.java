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
package org.eclipse.ec4j.core;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.ec4j.core.model.Property;
import org.eclipse.ec4j.core.model.PropertyType;
import org.eclipse.ec4j.core.parser.InvalidPropertyValueException;

/**
 * A collection of {@link Property}s applicable to a {@link Resource} as returned by
 * {@link ResourcePropertiesService#queryProperties(org.eclipse.ec4j.core.Resource.Resources.Resource)}.
 * <p>
 * This is basically just a wrapper around a {@link Map} of {@link Property}s that offers utility methods for getting
 * entries from the underlying {@link Map} not only by name but also by {@link PropertyType} in a type safe manner.
 *
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public class ResourceProperties {

    /**
     * A {@link ResourceProperties} builder.
     */
    public static class Builder {
        private final Map<String, Property> properties = new LinkedHashMap<>();

        public ResourceProperties build() {
            return new ResourceProperties(properties);
        }

        /**
         * Adds multiple properties.
         *
         * @param properties
         *            the properties to add
         * @return this {@link Builder}
         */
        public Builder properties(Collection<Property> properties) {
            for (Property property : properties) {
                this.properties.put(property.getName(), property);
            }
            return this;
        }

        /**
         * Adds multiple properties.
         *
         * @param properties
         *            the properties to add
         * @return this {@link Builder}
         */
        public Builder properties(Map<String, Property> properties) {
            this.properties.putAll(properties);
            return this;
        }

        /**
         * Adds multiple properties.
         *
         * @param properties
         *            the properties to add
         * @return this {@link Builder}
         */
        public Builder properties(Property... properties) {
            for (Property property : properties) {
                this.properties.put(property.getName(), property);
            }
            return this;
        }

        /**
         * Adds a single property.
         *
         * @param properties
         *            the properties to add
         * @return this {@link Builder}
         */
        public Builder property(Property property) {
            this.properties.put(property.getName(), property);
            return this;
        }
    }

    /**
     * @return a new {@link Builder}
     */
    public static Builder builder() {
        return new Builder();
    }

    private final Map<String, Property> properties;

    ResourceProperties(Map<String, Property> properties) {
        super();
        this.properties = properties;
    }

    /**
     * @return the underlying {@link Map} of {@link Property}s
     */
    public Map<String, Property> getProperties() {
        return properties;
    }

    /**
     * Gets a {@link Property} from {@link #properties} by the {@code name} of the given {@link PropertyType} and
     * returns {@link Property#getValueAs()} or returns {@code defaultValue} if no such {@code name} is available in
     * {@link #properties}.
     *
     * @param type
     *            the {@link PropertyType} whose {@code name} will be used to get the value fron {@link #properties}
     * @param defaultValue
     *            the value to return if the {@code name} is not available in {@link #properties}
     * @param throwInvalid
     *            if {@code true} and the underlying {@link Property} value is invalid, an
     *            {@link InvalidPropertyValueException} will be thrown. Otherwise, in the same case the invalid value
     *            will be ignored and {@code defaultValue} will be returned instead
     * @return the value associated with the given {@code type}'s {@code name} or the {@code defaultValue} if the
     *         {@code name} is not available in {@link #properties}
     * @throws InvalidPropertyValueException
     *             if {@code throwInvalid} is {@code true} and the underlying {@link Property} value is invalid.
     */
    public <T> T getValue(PropertyType<T> type, T defaultValue, boolean throwInvalid) {
        return getValue(type.getName(), defaultValue, throwInvalid);
    }

    /**
     * Gets a {@link Property} from {@link #properties} by the given {@code name} and returns
     * {@link Property#getValueAs()} or returns {@code defaultValue} if no such {@code name} is available in
     * {@link #properties}.
     *
     * @param name
     *            the name to use to get the value fron {@link #properties}
     * @param defaultValue
     *            the value to return if the {@code name} is not available in {@link #properties}
     * @param throwInvalid
     *            if {@code true} and the underlying {@link Property} value is invalid, an
     *            {@link InvalidPropertyValueException} will be thrown. Otherwise, in the same case the invalid value
     *            will be ignored and {@code defaultValue} will be returned instead
     * @return the value associated with the given {@code name} or the {@code defaultValue} if the {@code name} is not
     *         available in {@link #properties}
     * @throws InvalidPropertyValueException
     *             if {@code throwInvalid} is {@code true} and the underlying {@link Property} value is invalid.
     */
    @SuppressWarnings("unchecked")
    public <T> T getValue(String name, T defaultValue, boolean throwInvalid) {
        Property prop = properties.get(name);
        if (prop == null) {
            return defaultValue;
        } else if (throwInvalid || prop.isValid()) {
            return (T) prop.getValueAs();
        } else {
            /* !throwInvalid && !prop.isValid() */
            return defaultValue;
        }
    }
}
