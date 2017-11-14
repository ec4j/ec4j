/**
 * Copyright (c) 2017 Angelo Zerr and other contributors as
 * indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ec4j.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * An EditorConfig glob. Citing from <a href="http://editorconfig.org/">:
 * <p>
 * Special characters recognized in section names for wildcard matching:
 * <ul>
 * <li>* Matches any string of characters, except path separators (/)</li>
 * <li>** Matches any string of characters</li>
 * <li>? Matches any single character</li>
 * <li>[name] Matches any single character in name</li>
 * <li>[!name] Matches any single character not in name</li>
 * <li>{s1,s2,s3} Matches any of the strings given (separated by commas) (Available since EditorConfig Core 0.11.0)</li>
 * <li>{num1..num2} Matches any integer numbers between num1 and num2, where num1 and num2 can be either positive or
 * negative</li>
 * </ul>
 * <p>
 * Methods in this class stating so were copied from <a href=
 * "https://github.com/editorconfig/editorconfig-core-java/blob/e3e090545f44d20f5f228ef1068af4c9d7323a51/src/main/java/org/editorconfig/core/EditorConfig.java">EditorConfig</a>
 * by Dennis Ushakov.
 *
 * @author Dennis Ushakov
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public class Glob {

    private static final int MAX_GLOB_LENGTH = 4096;
    private final PatternSyntaxException error;
    private final List<int[]> ranges;
    private final Pattern regex;
    private final String source;
    static final Pattern ESCAPED_COMMENT_SIGNS = Pattern.compile("\\\\([#;])");

    public Glob(String configDirname, String pattern) {
        this.source = pattern;
        this.ranges = new ArrayList<int[]>();
        if (pattern.length() > MAX_GLOB_LENGTH) {
            this.regex = null;
            this.error = new PatternSyntaxException(
                    "Glob length exceeds the maximal allowed length of " + MAX_GLOB_LENGTH + " characters", pattern,
                    MAX_GLOB_LENGTH);
        } else {
            //pattern = pattern.replace(File.separatorChar, '/');
            pattern = ESCAPED_COMMENT_SIGNS.matcher(pattern).replaceAll("$1");
            int slashPos = pattern.indexOf('/');
            if (slashPos >= 0) {
                pattern = configDirname + "/" + (slashPos == 0 ? pattern.substring(1) : pattern);
            } else {
                pattern = "**/" + pattern;
            }
            final StringBuilder regex = new StringBuilder(pattern.length());
            convertGlobToRegEx(pattern, ranges, regex);
            PatternSyntaxException err = null;
            Pattern pat = null;
            try {
                pat = Pattern.compile(regex.toString());
            } catch (PatternSyntaxException e) {
                err = e;
            }
            this.error = err;
            this.regex = pat;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Glob other = (Glob) obj;
        if (source == null) {
            if (other.source != null)
                return false;
        } else if (!source.equals(other.source))
            return false;
        return true;
    }

    /**
     * @return the {@link PatternSyntaxException} that was thrown when parsing the {@link #source} or {@code null} when
     *         no {@link PatternSyntaxException} was thrown.
     */
    public PatternSyntaxException getError() {
        return error;
    }

    /**
     * @return the glob string out of which this {@link Glob} was constructed
     */
    public String getSource() {
        return source;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        return result;
    }

    public boolean isEmpty() {
        return source.isEmpty();
    }

    public boolean isValid() {
        return error == null;
    }

    /**
     * Matches the given slash ({@code /}) separated path against this {@link Glob}.
     * <p>
     * Based on <a href=
     * "https://github.com/editorconfig/editorconfig-core-java/blob/e3e090545f44d20f5f228ef1068af4c9d7323a51/src/main/java/org/editorconfig/core/EditorConfig.java#L242">EditorConfig</a>
     * by Dennis Ushakov.
     *
     * @param filePath
     *            a slash ({@code /}) separated file path to match against this {@link Glob}
     * @return {@code true} if the given {@code filePath} matches; {@code false} otherwise
     */
    public boolean match(String filePath) {
        if (!isValid()) {
            return false;
        }
        final Matcher matcher = regex.matcher(filePath);
        if (matcher.matches()) {
            for (int i = 0; i < matcher.groupCount(); i++) {
                final int[] range = ranges.get(i);
                final String numberString = matcher.group(i + 1);
                if (numberString == null || numberString.startsWith("0")) {
                    return false;
                }
                int number = Integer.parseInt(numberString);
                if (number < range[0] || number > range[1]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * @return the glob string out of which this {@link Glob} was constructed
     */
    @Override
    public String toString() {
        return source;
    }

    /**
     * Copied from <a href=
     * "https://github.com/editorconfig/editorconfig-core-java/blob/e3e090545f44d20f5f228ef1068af4c9d7323a51/src/main/java/org/editorconfig/core/EditorConfig.java#L256">EditorConfig</a>
     * by Dennis Ushakov.
     */
    static void convertGlobToRegEx(final String globString, List<int[]> ranges, final StringBuilder result) {
        int length = globString.length();
        int i = 0;
        int braceLevel = 0;
        boolean matchingBraces = matchingBraces(globString);
        boolean escaped = false;
        boolean inBrackets = false;
        while (i < length) {
            char current = globString.charAt(i);
            i++;
            if ('*' == current) {
                if (i < length && globString.charAt(i) == '*') {
                    result.append(".*");
                    i++;
                } else {
                    result.append("[^/]*");
                }
            } else if ('?' == current) {
                result.append(".");
            } else if ('[' == current) {
                boolean seenSlash = findChar('/', ']', globString, length, i) >= 0;
                if (seenSlash || escaped) {
                    result.append("\\[");
                } else if (i < length && "!^".indexOf(globString.charAt(i)) >= 0) {
                    i++;
                    result.append("[^");
                } else {
                    result.append("[");
                }
                inBrackets = true;
            } else if (']' == current || ('-' == current && inBrackets)) {
                if (escaped) {
                    result.append("\\");
                }
                result.append(current);
                inBrackets = current != ']' || escaped;
            } else if ('{' == current) {
                int j = findChar(',', '}', globString, length, i);
                if (j < 0 && -j < length) {
                    final String choice = globString.substring(i, -j);
                    final int[] range = getNumericRange(choice);
                    if (range != null) {
                        result.append("(\\d+)");
                        ranges.add(range);
                    } else {
                        result.append("\\{");
                        convertGlobToRegEx(choice, ranges, result);
                        result.append("\\}");
                    }
                    i = -j + 1;
                } else if (matchingBraces) {
                    result.append("(?:");
                    braceLevel++;
                } else {
                    result.append("\\{");
                }
            } else if (',' == current) {
                result.append(braceLevel > 0 && !escaped ? "|" : ",");
            } else if ('/' == current) {
                if (i < length && globString.charAt(i) == '*') {
                    if (i + 1 < length && globString.charAt(i + 1) == '*' && i + 2 < length
                            && globString.charAt(i + 2) == '/') {
                        result.append("(?:/|/.*/)");
                        i += 3;
                    } else {
                        result.append(current);
                    }
                } else {
                    result.append(current);
                }
            } else if ('}' == current) {
                if (braceLevel > 0 && !escaped) {
                    result.append(")");
                    braceLevel--;
                } else {
                    result.append("}");
                }
            } else if ('\\' != current) {
                escapeToRegex(current, result);
            }
            if ('\\' == current) {
                if (escaped)
                    result.append("\\\\");
                escaped = !escaped;
            } else {
                escaped = false;
            }
        }
    }

    /**
     * @param globString the glob string to check
     * @return {@code true} if the count of opening braces is equal to the count of the closing braces; {@code false}
     *         otherwise
     */
    static boolean matchingBraces(String globString) {
        int i = 0;
        final int len = globString.length();
        int openedCount = 0;
        while (i < len) {
            switch (globString.charAt(i++)) {
            case '\\':
                i++;
                break;
            case '{':
                openedCount++;
                break;
            case '}':
                openedCount--;
                break;
            default:
                break;
            }
        }
        return openedCount == 0;
    }

    /**
     * Copied from <a href=
     * "https://github.com/editorconfig/editorconfig-core-java/blob/e3e090545f44d20f5f228ef1068af4c9d7323a51/src/main/java/org/editorconfig/core/EditorConfig.java#L349">EditorConfig</a>
     * by Dennis Ushakov.
     */
    static int[] getNumericRange(String choice) {
        final int separator = choice.indexOf("..");
        if (separator < 0)
            return null;
        try {
            int start = Integer.parseInt(choice.substring(0, separator));
            int end = Integer.parseInt(choice.substring(separator + 2));
            return new int[] { start, end };
        } catch (NumberFormatException ignored) {
        }
        return null;
    }

    /**
     * Copied from <a href=
     * "https://github.com/editorconfig/editorconfig-core-java/blob/e3e090545f44d20f5f228ef1068af4c9d7323a51/src/main/java/org/editorconfig/core/EditorConfig.java#L360">EditorConfig</a>
     * by Dennis Ushakov.
     */
    static int findChar(final char c, final char stopAt, String pattern, int length, int start) {
        int j = start;
        boolean escapedChar = false;
        while (j < length && (pattern.charAt(j) != stopAt || escapedChar)) {
            if (pattern.charAt(j) == c && !escapedChar) {
                return j;
            }
            escapedChar = pattern.charAt(j) == '\\' && !escapedChar;
            j++;
        }
        return -j;
    }

    /**
     * Copied from <a href=
     * "https://github.com/editorconfig/editorconfig-core-java/blob/e3e090545f44d20f5f228ef1068af4c9d7323a51/src/main/java/org/editorconfig/core/EditorConfig.java#L373">EditorConfig</a>
     * by Dennis Ushakov.
     */
    static void escapeToRegex(char c, StringBuilder result) {
        if (c == ' ' || Character.isLetter(c) || Character.isDigit(c) || c == '_' || c == '-') {
            result.append(c);
        } else if (c == '\n') {
            result.append("\\n");
        } else {
            result.append('\\').append(c);
        }
    }
}
