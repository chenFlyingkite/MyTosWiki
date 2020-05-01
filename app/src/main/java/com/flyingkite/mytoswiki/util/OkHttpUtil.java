package com.flyingkite.mytoswiki.util;

import java.io.IOException;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

public class OkHttpUtil {

    public static String getResponse(String link, Map<String, String> map) {
        OkHttpClient c = new OkHttpClient();
        HttpUrl url = HttpUrl.parse(link);
        if (url == null) {
            return "";
        }
        // Append parameters
        HttpUrl.Builder ub = url.newBuilder();
        if (map != null) {
            for (String key : map.keySet()) {
                String value = map.get(key);
                ub.addQueryParameter(key, value);
            }
        }

        Request r = new Request.Builder().url(ub.build()).build();
        ResponseBody rb = null;
        try {
            rb = c.newCall(r).execute().body();
            if (rb != null) {
                return rb.string();
            } else {
                return "";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
//
//    private Document getByOkHttp(String link) {
//        OkHttpClient c = new OkHttpClient();
//        Request r = new Request.Builder().url(link).build();
//        ResponseBody rb = null;
//        try {
//            rb = c.newCall(r).execute().body();
//            if (rb != null) {
//                return Jsoup.parse(rb.string());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}
