package com.flyingkite.util.lib;

import com.flyingkite.library.log.Loggable;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class Gsons {

    public static <T> T from(String src, Class<T> clazz) {
        Gson g = new Gson();
        T ans = null;
        try {
            ans = g.fromJson(src, clazz);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            z.logE("login fail for %s", src);
        }
        return ans;
    }

    private static final Loggable z = new Loggable() {
        @Override
        public String LTag() {
            return "Gsons";
        }
    };
}
