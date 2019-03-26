package com.flyingkite.mytoswiki.data.seal;

import android.os.Parcel;

public class UnearthlyCharm extends BaseSeal {
    public UnearthlyCharm() {
        String[][] table = {
                // idNorm, normal p, raised p (probability in thousand)
                {"2053", "0100", "0250"},// Eve, 夏娃
                {"2057", "0450", "1000"},// Wu Zetian, 武則天
                {"2059", "0450", "1000"},// Cleo, 克麗奧
                {"2051", "1800", "1550"},// Li Ji, 驪姬
                {"2055", "1800", "1550"},// Longyang Jun, 龍陽君
                {"2061", "1800", "1550"},// Bao Si, 褒姒
                {"2063", "1800", "1550"},// Yang Yuhuan, 楊玉環
                {"2065", "1800", "1550"},// Mo Xi, 妺喜
        };
        init(table);
    }

    protected UnearthlyCharm(Parcel in) {
        super(in);
    }

    public String name() {
        return "UnearthlyCharm";
    }
}
