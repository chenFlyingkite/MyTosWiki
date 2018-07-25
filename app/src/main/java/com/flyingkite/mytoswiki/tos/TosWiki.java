package com.flyingkite.mytoswiki.tos;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.flyingkite.library.Say;
import com.flyingkite.library.TicTac2;
import com.flyingkite.library.log.Loggable;
import com.flyingkite.library.util.GsonUtil;
import com.flyingkite.library.util.ThreadUtil;
import com.flyingkite.mytoswiki.data.tos.TosCard;
import com.flyingkite.util.TaskMonitor;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;

public class TosWiki {
    private TosWiki() {}
    private static TosCard[] allCards;
    // Tags for Task monitor
    public static final String TAG_ALL_CARDS = "AllCards";
    public static final String[] TAG_ALL_TASKS = {TAG_ALL_CARDS
    };

    public static void init(Context ctx) {
        ExecutorService p = ThreadUtil.cachedThreadPool;

        boolean mock = false;
        p.submit(() -> {
            TicTac2 t = new TicTac2();
            z.log("Load Cards");
            t.tic();
            if (mock) {
                Say.sleep(10_000);
                allCards = new TosCard[1];
            } else {
                allCards = GsonUtil.loadAsset("cardList.json", TosCard[].class, ctx.getAssets());
            }
            t.tac("%s cards loaded", allCards == null ? 0 : allCards.length);
            monitorDB.notifyClientsState();
        });

        if (mock) {
            Say.sleep(500);
        }

//        p.submit(() -> {
//            TicTac2 t = new TicTac2();
//            z.log("Load Ame");
//            t.tic();
//            if (mock) {
//                Say.sleep(3_000);
//                //ameSkills = new AmeSkill[1];
//            } else {
//                //ameSkills = GsonUtil.loadAsset("ameActiveSkills.json", AmeSkill[].class, ctx.getAssets());
//            }
//
//            t.tac("%s ame skill loaded", 0);
//            monitorDB.notifyClientsState();
//        });

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
                case TAG_ALL_CARDS: return allCards != null;
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

    public static int getAllCardsCount() {
        if (allCards == null) {
            return 0;
        } else {
            return allCards.length;
        }
    }

    public static TosCard[] allCards() {
        if (allCards == null) {
            return null;
        } else {
            return Arrays.copyOf(allCards, allCards.length);
        }
    }

    private static Loggable z = new Loggable() {
        @Override
        public void log(String message) {
            Log.i(LTag(), message);
        }
    };
}
