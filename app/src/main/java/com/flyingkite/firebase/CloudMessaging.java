package com.flyingkite.firebase;

import com.flyingkite.library.preference.BasePreference;
import com.flyingkite.library.util.GsonUtil;
import com.flyingkite.mytoswiki.App;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.File;

public class CloudMessaging {
    private static final FCMPref pref = new FCMPref();

    public static String getToken() {
        return pref.getFcmToken();
    }

    public static void setToken(String token) {
        pref.setFcmToken(token);
    }

    private static class FCMPref extends BasePreference {
        private static final String FCM_TOKEN = "fcmToken";

        public FCMPref() {
            super(App.me);
        }

        public String getFcmToken() {
            return getString(FCM_TOKEN);
        }
        public void setFcmToken(String token) {
            putString(FCM_TOKEN, token);
            setToFile(token);
        }

        private void setToFile(String token) {
            // Set to file
            FCMData f = new FCMData();
            f.token = token;
            File file = new File(App.me.getExternalFilesDir("fcm"), "token.txt");
            GsonUtil.writeFile(file, new Gson().toJson(f));
        }
    }

    private static class FCMData {
        @SerializedName("token")
        private String token;
    }
}
