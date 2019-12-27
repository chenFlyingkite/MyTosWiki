package com.flyingkite.fabric;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.AnswersEvent;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import com.flyingkite.firebase.CloudMessaging;
import com.flyingkite.library.log.Loggable;
import com.flyingkite.mytoswiki.BuildConfig;

import java.util.Map;

public class FabricAnswers {
    private static final String TAG = "FabricAnswers";

    //-- Tool bars
    public static void logSkillEat(Map<String, String> attributes) {
        logCustom("SkillEat", attributes);
    }

    public static void logTosFragment(Map<String, String> attributes) {
        logCustom("TosFragment", attributes);
    }

    public static void logSkillEatSample(Map<String, String> attributes) {
        logCustom("SkillEatSample", attributes);
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

    public static void logTextEditor(Map<String, String> attributes) {
        logCustom("TextEditor", attributes);
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

    public static void logSelectCard(Map<String, String> attributes) {
        logCustom("SelectCard", attributes);
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
    public static void logAppOnCreate(Map<String, String> attributes) {
        ContentViewEvent c = new ContentViewEvent();
        c.putContentName("App.OnCreate");
        c = addAttr(c, attributes);

        c.putCustomAttribute("FCMToken", token());
        Answers.getInstance().logContentView(c);
        if (BuildConfig.DEBUG) {
            log("log %s", c);
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
        CustomEvent c = new CustomEvent(name);
        c = addAttr(c, attributes);
        c.putCustomAttribute("FCMToken", token());
        Answers.getInstance().logCustom(c);
        if (BuildConfig.DEBUG) {
            log("log %s", c);
        }
    }

    private static <T extends AnswersEvent> T addAttr(T c, Map<String, String> attributes) {
        if (attributes != null) {
            for (String k : attributes.keySet()) {
                String v = attributes.get(k);
                c.putCustomAttribute(k, v);
            }
        }
        return c;
    }

    private static void log(String message) {
        z.logI(message);
    }

    private static void log(String fmt, Object... params) {
        z.logI(fmt, params);
    }

    private static Loggable z = new Loggable() {
        @Override
        public String LTag() {
            return TAG;
        }
    };
}
