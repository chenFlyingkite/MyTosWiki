package com.flyingkite.mytoswiki.room.ameskill;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.flyingkite.mytoswiki.data.AmeSkill;

@Database(entities = {AmeSkill.class}, version = AmeSkillDB.VERSION)
@TypeConverters({ZConverter.class})
public abstract class AmeSkillDB extends RoomDatabase {
    public static final int VERSION = 1;

    public abstract AmeSkillDao dao();

//    public void printDB() {
//        Say.Log("%s records in %s", dao().all().size(), getClass().getSimpleName());
//    }
}
