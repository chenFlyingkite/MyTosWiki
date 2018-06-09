package com.flyingkite.util;

import com.flyingkite.library.FilesHelper;
import com.flyingkite.library.IOUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileUtil {
    private FileUtil() {}

    public static List<String> readFromFile(String name) {
        return readFromFile(new File(name));
    }

    public static List<String> readFromFile(File file) {
        if (FilesHelper.isGone(file)){
            return Collections.emptyList();
        }

        List<String> contents = new ArrayList<>();
        BufferedReader br = null;
        InputStreamReader is = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            is = new InputStreamReader(fis, "UTF-8");
            br = new BufferedReader(is);

            String line;
            while ((line = br.readLine()) != null) {
                contents.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeIt(fis, is, br);
        }
        return contents;
    }
}
