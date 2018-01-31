package com.flyingkite.mytoswiki;

import android.content.res.AssetManager;

import com.flyingkite.library.IOUtil;
import com.flyingkite.library.Say;
import com.flyingkite.library.ThreadUtil;
import com.flyingkite.library.TicTac;
import com.flyingkite.library.TicTac2;
import com.flyingkite.mytoswiki.data.TosCard;
import com.flyingkite.mytoswiki.wikia.articles.UnexpandedArticle;
import com.flyingkite.mytoswiki.wikia.articles.UnexpandedListArticleResultSet;
import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class TosWiki {
    private static final String cardJson = "card.json";
    private static final TosWiki me = new TosWiki();

    private TosWiki() {}

    public static TosCard[] parseCards(AssetManager am) {
        Say.Log("parseCards");
        Gson gson = new Gson();
        Reader reader = null;
        TosCard[] cards = null;
        try {
            reader = getReader(cardJson, am);
            if (reader != null) {
                cards = gson.fromJson(reader, TosCard[].class);
            }

            Say.Log("%s cards", cards == null ? 0 : cards.length);
        } finally {
            IOUtil.closeIt(reader);
        }
        return cards;
    }

    private static InputStreamReader getReader(String assetFile, AssetManager am) {
        try {
            return new InputStreamReader(am.open(assetFile), "UTF-8");
            //return new InputStreamReader(new FileInputStream(file), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static InputStreamReader getReader(File file) {
        try {
            return new InputStreamReader(new FileInputStream(file), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private static InputStreamReader getReader(InputStream is) {
        try {
            return new InputStreamReader(is, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void ff() {
        ThreadUtil.runOnWorkerThread(() -> {
            TicTac.tic();
            f();
            TicTac.tac("f is done");
        });
    }

    private static void f() {
        //if (true) return;
        OkHttpClient client = new OkHttpClient();
        String tosApi = "http://zh.tos.wikia.com/api/v1/Articles/List?limit=2500000";

        Request request = new Request.Builder().url(tosApi).build();

        TicTac2 t = new TicTac2();
        Gson gson = new Gson();

        Response response;
        ResponseBody body;
        try {
            t.tic();
            response = client.newCall(request).execute();
            t.tac("response = %s", response);
            t.tic();
            body = response.body();
            t.tac("body = %s", body);
            if (body == null) return;

            t.tic();
            ResultSet set = gson.fromJson(body.string(), ResultSet.class);
            //UnexpandedListArticleResultSet set = gson.fromJson(body.string(), UnexpandedListArticleResultSet.class);

            t.tac("from gson, %s", set);


            String jsoupLink = null;
            if (set != null && set.getItems() != null) {
                int max = 10;
                max = set.getItems().length;
                for (int i = 0; i < max; i++) {
                    UnexpandedArticle a = set.getItems()[i];
                    String link = set.getBasePath() + "" + a.getUrl();
                    Say.Log("#%02d -> %s, %s", i, link, set.getItems()[i]);
                    //if (i == 5) {
                        jsoupLink = link;
                    //}
                    //--

                    Say.Log("---------------------");
                    Say.Log("jsoup link = %s", jsoupLink);
                    Document doc = Jsoup.connect(jsoupLink).get();
                    Say.Log("Title = %s, Children = %s", doc.title(), doc.getAllElements().size());
                    Elements centers = doc.getElementsByTag("center");
                    int n = centers.size();
                    Say.Log("%s centers", centers.size());
                /*
                for (Element e : centers) {
                    Say.Log("e = %s", e);
                }
                */

                    if (n > 0) {
                        Element e = centers.get(0);
                        Elements nos = e.getElementsByTag("noscript");
                        Element x;
                        Elements imgs;
                        if (nos != null && nos.size() > 0) {
                            x = nos.get(0);
                            imgs = x.getElementsByTag("img");
                            if (imgs != null && imgs.size() > 0) {
                                Element en = imgs.get(0);
                                if (en != null) {
                                    String ig = en.attr("src");
                                    Say.Log("image #1 = %s", ig);
                                }
                            }
                        }
                    }

                    // 1
                    if (n > 1) {
                        Element e = centers.get(1);
                        Elements nos = e.getElementsByTag("noscript");
                        Element x;
                        Elements imgs;
                        if (nos != null && nos.size() > 0) {
                            x = nos.get(0);
                            imgs = x.getElementsByTag("img");
                            if (imgs != null && imgs.size() > 0) {
                                Element en = imgs.get(0);
                                if (en != null) {
                                    String ig = en.attr("src");
                                    Say.Log("image #2 = %s", ig);
                                }
                            }
                        }
                    }
                    Say.Log("---------------------");
                }
            }

            Say.Log("--------------------- xxxx -----");

            if (jsoupLink != null) {
                //Say.Log("jsoup link = %s", jsoupLink);
                Document doc = Jsoup.connect(jsoupLink).get();
                //Say.Log("Title = %s, Children = %s", doc.title(), doc.getAllElements().size());
                Elements centers = doc.getElementsByTag("center");
                //Elements centers = doc.select("<center>");
                int n = centers.size();
                //Say.Log("%s centers", centers.size());
                /*
                for (Element e : centers) {
                    Say.Log("e = %s", e);
                }
                */

                if (n > 0) {
                    Element e = centers.get(0);
                    Elements nos = e.getElementsByTag("noscript");
                    Element x;
                    Elements imgs;
                    if (nos != null && nos.size() > 0) {
                        x = nos.get(0);
                        imgs = x.getElementsByTag("img");
                        if (imgs != null && imgs.size() > 0) {
                            Element en = imgs.get(0);
                            if (en != null) {
                                String ig = en.attr("src");
                                Say.Log("image #1 = %s", ig);
                            }
                        }
                    }
                }

                // 1
                if (n > 1) {
                    Element e = centers.get(1);
                    Elements nos = e.getElementsByTag("noscript");
                    Element x;
                    Elements imgs;
                    if (nos != null && nos.size() > 0) {
                        x = nos.get(0);
                        imgs = x.getElementsByTag("img");
                        if (imgs != null && imgs.size() > 0) {
                            Element en = imgs.get(0);
                            if (en != null) {
                                String ig = en.attr("src");
                                Say.Log("image #2 = %s", ig);
                            }
                        }
                    }
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // class abbreviation
    private class ResultSet extends UnexpandedListArticleResultSet {
    }
}



