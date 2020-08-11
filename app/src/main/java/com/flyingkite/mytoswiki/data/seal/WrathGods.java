package com.flyingkite.mytoswiki.data.seal;

import android.os.Parcel;

public class WrathGods extends BaseSeal {

    public WrathGods() {
        String[][] table = {
                // idNorm, normal p, raised p (probability in thousand)
                {"2454", "0100", "0250"},// 憤怒之罪 ‧ 梅里奧達斯
                {"2451", "0450", "1000"},// 貪婪之罪 ‧ 班
                {"2452", "0450", "1000"},// 怠惰之罪 ‧ 金恩
                {"2453", "1800", "1550"},// 看板娘 ‧ 伊麗莎白
                {"2456", "1800", "1550"},// 卡梅洛國王 ‧ 亞瑟
                {"2457", "1800", "1550"},// 嫉妒之罪 ‧ 黛安娜
                {"2458", "1800", "1550"},// 暴食之罪 ‧ 瑪琳
                {"2459", "1800", "1550"},// 色慾之罪 ‧ 哥塞爾
        };
        init(table);
    }

    protected WrathGods(Parcel in) {
        super(in);
    }

    public String name() {
        return "WrathGods";
    }
}
