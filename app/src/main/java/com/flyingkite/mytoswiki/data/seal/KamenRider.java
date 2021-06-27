package com.flyingkite.mytoswiki.data.seal;

import android.os.Parcel;

public class KamenRider extends BaseSeal {
    public KamenRider() {
        String[][] table = {
                // idNorm, normal p, raised p (probability in thousand)
                {"2701", "0100", "0250"},// Natsu, 納茲
                {"2699", "0400", "1000"},// Lucy, 露西
                {"2702", "0400", "1000"},// Erza, 艾爾莎
                {"2704", "0400", "1000"},// Gray, 格雷
                {"2706", "2175", "1687"},// Gajeel, 戈吉爾
                {"2707", "2175", "1687"},// Wendy, 溫蒂
                {"2708", "2175", "1687"},// Laxus, 拉格薩斯
                {"2709", "2175", "1687"},// Mirajane, 米拉珍
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