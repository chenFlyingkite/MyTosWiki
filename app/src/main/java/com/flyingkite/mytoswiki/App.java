package com.flyingkite.mytoswiki;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.widget.Toast;

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

    public static void showToast(@StringRes int id) {
        Toast.makeText(me, id, Toast.LENGTH_LONG).show();
    }

    public static void showToast(String s) {
        Toast.makeText(me, s, Toast.LENGTH_LONG).show();
    }
}
