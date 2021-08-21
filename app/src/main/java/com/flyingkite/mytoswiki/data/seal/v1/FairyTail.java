package com.flyingkite.mytoswiki.data.seal.v1;

import android.os.Parcel;

import com.flyingkite.mytoswiki.data.seal.BaseSeal;

public class FairyTail extends BaseSeal {

    public FairyTail() {
        String[][] table = {
                // idNorm, normal p, raised p (probability in thousand)
                {"1956", "0100", "0250"},// Natsu, 納茲
                {"1955", "0450", "1000"},// Lucy, 露西
                {"1958", "0450", "1000"},// Erza, 艾爾莎
                {"1960", "1800", "1550"},// Gray, 格雷
                {"1959", "1800", "1550"},// Gajeel, 戈吉爾
                {"1957", "1800", "1550"},// Wendy, 溫蒂
                {"1961", "1800", "1550"},// Laxus, 拉格薩斯
                {"1962", "1800", "1550"},// Mirajane, 米拉珍
        };
        init(table);
    }

    protected FairyTail(Parcel in) {
        super(in);
    }

    public String name() {
        return "FairyTail";
    }
}
