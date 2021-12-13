package com.flyingkite.mytoswiki.data.seal.v1;

import android.os.Parcel;

import com.flyingkite.mytoswiki.data.seal.BaseSeal;

public class MyHero extends BaseSeal {

    public MyHero() {
        String[][] table = {
                // idNorm, normal p, raised p (probability in thousand)
                {"10123", "0100", "0250"},// 綠谷出久
                {"10122", "0450", "1000"},// 爆豪勝己
                {"10121", "0450", "1000"},// 轟焦凍
                {"10124", "1800", "1550"},// 飯田天哉
                {"10125", "1800", "1550"},// 心操人使
                {"10126", "1800", "1550"},// 麗日御茶子
                {"10127", "1800", "1550"},// 切島銳兒郎
                {"10128", "1800", "1550"},// 蛙吹梅雨
        };
        init(table);
    }

    protected MyHero(Parcel in) {
        super(in);
    }

    public String name() {
        return "MyHero";
    }
}