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
                //doc = OkHttpUtil.getByOkHttp(link);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        ts.tac("JSoup OK " + link);
        return doc;
    }

}
