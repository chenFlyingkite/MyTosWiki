package com.flyingkite.mytoswiki.data.pack.response;

import com.google.gson.annotations.SerializedName;

public class TokenRes extends BaseRes {

    @SerializedName("token")
    public String token = "";

    @SerializedName("user")
    public TokenUser user;
}
