package com.flyingkite.mytoswiki.tos;

import com.flyingkite.mytoswiki.data.TosCard;

public final class TosMath {
    private TosMath() {}

    /**
     * @param lv 等級, 1 ~ 99
     * @param c 卡片
     * @return 累積經驗
     */
    public static long getExpSum(int lv, TosCard c) {
        return getExpSum(lv, c.expCurve);
    }

    /**
     * @param lv 等級, 1 ~ 99
     * @param curve10K 經驗曲線
     * @return 累積經驗
     */
    public static long getExpSum(int lv, int curve10K) {
        double p = (lv - 1) / 98.0;
        double sumExp = curve10K * 10_000F * p * p;
        return (long) Math.ceil(sumExp);
    }

    /**
     * @param lv 等級, 1 ~ 99
     * @param c 卡片
     * @return 堆肥時貢獻經驗
     */
    public static long getExpSacrifice(int lv, TosCard c) {
        return c.minExpSacrifice + (lv - 1) * c.perLvExpSacrifice;
    }
}
