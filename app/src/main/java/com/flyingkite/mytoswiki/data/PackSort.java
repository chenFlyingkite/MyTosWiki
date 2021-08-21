package com.flyingkite.mytoswiki.data;

import com.google.gson.annotations.SerializedName;

public class PackSort {
    @SerializedName("ownExist")
    public boolean ownExist;

    @SerializedName("ownZero")
    public boolean ownZero;

    @SerializedName("unownedPath")
    public boolean unownedPath;

    @SerializedName("farm")
    public boolean farm;

    @SerializedName("normal")
    public boolean normal;

    @SerializedName("tauFa")
    public boolean tauFa;

    @SerializedName("disney")
    public boolean disney;

    @SerializedName("demon72")
    public boolean demon72;

    @SerializedName("display")
    public int display = 1;
}
