package com.flyingkite.mytoswiki.data.tos;

import com.google.gson.annotations.SerializedName;

public class CraftIcon {
    @SerializedName(CRS.iconLink)
    public String iconLink = "";

    @SerializedName(CRS.iconKey)
    public String iconKey = "";

    @Override
    public String toString(){
        return iconKey + " " + iconLink;
    }
}
