package com.flyingkite.mytoswiki.data.seal.v1;

import android.os.Parcel;
import android.os.Parcelable;

import flyingkite.math.DiscreteSample;

public class SealSample extends DiscreteSample implements Parcelable {

    public SealSample(int n) {
        super(n);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDoubleArray(pdf);
        dest.writeDoubleArray(cdf);
        dest.writeIntArray(observe);
        dest.writeDoubleArray(observePdf);

    }

    protected SealSample(Parcel in) {
        in.readDoubleArray(pdf);
        in.readDoubleArray(cdf);
        in.readIntArray(observe);
        in.readDoubleArray(observePdf);
    }

    public static final Creator<SealSample> CREATOR = new Creator<SealSample>() {
        @Override
        public SealSample createFromParcel(Parcel in) {
            return new SealSample(in);
        }

        @Override
        public SealSample[] newArray(int size) {
            return new SealSample[size];
        }
    };
}
