package com.flyingkite.mytoswiki;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.AnyRes;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.widget.Toast;

public class App extends MultiDexApplication {
    public static App me;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        me = this;
    }

    public static void showToast(@StringRes int id) {
        Toast.makeText(me, id, Toast.LENGTH_LONG).show();
    }

    public static void showToast(String s) {
        Toast.makeText(me, s, Toast.LENGTH_LONG).show();
    }

    public static Uri getUriOfResource(@AnyRes int resId) {
        Resources r = me.getResources();

        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + r.getResourcePackageName(resId)
                + '/' + r.getResourceTypeName(resId)
                + '/' + r.getResourceEntryName(resId));
    }

    public static int getColoar(@ColorRes int id) {
        return me.getResources().getColor(id);
    }
}
