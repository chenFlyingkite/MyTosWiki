package com.flyingkite.mytoswiki.tos;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.flyingkite.crashlytics.CrashReport;
import com.flyingkite.fabric.FabricAnswers;
import com.flyingkite.library.TicTac2;
import com.flyingkite.library.log.Loggable;
import com.flyingkite.library.util.GsonUtil;
import com.flyingkite.library.util.ThreadUtil;
import com.flyingkite.mytoswiki.data.CardFavor;
import com.flyingkite.mytoswiki.data.tos.BaseCraft;
import com.flyingkite.mytoswiki.data.tos.CraftsArm;
import com.flyingkite.mytoswiki.data.tos.CraftsNormal;
import com.flyingkite.mytoswiki.data.tos.TosCard;
import com.flyingkite.mytoswiki.dialog.OnAction;
import com.flyingkite.mytoswiki.share.ShareHelper;
import com.flyingkite.util.TaskMonitor;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class TosWiki {
    private TosWiki() {}
    private static TosCard[] allCards;
    private static CraftsNormal[] normalCrafts;
    private static CraftsArm[] armCrafts;
    private static HashMap<String, TosCard> allCardsByIdNorm = new HashMap<>();
    private static HashMap<String, BaseCraft> allCraftsByIdNorm = new HashMap<>();
    private static CardFavor cardFavor;
    private static List<OnAction> favorActions = new ArrayList<>();
    // Tags for Task monitor
    public static final String TAG_ALL_CARDS = "AllCards";
    public static final String TAG_NORMAL_CRAFTS = "Crafts";
    public static final String TAG_ARM_CRAFTS = "ArmCards";
    public static final String TAG_CARD_FAVOR = "CardFavor";
    public static final String[] TAG_ALL_TASKS = {
            TAG_ALL_CARDS, TAG_NORMAL_CRAFTS, TAG_ARM_CRAFTS, TAG_CARD_FAVOR
    };

    public static void init(Context ctx) {
        ExecutorService p = ThreadUtil.cachedThreadPool;

        p.submit(() -> {
            TicTac2 t = new TicTac2();
            t.tic();
            allCards = GsonUtil.loadAsset("cardList.json", TosCard[].class, ctx.getAssets());
            t.tac("%s cards loaded", len(allCards));

            t.tic();
            allCardsByIdNorm.clear();
            for (TosCard c : allCards) {
                allCardsByIdNorm.put(c.idNorm, c);
            }
            t.tac("%s in set OK", allCardsByIdNorm.size());
            monitorDB.notifyClientsState();
        });

        p.submit(() -> {
            TicTac2 t = new TicTac2.v();
            t.tic();
            normalCrafts = GsonUtil.loadAsset("crafts.json", CraftsNormal[].class, ctx.getAssets());
            t.tac("%s craft loaded, Norm", len(normalCrafts));

            for (CraftsNormal c : normalCrafts) {
                allCraftsByIdNorm.put(c.idNorm, c);
            }
            monitorDB.notifyClientsState();
        });

        p.submit(() -> {
            TicTac2 t = new TicTac2.v();
            t.tic();
            armCrafts = GsonUtil.loadAsset("armCrafts.json", CraftsArm[].class, ctx.getAssets());
            t.tac("%s craft loaded, Arm", len(armCrafts));

            for (CraftsArm c : armCrafts) {
                allCraftsByIdNorm.put(c.idNorm, c);
            }
            monitorDB.notifyClientsState();
        });

        p.submit(() -> {
            TicTac2 t = new TicTac2();
            t.tic();
            File f = getTosCardFavorFile();
            if (f.exists()) {
                cardFavor = GsonUtil.loadFile(f, CardFavor.class);
            }
            if (cardFavor == null) {
                cardFavor = new CardFavor();
            }
            logFavorite(cardFavor);
            t.tac("%s cards favored", cardFavor.favors.size());
            monitorDB.notifyClientsState();
        });
    }

    public static CardFavor getCardFavor() {
        return cardFavor;
    }

    public static void saveCardFavor() {
        GsonUtil.writeFile(getTosCardFavorFile(), new Gson().toJson(cardFavor));
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

    private static File getTosCardFavorFile() {
        return ShareHelper.extFilesFile("cardFavor.txt");
    }

    public static int getAllCardsCount() {
        return len(allCards);
    }

    public static TosCard[] allCards() {
        return copy(allCards);
    }

    public static TosCard getCardByIdNorm(String id) {
        TosCard c = allCardsByIdNorm.get(id);
        if (c == null) {
            try {
                throw new NullPointerException("Card null : " + id + ", map = " + allCardsByIdNorm.size() + ", all = " + getAllCardsCount());
            } catch (NullPointerException npe) {
                CrashReport.logException(npe);
            }
        }
        return c;
    }

    public static BaseCraft getCraftByIdNorm(String id) {
        BaseCraft c = allCraftsByIdNorm.get(id);
        if (c == null) {
            try {
                throw new NullPointerException("Craft null : " + id);
            } catch (NullPointerException npe) {
                CrashReport.logException(npe);
            }
        }
        return c;
    }

    public static CraftsNormal[] allNormalCrafts() {
        return copy(normalCrafts);
    }

    public static CraftsArm[] allArmCrafts() {
        return copy(armCrafts);
    }

    private static TaskMonitor.TaskOwner monitorSource = new TaskMonitor.TaskOwner() {
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
    private static TaskMonitor monitorDB = new TaskMonitor(monitorSource);

    // attend & absent
    // retain & remove
    public static void attendDatabaseTasks(@NonNull TaskMonitor.OnTaskState listener) {
        monitorDB.registerClient(listener);
    }



    //-- Events
    private static void logFavorite(CardFavor f) {
        Map<String, String> m = new HashMap<>();
        String card = String.valueOf(f == null ? 0 : f.favors.size());
        m.put("card", card);
        FabricAnswers.logFavorite(m);
    }
    //-- Events

    private static <T> int len(T[] a) {
        return a == null ? 0 : a.length;
    }

    private static <T> T[] copy(T[] a) {
        if (a == null) {
            return null;
        } else {
            return Arrays.copyOf(a, len(a));
        }
    }

    private static Loggable z = new Loggable() {
        @Override
        public void log(String message) {
            Log.i(LTag(), message);
        }
    };
}
