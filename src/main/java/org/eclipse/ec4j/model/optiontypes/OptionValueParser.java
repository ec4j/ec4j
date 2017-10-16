/**
 * The MIT License
 * Copyright © 2017 Angelo Zerr and other contributors as
 * indicated by the @author tags.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.eclipse.ec4j.model.optiontypes;

interface OptionValueParser<T> {

	public static OptionValueParser<String> IDENTITY_VALUE_PARSER = new OptionValueParser<String>() {
		
		@Override
		public String parse(final String value) {
			return value;
		}
		
		@Override
		public void validate(String name, String value) throws OptionException {

		}
	};

	public static OptionValueParser<Boolean> BOOLEAN_VALUE_PARSER = new OptionValueParser<Boolean>() {
		
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

	public static OptionValueParser<Integer> POSITIVE_INT_VALUE_PARSER = new OptionValueParser<Integer>() {
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
