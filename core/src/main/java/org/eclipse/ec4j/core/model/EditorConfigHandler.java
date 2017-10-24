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

import org.eclipse.ec4j.core.model.optiontypes.OptionNames;
import org.eclipse.ec4j.core.model.optiontypes.OptionTypeRegistry;
import org.eclipse.ec4j.core.parser.ParseException;
import org.eclipse.ec4j.core.parser.handlers.AbstractEditorConfigHandler;

/**
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
public class EditorConfigHandler extends AbstractEditorConfigHandler<Section, Option> {

    private final EditorConfig editorConfig;

    public EditorConfigHandler(OptionTypeRegistry registry, String version) {
        this.editorConfig = new EditorConfig(registry, version);
    }

    @Override
    public void startDocument() {

    }

    @Override
    public void endDocument() {
        for (Section section : editorConfig.getSections()) {
            section.preprocessOptions();
        }
    }

    @Override
    public Section startSection() {
        return new Section(editorConfig);
    }

    @Override
    public void endSection(Section section) {
        editorConfig.addSection(section);
    }

    @Override
    public void startPattern(Section section) {

    }

    @Override
    public void endPattern(Section section, String pattern) {
        if(section == null) {
            return;
        }
        section.setPattern(pattern);
    }

    @Override
    public void startOption() {

    }

    @Override
    public void endOption(Option option, Section section) {
        if (option == null) {
            return;
        }
        if (section != null) {
            if (option.checkMax()) {
                // name <= 50 (see max_property_name test)
                // value <= 255 (see max_property_value test)
                section.addOption(option);
            }
        } else if (OptionNames.get(option.getName()) == OptionNames.root) {
            editorConfig.setRoot("true".equals(option.getValue()));
        }
    }

    @Override
    public void startOptionName() {

    }

    @Override
    public Option endOptionName(String name) {
        return new Option(name, editorConfig);
    }

    @Override
    public void startOptionValue(Option option, String name) {

    }

    @Override
    public void endOptionValue(Option option, String value, String name) {
        if (option == null) {
            return;
        }
        option.setValue(value);
    }

    @Override
    public void error(ParseException e) {
        e.printStackTrace();
    }

    public EditorConfig getEditorConfig() {
        return editorConfig;
    }

}