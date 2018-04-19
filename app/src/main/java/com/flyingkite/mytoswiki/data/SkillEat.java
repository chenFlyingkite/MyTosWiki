package com.flyingkite.mytoswiki.data;

import com.google.gson.annotations.SerializedName;

public class SkillEat {
    @SerializedName("fromLevel")
    public int fromLevel;

    @SerializedName("progress")
    public int progress;

    @SerializedName("toLevel")
    public int toLevel;

    @SerializedName("use600")
    public boolean use600;
}
