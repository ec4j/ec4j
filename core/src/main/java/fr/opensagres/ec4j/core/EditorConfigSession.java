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
package fr.opensagres.ec4j.core;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.opensagres.ec4j.core.Caches.Cache;
import fr.opensagres.ec4j.core.ResourcePaths.ResourcePath;
import fr.opensagres.ec4j.core.Resources.Resource;
import fr.opensagres.ec4j.core.model.EditorConfig;
import fr.opensagres.ec4j.core.model.Property;
import fr.opensagres.ec4j.core.model.Section;

/**
 * A session that keeps a {@link Cache} and {@link EditorConfigLoader} to be able to query {@link Property}s applicable to
 * a {@link Resource}.
 * <p>
 * This is a typical entry point for the users of {@code ec4j.core}.
 * <p>
 * To create a new default {@link EditorConfigSession} use
 *
 * <pre>
 * EditorConfigSession mySession = EditorConfigSession.default_();
 * </pre>
 *
 * Use {@link #builder()} if you need something special:
 *
 * <pre>
 * Cache myCache = ...;
 * EditorConfigLoader myLoader = ...;
 * EditorConfigSession mySession = EditorConfigSession.builder()
 *         .cache(myCache)
 *         .configFileName("my-custom-editorconfig.txt")
 *         .loader(myLoader)
 *         .rootDirectory(ResourcePaths.ofPath(Paths.get("/my/dir1")))
 *         .rootDirectory(ResourcePaths.ofPath(Paths.get("/my/dir2")))
 *         .build();
 * Collection<Property> opts1 = mySession.queryProperties(Resources.ofPath(Paths.get("/my/dir1/Class1.java")));
 * Collection<Property> opts2 = mySession.queryProperties(Resources.ofPath(Paths.get("/my/dir2/Class2.java")));
 * </pre>
 *
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public class EditorConfigSession {

    public static class Builder {
        private Cache cache = Caches.none();
        private String configFileName = EditorConfigConstants.EDITORCONFIG;
        private EditorConfigLoader loader = EditorConfigLoader.getDefault();
        private Set<ResourcePath> rootDirectories = new LinkedHashSet<>();

        public EditorConfigSession build() {
            return new EditorConfigSession(configFileName, Collections.unmodifiableSet(rootDirectories), cache, loader);
        }

        public Builder cache(Cache cache) {
            this.cache = cache;
            return this;
        }

        public Builder configFileName(String configFileName) {
            this.configFileName = configFileName;
            return this;
        }

        public Builder loader(EditorConfigLoader loader) {
            this.loader = loader;
            return this;
        }

        public Builder rootDirectories(Collection<ResourcePath> rootDirectories) {
            this.rootDirectories.addAll(rootDirectories);
            return this;
        }

        public Builder rootDirectories(ResourcePath... rootDirectories) {
            for (ResourcePath rootDirectory : rootDirectories) {
                this.rootDirectories.add(rootDirectory);
            }
            return this;
        }

        public Builder rootDirectory(ResourcePath rootDirectory) {
            this.rootDirectories.add(rootDirectory);
            return this;
        }
    }

    /**
     * @return a new {@link EditorConfigSession} {@link Builder}.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * A shorthand for {@code EditorConfigSession.builder().build()}. Returns a new {@link EditorConfigSession} with
     * {@link Caches#none()} {@link Cache}, {@link EditorConfigConstants#EDITORCONFIG} {@code configFileName},
     * {@link EditorConfigLoader#getDefault()} {@code loader} and empty {@code rootDirectories}.
     *
     * @return a new default {@link EditorConfigSession}
     */
    public static EditorConfigSession default_() {
        return builder().build();
    }

    private final Cache cache;
    private final String configFileName;
    private final EditorConfigLoader loader;
    private final Set<ResourcePath> rootDirectories;

    EditorConfigSession(String configFileName, Set<ResourcePath> rootDirectories, Cache cache,
            EditorConfigLoader loader) {
        super();
        this.rootDirectories = rootDirectories;
        this.loader = loader;
        this.configFileName = configFileName;
        this.cache = cache;
    }

    public Cache getCache() {
        return cache;
    }

    /**
     * @return the name of the EditorConfig file this session looks for (the default value is {@code .editorconfig})
     */
    public String getConfigFileName() {
        return configFileName;
    }

    /**
     * @return the {@link EditorConfigLoader} associated with this {@link EditorConfigSession}
     */
    public EditorConfigLoader getLoader() {
        return loader;
    }

    /**
     * @return the {@link Set} of root directories associated with this {@link EditorConfigSession}; never {@code null}
     */
    public Set<ResourcePath> getRootDirectories() {
        return rootDirectories;
    }

    /**
     * Queries {@link Property}s applicable to a {@link Resource}.
     *
     * @param resource
     *            the resource to query the {@link Property}s for
     * @return an immutable {@link Collection} of {@link Property}s applicable to the given {@link Resource}
     * @throws EditorConfigException
     */
    public Collection<Property> queryProperties(Resource resource) throws EditorConfigException {
        Map<String, Property> oldProperties = Collections.emptyMap();
        Map<String, Property> properties = new LinkedHashMap<>();
        boolean root = false;
        final String path = resource.getPath();
        ResourcePath dir = resource.getParent();
        while (dir != null && !root) {
            Resource configFile = dir.resolve(configFileName);
            if (configFile.exists()) {
                EditorConfig config = cache.get(configFile, loader);
                root = config.isRoot();
                List<Section> sections = config.getSections();
                for (Section section : sections) {
                    if (section.match(path)) {
                        // Section matches the editor file, collect options of the section
                        List<Property> o = section.getProperties();
                        for (Property property : o) {
                            properties.put(property.getName(), property);
                        }
                    }
                }
            }
            properties.putAll(oldProperties);
            oldProperties = properties;
            properties = new LinkedHashMap<String, Property>();
            root |= rootDirectories.contains(dir);
            dir = dir.getParent();
        }
        return oldProperties.values();
    }

}
