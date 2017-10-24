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
package org.eclipse.ec4j.core.model.optiontypes;

/**
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
interface OptionValueParser<T> {

    OptionValueParser<String> IDENTITY_VALUE_PARSER = new OptionValueParser<String>() {

        @Override
        public String parse(final String value) {
            return value;
        }

        @Override
        public void validate(String name, String value) throws OptionException {

        }
    };

    OptionValueParser<Boolean> BOOLEAN_VALUE_PARSER = new OptionValueParser<Boolean>() {

        @Override
        public Boolean parse(final String value) {
            return Boolean.valueOf(value.toLowerCase());
        }

        @Override
        public void validate(String name, String value) throws OptionException {
            value = value.toLowerCase();
            if (!"true".equals(value) && !"false".equals(value)) {
                throw new OptionException(
                        "Option '" + name + "' expects a boolean. The value '" + value + "' is not a boolean.");
            }
        }
    };

    OptionValueParser<Integer> POSITIVE_INT_VALUE_PARSER = new OptionValueParser<Integer>() {
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
        public void validate(String name, String value) throws OptionException {
            try {
                Integer.valueOf(value);
            } catch (final NumberFormatException e) {
                throw new OptionException(
                        "Option '" + name + "' expects an integer. The value '" + value + "' is not an integer.");
            }
        }
    };

    void validate(String name, String value) throws OptionException;

    T parse(String value);

}
