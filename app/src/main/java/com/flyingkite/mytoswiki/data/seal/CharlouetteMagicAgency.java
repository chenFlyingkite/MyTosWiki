package com.flyingkite.mytoswiki.data.seal;

import android.os.Parcel;

public class CharlouetteMagicAgency extends BaseSeal {

    public CharlouetteMagicAgency() {
        String[][] table = {
                // idNorm, normal p, raised p (probability in thousand)
                {"2570", "0100", "0250"},// 夢詠守望 ‧ 英格麗
                {"2567", "0450", "1000"},// 緋曦赤霞 ‧ 紅璦
                {"2568", "0450", "1000"},// 比擬萬象 ‧ 達格
                {"2566", "1800", "1550"},// 水幻冰藍 ‧ 米菲波
                {"2569", "1800", "1550"},// 天才使魔 ‧ 豹豹
                {"2571", "1800", "1550"},// 黛蔻藥師 ‧ 茜蘿珊
                {"2572", "1800", "1550"},// 破幻銀鈴 ‧ 傾霞
                {"2573", "1800", "1550"},// 無微珍護 ‧ 曼特裘
        };
        init(table);
    }

    protected CharlouetteMagicAgency(Parcel in) {
        super(in);
    }

    public String name() {
        return "CharlouetteMagicAgency";
    }
}