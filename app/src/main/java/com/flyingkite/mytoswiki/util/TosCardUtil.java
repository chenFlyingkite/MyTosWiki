package com.flyingkite.mytoswiki.util;

import android.text.TextUtils;

import com.flyingkite.library.log.Loggable;
import com.flyingkite.library.util.MathUtil;
import com.flyingkite.mytoswiki.data.pack.PackCard;
import com.flyingkite.mytoswiki.data.tos.TosCard;
import com.flyingkite.mytoswiki.tos.TosWiki;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class TosCardUtil {

    // 變身後的卡片idNorm
    public static final String idNormOfSwitched = "(1167|1169|1171|1173|1175|1761|1763|1765|1767|1787|1789|1791|1794|1802|1804|1948|1949|2042|2044|2046|2048|2050)";

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

    /** 動態造型 */
    public static boolean isSkin(TosCard c) {
        int idNorm = Integer.parseInt(c.idNorm);
        return MathUtil.isInRange(idNorm, 6000, 7000);
    }

    /** 討伐戰 */
    public static boolean isTauFa(TosCard c) {
        int idNorm = Integer.parseInt(c.idNorm);
        return MathUtil.isInRange(idNorm, 7000, 8000);
    }

    /** 迪士尼 */
    public static boolean isDisney(TosCard c) {
        int idNorm = Integer.parseInt(c.idNorm);
        return MathUtil.isInRange(idNorm, 8000, 8046);
    }

    /** 存音石 */
    public static boolean isTunestone(TosCard c) {
        int idNorm = Integer.parseInt(c.idNorm);
        return MathUtil.isInRange(idNorm, 8046, 9000);
    }

    /** 72 柱魔神 */
    public static boolean is72Demon(TosCard c) {
        int idNorm = Integer.parseInt(c.idNorm);
        return MathUtil.isInRange(idNorm, 9000, 9020);
    }

    public static boolean isMaterial(TosCard c) {
        Pattern p = Pattern.compile("(進化素材|強化素材)");
        return p.matcher(c.race).find();
    }

    public static boolean isInBag(TosCard c) {
        Pattern p;
        if (isSkin(c) || isTunestone(c)) {
            return false;
        }
        if (isMaterial(c)) {
            // Series, valid
            List<String> s = new ArrayList<>();
            s.add("希臘神像");
            p = Pattern.compile(RegexUtil.toRegexOr(s));
            if (p.matcher(c.series).find()) {
                 return true;
            } else {
                return false;
            }
        }
        // 合體卡的合體後
        if (c.combineTo.size() > 0) {
            String to = c.combineTo.get(0);
            return !to.equals(c.idNorm);
        }
        // 變身後
        p = Pattern.compile(idNormOfSwitched);
        if (p.matcher(c.idNorm).find()) {
            return false;
        }

        // Series
        List<String> s = new ArrayList<>();
        s.add("豐腴珍獸");
        s.add("巨型蟾蜍");
        s.add("Android 機械人");
        s.add("忠誠侍童");
        p = Pattern.compile(RegexUtil.toRegexOr(s));
        if (p.matcher(c.series).find()) {
            return false;
        }

        return true;
    }

    // see "https://review.towerofsaviors.com/199215954"
    public static PackCard parseCard(String s) {
        PackCard p = new PackCard();
        String[] c = s.split("[|]");

        p.source = s;

        p.index = Integer.parseInt(c[0]);
        p.idNorm = idNorm(c[1]);
        p.exp = Integer.parseInt(c[2]);
        p.lv = Integer.parseInt(c[3]);
        p.skillLv = Integer.parseInt(c[4]);
        p.createAt = Long.parseLong(c[5]);
        p.soulIfSell = Integer.parseInt(c[6]); // soul gain if sell
        p.soulOwned = Integer.parseInt(c[7]); // added soul to amelioration
        p.refineLv = Integer.parseInt(c[8]); // refine 5 = 突破
        p.skinId = Integer.parseInt(c[9]);
        p.skillExp = Integer.parseInt(c[10]);
        p.normalSkillCd = Integer.parseInt(c[11]);
        return p;
    }

    public static void genSwitched() {
        TosCard[] all = TosWiki.allCards();

        Set<String> s = new TreeSet<>();
        for (TosCard c : all) {
            if (!TextUtils.isEmpty(c.switchChange)) {
                s.add(c.switchChange);
            }
        }
        List<String> a = new ArrayList<>(s);
        String x = RegexUtil.toRegexOr(a);
        z.logE("%s result = \n%s\n", a.size(), x);
    }

    private static final Loggable z = new Loggable() {};
}
