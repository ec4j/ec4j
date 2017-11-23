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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ec4j.core.model.PropertyType.IndentStyleValue;

/**
 * A section in an {@code .editorconfig} file. A section consists of a {@link Glob} and a collection of
 * {@link Property}s.
 *
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public class Section extends Adaptable {

    /**
     * A {@link Section} builder.
     */
    public static class Builder extends Adaptable.Builder<Builder> {

        private Glob glob;
        private boolean parentAware = false;

        final EditorConfig.Builder parentBuilder;
        private Map<String, Property.Builder> properties = new LinkedHashMap<>();

        /**
         * Constructs a new {@link Builder}
         *
         * @param parentBuilder
         *            can be {@code null} if you do not to call {@link #closeSection()}
         */
        public Builder(org.ec4j.core.model.EditorConfig.Builder parentBuilder) {
            super();
            this.parentBuilder = parentBuilder;
        }

        /**
         * Applies the defaults required by the core-tests, if necessary.
         *
         * @return this {@link Builder}
         */
        public Builder applyDefaults() {
            final Version version = parentBuilder.version;
            Property.Builder indentStyle = properties.get(PropertyType.indent_style.getName());
            Property.Builder indentSize = properties.get(PropertyType.indent_size.getName());
            Property.Builder tabWidth = properties.get(PropertyType.tab_width.getName());

            // Set indent_size to "tab" if indent_size is unspecified and
            // indent_style is set to "tab".
            if (indentStyle != null && IndentStyleValue.tab.name().equals(indentStyle.value.getSource())
                    && indentSize == null && version.compareTo(Version._0_10_0) >= 0) {
                final PropertyType<?> type = PropertyType.indent_size;
                final String value = "tab";
                indentSize = openProperty().type(type).value(type.parse(value));
                this.property(indentSize);
            }

            // Set tab_width to indent_size if indent_size is specified and
            // tab_width is unspecified
            if (indentSize != null && !"tab".equals(indentSize.value.getSource()) && tabWidth == null) {
                final PropertyType<?> type = PropertyType.tab_width;
                final String value = indentSize.value.getSource();
                tabWidth = openProperty().type(type).value(type.parse(value));
                this.property(tabWidth);
            }

            // Set indent_size to tab_width if indent_size is "tab"
            if (indentSize != null && "tab".equals(indentSize.value.getSource()) && tabWidth != null) {
                final PropertyType<?> type = PropertyType.indent_size;
                final String value = tabWidth.value.getSource();
                indentSize = openProperty().type(type).value(type.parse(value));
                this.property(indentSize);
            }
            return this;
        }

        /**
         * @return a new {@link Section}
         */
        public Section build() {
            final Map<String, Property> useProps = new LinkedHashMap<>(properties.size());
            /*
             * This is a controled leak of the adapters list that we are going to ammend after the properties were
             * actually built and their respective adapter lists sealed. This leak should be harmless as the Property
             * objects should not be visible to the outside world before we return from the present method.
             */
            List<List<Object>> propAdapters = new ArrayList<>();
            for (Property.Builder propBuilder : properties.values()) {
                if (parentAware) {
                    propAdapters.add(propBuilder.adapters);
                }
                Property prop = propBuilder.build();
                useProps.put(prop.getName(), prop);
            }
            this.properties = null;
            final Section result = new Section(sealAdapters(), glob, Collections.unmodifiableMap(useProps));
            for (List<Object> adapters : propAdapters) {
                adapters.add(result);
            }
            return result;
        }

        /**
         * Adds this {@link Builder} to {@link #parentBuilder} using
         * {@link EditorConfig.Builder#section(Section.Builder)} and returns the parent {@link EditorConfig.Builder}.
         *
         * @return the parent {@link EditorConfig.Builder}
         */
        public EditorConfig.Builder closeSection() {
            if (glob == null) {
                /* this is the first glob-less section */
                Property.Builder rootProp = properties.remove(PropertyType.root.getName());
                if (rootProp != null) {
                    parentBuilder.root(rootProp.value.getSource().equalsIgnoreCase(Boolean.TRUE.toString()));
                }
            } else {
                parentBuilder.section(this);
            }
            return parentBuilder;
        }

        /**
         * Sets the {@link Glob}
         *
         * @param glob
         *            the {@link Glob} to set
         * @return this {@link Builder}
         */
        public Builder glob(Glob glob) {
            this.glob = glob;
            return this;
        }

        /**
         * @return a new {@link Property.Builder}
         */
        public Property.Builder openProperty() {
            return new Property.Builder(this);
        }

        /**
         * @param parentAware
         *            if {@code true} the {@link Section#getProperties()} of the resulting {@link Section} will have the
         *            Section set in their adapters (which can be used as the link to the parent {@link Section});
         *            othwise the seczion will not be added to {@link Property}'s adapters
         *
         * @return this {@link Builder}
         */
        public Builder parentAware(boolean parentAware) {
            this.parentAware = parentAware;
            return this;
        }

        /**
         * Adds multiple {@link Property.Builder}s to {@link #properties}.
         *
         * @param properties
         *            the {@link Property.Builder}s to add
         * @return this {@link Builder}
         */
        public Builder properties(Collection<Property.Builder> properties) {
            for (Property.Builder property : properties) {
                this.properties.put(property.name, property);
            }
            return this;
        }

        /**
         * Adds multiple {@link Property.Builder}s to {@link #properties}.
         *
         * @param properties
         *            the {@link Property.Builder}s to add
         * @return this {@link Builder}
         */
        public Builder properties(Property.Builder... properties) {
            for (Property.Builder property : properties) {
                this.properties.put(property.name, property);
            }
            return this;
        }

        /**
         * Adds the given {@link Property.Builder} to {@link #properties}.
         *
         * @param property
         *            the {@link Property.Builder} to add
         * @return this {@link Builder}
         */
        public Builder property(Property.Builder property) {
            this.properties.put(property.name, property);
            return this;
        }

    }

    /**
     * @return a new {@link Builder} with no parent builder set
     */
    public static Builder builder() {
        return new Builder(null);
    }

    private final Glob glob;

    private final Map<String, Property> properties;

    /**
     * Use the {@link Builder} to create new instances.
     *
     * @param adapters
     * @param glob
     * @param properties
     */
    Section(List<Object> adapters, Glob glob, Map<String, Property> properties) {
        super(adapters);
        this.glob = glob;
        this.properties = properties;
    }

    public void appendTo(StringBuilder s) {
        // glob
        if (!glob.isEmpty()) {
            s.append('[');
            s.append(glob.toString());
            s.append("]\n");
        }
        // properties
        int i = 0;
        for (Property property : properties.values()) {
            if (i > 0) {
                s.append("\n");
            }
            s.append(property.toString());
            i++;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Section other = (Section) obj;
        if (glob == null) {
            if (other.glob != null)
                return false;
        } else if (!glob.equals(other.glob))
            return false;
        if (properties == null) {
            if (other.properties != null)
                return false;
        } else if (!properties.equals(other.properties))
            return false;
        return true;
    }

    public Glob getGlob() {
        return glob;
    }

    public Map<String, Property> getProperties() {
        return properties;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((glob == null) ? 0 : glob.hashCode());
        result = prime * result + ((properties == null) ? 0 : properties.hashCode());
        return result;
    }

    public boolean match(String filePath) {
        /* null glob matches all */
        return glob == null ? true : glob.match(filePath);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        appendTo(s);
        return s.toString();
    }
}
