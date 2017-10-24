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
package org.eclipse.ec4j.core.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
public class Glob {

    private final List<int[]> ranges;
    private Pattern regex;
    private PatternSyntaxException error;

    public Glob(String configDirname, String pattern) {
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
        try {
            this.regex = Pattern.compile(regex);
        } catch (PatternSyntaxException e) {
            this.error = e;
        }
    }

    public boolean match(String filePath) {
        if (!isValid()) {
            return false;
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

    public boolean isValid() {
        return getError() == null;
    }

    public PatternSyntaxException getError() {
        return error;
    }
}
