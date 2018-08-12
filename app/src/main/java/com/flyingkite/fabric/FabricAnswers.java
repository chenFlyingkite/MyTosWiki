package com.flyingkite.fabric;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.flyingkite.library.log.Loggable;

import java.util.Map;

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

    public static void logStageMemo(Map<String, String> attributes) {
        logCustom("StageMemo", attributes);
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
    //-- Tool bars

    //-- Tos Items
    // Show up one craft
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
    //-- Tos Items


    private static void logCustom(String name, Map<String, String> attributes) {
        CustomEvent c = new CustomEvent(name);
        c = addAttr(c, attributes);
        Answers.getInstance().logCustom(c);
        log("log %s", c);
    }

    private static CustomEvent addAttr(CustomEvent c, Map<String, String> attributes) {
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
