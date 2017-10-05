/**
 *  Copyright (c) 2017 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.ec4j.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Section {

	private final EditorConfig editorConfig;
	private final List<String> patterns;
	private final List<Option> options;

	private Pattern regex;
	private List<int[]> ranges;

	public Section(EditorConfig editorConfig) {
		this.editorConfig = editorConfig;
		this.patterns = new ArrayList<>();
		this.options = new ArrayList<>();
	}

	public boolean match(File file) {
		return match(file.toString().replaceAll("[\\\\]", "/"));
	}

	public void addOption(Option option) {
		options.add(option);
	}

	public List<Option> getOptions() {
		return options;
	}

	public void addPattern(String pattern) {
		patterns.add(pattern);
	}

	public List<String> getPatterns() {
		return patterns;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		// patterns
		List<String> patterns = this.getPatterns();
		if (!patterns.isEmpty()) {
			s.append("[");
			s.append(toString(patterns));
			s.append("]\n");
		}
		// options
		int i = 0;
		for (Option option : this.getOptions()) {
			if (i > 0) {
				s.append("\n");
			}
			s.append(option.toString());
			i++;
		}
		return s.toString();
	}

	private String toString(List<String> patterns) {
		StringBuilder s = new StringBuilder();
		if (patterns.size() == 1) {
			s.append(patterns.get(0));
		} else {
			int i = 0;
			s.append("{");
			for (String pattern : patterns) {
				if (i > 0) {
					s.append(",");
				}
				s.append(pattern);
				i++;
			}
			s.append("}");
		}
		return s.toString();
	}

	private boolean match(String filePath) {
		if (regex == null) {
			String configDirname = editorConfig.getConfigFile().toString();
			String pattern = toString(patterns);
			pattern = pattern.replace(File.separatorChar, '/');
			pattern = pattern.replaceAll("\\\\#", "#");
			pattern = pattern.replaceAll("\\\\;", ";");
			int separator = pattern.indexOf("/");
			if (separator >= 0) {
				pattern = configDirname.replace(File.separatorChar, '/')
						+ (separator == 0 ? pattern.substring(1) : pattern);
			} else {
				pattern = "**/" + pattern;
			}
			ranges = new ArrayList<int[]>();
			final String regex = RegexpUtils.convertGlobToRegEx(pattern, ranges);
			this.regex = Pattern.compile(regex);
		}
		final Matcher matcher = regex.matcher(filePath);
		if (matcher.matches()) {
			for (int i = 0; i < matcher.groupCount(); i++) {
				final int[] range = ranges.get(i);
				final String numberString = matcher.group(i + 1);
				if (numberString == null || numberString.startsWith("0"))
					return false;
				int number = Integer.parseInt(numberString);
				if (number < range[0] || number > range[1])
					return false;
			}
			return true;
		}
		return false;
	}
}
