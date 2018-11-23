package com.flyingkite.mytoswiki.data.stage;

import com.google.gson.annotations.SerializedName;

public class RelicStage extends Stage {

    @SerializedName("coin")
    public int coin = 0; // stage price

    @Override
    public String toString() {
        return "$" + coin + " = " + super.toString();
    }
}
