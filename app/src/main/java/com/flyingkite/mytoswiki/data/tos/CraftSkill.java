package com.flyingkite.mytoswiki.data.tos;

import com.google.gson.annotations.SerializedName;

import java.util.Locale;

public class CraftSkill {
    @SerializedName(CRS.level)
    public int level;
    @SerializedName(CRS.score)
    public int score;
    @SerializedName(CRS.detail)
    public String detail = "";

    @Override
    public String toString() {
        return String.format(Locale.US, "%s : %s -> %s", level, score, detail);
    }
}