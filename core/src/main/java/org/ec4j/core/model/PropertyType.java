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
package org.ec4j.core.model;

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
 *            the type of the parsed {@link Property} value
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

        /**
         * @param endOfLineString
         * @return an {@link EndOfLineValue} that corresponds to the given {@code endOfLineString}
         */
        public static EndOfLineValue ofEndOfLineString(String endOfLineString) {
            switch (endOfLineString) {
            case "\r":
                return EndOfLineValue.cr;
            case "\r\n":
                return EndOfLineValue.crlf;
            case "\n":
                return EndOfLineValue.lf;
            default:
                return null;
            }
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

        space("Space", ' '),

        tab("Tab", '\t');

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

        private final char indentChar;

        IndentStyleValue(String displayValue, char indentChar) {
            this.displayValue = displayValue;
            this.indentChar = indentChar;
        }

        /**
         * @return a space or tab character
         */
        public char getIndentChar() {
            return indentChar;
        }

    }

    /**
     * Because some values need to be lowercased, as required by "lowercase_values1" a "lowercase_values2" tests (v0.9.0
     * properties), this {@link PropertyType} subclass applies the lower-casing in {@link #normalizeIfNeeded(String)}.
     *
     * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
     *
     * @param <T>
     *            the type of the {@link Property} parsed
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
     * The result of {@link Property} parsed parsing. The result may either be valid when it has {@link #parsed} or
     * invalid when it has no {@link #parsed}, but it has an {@link #errorMessage}.
     *
     * @param <T>
     *            the type of the parsed parsed
     */
    public static class PropertyValue<T> {
        public static <T> PropertyValue<T> invalid(String source, String errorMessage) {
            return new PropertyValue<T>(source, null, errorMessage);
        }

        public static <T> PropertyValue<T> valid(String source, T value) {
            return new PropertyValue<T>(source, value, null);
        }

        private final String errorMessage;

        private final T parsed;
        private final String source;

        PropertyValue(String source, T value, String errorMessage) {
            super();
            this.source = source;
            this.parsed = value;
            this.errorMessage = errorMessage;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            PropertyValue other = (PropertyValue) obj;
            if (errorMessage == null) {
                if (other.errorMessage != null)
                    return false;
            } else if (!errorMessage.equals(other.errorMessage))
                return false;
            if (parsed == null) {
                if (other.parsed != null)
                    return false;
            } else if (!parsed.equals(other.parsed))
                return false;
            if (source == null) {
                if (other.source != null)
                    return false;
            } else if (!source.equals(other.source))
                return false;
            return true;
        }

        /**
         * @return the error message describing why the parsing failed or {@code null} if the parsing succeeded
         */
        public String getErrorMessage() {
            return errorMessage;
        }

        /**
         * @return the parsed parsed or {@code null} if the parsing failed.
         */
        public T getParsed() {
            return parsed;
        }

        /**
         * @return the string from which this {@link PropertyValue} was created
         */
        public String getSource() {
            return source;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((errorMessage == null) ? 0 : errorMessage.hashCode());
            result = prime * result + ((parsed == null) ? 0 : parsed.hashCode());
            result = prime * result + ((source == null) ? 0 : source.hashCode());
            return result;
        }

        /**
         * @return {@code true} if the parsing succeeded or {@code false} otherwise
         */
        public boolean isValid() {
            return errorMessage == null;
        }

        @Override
        public String toString() {
            return "PropertyValue [errorMessage=" + errorMessage + ", parsed=" + parsed + ", source=" + source + "]";
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
            public PropertyValue<T> parse(String name, String value) {
                if (value == null) {
                    return PropertyValue.invalid(value, "Cannot make enum " + enumType.getName() + " out of null");
                }
                try {
                    return PropertyValue.valid(value, (T) Enum.valueOf(enumType, value.toLowerCase()));
                } catch (final IllegalArgumentException e) {
                    return PropertyValue.invalid(value,
                            "Unexpected parsed \"" + value + "\" for enum " + enumType.getName());
                }
            }

        }

        /** A parser for boolean values {@code true} and {@code false} */
        PropertyValueParser<Boolean> BOOLEAN_VALUE_PARSER = new PropertyValueParser<Boolean>() {

            @Override
            public PropertyValue<Boolean> parse(String name, String value) {
                if (value == null) {
                    return PropertyValue.invalid(null, "Property '" + name + "' expects a boolean; found: null");
                } else if ("true".equalsIgnoreCase(value)) {
                    return PropertyValue.valid(value, Boolean.TRUE);
                } else if ("false".equalsIgnoreCase(value)) {
                    return PropertyValue.valid(value, Boolean.FALSE);
                } else {
                    return PropertyValue.invalid(value,
                            "Property '" + name + "' expects a boolean. The parsed '" + value + "' is not a boolean.");
                }
            }

        };

        /** A dummy parser that does no parsing at all. */
        PropertyValueParser<String> IDENTITY_VALUE_PARSER = new PropertyValueParser<String>() {

            @Override
            public PropertyValue<String> parse(String name, String value) {
                return PropertyValue.valid(value, value);
            }

        };

        PropertyValueParser<Integer> INDENT_SIZE_VALUE_PARSER = new PropertyValueParser<Integer>() {
            @Override
            public PropertyValue<Integer> parse(String name, String value) {
                if ("tab".equalsIgnoreCase(value)) {
                    return PropertyValue.valid(value, null);
                } else {
                    return POSITIVE_INT_VALUE_PARSER.parse(name, value);
                }
            }
        };

        /** A parser accepting positive integer numbers. */
        PropertyValueParser<Integer> POSITIVE_INT_VALUE_PARSER = new PropertyValueParser<Integer>() {
            @Override
            public PropertyValue<Integer> parse(String name, String value) {
                try {
                    final int val = Integer.parseInt(value);
                    if (val <= 0) {
                        return PropertyValue.invalid(value,
                                "Property '" + name + "' expects a positive integer; found '" + value + "'");
                    } else {
                        return PropertyValue.valid(value, Integer.valueOf(val));
                    }
                } catch (final NumberFormatException e) {
                    return PropertyValue.invalid(value, "Property '" + name + "' expects an integer. The parsed '"
                            + value + "' is not an integer.");
                }
            }
        };

        /**
         * Parses the given {@code parsed} into a {@link PropertyValue}
         *
         * @param name
         *            the name of the parsed property
         * @param parsed
         *            the parsed to parse
         * @return the {@link PropertyType.PropertyValue}
         */
        PropertyValue<T> parse(String name, String value);

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
     * Note that {@code tab} is a legal value of {@code indent_size} - see
     * https://github.com/editorconfig/editorconfig/wiki/EditorConfig-Properties#indent_size and
     * https://github.com/editorconfig/editorconfig/issues/54
     * <p>
     * If {@code indent_size} {@link Property} is constructed out of {@code tab}, the resulting instance's
     * {@link Property#getValueAs()} returns {@code null}.
     */
    public static final PropertyType<Integer> indent_size = new LowerCasingPropertyType<>( //
            "indent_size", //
            "a whole number defining the number of columns used for each indentation level and the width of soft tabs (when supported). When set to tab, the parsed of tab_width (if specified) will be used.", //
            PropertyValueParser.INDENT_SIZE_VALUE_PARSER, //
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
            "a whole number defining the number of columns used to represent a tab character. This defaults to the parsed of indent_size and doesn't usually need to be specified.", //
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
     * @param parsed
     *            the parsed to normalize
     * @return the normalized parsed or the passed-in {@code parsed} if no transformation is necessary
     */
    public String normalizeIfNeeded(String value) {
        return value;
    }

    /**
     * Parses the given {@code parsed} into a {@link PropertyValue}
     *
     * @param parsed
     *            the parsed to parse
     * @return the {@link PropertyValue}
     */
    public PropertyValue<T> parse(String value) {
        return parser.parse(name, value);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return name;
    }

}