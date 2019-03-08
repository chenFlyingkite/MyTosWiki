package com.flyingkite.mytoswiki.util;

import java.util.List;

public class RegexUtil {

    public static String toRegexOr(List<String> keys) {
        if (keys.isEmpty()) return "";

        StringBuilder s = new StringBuilder("(");
        for (int i = 0; i < keys.size(); i++) {
            if (i > 0) {
                s.append("|");
            }
            s.append(keys.get(i));
        }
        s.append(")");
        return s.toString();
    }
}
