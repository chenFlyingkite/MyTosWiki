package com.flyingkite.mytoswiki.data.seal;

import android.os.Parcel;

public class TruthSeekers extends BaseSeal {

    public TruthSeekers() {
        String[][] table = {
                // idNorm, normal p, raised p (probability in thousand)
                {"2338", "0100", "0250"},// Edward Elric, 愛德華 ‧ 愛力克
                {"2337", "0450", "1000"},// Mustang, 馬斯坦古
                {"2339", "0450", "1000"},// Alphonse Elric, 阿爾馮斯 ‧ 愛力克
                {"2336", "1800", "1550"},// Curtis, 卡迪斯
                {"2340", "1800", "1550"},// Ling Yao, 姚麟
                {"2341", "1800", "1550"},// Hawkeye, 霍克愛
                {"2342", "1800", "1550"},// Armstrong, 阿姆斯壯
                {"2343", "1800", "1550"},// Lan Fan, 蘭芳
        };
        init(table);
    }

    protected TruthSeekers(Parcel in) {
        super(in);
    }

    public String name() {
        return "TruthSeekers";
    }
}
