package com.flyingkite.mytoswiki.data.seal;

import android.os.Parcel;

public class MasterCathieves extends BaseSeal {

    public MasterCathieves() {
        String[][] table = {
            // idNorm, normal p, raised p (probability in thousand)
            {"1927", "0100", "0250"},// Zero, 零
            {"1925", "0450", "1000"},// Ghostie, 阿飄
            {"1929", "0450", "1000"},// Mellow, 蜜兒
            {"1921", "1800", "1550"},// Plumpy, 肉圓
            {"1923", "1800", "1550"},// Copperfield, 高柏飛
            {"1931", "1800", "1550"},// Wimpy, 慌慌
            {"1933", "1800", "1550"},// Choux, 泡芙
            {"1935", "1800", "1550"},// Tim, 添哥
        };
        init(table);
    }

    protected MasterCathieves(Parcel in) {
        super(in);
    }
}
