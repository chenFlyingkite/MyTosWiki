package com.flyingkite.mytoswiki.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Texts {
    @SerializedName("data")
    public List<String> data = new ArrayList<>();
}
