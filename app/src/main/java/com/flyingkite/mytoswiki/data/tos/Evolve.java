package com.flyingkite.mytoswiki.data.tos;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Evolve {
    /** Evolution from card idNorm */
    @SerializedName(TC.evolveFrom)
    public String evolveFrom = "";

    /** Evolution material card idNorm */
    @SerializedName(TC.evolveNeed)
    public List<String> evolveNeed = new ArrayList<>();

    /** Evolution to card idNorm */
    @SerializedName(TC.evolveTo)
    public String evolveTo = "";

    @Override
    public String toString() {
        return String.format("%s <= %s + %s", evolveTo, evolveFrom, evolveNeed);
    }
}
