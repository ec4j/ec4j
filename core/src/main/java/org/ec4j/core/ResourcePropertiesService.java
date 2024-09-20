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
package org.ec4j.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.ec4j.core.Cache.Caches;
import org.ec4j.core.model.Ec4jPath;
import org.ec4j.core.model.EditorConfig;
import org.ec4j.core.model.Property;
import org.ec4j.core.model.Section;

/**
 * A service able to query {@link Property}s applicable to a given {@link Resource}.
 * <p>
 * This is a typical entry point for the users of {@code ec4j.core}.
 * <p>
 * To create a new default {@link ResourcePropertiesService} use
 *
 * <pre>
 * ResourcePropertiesService propService = ResourcePropertiesService.default_();
 * </pre>
 *
 * Use {@link #builder()} if you need something special:
 *
 * <pre>
 * Cache myCache = ...;
 * EditorConfigLoader myLoader = ...;
 * ResourcePropertiesService propService = ResourcePropertiesService.builder()
 *         .cache(myCache)
 *         .loader(myLoader)
 *         .rootDirectory(ResourcePaths.ofPath(Paths.get("/my/dir")))
 *         .build();
 *
 * ResourceProperties props = propService.queryProperties(Resources.ofPath(Paths.get("/my/dir1/Class1.java")));
 * IndentStyleValue indentStyleValue = props.getValue(PropertyType.indent_style, IndentStyleValue.space);
 * char indentChar = indentStyleValue.getIndentChar();
 * // Now you can e.g. check that /my/dir1/Class1.java is indented using indentChar
 * </pre>
 *
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public class ResourcePropertiesService {

    public static class Builder {
        private Cache cache = Caches.none();
        private String configFileName = EditorConfigConstants.EDITORCONFIG;
        private List<EditorConfig> defaultEditorConfigs = new ArrayList<>();
        private boolean keepUnset = false;
        private EditorConfigLoader loader = EditorConfigLoader.default_();
        private Set<ResourcePath> rootDirectories = new LinkedHashSet<>();

        public ResourcePropertiesService build() {
            final Set<ResourcePath> useRootDirs = Collections.unmodifiableSet(rootDirectories);
            this.rootDirectories = null;
            final List<EditorConfig> useDefaultEditorConfigs = Collections.unmodifiableList(defaultEditorConfigs);
            this.defaultEditorConfigs = null;
            return new ResourcePropertiesService(configFileName, useRootDirs, useDefaultEditorConfigs, cache, loader,
                    keepUnset);
        }

        /**
         * Sets the cache implementation
         *
         * @param cache the Cache to set
         * @return this {@link Builder}
         */
        public Builder cache(Cache cache) {
            this.cache = cache;
            return this;
        }

        /**
         * Sets the file name to consider as an {@code .editorconfig} file.
         *
         * @param configFileName an alternative name {@code .editorconfig} file name
         * @return this {@link Builder}
         */
        public Builder configFileName(String configFileName) {
            this.configFileName = configFileName;
            return this;
        }

        /**
         * Adds a single {@link EditorConfig} model that contains default settings beyond the ones set in the
         * filesystem. This can be used to implement user or system defaults. Note several default files can be added.
         * In such a case the order of adding matters: the file added as the last one will least significant. In other
         * words, you should add user defaults before system defaults.
         *
         * @param defaultEditorConfig the default {@link EditorConfig} model to add
         * @return this {@link Builder}
         */
        public Builder defaultEditorConfig(EditorConfig defaultEditorConfig) {
            this.defaultEditorConfigs.add(defaultEditorConfig);
            return this;
        }

        /**
         * Adds multiple {@link EditorConfig} models that contain default settings beyond the ones set in the
         * filesystem. This can be used to implement user or system defaults. The order of the added files matters: the
         * file added as the last one will be least significant. In other words, you should add user defaults before
         * system defaults.
         *
         * @param defaultEditorConfigs a collection of default {@link EditorConfig} models to add
         * @return this {@link Builder}
         */
        public Builder defaultEditorConfigs(Collection<EditorConfig> defaultEditorConfigs) {
            this.defaultEditorConfigs.addAll(defaultEditorConfigs);
            return this;
        }

        /**
         * Adds multiple {@link EditorConfig} models that contain default settings beyond the ones set in the
         * filesystem. This can be used to implement user or system defaults. The order of the added files matters: the
         * file added as the last one will be least significant. In other words, you should add user defaults before
         * system defaults.
         *
         * @param defaultEditorConfigs default {@link EditorConfig} models to add
         * @return this {@link Builder}
         */
        public Builder defaultEditorConfigs(EditorConfig... defaultEditorConfigs) {
            for (EditorConfig defaultEditorConfig : defaultEditorConfigs) {
                this.defaultEditorConfigs.add(defaultEditorConfig);
            }
            return this;
        }

        /**
         * When set to {@code true} the {@link Property}s with the {@code unset} value will be kept in the
         * {@link ResourceProperties} returned by {@link ResourcePropertiesService#queryProperties(Resource)}; otherwise
         * the {@link Property}s with the {@code unset} value will be removed from the {@link ResourceProperties}
         * returned by {@link ResourcePropertiesService#queryProperties(Resource)}.
         *
         * @param keepUnset see above
         * @return this {@link Builder}
         */
        public Builder keepUnset(boolean keepUnset) {
            this.keepUnset = keepUnset;
            return this;
        }

        /**
         * Sets the {@link EditorConfigLoader}
         *
         * @param loader the {@link EditorConfigLoader} to set
         * @return this {@link Builder}
         */
        public Builder loader(EditorConfigLoader loader) {
            this.loader = loader;
            return this;
        }

        /**
         * Adds multiple root directories
         *
         * @param rootDirectories the root directories to add
         * @return this {@link Builder}
         */
        public Builder rootDirectories(Collection<ResourcePath> rootDirectories) {
            this.rootDirectories.addAll(rootDirectories);
            return this;
        }

        /**
         * Adds multiple root directories
         *
         * @param rootDirectories the root directories to add
         * @return this {@link Builder}
         */
        public Builder rootDirectories(ResourcePath... rootDirectories) {
            for (ResourcePath rootDirectory : rootDirectories) {
                this.rootDirectories.add(rootDirectory);
            }
            return this;
        }

        /**
         * Adds a single root directory
         *
         * @param rootDirectory the root directory to add
         * @return this {@link Builder}
         */
        public Builder rootDirectory(ResourcePath rootDirectory) {
            this.rootDirectories.add(rootDirectory);
            return this;
        }

    }

    /**
     * A pair of {@link EditorConfigLoader} and {@link ResourcePath} of the directory under which the underlyinf
     * {@code .editorconfig} file is located.
     */
    private static class DirEditorConfigPair {

        private final ResourcePath directory;
        private final EditorConfig editorConfig;

        private DirEditorConfigPair(ResourcePath directory, EditorConfig editorConfig) {
            super();
            this.directory = directory;
            this.editorConfig = editorConfig;
        }
    }

    /**
     * @return a new {@link ResourcePropertiesService} {@link Builder}.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * A shorthand for {@code ResourcePropertiesService.builder().build()}. Returns a new
     * {@link ResourcePropertiesService} with {@link Caches#none()} {@link Cache},
     * {@link EditorConfigConstants#EDITORCONFIG} {@code configFileName}, {@link EditorConfigLoader#default_()}
     * {@code loader}, empty {@code rootDirectories} and {@code keepUnset = false}.
     *
     * @return a new default {@link ResourcePropertiesService}
     */
    public static ResourcePropertiesService default_() {
        return builder().build();
    }

    private final Cache cache;
    private final String configFileName;
    private final List<EditorConfig> defaultEditorConfigs;
    private final boolean keepUnset;
    private final EditorConfigLoader loader;
    private final Set<ResourcePath> rootDirectories;

    ResourcePropertiesService(String configFileName, Set<ResourcePath> rootDirectories,
            List<EditorConfig> defaultEditorConfigs, Cache cache, EditorConfigLoader loader, boolean keepUnset) {
        super();
        this.rootDirectories = rootDirectories;
        this.defaultEditorConfigs = defaultEditorConfigs;
        this.loader = loader;
        this.configFileName = configFileName;
        this.cache = cache;
        this.keepUnset = keepUnset;
    }

    public Cache getCache() {
        return cache;
    }

    /**
     * @return the name of the EditorConfig file this {@link ResourcePropertiesService} looks for (the default value is
     *         {@code .editorconfig})
     */
    public String getConfigFileName() {
        return configFileName;
    }

    /**
     * @return a {@link List} of {@link EditorConfig} models that contain default settings beyond the ones set in the
     *         filesystem. These can be used to implement user or system defaults. The order of the files matters: the
     *         last file will least significant. In other words, user defaults would appear before system defaults in
     *         the returned list.
     */
    public List<EditorConfig> getDefaultEditorConfigs() {
        return defaultEditorConfigs;
    }

    /**
     * @return the {@link EditorConfigLoader} associated with this {@link ResourcePropertiesService}
     */
    public EditorConfigLoader getLoader() {
        return loader;
    }

    /**
     * @return the {@link Set} of root directories associated with this {@link ResourcePropertiesService}; never
     *         {@code null}
     */
    public Set<ResourcePath> getRootDirectories() {
        return rootDirectories;
    }

    /**
     * Walks up the resource tree from the given {@link Resource}, visits all {@code .editorconfig} files and filters
     * {@link Property}s applicable to the given {@link Resource}.
     * <p>
     * Note that the performance of this method is strongly influenced by the {@link Cache} implementation this
     * {@link ResourcePropertiesService} uses. If you do not specify any {@link Cache} via {@link Builder#cache(Cache)}
     * explicitly, {@link Caches#none()} is used that causes this method to parse each {@code .editorconfig} file every
     * time it is necessary.
     *
     * @param resource the resource to find the {@link Property}s for
     * @return a {@link ResourceProperties} that contains {@link Property}s applicable to the given {@link Resource}
     * @throws IOException on I/O problems during the reading from the {@code .editorconfig} files.
     */
    public ResourceProperties queryProperties(Resource resource) throws IOException {
        ResourceProperties.Builder result = ResourceProperties.builder();
        List<DirEditorConfigPair> editorConfigs = new ArrayList<>();
        boolean root = false;
        ResourcePath dir = resource.getParent();
        /* Walk up the tree storing the .editorconfig models to editorConfigs */
        while (dir != null && !root) {
            Resource configFile = dir.resolve(configFileName);
            if (configFile.exists()) {
                EditorConfig config = cache.get(configFile, loader);
                result.editorConfigFile(configFile.getPath());
                root = config.isRoot();
                editorConfigs.add(new DirEditorConfigPair(configFile.getParent(), config));
            }
            root |= rootDirectories.contains(dir);
            dir = dir.getParent();
        }

        /* Add the defaults in order */
        if (!defaultEditorConfigs.isEmpty()) {
            final ResourcePath lastDir = editorConfigs.isEmpty() //
                    ? resource.getParent() //
                    : editorConfigs.get(editorConfigs.size() - 1).directory;
            for (EditorConfig ec : defaultEditorConfigs) {
                editorConfigs.add(new DirEditorConfigPair(lastDir, ec));
            }
        }

        /* Now go back top down so that the duplicate properties defined closer to the given resource win */
        int i = editorConfigs.size() - 1;
        while (i >= 0) {
            final DirEditorConfigPair pair = editorConfigs.get(i--);
            final ResourcePath editorConfigDir = pair.directory;
            final EditorConfig config = pair.editorConfig;
            final Ec4jPath path = editorConfigDir.relativize(resource).getPath();
            List<Section> sections = config.getSections();
            for (Section section : sections) {
                if (section.match(path)) {
                    // Section matches the editor file, collect options of the section
                    if (keepUnset) {
                        result.properties(section.getProperties());
                    } else {
                        for (Property prop : section.getProperties().values()) {
                            if (prop.isUnset()) {
                                result.removeProperty(prop);
                            } else {
                                result.property(prop);
                            }
                        }
                    }
                }
            }
        }
        return result.build();
    }

}
