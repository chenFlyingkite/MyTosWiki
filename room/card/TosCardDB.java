package com.flyingkite.mytoswiki.room.card;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.flyingkite.mytoswiki.data.tos.TosCard;

@Database(entities = {TosCard.class}, version = TosCardDB.VERSION)
@TypeConverters({ZConverter.class})
public abstract class TosCardDB extends RoomDatabase {
    // https://developer.android.com/reference/android/arch/persistence/room/Database#exportSchema()

    public static final int VERSION = 1;

    public abstract TosCardDao dao();

//    public void printDB() {
//        Say.Log("%s records in %s", dao().all().size(), getClass().getSimpleName());
//    }
}

