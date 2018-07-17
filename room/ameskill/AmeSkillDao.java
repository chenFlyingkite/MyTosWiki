package com.flyingkite.mytoswiki.room.ameskill;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverter;
import android.text.TextUtils;

import com.flyingkite.mytoswiki.data.AmeSkill;

import java.util.Arrays;
import java.util.List;

@Dao
public interface AmeSkillDao {

    @Query("SELECT * FROM " + AA.AmeDB)
    List<AmeSkill> all();

    @Query("SELECT * FROM " + AA.AmeDB + " WHERE " + AA.skillCDMax + " IN (:cd) ")
    List<AmeSkill> getAll2(int... cd);

    @Insert
    void insertAll(AmeSkill... cards);

    @Delete
    void delete(AmeSkill card);
}

class ZConverter {
    @TypeConverter
    public static List<String> split(String data) {
        return Arrays.asList(data.split(","));
    }

    @TypeConverter
    public static String join(List<String> data) {
        return TextUtils.join(",", data);
    }

}
