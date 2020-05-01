package com.flyingkite.mytoswiki.util;

public class StringUtil3 {

    public static String find(String src, int pos, String head, String tail) {
        int a = src.indexOf(head, pos);
        if (a < 0) return "";
        int b = src.indexOf(tail, a);

        return src.substring(a + head.length(), b);
    }
}
