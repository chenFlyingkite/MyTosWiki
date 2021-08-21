package com.flyingkite.mytoswiki.data.seal.v1;

import android.os.Parcel;

import com.flyingkite.mytoswiki.data.seal.BaseSeal;

public class KimetsuNoYaiba extends BaseSeal {

    public KimetsuNoYaiba() {
        String[][] table = {
                // idNorm, normal p, raised p (probability in thousand)
                {"2661", "0100", "0250"},// 竈門炭治郎 (1.0%)
                {"2663", "0400", "0667"},// 竈門禰豆子 (4.0%)
                {"2667", "0400", "0667"},// 冨岡義勇 (4.0%)
                {"2668", "0400", "0667"},// 煉獄杏壽郎 (4.0%)
                {"2664", "1740", "1549"},// 嘴平伊之助 (17.4%)
                {"2665", "1740", "1550"},// 我妻善逸 (17.4%)
                {"2666", "1740", "1550"},// 栗花落香奈乎 (17.4%)
                {"2669", "1740", "1550"},// 胡蝶忍 (17.4%)
                {"2670", "1740", "1550"},// 珠世與愈史郎 (17.4%)
        };
        init(table);
    }

    protected KimetsuNoYaiba(Parcel in) {
        super(in);
    }

    public String name() {
        return "KimetsuNoYaiba";
    }
}
