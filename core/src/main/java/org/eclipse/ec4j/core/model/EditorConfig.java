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
import java.util.List;

import org.eclipse.ec4j.core.model.optiontypes.OptionTypeRegistry;

/**
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
public class EditorConfig {

    private final List<Section> sections;
    private final OptionTypeRegistry registry;
    private final String version;

    private Boolean root;
    private String dirPath;

    public EditorConfig(OptionTypeRegistry registry, String version) {
        this.sections = new ArrayList<>();
        this.registry = registry;
        this.version = version;
    }

    public OptionTypeRegistry getRegistry() {
        return registry;
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
