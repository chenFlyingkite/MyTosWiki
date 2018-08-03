package com.flyingkite.mytoswiki.data.tos;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CraftsArm extends BaseCraft {
    //-- For Arm
    @SerializedName(CRS.cardLimit)
    public List<String> cardLimit = new ArrayList<>();
    @SerializedName(CRS.cardLimitName)
    public List<String> cardLimitName = new ArrayList<>();
    @SerializedName(CRS.upHp)
    public String upHp = "";
    @SerializedName(CRS.upAttack)
    public String upAttack = "";
    @SerializedName(CRS.upRecovery)
    public String upRecovery = "";

    @Override
    public String toString() {
        return _fmt("#%s : %s★, %s, %s, %s", idNorm, rarity, mode, cardLimit, cardLimitName);
    }

    //-- For parcelable

    public static final Parcelable.Creator<CraftsArm> CREATOR = new Parcelable.Creator<CraftsArm>() {
        @Override
        public CraftsArm createFromParcel(Parcel in) {
            return gson.fromJson(in.readString(), CraftsArm.class);
        }

        @Override
        public CraftsArm[] newArray(int size) {
            return new CraftsArm[size];
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
