package com.flyingkite.mytoswiki.util;

import android.text.TextUtils;

import com.flyingkite.mytoswiki.data.pack.PackCard;
import com.flyingkite.mytoswiki.data.tos.TosCard;

public interface TosCardUtil {

    default String strCard(TosCard c) {
        return "#" + c.idNorm + "," + c.name
                + "\n  " + c.skillDesc1 + "," + c.skillDesc2
                + "\n  " + c.skillLeaderDesc
                ;
        //return String.format("#%4s,%s\n      %s\n      %s", c.idNorm, c.name, c.skillDesc1 + "," + c.skillDesc2, c.skillLeaderDesc);
    }

    default String idNorm(String s) {
        if (TextUtils.isEmpty(s)) return s;

        try {
            int n = Integer.parseInt(s);
            return String.format(java.util.Locale.US, "%04d", n);
        } catch (NumberFormatException e) {
            return s;
        }
    }

    // see "https://review.towerofsaviors.com/199215954"
    default PackCard parseCard(String s) {
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
}
