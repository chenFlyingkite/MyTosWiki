package com.flyingkite.mytoswiki.data.seal.v1;

import android.os.Parcel;

import com.flyingkite.mytoswiki.data.seal.BaseSeal;

public class SaintSeiyaHades extends BaseSeal {
    public SaintSeiyaHades() {
        String[][] table = {
                // idNorm, normal p, raised p (probability in thousand)
                {"10072", "0100", "0250"},// 終極青銅天馬座 ‧ 星矢
                {"10068", "0450", "1000"},// 終極青銅鳳凰座 ‧ 一輝
                {"10074", "0450", "1000"},// 終極青銅仙女座 ‧ 瞬
                {"10066", "1800", "1550"},// 終極青銅天鵝座 ‧ 冰河
                {"10070", "1800", "1550"},// 終極青銅天龍座 ‧ 紫龍
                {"10076", "1800", "1550"},// 天琴座 ‧ 奧路菲
                {"10077", "1800", "1550"},// 雙子座 ‧ 卡農
                {"10078", "1800", "1550"},// 冥界指揮官 ‧ 潘朵拉
        };
        init(table);
    }

    protected SaintSeiyaHades(Parcel in) {
        super(in);
    }

    public String name() {
        return "SaintSeiyaHades";
    }
}