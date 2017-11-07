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

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

/**
 * A type of a {@link Property}. This class also contains the <a href=
 * "https://github.com/editorconfig/editorconfig/wiki/EditorConfig-Properties#widely-supported-by-editors">"widely
 * supported" property types</a> of as constants.
 *
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 *
 * @param <T>
 *            the type of the {@link Property} value
 */
public class PropertyType<T> {

    /**
     * An enumeration of allowed end of line values.
     */
    public enum EndOfLineValue {

        cr("Carriage Return", "\r"),

        crlf("Carriage Return + Line Feed", "\r\n"),

        lf("Line Feed", "\n");

        private static final Set<String> VALUE_SET;
        static {
            Set<String> s = new LinkedHashSet<>();
            for (EndOfLineValue v : values()) {
                s.add(v.name());
            }
            VALUE_SET = Collections.unmodifiableSet(s);
        }

        public static Set<String> valueSet() {
            return VALUE_SET;
        }

        private final String displayValue;

        private final String eolString;

        EndOfLineValue(final String displayValue, final String eolString) {
            this.displayValue = displayValue;
            this.eolString = eolString;
        }

        public String getEndOfLineString() {
            return eolString;
        }
    }

    /**
     * An enumeration of allowed indentation style values.
     */
    public enum IndentStyleValue {

        space("Space"),

        tab("Tab");

        private static final Set<String> VALUE_SET;

        static {
            Set<String> s = new LinkedHashSet<>();
            for (IndentStyleValue v : values()) {
                s.add(v.name());
            }
            VALUE_SET = Collections.unmodifiableSet(s);
        }

        public static Set<String> valueSet() {
            return VALUE_SET;
        }

        private final String displayValue;

        IndentStyleValue(final String displayValue) {
            this.displayValue = displayValue;
        }

    }

    /**
     * Because some values need to be lowercased, as required by "lowercase_values1" a "lowercase_values2" tests (v0.9.0
     * properties), this {@link PropertyType} subclass applies the lower-casing in {@link #normalizeIfNeeded(String)}.
     *
     * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
     *
     * @param <T>
     *            the type of the {@link Property} value
     */
    public static class LowerCasingPropertyType<T> extends PropertyType<T> {

        public LowerCasingPropertyType(String name, String description, PropertyValueParser<T> parser,
                Set<String> possibleValues) {
            super(name, description, parser, possibleValues);
        }

        public LowerCasingPropertyType(String name, String description, PropertyValueParser<T> parser,
                String... possibleValues) {
            super(name, description, parser, possibleValues);
        }

        @Override
        public String normalizeIfNeeded(String value) {
            return value == null ? null : value.toLowerCase(Locale.US);
        }

    }

    /**
     * The result of {@link Property} value parsing. The result may either be valid when it has {@link #value} or
     * invalid when it has no {@link #value}, but it has an {@link #errorMessage}.
     *
     * @param <T>
     *            the type of the parsed value
     */
    public static class ParsedValue<T> {
        public static <T> ParsedValue<T> invalid(String errorMessage) {
            return new ParsedValue<T>(null, errorMessage);
        }

        public static <T> ParsedValue<T> valid(T value) {
            return new ParsedValue<T>(value, null);
        }

        private final String errorMessage;

        private final T value;

        ParsedValue(T value, String errorMessage) {
            super();
            this.value = value;
            this.errorMessage = errorMessage;
        }

        /**
         * @return the error message describing why the parsing failed or {@code null} if the parsing succeeded
         */
        public String getErrorMessage() {
            return errorMessage;
        }

        /**
         * @return the parsed value or {@code null} if the parsing failed.
         */
        public T getValue() {
            return value;
        }

        /**
         * @return {@code true} if the parsing succeeded or {@code false} otherwise
         */
        public boolean isValid() {
            return errorMessage == null;
        }
    }

    /**
     * A facility able to parse string values into other types.
     *
     * @param <T>
     *            the type of the parse result
     */
    public interface PropertyValueParser<T> {

        /**
         * A {@link PropertyValueParser} implementation that allows only members of a given {@link Enum} type.
         *
         * @param <T>
         */
        class EnumValueParser<T extends Enum<T>> implements PropertyValueParser<T> {

