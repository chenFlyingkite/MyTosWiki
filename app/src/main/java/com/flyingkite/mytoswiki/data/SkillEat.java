package com.flyingkite.mytoswiki.data;

import com.google.gson.annotations.SerializedName;

public class SkillEat {
    @SerializedName("fromLevel")
    public int fromLevel;

    @SerializedName("progress")
    public int progress;

    @SerializedName("toLevel")
    public int toLevel;

    @SerializedName("use3000")
    public boolean use3000;

    @SerializedName("use1000")
    public boolean use1000;

    @SerializedName("use600")
    public boolean use600;

    @SerializedName("use50")
    public boolean use50;
}
