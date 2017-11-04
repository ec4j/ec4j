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
public interface PropertyValueParser<T> {

    class EnumValueParser<T extends Enum<T>> implements PropertyValueParser<T> {

        private final Class<? extends Enum> enumType;

        public EnumValueParser(final Class<? extends T> enumType) {
            this.enumType = enumType;
        }

        @Override
        public T parse(final String value) {
            try {
                return (T) Enum.valueOf(enumType, value.toUpperCase());
            } catch (final IllegalArgumentException e) {
                return null;
            }
        }

        @Override
        public void validate(final String name, final String value) throws PropertyException {
            try {
                Enum.valueOf(enumType, value.toUpperCase());
            } catch (final IllegalArgumentException e) {
                throw new PropertyException("enum");
            }
        }

    }

    PropertyValueParser<String> IDENTITY_VALUE_PARSER = new PropertyValueParser<String>() {

        @Override
        public String parse(final String value) {
            return value;
        }

        @Override
        public void validate(String name, String value) throws PropertyException {

        }
    };

    PropertyValueParser<Boolean> BOOLEAN_VALUE_PARSER = new PropertyValueParser<Boolean>() {

        @Override
        public Boolean parse(final String value) {
            return Boolean.valueOf(value.toLowerCase());
        }

        @Override
        public void validate(String name, String value) throws PropertyException {
            value = value.toLowerCase();
            if (!"true".equals(value) && !"false".equals(value)) {
                throw new PropertyException(
                        "Property '" + name + "' expects a boolean. The value '" + value + "' is not a boolean.");
            }
        }
    };

    PropertyValueParser<Integer> POSITIVE_INT_VALUE_PARSER = new PropertyValueParser<Integer>() {
        @Override
        public Integer parse(final String value) {
            try {
                final Integer integer = Integer.valueOf(value);
                return integer <= 0 ? null : integer;
            } catch (final NumberFormatException e) {
                return null;
            }
        }

        @Override
        public void validate(String name, String value) throws PropertyException {
            try {
                Integer.valueOf(value);
            } catch (final NumberFormatException e) {
                throw new PropertyException(
                        "Property '" + name + "' expects an integer. The value '" + value + "' is not an integer.");
            }
        }
    };

    void validate(String name, String value) throws PropertyException;

    T parse(String value);

}
