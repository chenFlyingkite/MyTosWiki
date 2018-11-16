package com.flyingkite.mytoswiki.data.tos;

import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import flyingkite.log.Formattable;

public abstract class BaseCraft implements Parcelable, Formattable {
    @SerializedName(CRS.idNorm)
    public String idNorm = ""; // %4d
    @SerializedName(CRS.name)
    public String name = "";
    @SerializedName(CRS.link)
    public String link = "";
    @SerializedName(CRS.icon)
    public CraftIcon icon = new CraftIcon();

    @SerializedName(CRS.rarity)
    public int rarity; // 1, 2, 3
    @SerializedName(CRS.level)
    public int level;
    @SerializedName(CRS.mode)
    public String mode = "";
    @SerializedName(CRS.charge)
    public String charge = "";
    @SerializedName(CRS.craftSkill)
    public List<CraftSkill> craftSkill = new ArrayList<>();
    @SerializedName(CRS.extraSkill)
    public List<CraftSkill> extraSkill = new ArrayList<>();

    //-- For Common
    @SerializedName(CRS.attrLimit)
    public String attrLimit = "";
    @SerializedName(CRS.raceLimit)
    public String raceLimit = "";

    protected static final Gson gson = new Gson();

    @Override
    public String toString() {
        return _fmt("#%s : %s★, %s", idNorm, rarity, mode);
    }
}
