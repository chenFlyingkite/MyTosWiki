package com.flyingkite.mytoswiki.data.seal.v1;

import android.os.Parcel;

import com.flyingkite.mytoswiki.data.seal.BaseSeal;

public class PhantomTroupe extends BaseSeal {

    public PhantomTroupe() {
        String[][] table = {
                // idNorm, normal p, raised p (probability in thousand)
                {"2285", "0100", "0250"},// Leader of Phantom Troupe - Chrollo, 幻影旅團團長 ‧ 庫洛洛
                {"2281", "0450", "1000"},// Machi, 瑪奇
                {"2282", "0450", "1000"},// Hisoka the Magician, 魔術師 ‧ 西索
                {"2283", "1500", "1290"},// Franklin, 富蘭克林
                {"2284", "1500", "1290"},// Pakunoda, 派克諾妲
                {"2286", "1500", "1290"},// Nobunaga (Phantom Troupe), 信長
                {"2287", "1500", "1290"},// Shalnark, 俠客
                {"2288", "1500", "1290"},// Feitan, 飛坦
                {"2289", "1500", "1290"},// Shizuku, 小滴
        };
        init(table);
    }

    protected PhantomTroupe(Parcel in) {
        super(in);
    }

    public String name() {
        return "PhantomTroupe";
    }
}
