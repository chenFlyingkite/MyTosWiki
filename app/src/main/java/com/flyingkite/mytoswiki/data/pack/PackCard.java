package com.flyingkite.mytoswiki.data.pack;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import androidx.annotation.NonNull;

public class PackCard {

    @SerializedName("id")
    public int id;

    @SerializedName("level")
    public int level;

    @SerializedName("index")
    public int index;

    @SerializedName("skillLevel")
    public int skillLevel;

    @SerializedName("enhanceLevel")
    public int enhanceLevel;

    @SerializedName("acquiredAt")
    public long acquiredAt;

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
