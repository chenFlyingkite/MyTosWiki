package com.flyingkite.mytoswiki.data.seal;

import android.os.Parcel;

public class SummonerCandidates extends BaseSeal {

    public SummonerCandidates() {
        String[][] table = {
                // idNorm, normal p, raised p (if p = 1234 => 12.34%)
                {"10047", "0150", "0150"},// 嬌憨龍娃 ‧ 爪娃諾
                {"10048", "0850", "0850"},// 激濤獅心 ‧ 奎內塞
                {"10036", "0900", "0900"},// 巧舌饞涎 ‧ 柯柯
                {"10037", "0900", "0900"},// 炙紋幻轉 ‧ 各各爾
                {"10038", "0900", "0900"},// 童夢果匠 ‧ 謝貝妮
                {"10039", "0900", "0900"},// 血杖強權 ‧ 果樂兒
                {"10040", "0900", "0900"},// 幽浮雙戟 ‧ 鬼塚羽
                {"10041", "0900", "0900"},// 龍角焰息 ‧ 珂婍娜
                {"10042", "0900", "0900"},// 繞尖芬芳 ‧ 睡蓮
                {"10043", "0900", "0900"},// 笛音夢韻 ‧ 漣璇
                {"10044", "0900", "0900"},// 蓄力鐳射 ‧ 飛蘿
                {"10045", "0900", "0900"},// 革新鑄造 ‧ 都雷托
        };
        init(table);
    }

    protected SummonerCandidates(Parcel in) {
        super(in);
    }

    public String name() {
        return "SummonerCandidates";
    }
}
