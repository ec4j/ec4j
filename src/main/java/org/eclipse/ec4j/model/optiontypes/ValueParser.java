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

interface ValueParser<T> {

	public static ValueParser<String> IDENTITY_VALUE_PARSER = new ValueParser<String>() {
		@Override
		public String parse(final String value) {
			return value;
		}
	};

	public static ValueParser<Boolean> BOOLEAN_VALUE_PARSER = new ValueParser<Boolean>() {
		@Override
		public Boolean parse(final String value) {
			return Boolean.valueOf(value.toLowerCase());
		}
	};

	public static ValueParser<Integer> POSITIVE_INT_VALUE_PARSER = new ValueParser<Integer>() {
		@Override
		public Integer parse(final String value) {
			try {
				final Integer integer = Integer.valueOf(value);
				return integer <= 0 ? null : integer;
			} catch (final NumberFormatException e) {
				return null;
			}
		}
	};

	T parse(String value);

}