            private final Class<? extends Enum> enumType;

            public EnumValueParser(final Class<? extends T> enumType) {
                this.enumType = enumType;
            }

            @SuppressWarnings("unchecked")
            @Override
            public ParsedValue<T> parse(String name, String value) {
                try {
                    return ParsedValue.valid((T) Enum.valueOf(enumType, value));
                } catch (final IllegalArgumentException e) {
                    return ParsedValue.invalid("Unexpected value \"" + value + "\" for enum " + enumType.getName());
                }
            }

        }

        /** A parser for boolean values {@code true} and {@code false} */
        PropertyValueParser<Boolean> BOOLEAN_VALUE_PARSER = new PropertyValueParser<Boolean>() {

            @Override
            public ParsedValue<Boolean> parse(String name, String value) {
                value = value.toLowerCase();
                if (!"true".equals(value) && !"false".equals(value)) {
                    return ParsedValue.invalid(
                            "Property '" + name + "' expects a boolean. The value '" + value + "' is not a boolean.");
                } else {
                    return ParsedValue.valid(Boolean.valueOf(value.toLowerCase()));
                }
            }

        };

        /** A dummy parser that does no parsing at all. */
        PropertyValueParser<String> IDENTITY_VALUE_PARSER = new PropertyValueParser<String>() {

            @Override
            public ParsedValue<String> parse(String name, String value) {
                return ParsedValue.valid(value);
            }

        };

        /** A parser accepting positive integer numbers. */
        PropertyValueParser<Integer> POSITIVE_INT_VALUE_PARSER = new PropertyValueParser<Integer>() {
            @Override
            public ParsedValue<Integer> parse(String name, String value) {
                try {
                    final int val = Integer.parseInt(value);
                    if (val <= 0) {
                        return ParsedValue
                                .invalid("Property '" + name + "' expects a positive integer; found '" + value + "'");
                    } else {
                        return ParsedValue.valid(Integer.valueOf(val));
                    }
                } catch (final NumberFormatException e) {
                    return ParsedValue.invalid(
                            "Property '" + name + "' expects an integer. The value '" + value + "' is not an integer.");
                }
            }
        };

        /**
         * Parses the given {@code value} into a {@link ParsedValue}
         *
         * @param name
         *            the name of the parsed property
         * @param value
         *            the value to parse
         * @return the {@link PropertyType.ParsedValue}
         */
        ParsedValue<T> parse(String name, String value);

    }

    private static final String[] BOOLEAN_POSSIBLE_VALUES = new String[] { Boolean.TRUE.toString(),
            Boolean.FALSE.toString() };

    public static final PropertyType<String> charset = new LowerCasingPropertyType<>( //
            "charset", //
            "set to latin1, utf-8, utf-8-bom, utf-16be or utf-16le to control the character set. Use of utf-8-bom is discouraged.", //
            PropertyValueParser.IDENTITY_VALUE_PARSER, //
            "utf-8", "utf-8-bom", "utf-16be", "utf-16le", "latin1", "tab" //
    );

    public static final PropertyType<EndOfLineValue> end_of_line = new LowerCasingPropertyType<>(//
            "end_of_line", //
            "set to lf, cr, or crlf to control how line breaks are represented.", //
            new PropertyValueParser.EnumValueParser<EndOfLineValue>(EndOfLineValue.class), //
            EndOfLineValue.valueSet() //
    );

    /**
     * Indeed, {@code tab} is a legal value - see
     * https://github.com/editorconfig/editorconfig/wiki/EditorConfig-Properties#indent_size and
     * https://github.com/editorconfig/editorconfig/issues/54
     */
    public static final PropertyType<Integer> indent_size = new LowerCasingPropertyType<>( //
            "indent_size", //
            "a whole number defining the number of columns used for each indentation level and the width of soft tabs (when supported). When set to tab, the value of tab_width (if specified) will be used.", //
            PropertyValueParser.POSITIVE_INT_VALUE_PARSER, //
            "1", "2", "3", "4", "5", "6", "7", "8", "tab" //
    );

