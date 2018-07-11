package com.flyingkite.mytoswiki.room.ameskill;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface AmeSkillDao {

    @Query("SELECT * FROM " + AA.AmeDB)
    List<AmeSkill> getAll();

    @Query("SELECT * FROM " + AA.AmeDB + " WHERE " + AA.skillCDMax + " IN (:cd) ")
    List<AmeSkill> getAll2(int... cd);

    @Insert
    void insertAll(AmeSkill... cards);

    @Delete
    void delete(AmeSkill card);
}
