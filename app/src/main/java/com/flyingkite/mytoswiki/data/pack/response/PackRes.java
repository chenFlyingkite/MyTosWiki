package com.flyingkite.mytoswiki.data.pack.response;

import com.flyingkite.mytoswiki.data.pack.PackCard;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PackRes {

    @SerializedName("userData")
    public PackUser userData;

    @SerializedName("cards")
    public List<PackCard> card = new ArrayList<>();
}
