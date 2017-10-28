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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.ec4j.core.EditorConfigConstants;
import org.eclipse.ec4j.core.ResourcePaths.ResourcePath;
import org.eclipse.ec4j.core.model.optiontypes.OptionTypeRegistry;

/**
 * A immutable model of an {@code .editorconfig} file.
 *
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public class EditorConfig {

    /**
     * An {@link EditorConfig} builder.
     */
    public static class Builder {
        final OptionTypeRegistry registry;

        ResourcePath resourcePath;
        Boolean root;
        List<Section> sections;
        String version = EditorConfigConstants.VERSION;

        public Builder(OptionTypeRegistry registry) {
            super();
            this.sections = new ArrayList<>();
            this.registry = registry;
        }

        /**
         * @return a new {@link EditorConfig}
         */
        public EditorConfig build() {
            List<Section> useSections = sections;
            sections = null;
            return new EditorConfig(root, version, resourcePath, Collections.unmodifiableList(useSections));
        }

        /**
         * @return a new {@link Section.Builder}
         */
        public Section.Builder openSection() {
            return new Section.Builder(this);
        }

        /**
         * Sets the {@link #resourcePath}.
         *
         * @param resourcePath
         *            the directory where the underlying {@code .editorconfig} file resides
         * @return this {@link Builder}
         */
        public Builder resourcePath(ResourcePath resourcePath) {
            this.resourcePath = resourcePath;
            return this;
        }

        /**
         * Sets the {@link #root} field.
         *
         * @param root
         *            if {@link Boolean#TRUE} this is the top-most {@code .editorconfig} file and the a search for
         *            {@code .editorconfig} files should stop at the present directory level. If {@link Boolean#FALSE},
         *            this is not the top-most {@code .editorconfig} file and underlying source tree should be searched
         *            for further {@code .editorconfig} files in the ancestor directories. A {@code null} {@link #root}
         *            means that the `root` property was not available in the underlying {@code .editorconfig} file and
         *            that the default {@code root = false}
         * @return this {@link Builder}
         */
        public Builder root(Boolean root) {
            this.root = root;
            return this;
        }

        /**
         * Adds a single {@link Section} to {@link #sections}.
         *
         * @param section
         *            the {@link Section} to add
         * @return this {@link Builder}
         */
        public Builder section(Section section) {
            this.sections.add(section);
            return this;
        }

        /**
         * Adds multiple Sections to {@link #sections}
         *
         * @param sections
         *            the {@link Section}s to add
         * @return this {@link Builder}
         */
        public Builder sections(Collection<Section> sections) {
            this.sections.addAll(sections);
            return this;
        }

        /**
         * Adds multiple Sections to {@link #sections}
         *
         * @param sections
         *            the {@link Section}s to add
         * @return this {@link Builder}
         */
        public Builder sections(Section... sections) {
            for (Section section : sections) {
                this.sections.add(section);
            }
            return this;
        }

        /**
         * Sets the {@link #version} field.
         *
         * @param version
         *            the version to set
         * @return this {@link Builder}
         */
        public Builder version(String version) {
            this.version = version;
            return this;
        }
    }

    /**
     * @param registry
     *            the {@link OptionTypeRegistry} to use when adding {@link Option}s.
     *
     * @return a new {@link EditorConfig.Builder}
     */
    public static Builder builder(OptionTypeRegistry registry) {
        return new Builder(registry);
    }

    private final ResourcePath resourcePath;

    /**
     * Citing from <a href="http://editorconfig.org/">http://editorconfig.org/</a> : "A search for .editorconfig files
     * will stop if the root filepath is reached or an EditorConfig file with root=true is found."
     * <p>
     * Note that the type of {@link #root} is {@link Boolean} rather than {@code boolean}. A {@code null} {@link #root}
     * means that the `root` property was not available in the file.
     */
    private final Boolean root;

    private final List<Section> sections;

    private final String version;

    /**
     * You look for {@link #builder(OptionTypeRegistry)} if you wonder why this constructor this package private.
     *
     * @param root
     * @param version
     * @param resourcePath
     * @param sections
     */
    EditorConfig(Boolean root, String version, ResourcePath resourcePath, List<Section> sections) {
        super();
        this.root = root;
        this.version = version;
        this.resourcePath = resourcePath;
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
        if (resourcePath == null) {
            if (other.resourcePath != null)
                return false;
        } else if (!resourcePath.equals(other.resourcePath))
            return false;
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
     * @return The directory where the underlying {@code .editorconfig} file resides
     */
    public ResourcePath getResourcePath() {
        return resourcePath;
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
    public String getVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((resourcePath == null) ? 0 : resourcePath.hashCode());
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
