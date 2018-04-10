package com.flyingkite.util;

import android.content.res.AssetManager;

import com.flyingkite.library.IOUtil;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;

public class GsonUtil {
    public static void writeFile(File file, String msg) {
        file.delete();
        PrintWriter fos = null;
        try {
            fos = new PrintWriter(file);
            fos.print(msg);
            fos.flush();
        } catch (IOException e) {
            IOUtil.closeIt(fos);
        }
    }

    public static <T> T loadFile(File file, Class<T> clazz) {
        return load(getReader(file), clazz);
    }

    public static <T> T loadAsset(String assetFile, Class<T> clazz, AssetManager am) {
        return load(getReader(assetFile, am), clazz);
    }

    private static <T> T load(Reader reader, Class<T> clazz) {
        Gson gson = new Gson();
        try {
            if (reader != null) {
                return gson.fromJson(reader, clazz);
            }
        } finally {
            IOUtil.closeIt(reader);
        }
        return null;
    }

    private static InputStreamReader getReader(String assetFile, AssetManager am) {
        try {
            return new InputStreamReader(am.open(assetFile), "UTF-8");
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
