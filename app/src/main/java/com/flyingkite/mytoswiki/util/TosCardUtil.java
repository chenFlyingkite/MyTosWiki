package com.flyingkite.mytoswiki.util;

import android.text.TextUtils;

import com.flyingkite.mytoswiki.data.tos.TosCard;
import com.flyingkite.mytoswiki.tos.TosWiki;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import flyingkite.library.java.util.MathUtil;
import flyingkite.library.java.util.RegexUtil;

public class TosCardUtil {

    public static String strCard(TosCard c) {
        return "#" + c.idNorm + ", " + c.name
                + "\n  " + c.skillDesc1 + ", " + c.skillDesc2
                + "\n  " + c.skillLeaderDesc
                ;
        //return String.format("#%4s,%s\n      %s\n      %s", c.idNorm, c.name, c.skillDesc1 + "," + c.skillDesc2, c.skillLeaderDesc);
    }

    public static String idNorm(String s) {
        if (TextUtils.isEmpty(s)) return s;

        try {
            int n = Integer.parseInt(s);
            return String.format(java.util.Locale.US, "%04d", n);
        } catch (NumberFormatException e) {
            return s;
        }
    }

    // 動態造型
    public static boolean isSkin(TosCard c) {
        int idNorm = Integer.parseInt(c.idNorm);
        return MathUtil.isInRange(idNorm, 6000, 7000);
    }

    // 討伐戰
    public static boolean isTauFa(TosCard c) {
        int idNorm = Integer.parseInt(c.idNorm);
        return MathUtil.isInRange(idNorm, 7000, 8000);
    }

    // 迪士尼
    public static boolean isDisney(TosCard c) {
        int idNorm = Integer.parseInt(c.idNorm);
        return MathUtil.isInRange(idNorm, 8000, 8046);
    }

    // 存音石
    public static boolean isTunestone(TosCard c) {
        int idNorm = Integer.parseInt(c.idNorm);
        return MathUtil.isInRange(idNorm, 8046, 9000);
    }

    // 72 柱魔神
    public static boolean is72Demon(TosCard c) {
        int idNorm = Integer.parseInt(c.idNorm);
        return MathUtil.isInRange(idNorm, 9000, 9020);
    }

    public static boolean isMaterial(TosCard c) {
        Pattern p = Pattern.compile("(進化素材|強化素材)");
        return p.matcher(c.race).find();
    }

    public static boolean isInBag(TosCard c) {
        if (c == null) return false;

        Pattern p;
        if (isSkin(c) || isTunestone(c)) {
            return false;
        }

        if (isMaterial(c)) {
            // Series, valid
            List<String> s = new ArrayList<>();
            s.add("希臘神像");
            p = Pattern.compile(RegexUtil.toRegexOr(s));
            return p.matcher(c.series).find();
        }
        // 合體卡的合體後
        if (c.combineTo.size() > 0) {
            String to = c.combineTo.get(0);
            return !to.equals(c.idNorm);
        }
        // 變身後
        if (TosWiki.isSwitchChangedCard(c.idNorm)) {
            return false;
        }

        // Series
        List<String> s = new ArrayList<>();
        s.add("豐腴珍獸");
        s.add("Android 機械人");
        p = Pattern.compile(RegexUtil.toRegexOr(s));
        if (p.matcher(c.series).find()) {
            return false;
        }

        return true;
    }
}
