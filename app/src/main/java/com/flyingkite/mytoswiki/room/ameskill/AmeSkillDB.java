package com.flyingkite.mytoswiki.room.ameskill;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

@Database(entities = {AmeSkill.class}, version = AmeSkillDB.VERSION)
@TypeConverters({Converter.class})
public abstract class AmeSkillDB extends RoomDatabase {
    public static final int VERSION = 1;

    public abstract AmeSkillDao dao();
}
