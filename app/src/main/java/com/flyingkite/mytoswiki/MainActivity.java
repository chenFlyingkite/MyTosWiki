package com.flyingkite.mytoswiki;

import android.content.res.AssetManager;
import android.os.Bundle;

import com.flyingkite.library.IOUtil;
import com.flyingkite.library.Say;
import com.flyingkite.mytoswiki.data.TosCard;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class MainActivity extends BaseActivity {
    private static final String cardJson = "card.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parseCards();
    }

    private TosCard[] parseCards() {
        Say.Log("parseCards");
        Gson gson = new Gson();
        Reader reader = null;
        TosCard[] cards = null;
        try {
            reader = getReader(cardJson);
            if (reader != null) {
                cards = gson.fromJson(reader, TosCard[].class);
            }

            Say.Log("%s cards", cards == null ? 0 : cards.length);
        } finally {
            IOUtil.closeIt(reader);
        }
        return cards;
    }


    private InputStreamReader getReader(String assetFile) {
        AssetManager am = getAssets();
        try {
            return new InputStreamReader(am.open(assetFile), "UTF-8");
            //return new InputStreamReader(new FileInputStream(file), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private InputStreamReader getReader(File file) {
        try {
            return new InputStreamReader(new FileInputStream(file), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
