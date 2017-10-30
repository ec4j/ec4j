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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.opensagres.ec4j.core.model.propertytype.PropertyName;
import fr.opensagres.ec4j.core.model.propertytype.PropertyType;
import fr.opensagres.ec4j.core.model.propertytype.PropertyTypeRegistry;

/**
 * A section in an {@code .editorconfig} file. A section consists of a {@link Glob} and a collection of {@link Property}s.
 *
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public class Section {

    /**
     * A {@link Section} builder.
     */
    public static class Builder {

        private Glob glob;

        private Map<String, Property> properties = new LinkedHashMap<>();
        final EditorConfig.Builder parentBuilder;

        /**
         * Constructs a new {@link Builder}
         *
         * @param parentBuilder
         *            can be {@code null} if you do not to call {@link #closeSection()}
         */
        public Builder(fr.opensagres.ec4j.core.model.EditorConfig.Builder parentBuilder) {
            super();
            this.parentBuilder = parentBuilder;
        }

        /**
         * @return a new {@link Section}
         */
        public Section build() {
            preprocessProperties();
            return new Section(glob, Collections.unmodifiableList(new ArrayList<Property>(properties.values())));
        }

        /**
         * Creates a new instance of {@link Section} using {@link #build()}, adds the instance to {@link #parentBuilder}
         * using {@link EditorConfig.Builder#section(Section)} and returns the parent {@link EditorConfig.Builder}.
         *
         * @return the parent {@link EditorConfig.Builder}
         */
        public EditorConfig.Builder closeSection() {
            if (glob == null) {
                /* this is the first glob-less section */
                Property rootProp = properties.remove(PropertyName.root.name());
                if (rootProp != null) {
                    parentBuilder.root(rootProp.getSourceValue().equalsIgnoreCase(Boolean.TRUE.toString()));
                }
            } else {
                parentBuilder.section(build());
            }
            return parentBuilder;
        }

        /**
         * @return a new {@link Property.Builder}
         */
        public Property.Builder openProperty() {
            return new Property.Builder(this);
        }

        /**
         * Adds the given {@link Property} to {@link #properties}.
         *
         * @param property the {@link Property} to add
         * @return this {@link Builder}
         */
        public Builder property(Property property) {
            this.properties.put(property.getName(), property);
            return this;
        }

        /**
         * Adds multiple {@link Property}s to {@link #properties}.
         *
         * @param properties the {@link Property}s to add
         * @return this {@link Builder}
         */
        public Builder properties(Collection<Property> properties) {
            for (Property property : properties) {
                this.properties.put(property.getName(), property);
            }
            return this;
        }

        /**
         * Adds multiple {@link Property}s to {@link #properties}.
         *
         * @param properties the {@link Property}s to add
         * @return this {@link Builder}
         */
        public Builder properties(Property... properties) {
            for (Property property : properties) {
                this.properties.put(property.getName(), property);
            }
            return this;
        }

        /**
         * Creates a new {@link Glob} using the given {@code pattern} and assigns it to #
         * @param pattern
         * @return
         */
        public Builder pattern(String pattern) {
            this.glob = new Glob(parentBuilder.resourcePath.getPath(), pattern);
            return this;
        }

        private void preprocessProperties() {
            Version version = parentBuilder.version;
            Property indentStyle = null;
            Property indentSize = null;
            Property tabWidth = null;
            for (Property property : properties.values()) {
                PropertyName name = PropertyName.get(property.getName());
                // Lowercase property value for certain properties
                // get indent_style, indent_size, tab_width property
                switch (name) {
                case indent_style:
                    indentStyle = property;
                    break;
                case indent_size:
                    indentSize = property;
                    break;
                case tab_width:
                    tabWidth = property;
                    break;
                default:
                    break;
                }
            }

            // Set indent_size to "tab" if indent_size is unspecified and
            // indent_style is set to "tab".
            if (indentStyle != null && "tab".equals(indentStyle.getSourceValue()) && indentSize == null
                    && version.compareTo(Version._0_10_0) >= 0) {
                final String name = PropertyName.indent_size.name();
                final PropertyType<?> type = parentBuilder.registry.getType(name);
                final String value = "tab";
                indentSize = new Property(type, name, value, type.parse(value), true);
                this.property(indentSize);
            }

            // Set tab_width to indent_size if indent_size is specified and
            // tab_width is unspecified
            if (indentSize != null && !"tab".equals(indentSize.getSourceValue()) && tabWidth == null) {
                final String name = PropertyName.tab_width.name();
                final PropertyType<?> type = parentBuilder.registry.getType(name);
                final String value = indentSize.getSourceValue();
                tabWidth = new Property(type, name, value, type.parse(value), true);
                this.property(tabWidth);
            }

            // Set indent_size to tab_width if indent_size is "tab"
            if (indentSize != null && "tab".equals(indentSize.getSourceValue()) && tabWidth != null) {
                final String name = PropertyName.indent_size.name();
                final PropertyType<?> type = parentBuilder.registry.getType(name);
                final String value = tabWidth.getSourceValue();
                indentSize = new Property(type, name, value, type.parse(value), true);
                this.property(indentSize);
            }
        }

    }

    private final Glob glob;

    private final List<Property> properties;

    /**
     * You look for {@link #builder(PropertyTypeRegistry)} if you wonder why this constructor this package private.
     *
     * @param glob
     * @param properties
     */
    Section(Glob glob, List<Property> properties) {
        super();
        this.glob = glob;
        this.properties = properties;
    }
    public void appendTo(StringBuilder s) {
        // patterns
        if (!glob.isEmpty()) {
            s.append('[');
            s.append(glob.toString());
            s.append("]\n");
        }
        // properties
        int i = 0;
        for (Property property : properties) {
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

    public List<Property> getProperties() {
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
