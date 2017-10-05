package org.eclipse.ec4j.model.optiontypes;

public enum OptionNames {

	indent_style, indent_size, tab_width, end_of_line, charset, trim_trailing_whitespace, root, insert_final_newline, unknown;

	public static OptionNames get(String name) {
		try {
			return OptionNames.valueOf(name.toLowerCase());
		} catch (Exception e) {
			return unknown;
		}
	}
}
