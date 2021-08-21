package com.flyingkite.mytoswiki.data.seal.v1;

import android.os.Parcel;

import com.flyingkite.mytoswiki.data.seal.BaseSeal;

public class KamenRider extends BaseSeal {
    public KamenRider() {
        String[][] table = {
                // idNorm, normal p, raised p (probability in thousand)
                {"2701", "0100", "0250"},// 假面騎士Zero-One = 2.5%
                {"2699", "0400", "0667"},// 假面騎士空我(全能型態) = 6.667%
                {"2702", "0400", "0667"},// 假面騎士Black
                {"2704", "0400", "0666"},// 假面騎士Decade
                {"2706", "2175", "1937"},// 假面騎士Saber  = 19.375%
                {"2707", "2175", "1938"},// 假面騎士(新1號)
                {"2708", "2175", "1937"},// 假面騎士電王
                {"2709", "2175", "1938"},// 假面騎士Ex-Aid
        };
        init(table);
    }

    protected KamenRider(Parcel in) {
        super(in);
    }

    public String name() {
        return "KamenRider";
    }
}