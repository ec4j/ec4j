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

public abstract class OptionType<T> {

	private static final String[] BOOLEAN_POSSIBLE_VALUES = new String[] { "true", "false" };

	public static class IndentStyle extends OptionType<IndentStyleOption> {

		private static final String[] POSSIBLE_VALUES = new String[] { "tab", "space" };

		@Override
		public String getName() {
			return OptionNames.indent_style.name();
		}

		@Override
		public String getDescription() {
			return "set to tab or space to use hard tabs or soft tabs respectively.";
		}

		@Override
		public OptionValueParser<IndentStyleOption> getValueParser() {
			return new EnumValueParser<IndentStyleOption>(IndentStyleOption.class);
		}

		@Override
		public String[] getPossibleValues() {
			return POSSIBLE_VALUES;
		}

	}

	public static class IndentSize extends OptionType<Integer> {

		private static final String[] POSSIBLE_VALUES = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "tab" };

		@Override
		public String getName() {
			return OptionNames.indent_size.name();
		}

		@Override
		public String getDescription() {
			return "a whole number defining the number of columns used for each indentation level and the width of soft tabs (when supported). When set to tab, the value of tab_width (if specified) will be used.";
		}

		@Override
		public OptionValueParser<Integer> getValueParser() {
			return OptionValueParser.POSITIVE_INT_VALUE_PARSER;
		}

		@Override
		public String[] getPossibleValues() {
			return POSSIBLE_VALUES;
		}

	}

	public static class TabWidth extends OptionType<Integer> {

		private static final String[] POSSIBLE_VALUES = new String[] { "1", "2", "3", "4", "5", "6", "7", "8" };

		@Override
		public String getName() {
			return OptionNames.tab_width.name();
		}

		@Override
		public String getDescription() {
			return "a whole number defining the number of columns used to represent a tab character. This defaults to the value of indent_size and doesn't usually need to be specified.";
		}

		@Override
		public OptionValueParser<Integer> getValueParser() {
			return OptionValueParser.POSITIVE_INT_VALUE_PARSER;
		}

		@Override
		public String[] getPossibleValues() {
			return POSSIBLE_VALUES;
		}
	}

	public static class EndOfLine extends OptionType<EndOfLineOption> {

		private static final String[] POSSIBLE_VALUES = new String[] { "lf", "crlf", "cr" };

		@Override
		public String getName() {
			return OptionNames.end_of_line.name();
		}

		@Override
		public String getDescription() {
			return "set to lf, cr, or crlf to control how line breaks are represented.";
		}

		@Override
		public OptionValueParser<EndOfLineOption> getValueParser() {
			return new EnumValueParser<EndOfLineOption>(EndOfLineOption.class);
		}

		@Override
		public String[] getPossibleValues() {
			return POSSIBLE_VALUES;
		}
	}

	public static class Charset extends OptionType<String> {

		private static final String[] POSSIBLE_VALUES = new String[] { "utf-8", "utf-8-bom", "utf-16be", "utf-16le",
				"latin1", "tab" };

		@Override
		public String getName() {
			return OptionNames.charset.name();
		}

		@Override
		public String getDescription() {
			return "set to latin1, utf-8, utf-8-bom, utf-16be or utf-16le to control the character set. Use of utf-8-bom is discouraged.";
		}

		@Override
		public OptionValueParser<String> getValueParser() {
			return OptionValueParser.IDENTITY_VALUE_PARSER;
		}

		@Override
		public String[] getPossibleValues() {
			return POSSIBLE_VALUES;
		}

	}

	public static class TrimTrailingWhitespace extends OptionType<Boolean> {

		@Override
		public String getName() {
			return OptionNames.trim_trailing_whitespace.name();
		}

		@Override
		public String getDescription() {
			return "set to true to remove any whitespace characters preceding newline characters and false to ensure it doesn't.";
		}

		@Override
		public OptionValueParser<Boolean> getValueParser() {
			return OptionValueParser.BOOLEAN_VALUE_PARSER;
		}

		@Override
		public String[] getPossibleValues() {
			return BOOLEAN_POSSIBLE_VALUES;
		}
	}

	public static class InsertFinalNewline extends OptionType<Boolean> {

		@Override
		public String getName() {
			return OptionNames.insert_final_newline.name();
		}

		@Override
		public String getDescription() {
			return "set to true to ensure file ends with a newline when saving and false to ensure it doesn't.";
		}

		@Override
		public OptionValueParser<Boolean> getValueParser() {
			return OptionValueParser.BOOLEAN_VALUE_PARSER;
		}

		@Override
		public String[] getPossibleValues() {
			return BOOLEAN_POSSIBLE_VALUES;
		}
	}

	public static class Root extends OptionType<Boolean> {

		@Override
		public String getName() {
			return OptionNames.root.name();
		}

		@Override
		public String getDescription() {
			return "special property that should be specified at the top of the file outside of any sections. Set to true to stop .editorconfig files search on current file.";
		}

		@Override
		public OptionValueParser<Boolean> getValueParser() {
			return OptionValueParser.BOOLEAN_VALUE_PARSER;
		}

		@Override
		public String[] getPossibleValues() {
			return BOOLEAN_POSSIBLE_VALUES;
		}
	}

	public abstract String getName();

	public abstract String getDescription();

	public abstract OptionValueParser<T> getValueParser();

	public void validate(String value) throws OptionException {
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
