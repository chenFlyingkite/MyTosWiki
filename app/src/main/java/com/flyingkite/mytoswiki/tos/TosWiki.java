package com.flyingkite.mytoswiki.tos;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.NonNull;

import com.flyingkite.fabric.FabricAnswers;
import com.flyingkite.mytoswiki.BuildConfig;
import com.flyingkite.mytoswiki.data.CardFavor;
import com.flyingkite.mytoswiki.data.WebPin;
import com.flyingkite.mytoswiki.data.stage.MainStage;
import com.flyingkite.mytoswiki.data.stage.RelicStage;
import com.flyingkite.mytoswiki.data.stage.StageGroup;
import com.flyingkite.mytoswiki.data.tos.BaseCraft;
import com.flyingkite.mytoswiki.data.tos.CraftsArm;
import com.flyingkite.mytoswiki.data.tos.CraftsNormal;
import com.flyingkite.mytoswiki.data.tos.TosCard;
import com.flyingkite.mytoswiki.dialog.OnAction;
import com.flyingkite.mytoswiki.share.ShareHelper;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import flyingkite.library.android.log.Loggable;
import flyingkite.library.android.util.GsonUtil;
import flyingkite.library.android.util.ThreadUtil;
import flyingkite.library.androidx.TicTac2;
import flyingkite.library.java.tool.TaskMonitor;
import flyingkite.library.java.util.ArrayUtil;
import flyingkite.library.java.util.URLUtil;

public class TosWiki {
    private TosWiki() {}
    // Main Contents
    private static TosCard[] allCards;
    private static CraftsNormal[] normalCrafts;
    private static CraftsArm[] armCrafts;
    private static final Map<String, TosCard> allCardsByIdNorm = new HashMap<>();
    private static final Map<String, List<TosCard>> allCardsBySeries = new HashMap<>();
    private static final Map<String, BaseCraft> allCraftsByIdNorm = new HashMap<>();
    private static final Set<String> switchChangedIdNorms = new HashSet<>(); // the card ids that after switched
    private static CardFavor cardFavor;
    private static WebPin webPin;
    private static MainStage[] mainStages;
    private static RelicStage[][] relicStages;
    private static StageGroup[] storyStages;
    private static StageGroup ultimStages;
    private static MainStage[] realmStages;
    private static List<List<String>> evolvePath = new ArrayList<>();
    // Observers
    private static final List<OnAction> favorActions = new ArrayList<>();
    // Tags for Task monitor
    public static final String TAG_ALL_CARDS = "AllCards";
    public static final String TAG_NORMAL_CRAFTS = "Crafts";
    public static final String TAG_ARM_CRAFTS = "ArmCrafts";
    public static final String TAG_CARD_FAVOR = "CardFavor";
    public static final String TAG_MAIN_STAGE = "MainStage";
    public static final String TAG_RELIC_PASS = "RelicPass";
    public static final String TAG_STORY_STAGE = "StoryStage";
    public static final String TAG_REALM_STAGE = "RealmStage";
    public static final String TAG_WEB_PIN = "WebPin";
    public static final String TAG_ULTIM_STAGE = "UltimStage"; // Ultimate stages
    public static final String TAG_EVOLVE_PATH = "EvolvePath";
    public static final String[] TAG_ALL_TASKS = {
            TAG_ALL_CARDS, TAG_NORMAL_CRAFTS, TAG_ARM_CRAFTS, TAG_CARD_FAVOR,
            TAG_MAIN_STAGE, TAG_RELIC_PASS, TAG_STORY_STAGE, TAG_REALM_STAGE,
            TAG_WEB_PIN, TAG_ULTIM_STAGE, TAG_EVOLVE_PATH
    };

