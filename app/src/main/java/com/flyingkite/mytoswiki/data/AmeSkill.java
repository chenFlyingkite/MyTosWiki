package com.flyingkite.mytoswiki.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.flyingkite.mytoswiki.room.ameskill.AA;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity(tableName = AA.AmeDB)
public class AmeSkill {
    @SerializedName(AA.skillName)
    @PrimaryKey
    @ColumnInfo
    @NonNull
    public String skillName = "";

    @SerializedName(AA.skillLink)
    @ColumnInfo
    public String skillLink;

    @SerializedName(AA.skillCDMin)
    @ColumnInfo
    public int skillCDMin;

    @SerializedName(AA.skillCDMax)
    @ColumnInfo
    public int skillCDMax;

    @SerializedName(AA.skillDesc)
    @ColumnInfo
    public String skillDesc;

    // of idNorm
    @SerializedName(AA.skillMonsters)
    @ColumnInfo
    public List<String> skillMonsters;

    @Override
    public String toString() {
        return String.format("%s ~ %s -> %s => %s => %s\nmon = %s",
                skillCDMin, skillCDMax, skillName, skillLink, skillDesc, skillMonsters);
    }
}