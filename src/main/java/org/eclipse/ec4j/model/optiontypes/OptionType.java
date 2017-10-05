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
		public ValueParser<IndentStyleOption> getValueParser() {
			return new EnumValueParser<IndentStyleOption>(IndentStyleOption.class);
		}

		@Override
		public ValueValidator<IndentStyleOption> getValueValidator() {
			return new EnumValueValidator<IndentStyleOption>(IndentStyleOption.class);
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
		public ValueParser<Integer> getValueParser() {
			return ValueParser.POSITIVE_INT_VALUE_PARSER;
		}

		@Override
		public ValueValidator<Integer> getValueValidator() {
			return ValueValidator.POSITIVE_INT_VALUE_VALIDATOR;
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
		public ValueParser<Integer> getValueParser() {
			return ValueParser.POSITIVE_INT_VALUE_PARSER;
		}

		@Override
		public ValueValidator<Integer> getValueValidator() {
			return ValueValidator.POSITIVE_INT_VALUE_VALIDATOR;
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
		public ValueParser<EndOfLineOption> getValueParser() {
			return new EnumValueParser<EndOfLineOption>(EndOfLineOption.class);
		}

		@Override
		public ValueValidator<EndOfLineOption> getValueValidator() {
			return new EnumValueValidator<EndOfLineOption>(EndOfLineOption.class);
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
		public ValueParser<String> getValueParser() {
			return ValueParser.IDENTITY_VALUE_PARSER;
		}

		@Override
		public ValueValidator<String> getValueValidator() {
			return ValueValidator.IDENTITY_VALUE_VALIDATOR;
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
		public ValueParser<Boolean> getValueParser() {
			return ValueParser.BOOLEAN_VALUE_PARSER;
		}

		@Override
		public ValueValidator<Boolean> getValueValidator() {
			return ValueValidator.BOOLEAN_VALUE_VALIDATOR;
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
		public ValueParser<Boolean> getValueParser() {
			return ValueParser.BOOLEAN_VALUE_PARSER;
		}

		@Override
		public ValueValidator<Boolean> getValueValidator() {
			return ValueValidator.BOOLEAN_VALUE_VALIDATOR;
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
		public ValueParser<Boolean> getValueParser() {
			return ValueParser.BOOLEAN_VALUE_PARSER;
		}

		@Override
		public ValueValidator<Boolean> getValueValidator() {
			return ValueValidator.BOOLEAN_VALUE_VALIDATOR;
		}

		@Override
		public String[] getPossibleValues() {
			return BOOLEAN_POSSIBLE_VALUES;
		}
	}

	public abstract String getName();

	public abstract String getDescription();

	public abstract ValueParser<T> getValueParser();

	public abstract ValueValidator<T> getValueValidator();

	public void validate(String value) throws OptionException {
		getValueValidator().validate(getName(), value);
	}

	public abstract String[] getPossibleValues();

	@Override
	public String toString() {
		return getName();
	}

}
