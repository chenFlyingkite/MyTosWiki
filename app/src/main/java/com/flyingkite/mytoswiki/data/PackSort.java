package com.flyingkite.mytoswiki.data;

import com.google.gson.annotations.SerializedName;

public class PackSort {
    @SerializedName("ownExist")
    public boolean ownExist;

    @SerializedName("ownZero")
    public boolean ownZero;

    @SerializedName("unownedPath")
    public boolean unownedPath;

    @SerializedName("normal")
    public boolean normal;

    @SerializedName("tauFa")
    public boolean tauFa;

    @SerializedName("disney")
    public boolean disney;

    @SerializedName("demon72")
    public boolean demon72;

    @SerializedName("hide5xxxx")
    public boolean hide5xxxx;

    @SerializedName("hide6xxxx")
    public boolean hide6xxxx;

    @SerializedName("display")
    public int display = 1;
}
