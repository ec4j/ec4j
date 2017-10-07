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

interface ValueValidator<T> {

	public static ValueValidator<String> IDENTITY_VALUE_VALIDATOR = new ValueValidator<String>() {

		@Override
		public void validate(String name, String value) throws OptionException {

		}
	};

	public static ValueValidator<Boolean> BOOLEAN_VALUE_VALIDATOR = new ValueValidator<Boolean>() {

		@Override
		public void validate(String name, String value) throws OptionException {
			value = value.toLowerCase();
			if (!"true".equals(value) && !"false".equals(value)) {
				throw new OptionException(
						"Option '" + name + "' expects a boolean. The value '" + value + "' is not a boolean.");
			}
		}
	};

	public static ValueValidator<Integer> POSITIVE_INT_VALUE_VALIDATOR = new ValueValidator<Integer>() {

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

}
