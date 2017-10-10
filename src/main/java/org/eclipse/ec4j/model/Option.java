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
package org.eclipse.ec4j.model;

import org.eclipse.ec4j.model.optiontypes.OptionException;
import org.eclipse.ec4j.model.optiontypes.OptionType;

public class Option {

	private final String name;
	private final EditorConfig editorConfig;
	private final OptionType<?> type;
	private Boolean valid;
	private String value;

	public Option(String name, EditorConfig editorConfig) {
		this.name = name;
		this.editorConfig = editorConfig;
		this.type = editorConfig.getRegistry().getType(name);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
		this.valid = null;
	}

	@Override
	public String toString() {
		return new StringBuilder(name).append(" = ").append(value).toString();
	}

	public int getIntValue() {
		return Integer.parseInt(getValue());
	}

	public boolean getBooleanValue() {
		return Boolean.parseBoolean(getValue());
	}

	public boolean isValid() {
		if (valid == null) {
			valid = computeValid();
		}
		return valid;
	}

	private Boolean computeValid() {
		OptionType<?> type = getType();
		if (type == null) {
			return false;
		}
		try {
			type.validate(value);
		} catch (OptionException e) {
			return false;
		}
		return true;
	}

	public OptionType<?> getType() {
		return type;
	}

	public boolean checkMax() {
		if (name != null && name.length() > 50) {
			return false;
		}
		if (value != null && value.length() > 255) {
			return false;
		}
		return true;
	}

}
