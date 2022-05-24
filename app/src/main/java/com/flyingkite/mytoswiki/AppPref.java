package com.flyingkite.mytoswiki;

import android.content.Context;

import flyingkite.library.android.log.Loggable;
import flyingkite.library.android.preference.EasyPreference;

public class AppPref extends EasyPreference implements Loggable {

    public AppPref() {
        this(App.me);
    }

    public AppPref(Context context) {
        super(context, context.getPackageName() + "_preferences");
    }

    //---- MainActivity preference
    // Tool bar
    public final BoolPref showAppTool = new BoolPref("showAppTool", true);

    // Tool bar
    public final BoolPref showFavorite = new BoolPref("showFavorite", true);

    // User uid of pack
    public final StringPref userUid = new StringPref("UserUid", "");

    // User verify code of pack
    public final StringPref userVerify = new StringPref("UserVerify", "");

    // User pack's token
    public final StringPref userPackToken = new StringPref("UserPackToken", "");

    // User uid of pack
    public final StringPref userTosInventory = new StringPref("UserTosInventory", "");

    // Search text on cards
    public final StringPref cardsSearchText = new StringPref("CardsSearchText", "");
    //----
}
