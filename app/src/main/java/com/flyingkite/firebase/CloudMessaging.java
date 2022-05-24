package com.flyingkite.firebase;

import android.content.Context;

import com.flyingkite.mytoswiki.App;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.File;

import flyingkite.library.android.preference.EasyPreference;
import flyingkite.library.android.util.GsonUtil;

public class CloudMessaging {
    private static final FCMPref pref = new FCMPref();

    public static String getToken() {
        return pref.fcmToken.get();
    }

    public static void setToken(String token) {
        pref.fcmToken.set(token);
    }

    private static class FCMPref extends EasyPreference {

        public FCMPref() {
            this(App.me);
        }

        private FCMPref(Context c) {
            super(c, c.getPackageName() + "_preferences");
        }

        public final StringPref fcmToken = new StringPref("fcmToken"){
            @Override
            public void set(String s) {
                super.set(s);
                setToFile(s);
            }

            private void setToFile(String token) {
                // Set to file
                FCMData f = new FCMData();
                f.token = token;
                File file = new File(App.me.getExternalFilesDir("fcm"), "token.txt");
                GsonUtil.writeFile(file, new Gson().toJson(f));
            }
        };
    }

    private static class FCMData {
        @SerializedName("token")
        private String token;
    }
}
