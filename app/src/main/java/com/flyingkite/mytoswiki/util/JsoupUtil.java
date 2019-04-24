package com.flyingkite.mytoswiki.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import flyingkite.tool.TicTac2;

public interface JsoupUtil {

    default Document getDocument(String link) {
        return getDocument(link, false);
    }

    default Document getDocument(String link, boolean logTime) {
        // Step 1: Get the xml node from link by Jsoup
        Document doc = null;
        TicTac2 ts = new TicTac2();
        ts.enable(logTime);
        ts.tic();
        try {
            if (true) {
                doc = Jsoup.connect(link).timeout(60_000).maxBodySize(0).get();
            } else {
                //doc = getByOkHttp(link);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        ts.tac("JSoup OK " + link);
        return doc;
    }

    default String find(String src, int pos, String head, String tail) {
        int a = src.indexOf(head, pos);
        if (a < 0) return "";
        int b = src.indexOf(tail, a);

        return src.substring(a + head.length(), b);
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
