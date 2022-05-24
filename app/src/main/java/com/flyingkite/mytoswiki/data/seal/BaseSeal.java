package com.flyingkite.mytoswiki.data.seal;

import android.os.Parcel;
import android.os.Parcelable;

import com.flyingkite.mytoswiki.data.seal.v1.SealSample;

import java.util.ArrayList;
import java.util.List;

import flyingkite.library.android.log.Loggable;

public class BaseSeal implements Parcelable, Loggable {
    public SealSample normalSample; // normal probability
    public SealSample raisedSample; // raised probability

    public List<String> sealCards = new ArrayList<>();

    public BaseSeal() {
    }

    public String name() {
        return "BaseSeal";
    }

    /**
     * Create by table, like
     *
     * String[][] table = {
     *   // idNorm, normal p, raised p (probability in thousand)
     *   {"1473", "0100", "0250"},// Nobunaga, 織田信長
     *   {"1475", "0450", "1000"},// Hideyoshi, 豐臣秀吉
     *   {"1477", "0450", "1000"},// Honda, 本多忠勝
     *   {"1471", "1800", "1550"},// Azai, 淺井長政
     *   {"1479", "1800", "1550"},// Komatsuhime, 稻姬
     *   {"1481", "1800", "1550"},// Oichi, 阿市
     *   {"1483", "1800", "1550"},// Nohime, 濃姬
     *   {"1485", "1800", "1550"},// Tokugawa, 德川家康
     * };
     *
     */
    protected void init(String[][] table) {
        int n = table.length;
        normalSample = new SealSample(n);
        raisedSample = new SealSample(n);

        int sn = 0, sr = 0;
        for (int i = 0; i < n; i++) {
            sealCards.add(table[i][0]);
            normalSample.pdf[i] = Integer.parseInt(table[i][1]);
            raisedSample.pdf[i] = Integer.parseInt(table[i][2]);
            sn += normalSample.pdf[i];
            sr += raisedSample.pdf[i];
        }
        for (int i = 0; i < n; i++) {
            normalSample.pdf[i] /= sn;
            raisedSample.pdf[i] /= sr;
        }
        normalSample.evalCdf();
        raisedSample.evalCdf();
    }

// Normal probability, 0100 = 1%, 0450 = 4.50%, 1000 = 10.00%
// 粉碎狂熱 (Crash fever), 幽☆遊☆白書, Hunter x Hunter,
// 1   "0100"    "0100"    "0100"
// 2   "0450"    "0450"    "0450"
// 3   "0450"    "0450"    "0450"
// 4   "1500"    "1800"    "2250"
// 5   "1500"    "1800"    "2250"
// 6   "1500"    "1800"    "2250"
// 7   "1500"    "1800"    "2250"
// 8   "1500"    "1800"
// 9   "1500"

// Raised probability
// 1   "0250"    "0250"    "0250"
// 2   "1000"    "1000"    "1000"
// 3   "1000"    "1000"    "1000"
// 4   "1291"    "1550"    "1937"
// 5   "1291"    "1550"    "1938"
// 6   "1292"    "1550"    "1937"
// 7   "1292"    "1550"    "1938"
// 8   "1292"    "1550"
// 9   "1292"


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(normalSample, flags);
        dest.writeParcelable(raisedSample, flags);
        dest.writeStringList(sealCards);
    }

    protected BaseSeal(Parcel in) {
        normalSample = in.readParcelable(SealSample.class.getClassLoader());
        raisedSample = in.readParcelable(SealSample.class.getClassLoader());
        sealCards = in.createStringArrayList();
    }

    public static final Creator<BaseSeal> CREATOR = new Creator<BaseSeal>() {
        @Override
        public BaseSeal createFromParcel(Parcel in) {
            return new BaseSeal(in);
        }

        @Override
        public BaseSeal[] newArray(int size) {
            return new BaseSeal[size];
        }
    };
}
