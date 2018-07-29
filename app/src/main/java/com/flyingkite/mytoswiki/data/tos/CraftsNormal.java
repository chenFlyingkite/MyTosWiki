package com.flyingkite.mytoswiki.data.tos;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class CraftsNormal extends BaseCraft {
    //-- For Common
    @SerializedName(CRS.attrLimit)
    public String attrLimit = "";
    @SerializedName(CRS.raceLimit)
    public String raceLimit = "";


    //-- For parcelable

    public static final Parcelable.Creator<CraftsNormal> CREATOR = new Parcelable.Creator<CraftsNormal>() {
        @Override
        public CraftsNormal createFromParcel(Parcel in) {
            return gson.fromJson(in.readString(), CraftsNormal.class);
        }

        @Override
        public CraftsNormal[] newArray(int size) {
            return new CraftsNormal[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(gson.toJson(this));
    }
    //-- For parcelable - End
}
