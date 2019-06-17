package com.flyingkite.mytoswiki.data.seal;

import android.os.Parcel;

public class PrimalDeities extends BaseSeal {
    public PrimalDeities() {
        String[][] table = {
                // idNorm, normal p, raised p (probability in thousand)
                {"2131", "0100", "0250"},// 女媧
                {"2130", "0450", "1000"},// 伏羲
                {"2132", "0450", "1000"},// 西王母
                {"2129", "1800", "1550"},// 天吳
                {"2133", "1800", "1550"},// 太一
                {"2134", "1800", "1550"},// 畢方
                {"2135", "1800", "1550"},// 豎亥
                {"2136", "1800", "1550"},// 東王公
        };
        init(table);
    }

    protected PrimalDeities(Parcel in) {
        super(in);
    }

    public String name() {
        return "PrimalDeities";
    }
}