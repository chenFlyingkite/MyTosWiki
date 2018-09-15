package com.flyingkite.mytoswiki.data.stage;

import com.google.gson.annotations.SerializedName;

public class Stage {
    @SerializedName("link")
    public String link = "";

    @SerializedName("name")
    public String name = "";

    @SerializedName("icon")
    public String icon = ""; // card's idNorm

    @Override
    public String toString() {
        return icon + ", " + name + " -> " + link;
    }
}
