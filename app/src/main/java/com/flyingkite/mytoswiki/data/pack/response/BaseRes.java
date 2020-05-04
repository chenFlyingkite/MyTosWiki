package com.flyingkite.mytoswiki.data.pack.response;

import com.google.gson.annotations.SerializedName;

public class BaseRes {
    @SerializedName("isSuccess")
    public int isSuccess;

    @SerializedName("errorCode")
    public int errorCode;

    @SerializedName("errorMessage")
    public String errorMessage = "";
}
