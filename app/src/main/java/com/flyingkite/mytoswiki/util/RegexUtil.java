package com.flyingkite.mytoswiki.util;

import java.util.List;

public class RegexUtil {

    public static String toRegexOr(List<String> keys) {
        return join("(", ")", "|", keys);
    }

    public static String join(String prefix, String suffix, String delim, List<String> keys) {
        if (keys.isEmpty()) return "";

        StringBuilder s = new StringBuilder(prefix);
        for (int i = 0; i < keys.size(); i++) {
            if (i > 0) {
                s.append(delim);
            }
            s.append(keys.get(i));
        }
        s.append(suffix);
        return s.toString();
    }
}
