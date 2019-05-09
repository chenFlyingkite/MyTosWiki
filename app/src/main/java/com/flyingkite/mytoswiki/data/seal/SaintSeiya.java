package com.flyingkite.mytoswiki.data.seal;

import android.os.Parcel;

public class SaintSeiya extends BaseSeal {
    public SaintSeiya() {
        String[][] table = {
                // idNorm, normal p, raised p (probability in thousand)
                {"2107", "0100", "0250"},// 星矢
                {"2101", "0450", "1000"},// 冰河
                {"2105", "0450", "1000"},// 紫龍
                {"2103", "1800", "1550"},// 一輝
                {"2109", "1800", "1550"},// 瞬
                {"2111", "1800", "1550"},// 卡妙
                {"2112", "1800", "1550"},// 修羅
                {"2113", "1800", "1550"},// 艾奧里亞
        };
        init(table);
    }

    protected SaintSeiya(Parcel in) {
        super(in);
    }

    public String name() {
        return "SaintSeiya";
    }
}