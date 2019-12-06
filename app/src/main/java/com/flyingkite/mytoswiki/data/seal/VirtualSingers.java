package com.flyingkite.mytoswiki.data.seal;

import android.os.Parcel;

public class VirtualSingers extends BaseSeal {

    public VirtualSingers() {
        String[][] table = {
                // idNorm, normal p, raised p (probability in thousand)
                {"2188", "0100", "0250"},// Hatsune Miku, 初音未來
                {"2186", "0450", "1000"},// Hatsune Miku & KAITO, 彩璃羽躍 ‧ 初音未來與KAITO
                {"2189", "0450", "1000"},// Kagamine Rin & Len, 鏡音鈴與鏡音連
                {"2187", "1800", "1550"},// MEIKO
                {"2190", "1800", "1550"},// Megurine Luka, 巡音流歌
                {"2191", "1800", "1550"},// KAITO
                {"2192", "1800", "1550"},// Kagamine Rin, 鏡音鈴
                {"2193", "1800", "1550"},// Kagamine Len, 鏡音連
        };
        init(table);
    }

    protected VirtualSingers(Parcel in) {
        super(in);
    }

    public String name() {
        return "VirtualSingers";
    }
}
