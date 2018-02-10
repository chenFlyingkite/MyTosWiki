package com.flyingkite.mytoswiki.data;

import com.google.gson.annotations.SerializedName;

public class TosCardRMDKVIR {

////////////////
// Basic Profile
////////////////
    @SerializedName("display_num")
    public String display_num;

    @SerializedName("icon_name")
    public String icon_name;

    @SerializedName("icon_url")
    public String icon_url;

    @SerializedName("imageId")
    public String imageId;

    @SerializedName("img_url")
    public String img_url;

////////////////
// Basic Profile, Common selection
////////////////

    @SerializedName("number")
    public int number;

    @SerializedName("starCount")
    public int starCount;

    @SerializedName("name")
    public String name;

    @SerializedName("ethnicity")
    public String ethnicity;

    @SerializedName("series")
    public String series;

    @SerializedName("property")
    public String property;

    @SerializedName("spacing")
    public int spacing;

////////////////
// Levels & Experience
////////////////

    @SerializedName("levelMaxExperience")
    public int levelMaxExperience;

    @SerializedName("feedExperience")
    public int feedExperience;

    @SerializedName("levelInit")
    public int levelInit;

    @SerializedName("levelInitHP")
    public int levelInitHP;

    @SerializedName("levelInitAttack")
    public int levelInitAttack;

    @SerializedName("levelInitResilience")
    public int levelInitResilience;

    @SerializedName("levelInitTotal")
    public int levelInitTotal;

    @SerializedName("levelMax")
    public int levelMax;

    @SerializedName("levelMaxHP")
    public int levelMaxHP;

    @SerializedName("levelMaxAttack")
    public int levelMaxAttack;

    @SerializedName("levelMaxResilience")
    public int levelMaxResilience;

    @SerializedName("levelMaxTotal")
    public int levelMaxTotal;

////////////////
// Levels & Experience - All max
////////////////

    @SerializedName("allMaxHP")
    public int allMaxHP;
    @SerializedName("allMaxAttack")
    public int allMaxAttack;
    @SerializedName("allMaxResilience")
    public int allMaxResilience;
    @SerializedName("allMaxTotal")
    public int allMaxTotal;

////////////////
// Skill - Normal
////////////////

    @SerializedName("skillActivityName")
    public String skillActivityName;

    @SerializedName("skillActivityDesc")
    public String skillActivityDesc;

    @SerializedName("cdMin")
    public String cdMin;

    @SerializedName("cdMax")
    public String cdMax;

    @SerializedName("skillActivityName2")
    public String skillActivityName2;

    @SerializedName("skillActivityDesc2")
    public String skillActivityDesc2;

    @SerializedName("cdMin2")
    public String cdMin2;

    @SerializedName("cdMax2")
    public String cdMax2;

////////////////
// Skill - Leader
////////////////

    @SerializedName("skillLeaderName")
    public String skillLeaderName;

    @SerializedName("skillLeaderDesc")
    public String skillLeaderDesc;


////////////////
// Evolutions card number & required card materials
////////////////

    @SerializedName("beforeNumber")
    public String beforeNumber;

    @SerializedName("afterNumber")
    public String afterNumber;

    @SerializedName("evolution1")
    public String evolution1;

    @SerializedName("evolution2")
    public String evolution2;

    @SerializedName("evolution3")
    public String evolution3;

    @SerializedName("evolution4")
    public String evolution4;

    @SerializedName("evolution5")
    public String evolution5;


////////////////
// Others
////////////////

    @SerializedName("cardFall")
    public String cardFall;

    @SerializedName("sublimation_num")
    public String sublimation_num;

    @SerializedName("sublimation_name")
    public String sublimation_name;

    @SerializedName("liberation_name")
    public String liberation_name;

    @SerializedName("hell_name")
    public String hell_name;

    @SerializedName("reincarnation_name")
    public String reincarnation_name;
}
