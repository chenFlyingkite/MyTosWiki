package com.flyingkite.mytoswiki.data.seal.v1;

import android.os.Parcel;

import com.flyingkite.mytoswiki.data.seal.BaseSeal;

public class GodsDemons extends BaseSeal {

    public GodsDemons() {
        String[][] table = {
                // idNorm, normal p, raised p (probability in thousand)
                {"2498", "0100", "0250"},// 命軸剝奪 ‧ 諾索斯
                {"2497", "0450", "1000"},// 蜃樓星火 ‧ 伊斯塔
                {"2499", "0450", "1000"},// 星辰之理 ‧ 蘇因
                {"2496", "1800", "1550"},// 命序解構 ‧ 瑪努恩
                {"2500", "1800", "1550"},// 魔蟲戾心 ‧ 修德梅耳
                {"2501", "1800", "1550"},// 品嚐生命 ‧ 烏素姆
                {"2502", "1800", "1550"},// 卑猥雙生 ‧ 羅伊戈與札爾
                {"2503", "1800", "1550"},// 天樞光華 ‧ 安沙爾
        };
        init(table);
    }

    protected GodsDemons(Parcel in) {
        super(in);
    }

    public String name() {
        return "GodsDemons";
    }
}
