package com.flyingkite.mytoswiki.data.seal.v1;

import android.os.Parcel;

import com.flyingkite.mytoswiki.data.seal.BaseSeal;

public class Minerelves extends BaseSeal {

    public Minerelves() {
        String[][] table = {
                // idNorm, normal p, raised p (probability in thousand)
                {"2222", "0100", "0250"},// Diamond, 鑽石
                {"2218", "0450", "1000"},// Amber, 琥珀
                {"2220", "0450", "1000"},// Jade, 翡翠
                {"2216", "1800", "1550"},// Sapphire, 藍寶石
                {"2224", "1800", "1550"},// Obsidian, 黑曜石
                {"2226", "1800", "1550"},// Ruby, 紅寶石
                {"2228", "1800", "1550"},// Emerald, 祖母綠
                {"2230", "1800", "1550"},// Opal, 蛋白石
        };
        init(table);
    }

    protected Minerelves(Parcel in) {
        super(in);
    }

    public String name() {
        return "Minerelves";
    }
}
