package com.flyingkite.mytoswiki.data.seal;

import android.os.Parcel;

public class LostSaga extends BaseSeal {

    public LostSaga() {
        String[][] table = {
                // idNorm, normal p, raised p (probability in thousand)
                {"2688", "0100", "0250"},// 少華鈞天 ‧ 華曦
                {"2689", "0450", "1000"},// 琴息濯洗 ‧ 霏音
                {"2690", "0450", "1000"},// 傲志不訾 ‧ 姬臣
                {"2686", "1800", "1550"},// 鏡掠夢夕 ‧ 懷凌
                {"2687", "1800", "1550"},// 言采機靈 ‧ 熾夢
                {"2691", "1800", "1550"},// 翳沒幽謐 ‧ 洛墨
                {"2692", "1800", "1550"},// 戈戟煥發 ‧ 浮識
                {"2693", "1800", "1550"},// 逸豫知心 ‧ 渺紗
        };
        init(table);
    }

    protected LostSaga(Parcel in) {
        super(in);
    }

    public String name() {
        return "LostSaga";
    }
}
