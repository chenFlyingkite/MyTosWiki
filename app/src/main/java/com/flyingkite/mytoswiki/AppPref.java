package com.flyingkite.mytoswiki;

import android.content.Context;

import com.flyingkite.library.preference.BasePreference;

public class AppPref extends BasePreference {

    public AppPref() {
        this(App.me);
    }

    public AppPref(Context context) {
        super(context);
    }

    public AppPref(Context context, String name) {
        super(context, name);
    }

    //---- MainActivity preference
    // Tool bar
    private static final String KEY_showAppTool = "showAppTool";

    public boolean getShowAppTool() {
        return getBoolean(KEY_showAppTool, true);
    }

    public void setShowAppTool(boolean v) {
        putBoolean(KEY_showAppTool, v);
    }

    // Tool bar
    private static final String KEY_showFavorite = "showFavorite";

    public boolean getShowFavorite() {
        return getBoolean(KEY_showFavorite, false);
    }

    public void setShowFavorite(boolean v) {
        putBoolean(KEY_showFavorite, v);
    }
    //----
}
