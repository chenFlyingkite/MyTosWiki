package com.flyingkite.mytoswiki;

import android.content.res.AssetManager;

import com.flyingkite.library.IOUtil;
import com.flyingkite.library.Say;
import com.flyingkite.mytoswiki.data.TosCard;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

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
}
