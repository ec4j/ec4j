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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.eclipse.ec4j.core.model.Property;
import org.eclipse.ec4j.core.model.propertytype.PropertyType;

/**
 * A mapping from property names to {@link PropertyType}s. Note that the mapping is case insensitive - i.e. all names
 * are internally transformed to lower case using {@link Locale#US} {@link Locale}.
 *
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public class PropertyTypeRegistry {

    /**
     * A {@link PropertyTypeRegistry} builder.
     */
    public static class Builder {
        private Map<String, PropertyType<?>> types = new LinkedHashMap<>();

        /**
         * @return a new {@link PropertyTypeRegistry}
         */
        public PropertyTypeRegistry build() {
            Map<String, PropertyType<?>> useTypes = Collections.unmodifiableMap(types);
            types = null;
            return new PropertyTypeRegistry(useTypes);
        }

        /**
         * Adds the {@link PropertyType}s from {@link PropertyType#standardTypes()}
         *
         * @return this {@link Builder}
         */
        public Builder defaults() {
            for (PropertyType<?> t : PropertyType.standardTypes()) {
                type(t);
            }
            return this;
        }

        /**
         * Adds a single {@link PropertyType}
         *
         * @param type
         *            the {@link PropertyType} to add
         * @return this {@link Builder}
         */
        public Builder type(PropertyType<?> type) {
            types.put(type.getName().toLowerCase(Locale.US), type);
            return this;
        }
    }

    private static final PropertyTypeRegistry DEFAULT = builder().defaults().build();

    /**
     * @return a new {@link Builder}
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * @return a registry containing default mappings. See {@link Builder#defaults()}
     */
    public static final PropertyTypeRegistry getDefault() {
        return DEFAULT;
    }

    private final Map<String, PropertyType<?>> types;

    /**
     * Use the {@link #builder()} to create new instances.
     *
     * @param types
     */
    PropertyTypeRegistry(Map<String, PropertyType<?>> types) {
        this.types = types;
    }

    /**
     * @param name
     *            the name of a {@link Property}
     * @return the {@link PropertyType} associated with the given {@code name} or {@code null} if there is no
     *         {@link PropertyType} associated with the given {@code name}
     */
    public PropertyType<?> getType(String name) {
        return types.get(name.toLowerCase(Locale.US));
    }

    /**
     * @return the collection of the registered types
     */
    public Collection<PropertyType<?>> getTypes() {
        return types.values();
    }

}