    public static void init(Context ctx) {
        AssetManager am = ctx.getAssets();
        ExecutorService p = ThreadUtil.cachedThreadPool;

        p.submit(() -> {
            TicTac2 t = new TicTac2();
            t.tic();
            allCards = GsonUtil.loadAsset("cardList.json", TosCard[].class, am);
            t.tac("%s cards loaded", len(allCards));
            //checkCards();

            t.tic();
            // Records card info
            allCardsByIdNorm.clear();
            allCardsBySeries.clear();
            switchChangedIdNorms.clear();
            for (TosCard c : allCards) {
                // Make card to be (idNorm -> card)
                allCardsByIdNorm.put(c.idNorm, c);

                // Collect (series -> cards)
                List<TosCard> s = allCardsBySeries.get(c.series);
                if (s == null) {
                    s = new ArrayList<>();
                }
                s.add(c);
                allCardsBySeries.put(c.series, s);

                // Collect switched (idNorms)
                if (TextUtils.isEmpty(c.switchChange)) {
                } else { // changed
                    switchChangedIdNorms.add(c.switchChange);
                }
            }

            t.tac("%s in set OK", allCardsByIdNorm.size());
            z.logE("%s series", allCardsBySeries.size());
            z.logE("%s switched", switchChangedIdNorms.size());
            monitorDB.notifyClientsState();
        });

        p.submit(() -> {
            TicTac2 t = new TicTac2.v();
            t.tic();
            normalCrafts = GsonUtil.loadAsset("crafts.json", CraftsNormal[].class, am);
            t.tac("%s craft loaded, Norm", len(normalCrafts));

            for (CraftsNormal c : normalCrafts) {
                allCraftsByIdNorm.put(c.idNorm, c);
            }
            monitorDB.notifyClientsState();
        });

        p.submit(() -> {
            TicTac2 t = new TicTac2.v();
            t.tic();
            armCrafts = GsonUtil.loadAsset("armCrafts.json", CraftsArm[].class, am);
            t.tac("%s craft loaded, Arm", len(armCrafts));

            for (CraftsArm c : armCrafts) {
                allCraftsByIdNorm.put(c.idNorm, c);
            }
            monitorDB.notifyClientsState();
        });

        p.submit(() -> {
            TicTac2 t = new TicTac2.v();
            t.tic();
            File f = getTosCardFavorFile();
            if (f.exists()) {
                cardFavor = GsonUtil.fromFile(f, CardFavor.class);
            }
            if (cardFavor == null) {
                cardFavor = new CardFavor();
            }
            logFavorite(cardFavor);
            t.tac("%s cards favored", cardFavor.favors.size());
            monitorDB.notifyClientsState();
        });

        p.submit(() -> {
            TicTac2 t = new TicTac2.v();
            t.tic();
            mainStages = GsonUtil.loadAsset("mainStage.json", MainStage[].class, am);
            t.tac("%s main stages loaded", mainStages.length);
            monitorDB.notifyClientsState();
        });

        p.submit(() -> {
            TicTac2 t = new TicTac2.v();
            t.tic();
            relicStages = GsonUtil.loadAsset("relicPass.json", RelicStage[][].class, am);
            t.tac("%s relic stages loaded", relicStages.length);
            monitorDB.notifyClientsState();
        });

        p.submit(() -> {
            TicTac2 t = new TicTac2.v();
            t.tic();
            storyStages = GsonUtil.loadAsset("storyStage.json", StageGroup[].class, am);
            t.tac("%s story stages loaded", storyStages.length);
            monitorDB.notifyClientsState();
        });

        p.submit(() -> {
            TicTac2 t = new TicTac2.v();
            t.tic();
            ultimStages = GsonUtil.loadAsset("ultimateStage.json", StageGroup.class, am);
            t.tac("%s ultimate stages loaded", ultimStages.stages.size());
            monitorDB.notifyClientsState();
        });

        p.submit(() -> {
            TicTac2 t = new TicTac2.v();
            t.tic();
            realmStages = GsonUtil.loadAsset("voidRealm.json", MainStage[].class, am);
            t.tac("%s realm stages loaded", realmStages.length);
            monitorDB.notifyClientsState();
        });

        p.submit(() -> {
            TicTac2 t = new TicTac2.v();
            t.tic();
            File f = getWebPinFile();
            if (f.exists()) {
                webPin = GsonUtil.fromFile(f, WebPin.class);
            }
            if (webPin == null) {
                webPin = new WebPin();
            }
            logWebPin(webPin);
            t.tac("web pinned as\n%s", webPin);
            monitorDB.notifyClientsState();
        });

        p.submit(() -> {
            TicTac2 t = new TicTac2.v();
            t.tic();
            evolvePath = GsonUtil.loadAsset("evolvePath.json", evolvePath.getClass(), am);
            int n = 0;
            for (int i = 0; i < evolvePath.size(); i++) {
                n = Math.max(n, evolvePath.get(i).size());
            }
            t.tac("%s evolve path loaded, max = %d", evolvePath.size(), n);
            monitorDB.notifyClientsState();
        });
    }

    public static MainStage[] getRealmStages() {
        return realmStages;
    }

    public static List<List<String>> getEvolvePath() {
        return evolvePath;
    }

    public static StageGroup getUltimateStages() {
        return ultimStages;
    }

    public static MainStage[] getMainStages() {
        return mainStages;
    }

    public static RelicStage[][] getRelicStage() {
        return relicStages;
    }

    public static StageGroup[] getStoryStages() {
        return storyStages;
    }

    public static CardFavor getCardFavor() {
        return cardFavor;
    }

    public static void saveCardFavor(CardFavor cf) {
        GsonUtil.writeFile(getTosCardFavorFile(), new Gson().toJson(cf));
    }

    public static void joinFavorAction(@NonNull OnAction action) {
        favorActions.add(action);
        notifyFavor();
    }

    public static void notifyFavor() {
        for (OnAction a : favorActions) {
            a.onChanged();
        }
    }

    public static void leftFavorAction(@NonNull OnAction action) {
        favorActions.remove(action);
    }

