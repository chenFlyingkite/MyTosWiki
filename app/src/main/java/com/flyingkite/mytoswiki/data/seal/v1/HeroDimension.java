package com.flyingkite.mytoswiki.data.seal.v1;

import android.os.Parcel;

import com.flyingkite.mytoswiki.data.seal.BaseSeal;

public class HeroDimension extends BaseSeal {

    public HeroDimension() {
        String[][] table = {
                // idNorm, normal p, raised p (probability in thousand)
                {"2637", "0100", "0250"},// 寫作之神．菲呂拉
                {"2639", "0450", "1000"},// 扭曲天使．亞伯汗
                {"2640", "0450", "1000"},// 叛逆天使．路西法
                {"2636", "1800", "1550"},// 九尾狐．玉藻前
                {"2638", "1800", "1550"},// 傾世妖魅．妲己
                {"2641", "1800", "1550"},// 輝日神．天照大神
                {"2642", "1800", "1550"},// 神使．八咫烏
                {"2643", "1800", "1550"},// 冥妃神．伊邪那美
        };
        init(table);
    }

    protected HeroDimension(Parcel in) {
        super(in);
    }

    public String name() {
        return "HeroDimension";
    }
}
