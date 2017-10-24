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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.ec4j.core.EditorConfigConstants;
import org.eclipse.ec4j.core.ResourceProvider;
import org.eclipse.ec4j.core.model.optiontypes.OptionTypeRegistry;
import org.eclipse.ec4j.core.parser.EditorConfigParser;

/**
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
public class EditorConfig {

    private final List<Section> sections;
    private final OptionTypeRegistry registry;
    private final String version;

    private Boolean root;
    private String dirPath;

    public EditorConfig() {
        this(OptionTypeRegistry.DEFAULT, EditorConfigConstants.VERSION);
    }

    public EditorConfig(OptionTypeRegistry registry, String version) {
        this.sections = new ArrayList<>();
        this.registry = registry;
        this.version = version;
    }

    public OptionTypeRegistry getRegistry() {
        return registry;
    }

    public static EditorConfig load(Reader reader) throws IOException {
        return load(reader, OptionTypeRegistry.DEFAULT);
    }

    public static EditorConfig load(Reader reader, OptionTypeRegistry registry) throws IOException {
        return load(reader, registry, EditorConfigConstants.VERSION);
    }

    public static EditorConfig load(Reader reader, OptionTypeRegistry registry, String version) throws IOException {
        EditorConfigHandler handler = new EditorConfigHandler(registry, version);
        new EditorConfigParser<Section, Option>(handler).setVersion(version).parse(reader);
        return handler.getEditorConfig();
    }

    public static <T> EditorConfig load(T configFile, ResourceProvider<T> provider, OptionTypeRegistry registry,
            String version) throws IOException {
        try (BufferedReader reader = new BufferedReader(provider.getContent(configFile));) {
            EditorConfig config = load(reader, registry, version);
            T dir = provider.getParent(configFile);
            config.setDirPath(provider.getPath(dir) + "/");
            return config;
        }
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public List<Section> getSections() {
        return sections;
    }

    public Boolean getRoot() {
        return root;
    }

    public void setRoot(Boolean root) {
        this.root = root;
    }

    public boolean isRoot() {
        return root != null && root;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        if (getRoot() != null) {
            s.append("root = ");
            s.append(isRoot());
            s.append("\n\n");
        }
        int i = 0;
        for (Section section : sections) {
            if (i > 0) {
                s.append("\n\n");
            }
            s.append(section.toString());
            i++;
        }
        return s.toString();
    }

    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public String getVersion() {
        return version;
    }
}
