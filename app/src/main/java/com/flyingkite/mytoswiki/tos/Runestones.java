package com.flyingkite.mytoswiki.tos;

import com.flyingkite.mytoswiki.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Runestones {
    // 水火木光暗心/強化
    public static final String all = "wfeldhWFELDH";

    public static int imageId(char c) {
        int id = 0;
        switch (c) {
            // normal
            case 'w': id = R.drawable.stone_w; break;
            case 'f': id = R.drawable.stone_f;  break;
            case 'e': id = R.drawable.stone_e; break;
            case 'l': id = R.drawable.stone_l; break;
            case 'd': id = R.drawable.stone_d;  break;
            case 'h': id = R.drawable.stone_h; break;
            // enchanted
            case 'W': id = R.drawable.stone_w_en; break;
            case 'F': id = R.drawable.stone_f_en; break;
            case 'E': id = R.drawable.stone_e_en; break;
            case 'L': id = R.drawable.stone_l_en; break;
            case 'D': id = R.drawable.stone_d_en; break;
            case 'H': id = R.drawable.stone_h_en; break;
        }
        return id;
    }

    public static String random(int n) {
        StringBuilder s = new StringBuilder();
        Random r = new Random();
        char[] c = all.toCharArray();
        for (int i = 0; i < n; i++) {
            s.append(c[r.nextInt(all.length())]);
        }
        return s.toString();
    }

    public static List<Character> toList(String s) {
        char[] cs = s.toCharArray();
        List<Character> ans = new ArrayList<>();
        for (char c : cs) {
            ans.add(c);
        }
        return ans;
    }
}
