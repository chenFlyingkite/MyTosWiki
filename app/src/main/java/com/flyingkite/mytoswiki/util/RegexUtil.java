package com.flyingkite.mytoswiki.util;

import android.text.TextUtils;

import java.util.List;

public class RegexUtil {

    public static String toRegexOr(List<String> keys) {
        return join("(", ")", "|", keys);
    }

    public static String join(String prefix, String suffix, String delim, List<String> keys) {
        if (keys.isEmpty()) return "";

        StringBuilder s = new StringBuilder();
        s.append(prefix);
        s.append(TextUtils.join(delim, keys));
        s.append(suffix);
        return s.toString();
    }
}
