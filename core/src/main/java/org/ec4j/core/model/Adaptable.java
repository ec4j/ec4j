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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Allows extending some model class without having to bother with Java inheritance and generics.
 *
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public interface Adaptable {
    /**
     * Returns this adapted as an object of the given {@code type} or {@code null} if adapting to a given
     * {@code type} is not possible.
     * <p>
     * Can be used to give callers an access to some underlying representation of this, such as {@link Path}
     * for a {@link Path}-based implementation of {@link Ec4jPath}.
     * <p>
     * The set of supported types is implementation specific.
     *
     * @param type
     *        the type of the adapter to lookup
     * @param <T>
     *        the type to get an adapter for
     * @return the adapter of the given type or {@code null} if an adapter for the given type is not available
     */
    public <T> T getAdapter(Class<T> type);

    abstract class DefaultAdaptable implements Adaptable {

        /**
         * An {@link Adaptable} builder.
         *
         * @param <B>
         *        the type of the extending builder
         */
        public abstract static class Builder<B extends Builder<B>> {
            protected List<Object> adapters = null;

            /**
             * Adds a single {@code adapter}
             *
             * @param adapter
             *        the adapter to add
             * @return this {@link Builder}
             */
            @SuppressWarnings("unchecked")
            public B adapter(Object adapter) {
                if (this.adapters == null) {
                    this.adapters = new ArrayList<>();
                }
                this.adapters.add(adapter);
                return (B) this;
            }

            /**
             * Adds multiple {@code adapter}s
             *
             * @param adapters
             *        the adapters to add
             * @return this {@link Builder}
             */
            @SuppressWarnings("unchecked")
            public B adapters(Collection<Object> adapters) {
                if (this.adapters == null) {
                    this.adapters = new ArrayList<>(adapters);
                } else {
                    this.adapters.addAll(adapters);
                }
                return (B) this;
            }

            /**
             * Adds multiple {@code adapter}s
             *
             * @param adapters
             *        the adapters to add
             * @return this {@link Builder}
             */
            @SuppressWarnings("unchecked")
            public B adapters(Object... adapters) {
                if (this.adapters == null) {
                    this.adapters = new ArrayList<>(adapters.length);
                }
                for (Object adapter : adapters) {
                    this.adapters.add(adapter);
                }
                return (B) this;
            }

            /**
             * Returns an immutable {@link List} of adapters and invalidates this {@link Builder}.
             *
             * @return an immutable {@link List} of adapters
             */
            protected List<Object> sealAdapters() {
                if (adapters == null) {
                    return Collections.emptyList();
                } else {
                    List<Object> useAdapters = this.adapters;
                    this.adapters = null;
                    return Collections.unmodifiableList(useAdapters);
                }
            }

        }

        private final List<Object> adapters;

        DefaultAdaptable(List<Object> adapters) {
            super();
            this.adapters = adapters;
        }

        /**
         * @param type
         *        the type of the adapter to lookup
         * @param <T>
         *        the type to get an adapter for
         * @return the adapter of the given type or {@code null} if an adapter for the given type is not available
         */
        @SuppressWarnings("unchecked")
        public <T> T getAdapter(Class<T> type) {
            /* Try the exact match first */
            for (Object o : adapters) {
                if (type == o.getClass()) {
                    return (T) o;
                }
            }
            /* Otherwise try to return a subclass */
            for (Object o : adapters) {
                if (type.isAssignableFrom(o.getClass())) {
                    return (T) o;
                }
            }
            return null;
        }
    }
}
