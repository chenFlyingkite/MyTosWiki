package com.flyingkite.mytoswiki.data.tos;

import com.google.gson.annotations.SerializedName;

public class Skill extends SkillLite {
    //------------
    //---- Basic info, 基礎卡片內容
    //------------

    /** Normalized ID, in form of %04d */
    @SerializedName(SK.idNorm)
    public String idNorm = "";

    /** Wiki link of skill */
    @SerializedName(SK.wikiLink)
    public String wikiLink = "";

    public SkillLite lite() {
        SkillLite t = new SkillLite();
        t.name = name;
        t.cdMax = cdMax;
        t.cdMin = cdMin;
        t.effect = effect;
        return t;
    }
}
