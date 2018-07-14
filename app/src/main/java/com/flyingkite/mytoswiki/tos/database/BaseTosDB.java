package com.flyingkite.mytoswiki.tos.database;

import android.arch.persistence.room.RoomDatabase;

// Database does not work better than for-loop
@Deprecated
public abstract class BaseTosDB<T extends RoomDatabase> {
//    protected static final ExecutorService cachePool = Executors.newCachedThreadPool();
//    public T DB;
//    public Future future;
//
//    public void init(Context ctx, Class<T> clazz) {
//        if (DB == null) {
//            synchronized (this) {
//                if (DB == null) {
//                    DB = Room.inMemoryDatabaseBuilder(ctx, clazz).allowMainThreadQueries().build();
//                }
//            }
//        }
//        future = cachePool.submit(() -> {
//            getTask(ctx).run();
//        });
//    }
//
//    @NonNull
//    public Runnable getTask(Context ctx) {
//        return () -> {};
//    }
}