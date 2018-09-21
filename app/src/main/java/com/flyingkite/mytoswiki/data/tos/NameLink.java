package com.flyingkite.mytoswiki.data.tos;

import com.google.gson.annotations.SerializedName;

public class NameLink {
    @SerializedName(NL.name)
    public String name;

    @SerializedName(NL.link)
    public String link;
}
