package org.eclipse.ec4j.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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
