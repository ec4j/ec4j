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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.ec4j.ResourceProvider;

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

	public boolean match(String filePath) {
		if (regex == null) {
			String configDirname = editorConfig.getDirPath();
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
