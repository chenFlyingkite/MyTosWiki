package com.flyingkite.mytoswiki.data.seal.v1;

import android.os.Parcel;

import com.flyingkite.mytoswiki.data.seal.BaseSeal;

public class GiftedScientists extends BaseSeal {
    public GiftedScientists() {
        String[][] table = {
                // idNorm, normal p, raised p (probability in thousand)
                {"2006", "0100", "0250"},// Einstein, 愛因斯坦
                {"2010", "0450", "1000"},// Darwin, 達爾文
                {"2014", "0450", "1000"},// Galileo, 伽利略
                {"2008", "1800", "1550"},// Newton, 牛頓
                {"2012", "1800", "1550"},// Edison, 愛迪生
                {"2016", "1800", "1550"},// Marie Curie, 瑪麗居禮
                {"2018", "1800", "1550"},// Socrates, 蘇格拉底
                {"2020", "1800", "1550"},// Nicole, 妮可
        };
        init(table);
    }

    protected GiftedScientists(Parcel in) {
        super(in);
    }

    public String name() {
        return "GiftedScientists";
    }
}
