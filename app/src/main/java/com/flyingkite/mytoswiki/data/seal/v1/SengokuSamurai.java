package com.flyingkite.mytoswiki.data.seal.v1;

import android.os.Parcel;

import com.flyingkite.mytoswiki.data.seal.BaseSeal;

public class SengokuSamurai extends BaseSeal {

    // SengokuSamurai, seal name = Shogun Emblems
    public SengokuSamurai() {
        String[][] table = {
            // idNorm, normal p, raised p (probability in thousand)
            {"1473", "0100", "0250"},// Nobunaga, 織田信長
            {"1475", "0450", "1000"},// Hideyoshi, 豐臣秀吉
            {"1477", "0450", "1000"},// Honda, 本多忠勝
            {"1471", "1800", "1550"},// Azai, 淺井長政
            {"1479", "1800", "1550"},// Komatsuhime, 稻姬
            {"1481", "1800", "1550"},// Oichi, 阿市
            {"1483", "1800", "1550"},// Nohime, 濃姬
            {"1485", "1800", "1550"},// Tokugawa, 德川家康
        };
        init(table);
    }

    protected SengokuSamurai(Parcel in) {
        super(in);
    }

    public String name() {
        return "SengokuSamurai";
    }
}
