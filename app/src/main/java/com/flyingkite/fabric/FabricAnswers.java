package com.flyingkite.fabric;

import android.os.Bundle;

import com.flyingkite.crashlytics.CrashReport;
import com.flyingkite.firebase.CloudMessaging;
import com.flyingkite.firebase.RemoteConfig;
import com.flyingkite.firebase.RemoteConfigKey;
import com.flyingkite.library.log.Loggable;
import com.flyingkite.library.util.ThreadUtil;
import com.flyingkite.mytoswiki.App;
import com.flyingkite.mytoswiki.BuildConfig;
import com.flyingkite.mytoswiki.dialog.FeedbackException;
import com.flyingkite.mytoswiki.util.OkHttpUtil;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Map;

import flyingkite.log.L;

public class FabricAnswers {
    private static final String TAG = "FabricAnswers";

    //-- Tool bars
    public static void logSkillEat(Map<String, String> attributes) {
        logCustom("SkillEat", attributes);
    }

    public static void logWeb(Map<String, String> attributes) {
        logCustom("Web", attributes);
    }

    public static void logFeedback(Map<String, String> attributes) {
        logCustom("Feedback", attributes);
    }

    // Open crafts dialog
    public static void logCraftDialog(Map<String, String> attributes) {
        logCustom("CraftDialog", attributes);
    }

    public static void logBulletin(Map<String, String> attributes) {
        logCustom("Bulletin", attributes);
    }

    public static void logHelp(Map<String, String> attributes) {
        logCustom("Help", attributes);
    }

    public static void logStageMemo(Map<String, String> attributes) {
        logCustom("StageMemo", attributes);
    }

    public static void logFarmPool(Map<String, String> attributes) {
        logCustom("FarmPool", attributes);
    }

    public static void logCardSeal(Map<String, String> attributes) {
        logCustom("SealDraw", attributes);
    }

    public static void logTosEventMemo(Map<String, String> attributes) {
        logCustom("TosEventMemo", attributes);
    }

    public static void logSummonerLevel(Map<String, String> attributes) {
        logCustom("SummonerLevel", attributes);
    }

    public static void logMonsterLevel(Map<String, String> attributes) {
        logCustom("MonsterLevel", attributes);
    }

    public static void logMonsterEat(Map<String, String> attributes) {
        logCustom("MonsterEat", attributes);
    }

    public static void logFavorite(Map<String, String> attributes) {
        logCustom("Favorite", attributes);
    }

    public static void logWebPin(Map<String, String> attributes) {
        logCustom("WebPin", attributes);
    }

    public static void logRealmStage(Map<String, String> attributes) {
        logCustom("RealmStage", attributes);
    }

    public static void logUltimateStage(Map<String, String> attributes) {
        logCustom("UltimateStage", attributes);
    }

    public static void logMainStage(Map<String, String> attributes) {
        logCustom("MainStage", attributes);
    }

    public static void logRelicStage(Map<String, String> attributes) {
        logCustom("RelicStage", attributes);
    }

    public static void logStoryStage(Map<String, String> attributes) {
        logCustom("StoryStage", attributes);
    }

    public static void logDailyStage(Map<String, String> attributes) {
        logCustom("DailyStage", attributes);
    }

    public static void logStamina(Map<String, String> attributes) {
        logCustom("Stamina", attributes);
    }
    //-- Tool bars

    //-- Tos Items
    public static void logCardFragment(Map<String, String> attributes) {
        logCustom("CardFragment", attributes);
    }

    public static void logMyPack(Map<String, String> attributes) {
        logCustom("MyPack", attributes);
    }

    // Show up one card
    public static void logCard(Map<String, String> attributes) {
        logCustom("Card", attributes);
    }

    // Show up one craft
    public static void logCraft(Map<String, String> attributes) {
        logCustom("Craft", attributes);
    }

    public static void logSelectCraft(Map<String, String> attributes) {
        logCustom("SelectCraft", attributes);
    }

    public static void logRunestone(Map<String, String> attributes) {
        logCustom("Runestone", attributes);
    }

    public static void logEditRunestone(Map<String, String> attributes) {
        logCustom("EditRunestone", attributes);
    }

    public static void logFixRunestone(Map<String, String> attributes) {
        logCustom("FixRunestone", attributes);
    }
    //-- Tos Items

    //-- App statistics
    public static void logAppOnCreate() {
        logIP();
//        ContentViewEvent c = new ContentViewEvent();
//        c.putContentName("App.OnCreate");
//        c = addAttr(c, attributes);
//
//        c.putCustomAttribute("FCMToken", token());
//        Answers.getInstance().logContentView(c);
//        if (BuildConfig.DEBUG) {
//            log("log %s", c);
//        }
    }

    public static void logIP() {
        boolean on = RemoteConfig.getBoolean(RemoteConfigKey.APP_SEND_IP_INFO);
        if (App.isNetworkConnected() && on) {
            ThreadUtil.runOnWorkerThread(() -> {
                String link = "http://ip-api.com/json";
                final String s = OkHttpUtil.getResponse(link, null);
                ThreadUtil.runOnUiThread(() -> {
                    try {
                        String t = null;
                        t.length();
                    } catch (NullPointerException e) {
                        FeedbackException fe = new FeedbackException("ipapi\n" + s);
                        L.log("fe = %s", fe);
                        CrashReport.logException(fe);
                    }
                });
            });
        }
    }
    //-- App statistics
//    2018-08-14 12:19:57.921 E/Answers: Invalid user input detected
//    java.lang.IllegalArgumentException: String is too long, truncating to 100 characters

    private static String token() {
        String s = CloudMessaging.getToken();
        if (s.length() > 100) {
            s = s.substring(0, 100);
        }
        return s;
    }

    public static void logCustom(String name, Map<String, String> attributes) {
        Bundle p = new Bundle();
        if (attributes != null) {
            for (Map.Entry<String, String> e : attributes.entrySet()) {
                p.putString(e.getKey(), e.getValue());
            }
        }
        FirebaseAnalytics.getInstance(App.me).logEvent("zz_" + name, p);

        if (BuildConfig.DEBUG) {
            log("log %s, %s", name, p);
        }
    }

    private static void log(String message) {
        z.logE(message);
    }

    private static void log(String fmt, Object... params) {
        z.logE(fmt, params);
    }

    private static final Loggable z = new Loggable() {
        @Override
        public String LTag() {
            return TAG;
        }
    };
}
