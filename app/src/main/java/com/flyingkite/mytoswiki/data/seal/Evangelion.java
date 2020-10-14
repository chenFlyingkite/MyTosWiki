package com.flyingkite.mytoswiki.data.seal;

import android.os.Parcel;

public class Evangelion extends BaseSeal {

    public Evangelion() {
        String[][] table = {
                // idNorm, normal p, raised p (probability in thousand)
                {"2516", "0100", "0250"},// 2516
                {"2518", "0450", "1000"},// 2518
                {"2520", "0450", "1000"},// 2520
                {"2519", "1800", "1550"},// 2519
                {"2521", "1800", "1550"},// 2521
                {"2522", "1800", "1550"},// 2522
                {"2523", "1800", "1550"},// 2523
                {"2524", "1800", "1550"},// 2524
        };
        init(table);
    }

    protected Evangelion(Parcel in) {
        super(in);
    }

    public String name() {
        return "Evangelion";
    }
}