package com.flyingkite.mytoswiki;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

public class App extends MultiDexApplication {
    private static App me;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        me = this;
    }

    public static Context getContext() {
        return me;
    }
}
