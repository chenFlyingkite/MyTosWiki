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

import com.flyingkite.mytoswiki.share.ShareHelper;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class App extends MultiDexApplication {
    public static App me;
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh_mm_ss_SSS", Locale.US);
    private static Thread.UncaughtExceptionHandler defaultHandler;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        me = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initCrashHandler();
    }

    private void initCrashHandler() {
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                if (BuildConfig.DEBUG) {
                } else {
                    e.printStackTrace();
                    String name = String.format(Locale.US, "crash-%s.txt", timeFormat.format(new Date()));
                    try {
                        e.printStackTrace(new PrintStream(ShareHelper.cacheName(name)));
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    }
                }
                defaultHandler.uncaughtException(t, e);
            }
        });
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
