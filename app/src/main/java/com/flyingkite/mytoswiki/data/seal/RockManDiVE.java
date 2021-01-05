package com.flyingkite.mytoswiki.data.seal;

import android.os.Parcel;

public class RockManDiVE extends BaseSeal {

    public RockManDiVE() {
        String[][] table = {
                // idNorm, normal p, raised p (probability in thousand)
                {"2611", "0100", "0250"},// 艾克斯
                {"2612", "0450", "1000"},// 傑洛
                {"2616", "0450", "1000"},// 愛莉絲
                {"2614", "1800", "1550"},// 帕蕾特
                {"2615", "1800", "1550"},// 艾克賽爾
                {"2617", "1800", "1550"},// 艾莉雅
                {"2618", "1800", "1550"},// 瑪莉諾
                {"2619", "1800", "1550"},// 席娜蒙
        };
        init(table);
    }

    protected RockManDiVE(Parcel in) {
        super(in);
    }

    public String name() {
        return "RockManDiVE";
    }
}
