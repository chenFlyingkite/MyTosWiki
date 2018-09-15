package com.flyingkite.mytoswiki.data.stage;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import flyingkite.log.Formattable;

public class StageGroup implements Formattable {
    @SerializedName("group")
    public String group = ""; // 殺戮戰窟 in 10

    @SerializedName("stages")
    public List<Stage> stages = new ArrayList<>();

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(_fmt("%s > \n", group));
        for (int i = 0; i < stages.size(); i++) {
            s.append(_fmt("  #%s = %s\n", i, stages.get(i)));
        }

        return s.toString();
    }
}
