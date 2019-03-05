package com.flyingkite.mytoswiki.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class UrlUtil {

    public static String decodeURL(String s) {
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return s;
    }
}
