package com.flyingkite.mytoswiki.data.seal;

import android.os.Parcel;

public class KonoSubarashi extends BaseSeal {

    // KonoSubarashi, seal name =
    public KonoSubarashi() {
        String[][] table = {
                // idNorm, normal p, raised p (probability in thousand)
                {"1883", "0100", "0250"},// , 惠惠
                {"1882", "0450", "1000"},// , 阿克婭
                {"1885", "0450", "1000"},// , 達克妮絲
                {"1884", "1800", "1550"},// , 和真
                {"1886", "1800", "1550"},// , 維茲
                {"1887", "1800", "1550"},// , 御劍響夜
                {"1888", "1800", "1550"},// , 芸芸
                {"1889", "1800", "1550"},// , 克莉絲
        };
        init(table);
    }

    protected KonoSubarashi(Parcel in) {
        super(in);
    }
}
