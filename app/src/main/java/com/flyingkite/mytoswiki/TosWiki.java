package com.flyingkite.mytoswiki;

import android.content.res.AssetManager;

import com.flyingkite.library.IOUtil;
import com.flyingkite.library.Say;
import com.flyingkite.library.TicTac2;
import com.flyingkite.mytoswiki.data.TosCard;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class TosWiki {
    private TosWiki() {}

    public static TosCard[] parseCards(AssetManager am) {
        final String assetName = "cardList.json";
        Say.Log("parsing Cards");
        TicTac2 clk = new TicTac2();

        Gson gson = new Gson();
        Reader reader = null;
        TosCard[] cards = null;
        try {
            reader = getReader(assetName, am);
            if (reader == null) {
                Say.Log("reader not found, %s", assetName);
            } else {
                clk.tic();
                cards = gson.fromJson(reader, TosCard[].class);
                int n = cards == null ? 0 : cards.length;
                clk.tac("%s cards read", n);
            }
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
