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
package org.eclipse.ec4j.core.parser.handlers;

import org.eclipse.ec4j.core.parser.ParseException;

/**
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
public class LoggerEditorConfigHandler<Section, Option> extends AbstractEditorConfigHandler<Section, Option> {

    @Override
    public void startDocument() {

    }

    @Override
    public void endDocument() {

    }

    @Override
    public Section startSection() {
        System.err.println("Start section at " + getLocation());
        return null;
    }

    @Override
    public void endSection(Section section) {
        System.err.println("End section at " + getLocation());
    }

    @Override
    public void startPattern(Section section) {
        System.err.println("Start pattern at " + getLocation());
    }

    @Override
    public void endPattern(Section section, String pattern) {
        System.err.println("End pattern at " + getLocation() + ", pattern=" + pattern);
    }

    @Override
    public void startOption() {
        System.err.println("Start option at " + getLocation());
    }

    @Override
    public void endOption(Option option, Section section) {
        System.err.println("End option at " + getLocation());
    }

    @Override
    public void startOptionName() {
        System.err.println("Start option name at " + getLocation());
    }

    @Override
    public Option endOptionName(String name) {
        System.err.println("End option name at " + getLocation() + ", name=" + name);
        return null;
    }

    @Override
    public void startOptionValue(Option option, String name) {
        System.err.println("Start option value of '" + name + "' at " + getLocation());
    }

    @Override
    public void endOptionValue(Option option, String value, String name) {
        System.err.println("End option value of '" + name + "', value=" + value + " at " + getLocation());
    }

    @Override
    public void error(ParseException e) {
        e.printStackTrace();
    }
}
