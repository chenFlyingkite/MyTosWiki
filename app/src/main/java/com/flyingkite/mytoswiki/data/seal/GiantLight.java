package com.flyingkite.mytoswiki.data.seal;

import android.os.Parcel;

public class GiantLight extends BaseSeal {

    public GiantLight() {
        String[][] table = {
                // idNorm, normal p, raised p (probability in thousand)
                {"2154", "0100", "0250"},// ORB, ORB
                {"2150", "0450", "1000"},// ZERO, ZERO
                {"2157", "0450", "1000"},// Tiga, 迪加
                {"2152", "1800", "1550"},// Taro, 太郎
                {"2153", "1800", "1550"},// Gaia, 佳亞
                {"2155", "1800", "1550"},// Dyna, 帝拿
                {"2156", "1800", "1550"},// Ultraseven, 七星俠
                {"2159", "1800", "1550"},// Ultraman, 吉田
        };
        init(table);
    }

    protected GiantLight(Parcel in) {
        super(in);
    }

    public String name() {
        return "GiantLight";
    }
}
