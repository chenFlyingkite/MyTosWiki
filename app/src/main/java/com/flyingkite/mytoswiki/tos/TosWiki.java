package com.flyingkite.mytoswiki.tos;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.flyingkite.library.Say;
import com.flyingkite.library.TicTac2;
import com.flyingkite.library.logging.Loggable;
import com.flyingkite.library.util.GsonUtil;
import com.flyingkite.library.util.ThreadUtil;
import com.flyingkite.mytoswiki.data.tos.TosCard;
import com.flyingkite.util.TaskMonitor;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;

public class TosWiki {
    private TosWiki() {}
//    private static CardsDB tosCardDB = new CardsDB();
//    private static AmeSkillsDB ameSkillDB = new AmeSkillsDB();
    private static TosCard[] allCards;
    //private static final int ALL_DB = 2;// There are two DBs. allCards & ameSkills
    //private static List<OnLoadState> onDatabaseLoad = Collections.synchronizedList(new ArrayList<>());

    // Tags for Task monitor
    public static final String TAG_ALL_CARDS = "AllCards";
    public static final String TAG_AME_SKILL = "AmeSkill";
    public static final String[] TAG_ALL_TASKS = {TAG_ALL_CARDS//, TAG_AME_SKILL
    };


    public static void init(Context ctx) {
        ExecutorService e = ThreadUtil.cachedThreadPool;

        boolean mock = false;
        e.submit(() -> {
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
            //notifyDatabaseState();
            monitorDB.notifyClientsState();
        });

        if (mock) {
            Say.sleep(500);
        }

        e.submit(() -> {
            TicTac2 t = new TicTac2();
            z.log("Load Ame");
            t.tic();
            if (mock) {
                Say.sleep(3_000);
                //ameSkills = new AmeSkill[1];
            } else {
                //ameSkills = GsonUtil.loadAsset("ameActiveSkills.json", AmeSkill[].class, ctx.getAssets());
            }

            t.tac("%s ame skill loaded", 0);
            //notifyDatabaseState();
            monitorDB.notifyClientsState();
        });

        //tosCardDB.init(ctx, TosCardDB.class);
        //ameSkillDB.init(ctx, AmeSkillDB.class);
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
        Say.Log("Attend");
        monitorDB.registerClient(listener);
    }

//    public static void registerLoaded(OnLoadState listener) {
//        if (listener == null) return;
//
//        Say.Log("Register");
//        onDatabaseLoad.add(listener);
//        notifyDatabaseState();
//
//        monitorDB.registerClient(new TaskMonitor.OnTaskState() {
//            @Override
//            public void onTaskDone(int index) {
//                Say.Log("V #%s task", index);
//            }
//
//            @Override
//            public void onAllTaskDone() {
//                Say.Log("W All tasks done");
//            }
//        });
//    }
//
//    private static void notifyDatabaseState() {
//        Say.Log("+ onDB Load = %s", onDatabaseLoad);
//        List<Integer> toRemove = new ArrayList<>();
//        int n = onDatabaseLoad.size();
//        for (int i = 0; i < n; i++) {
//            int x = 0;
//            OnLoadState li = onDatabaseLoad.get(i);
//            if (isLoaded_ameSkills()) {
//                li.onLoaded_ameSkills();
//                x++;
//            }
//            if (isLoaded_allCards()) {
//                li.onLoaded_allCards();
//                x++;
//            }
//            if (x == ALL_DB) {
//                li.onLoaded_All();
//                toRemove.add(i);
//            }
//        }
//        Say.Log("remove = %s", toRemove);
//
//        n = toRemove.size();
//        for (int i = n - 1; i >= 0; i--) {
//            onDatabaseLoad.remove((int)toRemove.get(i));
//        }
//        Say.Log("- onDB %s lis, Load = %s", onDatabaseLoad.size(), onDatabaseLoad);
//    }
//
//    public interface OnLoadState {
//        default void onLoaded_allCards() {}
//        default void onLoaded_ameSkills() {}
//        default void onLoaded_All() {}
//    }

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
