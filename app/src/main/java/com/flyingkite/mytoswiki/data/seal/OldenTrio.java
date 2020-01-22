package com.flyingkite.mytoswiki.data.seal;

import android.os.Parcel;

public class OldenTrio extends BaseSeal {

    public OldenTrio() {
        String[][] table = {
                // idNorm, normal p, raised p (probability in thousand)
                {"2306", "0100", "0250"},// Auspice of Protection - Cang Bi, 蒼璧
                {"2307", "0450", "1000"},// Fire of Sagacity - Enlil, 恩莉兒
                {"2310", "0450", "1000"},// Blood of Obscurity - Veronica, 維洛妮卡
                {"2308", "1800", "1550"},// Prudent Insight - Spencer, 史賓賽
                {"2309", "1800", "1550"},// Lightning Rider - Huang Cong, 黃琮
                {"2311", "1800", "1550"},// Sympathetic Stream - Shui Huan, 水桓
                {"2312", "1800", "1550"},// Flicker of Scale - Chi Zhang, 赤璋
                {"2313", "1800", "1550"},// Righteous Fire - Marduk, 馬杜克
        };
        init(table);
    }

    protected OldenTrio(Parcel in) {
        super(in);
    }

    public String name() {
        return "OldenTrio";
    }
}
