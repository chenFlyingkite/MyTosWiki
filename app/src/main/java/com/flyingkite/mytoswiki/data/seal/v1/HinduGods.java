package com.flyingkite.mytoswiki.data.seal.v1;

import android.os.Parcel;

import com.flyingkite.mytoswiki.data.seal.BaseSeal;

public class HinduGods extends BaseSeal {

    public HinduGods() {
        String[][] table = {
                // idNorm, normal p, raised p (probability in thousand)
                {"1838", "0100", "0250"},// Brahma, 梵天
                {"1836", "0450", "1000"},// Shiva, 濕婆
                {"1844", "0450", "1000"},// Vishnu, 毗濕奴
                {"1840", "1800", "1550"},// Parvati, 帕爾瓦蒂
                {"1842", "1800", "1550"},// Lakshmi, 拉克什米
                {"1846", "1800", "1550"},// Varuna, 伐樓那
                {"1848", "1800", "1550"},// Agni, 阿耆尼
                {"1850", "1800", "1550"},// Varuni, 伐樓尼
        };
        init(table);
    }

    protected HinduGods(Parcel in) {
        super(in);
    }

    public String name() {
        return "HinduGods";
    }
}
