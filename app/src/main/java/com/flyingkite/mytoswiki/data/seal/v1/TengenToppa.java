package com.flyingkite.mytoswiki.data.seal.v1;

import android.os.Parcel;

import com.flyingkite.mytoswiki.data.seal.BaseSeal;

public class TengenToppa extends BaseSeal {

    public TengenToppa() {
        String[][] table = {
                // idNorm, normal p, raised p (probability in thousand)
                {"2407", "0100", "0250"},// 西蒙與螺巖
                {"2408", "0450", "1000"},// 卡米那與紅蓮
                {"2411", "0450", "1000"},// 庸子
                {"2406", "1800", "1550"},// 李龍
                {"2409", "1800", "1550"},// 羅修
                {"2410", "1800", "1550"},// 妮亞
                {"2412", "1800", "1550"},// 維拉爾與鹽基
                {"2413", "1800", "1550"},// 奇坦與大王奇坦
        };
        init(table);
    }

    protected TengenToppa(Parcel in) {
        super(in);
    }

    public String name() {
        return "TengenToppa";
    }
}
