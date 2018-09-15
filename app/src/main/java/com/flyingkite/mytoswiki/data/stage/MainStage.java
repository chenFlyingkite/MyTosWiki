package com.flyingkite.mytoswiki.data.stage;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MainStage {
    @SerializedName("title")
    public String title = ""; // 第十封印

    @SerializedName("substages")
    public List<StageGroup> substages = new ArrayList<>();

    @Override
    public String toString() {
        return title + " : \n  " + substages;
    }
}