    public static final PropertyType<IndentStyleValue> indent_style = new LowerCasingPropertyType<>( //
            "indent_style", //
            "set to tab or space to use hard tabs or soft tabs respectively.",
            new PropertyValueParser.EnumValueParser<IndentStyleValue>(IndentStyleValue.class), //
            IndentStyleValue.valueSet() //
    );

    public static final PropertyType<Boolean> insert_final_newline = new LowerCasingPropertyType<>( //
            "insert_final_newline", //
            "set to true to ensure file ends with a newline when saving and false to ensure it doesn't.", //
            PropertyValueParser.BOOLEAN_VALUE_PARSER, //
            BOOLEAN_POSSIBLE_VALUES //
    );

    public static final PropertyType<Boolean> root = new LowerCasingPropertyType<>( //
            "root", //
            "special property that should be specified at the top of the file outside of any sections. Set to true to stop .editorconfig files search on current file.", //
            PropertyValueParser.BOOLEAN_VALUE_PARSER, //
            BOOLEAN_POSSIBLE_VALUES //
    );

    private static final Set<PropertyType<?>> STANDARD_TYPES;

    public static final PropertyType<Integer> tab_width = new PropertyType<>( //
            "tab_width", //
            "a whole number defining the number of columns used to represent a tab character. This defaults to the value of indent_size and doesn't usually need to be specified.", //
            PropertyValueParser.POSITIVE_INT_VALUE_PARSER, //
            "1", "2", "3", "4", "5", "6", "7", "8" //
    );

    public static final PropertyType<Boolean> trim_trailing_whitespace = new LowerCasingPropertyType<>( //
            "trim_trailing_whitespace", //
            "set to true to remove any whitespace characters preceding newline characters and false to ensure it doesn't.", //
            PropertyValueParser.BOOLEAN_VALUE_PARSER, //
            BOOLEAN_POSSIBLE_VALUES //
    );

    static {
        STANDARD_TYPES = Collections
                .unmodifiableSet(new LinkedHashSet<PropertyType<?>>(Arrays.asList(charset, end_of_line, indent_size,
                        indent_style, insert_final_newline, root, tab_width, trim_trailing_whitespace)));
    }

    /**
     * @return a {@link Set} of the "widely supported" property types - see
     *         <a href="http://editorconfig.org/">http://editorconfig.org/</a>
     */
    public static Set<PropertyType<?>> standardTypes() {
        return STANDARD_TYPES;
    }

    private static Set<String> toSet(String[] possibleValues2) {
        Set<String> s = new LinkedHashSet<>();
        for (String v : possibleValues2) {
            s.add(v);
        }
        return Collections.unmodifiableSet(s);
    }

    private final String description;
    private final String name;
    private final PropertyValueParser<T> parser;
    private final Set<String> possibleValues;

    public PropertyType(String name, String description, PropertyValueParser<T> parser, Set<String> possibleValues) {
        super();
        this.name = name;
        this.description = description;
        this.possibleValues = possibleValues;
        this.parser = parser;
    }

    public PropertyType(String name, String description, PropertyValueParser<T> parser, String... possibleValues) {
        this(name, description, parser, toSet(possibleValues));
    }

    /**
     * @return a short description of this {@link PropertyType}
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the name of this {@link PropertyType}
     */
    public String getName() {
        return name;
    }

    /**
     * @return a {@link Set} of values that are valid for this {@link PropertyType}
     */
    public Set<String> getPossibleValues() {
        return possibleValues;
    }

    /**
     * Editor config tests require that some values need to be processed in various ways before being used. This method
     * is supposed to perform such transformations. This particular implementation performs no transformation. See also
     * {@link LowerCasingPropertyType}.
     *
     * @param value
     *            the value to normalize
     * @return the normalized value or the passed-in {@code value} if no transformation is necessary
     */
    public String normalizeIfNeeded(String value) {
        return value;
    }

    /**
     * Parses the given {@code value} into a {@link ParsedValue}
     * @param value the value to parse
     * @return the {@link ParsedValue}
     */
    public ParsedValue<T> parse(String value) {
        return parser.parse(name, value);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return name;
    }

}