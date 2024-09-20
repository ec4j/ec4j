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
import java.util.List;

/**
 * A immutable model of an {@code .editorconfig} file.
 *
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public class EditorConfig extends Adaptable.DefaultAdaptable {

    /**
     * An {@link EditorConfig} builder.
     */
    public static class Builder extends Adaptable.DefaultAdaptable.Builder<Builder> {

        boolean parentAware;
        Boolean root;
        List<Section.Builder> sections;
        Version version = Version.CURRENT;

        public Builder() {
            super();
            this.sections = new ArrayList<>();
        }

        /**
         * @return a new {@link EditorConfig}
         */
        public EditorConfig build() {
            List<Section> useSections = new ArrayList<>(sections.size());
            /*
             * This is a controled leak of the adapters list that we are going to ammend after the sections were
             * actually built and their respective adapter lists sealed. This leak should be harmless as the Section
             * objects should not be visible to the outside world before we return from the present method.
             */
            List<List<Object>> propAdapters = new ArrayList<>();
            for (Section.Builder sectionBuilder : sections) {
                if (parentAware) {
                    propAdapters.add(sectionBuilder.adapters);
                }
                Section section = sectionBuilder.build();
                useSections.add(section);
            }
            sections = null;
            final EditorConfig result = new EditorConfig(sealAdapters(), root, version,
                    Collections.unmodifiableList(useSections));
            for (List<Object> adapters : propAdapters) {
                adapters.add(result);
            }
            return result;
        }

        /**
         * @return a new {@link Section.Builder} and pass the current {@link #parentAware} value to it
         */
        public Section.Builder openSection() {
            return new Section.Builder(this).parentAware(parentAware);
        }

        /**
         * @param parentAware
         *        if {@code true} the {@link Section#getProperties()} of the resulting {@link Section} will have the
         *        Section set in their adapters (which can be used as the link to the parent {@link Section});
         *        othwise the section will not be added to {@link Property}'s adapters
         *
         * @return this {@link Builder}
         */
        public Builder parentAware(boolean parentAware) {
            this.parentAware = parentAware;
            return this;
        }

        /**
         * Sets the {@link #root} field.
         *
         * @param root
         *        if {@link Boolean#TRUE} this is the top-most {@code .editorconfig} file and the a search for
         *        {@code .editorconfig} files should stop at the present directory level. If {@link Boolean#FALSE},
         *        this is not the top-most {@code .editorconfig} file and underlying source tree should be searched
         *        for further {@code .editorconfig} files in the ancestor directories. A {@code null} {@link #root}
         *        means that the `root` property was not available in the underlying {@code .editorconfig} file and
         *        that the default {@code root = false}
         * @return this {@link Builder}
         */
        public Builder root(Boolean root) {
            this.root = root;
            return this;
        }

        /**
         * Adds a single {@link Section.Builder} to {@link #sections}.
         *
         * @param section
         *        the {@link Section.Builder} to add
         * @return this {@link Builder}
         */
        public Builder section(Section.Builder section) {
            this.sections.add(section);
            return this;
        }

        /**
         * Adds multiple {@link Section.Builder}s to {@link #sections}
         *
         * @param sections
         *        the {@link Section.Builder}s to add
         * @return this {@link Builder}
         */
        public Builder sections(Collection<Section.Builder> sections) {
            this.sections.addAll(sections);
            return this;
        }

        /**
         * Adds multiple {@link Section.Builder}s to {@link #sections}
         *
         * @param sections
         *        the {@link Section.Builder}s to add
         * @return this {@link Builder}
         */
        public Builder sections(Section.Builder... sections) {
            for (Section.Builder section : sections) {
                this.sections.add(section);
            }
            return this;
        }

        /**
         * Sets the {@link #version} field.
         *
         * @param version
         *        the version to set
         * @return this {@link Builder}
         */
        public Builder version(Version version) {
            this.version = version;
            return this;
        }
    }

    /**
     * @return a new {@link EditorConfig.Builder}
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Citing from <a href="http://editorconfig.org/">http://editorconfig.org/</a> : "A search for .editorconfig files
     * will stop if the root filepath is reached or an EditorConfig file with root=true is found."
     * <p>
     * Note that the type of {@link #root} is {@link Boolean} rather than {@code boolean}. A {@code null} {@link #root}
     * means that the `root` property was not available in the file.
     */
    private final Boolean root;

    private final List<Section> sections;

    private final Version version;

    /**
     * You look for {@link #builder()} if you wonder why this constructor is package visible.
     *
     * @param adapters
     * @param root
     * @param version
     * @param sections
     */
    EditorConfig(List<Object> adapters, Boolean root, Version version, List<Section> sections) {
        super(adapters);
        this.root = root;
        this.version = version;
        this.sections = sections;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EditorConfig other = (EditorConfig) obj;
        if (root == null) {
            if (other.root != null)
                return false;
        } else if (!root.equals(other.root))
            return false;
        if (sections == null) {
            if (other.sections != null)
                return false;
        } else if (!sections.equals(other.sections))
            return false;
        if (version == null) {
            if (other.version != null)
                return false;
        } else if (!version.equals(other.version))
            return false;
        return true;
    }

    /**
     * @return the {@link List} of {@link Section}s
     */
    public List<Section> getSections() {
        return sections;
    }

    /**
     * @return The version of EditorConfig specification, the current {@link EditorConfig} model is compliant with
     */
    public Version getVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((root == null) ? 0 : root.hashCode());
        result = prime * result + ((sections == null) ? 0 : sections.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
        return result;
    }

    /**
     * @return {@code true} if the underlying {@code .editorconfig} file had the {@code root} property specified;
     *         {@code false} otherwise. A shorthand for {@code getRoot() != null}
     *
     */
    public boolean hasRootProperty() {
        return root != null;
    }

    /**
     * @return {@code true} if a search for {@code .editorconfig} files should stop at the present directory level.
     */
    public boolean isRoot() {
        return Boolean.TRUE.equals(root);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        if (root != null) {
            s.append("root = ");
            s.append(root);
            s.append("\n\n");
        }
        int i = 0;
        for (Section section : sections) {
            if (i > 0) {
                s.append("\n\n");
            }
            section.appendTo(s);
            i++;
        }
        return s.toString();
    }
}
