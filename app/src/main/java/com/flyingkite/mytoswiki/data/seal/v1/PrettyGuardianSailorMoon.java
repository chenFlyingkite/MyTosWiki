package com.flyingkite.mytoswiki.data.seal.v1;

import android.os.Parcel;

import com.flyingkite.mytoswiki.data.seal.BaseSeal;

public class PrettyGuardianSailorMoon extends BaseSeal {

    public PrettyGuardianSailorMoon() {
        String[][] table = {
                // idNorm, normal p, raised p (probability in thousand)
                {"10344", "0100", "0250"},// 超級水手月亮
                {"10347", "0450", "1000"},// 超級水手小月亮與艾利歐斯
                {"10346", "0450", "1000"},// 超級水手土星
                {"10341", "1800", "1550"},// 超級水手水星
                {"10342", "1800", "1550"},// 超級水手火星
                {"10343", "1800", "1550"},// 超級水手木星
                {"10348", "1800", "1550"},// 超級水手金星
                {"10349", "1800", "1550"},// 超級水手冥王星
        };
        init(table);
    }

    protected PrettyGuardianSailorMoon(Parcel in) {
        super(in);
    }

    public String name() {
        return "PrettyGuardianSailorMoon";
    }
}