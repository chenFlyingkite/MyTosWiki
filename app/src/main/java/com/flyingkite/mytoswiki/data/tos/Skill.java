package com.flyingkite.mytoswiki.data.tos;

import com.google.gson.annotations.SerializedName;

public class Skill {
    //------------
    //---- Basic info, 基礎卡片內容
    //------------

    /** Normalized ID, in form of %04d */
    @SerializedName(SK.idNorm)
    public String idNorm = "";

    /** Wiki link of skill */
    @SerializedName(SK.wikiLink)
    public String wikiLink = "";

    /** Skill's name, unique,  */
    @SerializedName(SK.name)
    public String name = "";

    /** Skill's detail content */
    @SerializedName(SK.effect)
    public String effect = "";

    /** CD min */
    @SerializedName(SK.cdMin)
    public int cdMin = 0;

    /** CD max */
    @SerializedName(SK.cdMax)
    public int cdMax = 0;

    public boolean isLeader() {
        return cdMin < 0 && cdMax < 0;
    }

    public boolean isActive() {
        return cdMin >= 0 && cdMax >= 0;
    }
}
