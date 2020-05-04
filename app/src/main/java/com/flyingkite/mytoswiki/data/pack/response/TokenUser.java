package com.flyingkite.mytoswiki.data.pack.response;

import com.google.gson.annotations.SerializedName;

public class TokenUser {
    @SerializedName("uid")
    public long uid;

    @SerializedName("name")
    public String name = "";

    @SerializedName("campaignLoginDays")
    public int campaignLoginDays;

    @SerializedName("level")
    public int level;

    @SerializedName("role")
    public int role;
}
