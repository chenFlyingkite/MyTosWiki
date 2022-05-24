package com.flyingkite.mytoswiki.util;

//
//public class UrlUtil {
//
//    public static String decodeURL(String s) {
//        try {
//            return URLDecoder.decode(s, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        return s;
//    }
//
//    public static String liteLink(String link) {
//        String s = decodeURL(link);
//        int x = s.lastIndexOf("/");
//        if (MathUtil.isInRange(x, 0, s.length() - 1)) {
//            return s.substring(x + 1);
//        } else {
//            return s;
//        }
//    }
//}
