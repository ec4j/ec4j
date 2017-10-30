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
package org.eclipse.ec4j.core.model.propertytype;

/**
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
public abstract class PropertyType<T> {

    private static final String[] BOOLEAN_POSSIBLE_VALUES = new String[] { "true", "false" };

    public static class IndentStyle extends PropertyType<IndentStyleProperty> {

        private static final String[] POSSIBLE_VALUES = new String[] { "tab", "space" };

        @Override
        public String getName() {
            return PropertyName.indent_style.name();
        }

        @Override
        public String getDescription() {
            return "set to tab or space to use hard tabs or soft tabs respectively.";
        }

        @Override
        public PropertyValueParser<IndentStyleProperty> getValueParser() {
            return new EnumValueParser<IndentStyleProperty>(IndentStyleProperty.class);
        }

        @Override
        public String[] getPossibleValues() {
            return POSSIBLE_VALUES;
        }

    }

    public static class IndentSize extends PropertyType<Integer> {

        private static final String[] POSSIBLE_VALUES = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "tab" };

        @Override
        public String getName() {
            return PropertyName.indent_size.name();
        }

        @Override
        public String getDescription() {
            return "a whole number defining the number of columns used for each indentation level and the width of soft tabs (when supported). When set to tab, the value of tab_width (if specified) will be used.";
        }

        @Override
        public PropertyValueParser<Integer> getValueParser() {
            return PropertyValueParser.POSITIVE_INT_VALUE_PARSER;
        }

        @Override
        public String[] getPossibleValues() {
            return POSSIBLE_VALUES;
        }

    }

    public static class TabWidth extends PropertyType<Integer> {

        private static final String[] POSSIBLE_VALUES = new String[] { "1", "2", "3", "4", "5", "6", "7", "8" };

        @Override
        public String getName() {
            return PropertyName.tab_width.name();
        }

        @Override
        public String getDescription() {
            return "a whole number defining the number of columns used to represent a tab character. This defaults to the value of indent_size and doesn't usually need to be specified.";
        }

        @Override
        public PropertyValueParser<Integer> getValueParser() {
            return PropertyValueParser.POSITIVE_INT_VALUE_PARSER;
        }

        @Override
        public String[] getPossibleValues() {
            return POSSIBLE_VALUES;
        }
    }

    public static class EndOfLine extends PropertyType<EndOfLineProperty> {

        private static final String[] POSSIBLE_VALUES = new String[] { "lf", "crlf", "cr" };

        @Override
        public String getName() {
            return PropertyName.end_of_line.name();
        }

        @Override
        public String getDescription() {
            return "set to lf, cr, or crlf to control how line breaks are represented.";
        }

        @Override
        public PropertyValueParser<EndOfLineProperty> getValueParser() {
            return new EnumValueParser<EndOfLineProperty>(EndOfLineProperty.class);
        }

        @Override
        public String[] getPossibleValues() {
            return POSSIBLE_VALUES;
        }
    }

    public static class Charset extends PropertyType<String> {

        private static final String[] POSSIBLE_VALUES = new String[] { "utf-8", "utf-8-bom", "utf-16be", "utf-16le",
                "latin1", "tab" };

        @Override
        public String getName() {
            return PropertyName.charset.name();
        }

        @Override
        public String getDescription() {
            return "set to latin1, utf-8, utf-8-bom, utf-16be or utf-16le to control the character set. Use of utf-8-bom is discouraged.";
        }

        @Override
        public PropertyValueParser<String> getValueParser() {
            return PropertyValueParser.IDENTITY_VALUE_PARSER;
        }

        @Override
        public String[] getPossibleValues() {
            return POSSIBLE_VALUES;
        }

    }

    public static class TrimTrailingWhitespace extends PropertyType<Boolean> {

        @Override
        public String getName() {
            return PropertyName.trim_trailing_whitespace.name();
        }

        @Override
        public String getDescription() {
            return "set to true to remove any whitespace characters preceding newline characters and false to ensure it doesn't.";
        }

        @Override
        public PropertyValueParser<Boolean> getValueParser() {
            return PropertyValueParser.BOOLEAN_VALUE_PARSER;
        }

        @Override
        public String[] getPossibleValues() {
            return BOOLEAN_POSSIBLE_VALUES;
        }
    }

    public static class InsertFinalNewline extends PropertyType<Boolean> {

        @Override
        public String getName() {
            return PropertyName.insert_final_newline.name();
        }

        @Override
        public String getDescription() {
            return "set to true to ensure file ends with a newline when saving and false to ensure it doesn't.";
        }

        @Override
        public PropertyValueParser<Boolean> getValueParser() {
            return PropertyValueParser.BOOLEAN_VALUE_PARSER;
        }

        @Override
        public String[] getPossibleValues() {
            return BOOLEAN_POSSIBLE_VALUES;
        }
    }

    public static class Root extends PropertyType<Boolean> {

        @Override
        public String getName() {
            return PropertyName.root.name();
        }

        @Override
        public String getDescription() {
            return "special property that should be specified at the top of the file outside of any sections. Set to true to stop .editorconfig files search on current file.";
        }

        @Override
        public PropertyValueParser<Boolean> getValueParser() {
            return PropertyValueParser.BOOLEAN_VALUE_PARSER;
        }

        @Override
        public String[] getPossibleValues() {
            return BOOLEAN_POSSIBLE_VALUES;
        }
    }

    public abstract String getName();

    public abstract String getDescription();

    public abstract PropertyValueParser<T> getValueParser();

    public void validate(String value) throws PropertyException {
        getValueParser().validate(getName(), value);
    }

    public T parse(String value) {
        return getValueParser().parse(value);
    }

    public abstract String[] getPossibleValues();

    @Override
    public String toString() {
        return getName();
    }

}
