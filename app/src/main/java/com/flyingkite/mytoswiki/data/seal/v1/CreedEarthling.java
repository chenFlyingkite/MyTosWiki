package com.flyingkite.mytoswiki.data.seal.v1;

import android.os.Parcel;

import com.flyingkite.mytoswiki.data.seal.BaseSeal;

public class CreedEarthling extends BaseSeal {

    public CreedEarthling() {
        String[][] table = {
                // idNorm, normal p, raised p (probability in thousand)
                {"2383", "0100", "0250"},// 編號 6666 ‧ 依貝思
                {"2381", "0450", "1000"},// 編號 8299 ‧ 南納
                {"2385", "0450", "1000"},// 無束天賦 ‧ 因其都
                {"2382", "1800", "1550"},// 編號 5722 ‧ 雷爾夫
                {"2384", "1800", "1550"},// 編號 0017 ‧ 艾絲翠
                {"2386", "1800", "1550"},// 編號 0008 ‧ 奧蘿菈
                {"2387", "1800", "1550"},// 編號 1125 ‧ 沙迪
                {"2388", "1800", "1550"},// 編號 3578 ‧ 蕾達
        };
        init(table);
    }

    protected CreedEarthling(Parcel in) {
        super(in);
    }

    public String name() {
        return "CreedEarthling";
    }
}