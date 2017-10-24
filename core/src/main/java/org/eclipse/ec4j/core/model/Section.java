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
import java.util.regex.Pattern;

import org.eclipse.ec4j.core.EditorConfigConstants;
import org.eclipse.ec4j.core.model.optiontypes.OptionNames;

/**
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
public class Section {

    private final EditorConfig editorConfig;
    private String pattern;
    private final List<Option> options;

    private Pattern regex;
    private List<int[]> ranges;
    private Glob glob;

    public Section(EditorConfig editorConfig) {
        this.editorConfig = editorConfig;
        this.options = new ArrayList<>();
        this.pattern = "";
    }

    public void addOption(Option option) {
        options.add(option);
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        // patterns
        if (!pattern.isEmpty()) {
            s.append("[");
            s.append(pattern);
            s.append("]\n");
        }
        // options
        int i = 0;
        for (Option option : this.getOptions()) {
            if (i > 0) {
                s.append("\n");
            }
            s.append(option.toString());
            i++;
        }
        return s.toString();
    }

    public boolean match(String filePath) {
        return getGlob().match(filePath);
    }

    public Glob getGlob() {
        if (glob == null) {
            String configDirname = editorConfig != null ? editorConfig.getDirPath() : "/dir/";
            glob = new Glob(configDirname, pattern);
        }
        return glob;
    }

    public void preprocessOptions() {
        String version = editorConfig != null ? editorConfig.getVersion() : EditorConfigConstants.VERSION;
        Option indentStyle = null;
        Option indentSize = null;
        Option tabWidth = null;
        for (Option option : options) {
            OptionNames name = OptionNames.get(option.getName());
            // Lowercase option value for certain options
            option.setValue(preprocessOptionValue(name, option.getValue()));
            // get indent_style, indent_size, tab_width option
            switch (name) {
            case indent_style:
                indentStyle = option;
                break;
            case indent_size:
                indentSize = option;
                break;
            case tab_width:
                tabWidth = option;
                break;
            default:
                break;
            }
        }

        // Set indent_size to "tab" if indent_size is unspecified and
        // indent_style is set to "tab".
        if (indentStyle != null && "tab".equals(indentStyle.getValue()) && indentSize == null
                && RegexpUtils.compareVersions(version, "0.10.0") >= 0) {
            indentSize = new Option(OptionNames.indent_size.name(), editorConfig);
            indentSize.setValue("tab");
            this.addOption(indentSize);
        }

        // Set tab_width to indent_size if indent_size is specified and
        // tab_width is unspecified
        if (indentSize != null && !"tab".equals(indentSize.getValue()) && tabWidth == null) {
            tabWidth = new Option(OptionNames.tab_width.name(), editorConfig);
            tabWidth.setValue(indentSize.getValue());
            this.addOption(tabWidth);
        }

        // Set indent_size to tab_width if indent_size is "tab"
        if (indentSize != null && "tab".equals(indentSize.getValue()) && tabWidth != null) {
            indentSize.setValue(tabWidth.getValue());
        }
    }

    /**
     * Return the lowercased option value for certain options.
     *
     * @param name
     * @param value
     * @return the lowercased option value for certain options.
     */
    private static String preprocessOptionValue(OptionNames option, String value) {
        // According test "lowercase_values1" a "lowercase_values2": test that same
        // property values are lowercased (v0.9.0 properties)
        switch (option) {
        case end_of_line:
        case indent_style:
        case indent_size:
        case insert_final_newline:
        case trim_trailing_whitespace:
        case charset:
            return value.toLowerCase();
        default:
            return value;
        }
    }
}
