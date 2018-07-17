package com.flyingkite.mytoswiki.room.card;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverter;
import android.text.TextUtils;

import com.flyingkite.mytoswiki.data.tos.TosCard;

import java.util.Arrays;
import java.util.List;

@Dao
public interface TosCardDao {

    @Query("SELECT * FROM " + AA.CardDB)
    List<TosCard> all();

    @Query("SELECT * FROM " + AA.CardDB
            + " WHERE (" + AA.attribute + " IN (:attr)) "
            //+ " AND (" + AA.race + " IN (:race))"
            //+ " AND (" + AA.rarity + " IN (:rarity))"
    )
    List<TosCard> attr(List<String> attr);

    @Query("SELECT * FROM " + AA.CardDB
            + " WHERE (" + AA.attribute + " IN (:attr)) "
            + " AND (" + AA.race + " IN (:race))"
            //+ " AND (" + AA.rarity + " IN (:rarity))"
    )
    List<TosCard> attr_race(List<String> attr, List<String> race);

    @Query("SELECT * FROM " + AA.CardDB
            + " WHERE (" + AA.attribute + " IN (:attr)) "
            + " AND (" + AA.race + " IN (:race))"
            + " AND (" + AA.rarity + " IN (:rarity))"
    )
    List<TosCard> attr_race_rarity(List<String> attr, List<String> race, List<String> rarity);

    @Insert
    void insertAll(TosCard... cards);

    @Query("SELECT DISTINCT " + AA.attribute + " FROM " + AA.CardDB)
    List<String> allAttr();

    @Query("SELECT DISTINCT " + AA.race + " FROM " + AA.CardDB)
    List<String> allRace();

//    @Query("DELETE FROM " + AA.CardDB)
//    void drop();
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