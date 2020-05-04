package com.flyingkite.mytoswiki.util;

import android.text.TextUtils;

public class StringUtil3 {

    // Find specific string started with <head>, end with <tail>
    // from the src and begin with pos
    public static String find(String src, int pos, String head, String tail) {
        if (TextUtils.isEmpty(src)) {
            return "";
        }
        int a = src.indexOf(head, pos);
        if (a < 0) return "";
        int b = src.indexOf(tail, a);

        return src.substring(a + head.length(), b);
    }
}
