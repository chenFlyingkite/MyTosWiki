package com.flyingkite.mytoswiki;

import android.content.Context;

import com.flyingkite.library.log.Loggable;
import com.flyingkite.library.preference.BasePreference;

import java.util.Map;

public class AppPref extends BasePreference implements Loggable {

    public AppPref() {
        this(App.me);
    }

    public AppPref(Context context) {
        super(context);
    }

    public AppPref(Context context, String name) {
        super(context, name);
    }

    public void printAll() {
        Map<String, ?> m = mPreference.getAll();
        for (String s : m.keySet()) {
            logE("%s -> %s", s, m.get(s));
        }
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

    // User uid of pack
    private static final String KEY_UserUid = "UserUid";

    public String getUserUid() {
        return getString(KEY_UserUid, "");
    }

    public void setUserUid(String s) {
        putString(KEY_UserUid, s);
    }

    // User verify code of pack
    private static final String KEY_UserVerify = "UserVerify";

    public String getUserVerify() {
        return getString(KEY_UserVerify, "");
    }

    public void setUserVerify(String s) {
        putString(KEY_UserVerify, s);
    }

    // User pack's token
    private static final String KEY_UserPackToken = "UserPackToken";

    public String getUserPackToken() {
        return getString(KEY_UserPackToken, "");
    }

    public void setUserPackToken(String s) {
        putString(KEY_UserPackToken, s);
    }

    // User uid of pack
    private static final String KEY_UserTosInventory = "UserTosInventory";

    public String getUserTosInventory() {
        return getString(KEY_UserTosInventory, "");
    }

    public void setUserTosInventory(String s) {
        putString(KEY_UserTosInventory, s);
    }

    // User uid of pack
    private static final String KEY_CardsSearchText = "CardsSearchText";

    public String getCardsSearchText() {
        return getString(KEY_CardsSearchText, "");
    }

    public void setCardsSearchText(String s) {
        putString(KEY_CardsSearchText, s);
    }
    //----
}
