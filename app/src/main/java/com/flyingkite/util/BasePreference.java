package com.flyingkite.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.flyingkite.mytoswiki.App;

public class BasePreference {
    private final SharedPreferences mPreference;

    public BasePreference() {
        this(App.me);
    }

    public BasePreference(Context context) {
        this(context, context.getPackageName() + "_preferences");
    }

    public BasePreference(Context context, String preferenceName) {
        mPreference = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
    }

    protected boolean contains(String key) {
        return mPreference.contains(key);
    }

    private Editor edit() {
        return mPreference.edit();
    }

    // -- String Preference --
    protected String getString(String key) {
        return getString(key, "");
    }

    protected String getString(String key, String defaultValue) {
        return mPreference.getString(key, defaultValue);
    }

    protected void putString(String key, String value) {
        edit().putString(key, value).apply();
    }

    // -- Boolean Preference --
    protected void putBoolean(String key, boolean value) {
        edit().putBoolean(key, value).apply();
    }

    protected boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    protected boolean getBoolean(String key, boolean defaultValue) {
        return mPreference.getBoolean(key, defaultValue);
    }

    // -- Integer Preference --
    protected int getInt(String key) {
        return getInt(key, 0);
    }

    protected int getInt(String key, int defValue) {
        return mPreference.getInt(key, defValue);
    }

    protected void putInt(String key, int value) {
        edit().putInt(key, value).apply();
    }

    // -- Long Preference --
    protected long getLong(String key) {
        return mPreference.getLong(key, 0L);
    }

    protected long getLong(String key, long defValue) {
        return mPreference.getLong(key, defValue);
    }

    protected void putLong(String key, long value) {
        edit().putLong(key, value).apply();
    }

    protected void remove(String key) {
        edit().remove(key).apply();
    }
}
