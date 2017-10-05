/*
 * Copyright 2014 Nathan Jones
 *
 * This file is part of "EditorConfig Eclipse".
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