    private static File getWebPinFile() {
        return ShareHelper.extFilesFile("webPin.txt");
    }

    public static WebPin getWebPin() {
        return webPin;
    }

    public static void saveWebPin(WebPin w) {
        GsonUtil.writeFile(getWebPinFile(), new Gson().toJson(w));
    }

    private static File getTosCardFavorFile() {
        return ShareHelper.extFilesFile("cardFavor.txt");
    }

    public static int getAllCardsCount() {
        return len(allCards);
    }

    public static TosCard[] allCards() {
        return copy(allCards);
    }

    public static List<TosCard> getCardsBySeries(String series) {
        return allCardsBySeries.get(series);
    }

    public static boolean isSwitchChangedCard(String idNorm) {
        return switchChangedIdNorms.contains(idNorm);
    }

    public static TosCard getCardByIdNorm(String id) {
        TosCard c = allCardsByIdNorm.get(id);
        return c;
    }

    public static BaseCraft getCraftByIdNorm(String id) {
        BaseCraft c = allCraftsByIdNorm.get(id);
        return c;
    }

    public static CraftsNormal[] allNormalCrafts() {
        return copy(normalCrafts);
    }

    public static CraftsArm[] allArmCrafts() {
        return copy(armCrafts);
    }

    private static final TaskMonitor.TaskOwner monitorSource = new TaskMonitor.TaskOwner() {
        @Override
        public int taskCount() {
            return TAG_ALL_TASKS.length;
        }

        @Override
        public boolean isTaskDone(int index) {
            String tag = TAG_ALL_TASKS[index];
            switch (tag) {
                case TAG_ARM_CRAFTS: return armCrafts != null;
                case TAG_NORMAL_CRAFTS: return normalCrafts != null;
                case TAG_ALL_CARDS: return allCards != null;
                case TAG_CARD_FAVOR: return cardFavor != null;
                case TAG_MAIN_STAGE: return mainStages != null;
                case TAG_RELIC_PASS: return relicStages != null;
                case TAG_STORY_STAGE: return storyStages != null;
                case TAG_REALM_STAGE: return realmStages != null;
                case TAG_ULTIM_STAGE: return ultimStages != null;
                case TAG_EVOLVE_PATH: return evolvePath != null;
                case TAG_WEB_PIN: return webPin != null;
                //case TAG_AME_SKILL: return ameSkills != null;
                default:
                    throw new NullPointerException(taskCount() + " tasks but did not define done for " + index);
            }
        }

        @Override
        public String getTaskTag(int index) {
            return TAG_ALL_TASKS[index];
        }
    };
    private static final TaskMonitor monitorDB = new TaskMonitor(monitorSource);

    // attend & absent
    // retain & remove
    public static void attendDatabaseTasks(@NonNull TaskMonitor.OnTaskState listener) {
        ExecutorService p = ThreadUtil.cachedThreadPool;
        p.submit(() -> {
            monitorDB.registerClient(listener);
        });
    }



    //-- Events
    private static void logFavorite(CardFavor f) {
        Map<String, String> m = new HashMap<>();
        String card = String.valueOf(f == null ? 0 : f.favors.size());
        m.put("card", card);
        FabricAnswers.logFavorite(m);
    }

    private static void logWebPin(WebPin w) {
        Map<String, String> m = new HashMap<>();
        m.put("w1", URLUtil.liteLink(w.web1));
        m.put("w2", URLUtil.liteLink(w.web2));
        m.put("w3", URLUtil.liteLink(w.web3));
        FabricAnswers.logWebPin(m);
    }

    //-- Events

    private static void checkCards() {
        if (!BuildConfig.DEBUG) return;
        String key;

        Map<String, Integer> a = new HashMap<>();
        Map<String, Integer> r = new HashMap<>();
        Map<String, Integer> x = new HashMap<>();
        for (TosCard c : allCards) {
            // Count attr
            key = c.attribute;
            if (a.get(key) == null) {
                a.put(key, 1);
            } else {
                int n = a.get(key);
                a.put(key, n+1);
            }

            // Count race
            key = c.race;
            if (r.get(key) == null) {
                r.put(key, 1);
            } else {
                int n = r.get(key);
                r.put(key, n+1);
            }

            // Count rarity
            key = c.rarity + "";
            if (x.get(key) == null) {
                x.put(key, 1);
            } else {
                int n = x.get(key);
                x.put(key, n+1);
            }
        }
        z.logE("attr   -> %s", a);
        z.logE("race   -> %s", r);
        z.logE("rarity -> %s", x);
    }

    private static <T> int len(T[] a) {
        return a == null ? 0 : a.length;
    }

    private static <T> T[] copy(T[] a) {
        return ArrayUtil.copyOf(a);
    }

    private static final Loggable z = new Loggable() {
        @Override
        public void log(String message) {
            Log.i(LTag(), message);
        }
    };
}
