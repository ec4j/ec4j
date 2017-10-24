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

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.ec4j.core.model.EditorConfig;
import org.eclipse.ec4j.core.model.Option;
import org.eclipse.ec4j.core.model.RegexpUtils;
import org.eclipse.ec4j.core.model.Section;
import org.eclipse.ec4j.core.model.optiontypes.OptionTypeRegistry;
import org.eclipse.ec4j.core.parser.ParseException;

/**
 * Abstract class editorconfig manager.
 *
 * @param <T>
 *            the file type.
 *
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
public class AbstractEditorConfigManager<T> {

    private final String configFilename;
    private final String version;
    private final OptionTypeRegistry registry;
    private final ResourceProvider<T> provider;

    /**
     * Creates EditorConfig handler with default configuration filename
     * (.editorconfig) and version {@link EditorConfig#VERSION}
     */
    public AbstractEditorConfigManager(ResourceProvider<T> provider) {
        this(OptionTypeRegistry.DEFAULT, provider, EditorConfigConstants.EDITORCONFIG, EditorConfigConstants.VERSION);
    }

    /**
     * Creates EditorConfig handler with specified configuration filename and
     * version. Used mostly for debugging/testing.
     *
     * @param option
     *            type registry
     * @param configFilename
     *            configuration file name to be searched for instead of
     *            .editorconfig
     * @param version
     *            required version
     */
    public AbstractEditorConfigManager(OptionTypeRegistry registry, ResourceProvider<T> provider, String configFilename,
            String version) {
        this.registry = registry;
        this.configFilename = configFilename;
        this.version = version;
        this.provider = provider;
    }

    /**
     * Parse editorconfig files corresponding to the file path and return the
     * parsing result.
     *
     * @param file
     *            the file to be parsed. The path is usually the path of the file
     *            which is currently edited by the editor.
     * @return The parsing result stored in a list of {@link Option}.
     * @throws org.editorconfig.core.ParsingException
     *             If an {@code .editorconfig} file could not be parsed
     * @throws org.editorconfig.core.VersionException
     *             If version greater than actual is specified in constructor
     * @throws org.editorconfig.core.EditorConfigException
     *             If an EditorConfig exception occurs. Usually one of
     *             {@link ParseException} or {@link VersionException}
     */
    public Collection<Option> getOptions(T file, Set<T> explicitRootDirs) throws EditorConfigException {
        checkAssertions();
        Map<String, Option> oldOptions = Collections.emptyMap();
        Map<String, Option> options = new LinkedHashMap<>();
        try {
            String path = provider.getPath(file);
            boolean root = false;
            T dir = provider.getParent(file);
            while (dir != null && !root) {
                T configFile = provider.getResource(dir, getConfigFilename());
                if (provider.exists(configFile)) {
                    EditorConfig config = getEditorConfig(configFile);
                    root = config.isRoot();
                    List<Section> sections = config.getSections();
                    for (Section section : sections) {
                        if (section.match(path)) {
                            // Section matches the editor file, collect options of the section
                            List<Option> o = section.getOptions();
                            for (Option option : o) {
                                options.put(option.getName(), option);
                            }
                        }
                    }
                }
                options.putAll(oldOptions);
                oldOptions = options;
                options = new LinkedHashMap<String, Option>();
                root |= explicitRootDirs != null && explicitRootDirs.contains(dir);
                dir = provider.getParent(dir);
            }
        } catch (Exception e) {
            throw new EditorConfigException(null, e);
        }

        return oldOptions.values();
    }

    /**
     * Returns the editorconfig instance from the given .editorconfig file. This
     * method can be orverrided to manage cache.
     *
     * @param configFile
     *            the .editorconfig file
     * @return the editorconfig instance from the given .editorconfig file
     * @throws IOException
     */
    public EditorConfig getEditorConfig(T configFile) throws IOException {
        return EditorConfig.load(configFile, provider, getRegistry(), getVersion());
    }

    private void checkAssertions() throws VersionException {
        if (RegexpUtils.compareVersions(version, EditorConfigConstants.VERSION) > 0) {
            throw new VersionException("Required version is greater than the current version.");
        }
    }

    public OptionTypeRegistry getRegistry() {
        return registry;
    }

    public String getVersion() {
        return version;
    }

    public String getConfigFilename() {
        return configFilename;
    }
}
