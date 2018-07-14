package com.flyingkite.mytoswiki.tos;

import android.content.Context;

import com.flyingkite.library.Say;
import com.flyingkite.library.TicTac2;
import com.flyingkite.library.util.GsonUtil;
import com.flyingkite.library.util.ThreadUtil;
import com.flyingkite.mytoswiki.data.AmeSkill;
import com.flyingkite.mytoswiki.data.TosCard;
import com.flyingkite.util.TaskMonitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class TosWiki {
    private TosWiki() {}
//    private static CardsDB tosCardDB = new CardsDB();
//    private static AmeSkillsDB ameSkillDB = new AmeSkillsDB();
    private static TosCard[] allCards;
    private static AmeSkill[] ameSkills;
    private static final int ALL_DB = 2;// There are two DBs. allCards & ameSkills
    private static List<OnLoadState> onDatabaseLoad = Collections.synchronizedList(new ArrayList<>());


    public static void init(Context ctx) {
        ExecutorService e = ThreadUtil.cachedThreadPool;

        boolean mock = false;
        e.submit(() -> {
            TicTac2 t = new TicTac2();
            t.tic();
            if (mock) {
                Say.sleep(10_000);
                allCards = new TosCard[1];
            } else {
                allCards = GsonUtil.loadAsset("cardList.json", TosCard[].class, ctx.getAssets());
            }
            t.tac("%s cards loaded", allCards == null ? 0 : allCards.length);
            notifyDatabaseState();
            monitorDB.notifyClientsState();
        });

        if (mock) {
            Say.sleep(500);
        }

        e.submit(() -> {
            TicTac2 t = new TicTac2();
            t.tic();
            if (mock) {
                Say.sleep(3_000);
                ameSkills = new AmeSkill[1];
            } else {
                ameSkills = GsonUtil.loadAsset("ameActiveSkills.json", AmeSkill[].class, ctx.getAssets());
            }

            t.tac("%s ame skill loaded", ameSkills == null ? 0 : ameSkills.length);
            notifyDatabaseState();
            monitorDB.notifyClientsState();
        });

        //tosCardDB.init(ctx, TosCardDB.class);
        //ameSkillDB.init(ctx, AmeSkillDB.class);
    }

    private static TaskMonitor.Source monitorSource = new TaskMonitor.Source() {
        @Override
        public int taskCount() {
            return 2;
        }

        @Override
        public boolean isTaskDone(int index) {
            switch (index) {
                case 0: return allCards != null;
                case 1: return ameSkills != null;
                default:
                    throw new ArrayIndexOutOfBoundsException(taskCount() + " tasks but did not define done for " + index);
            }
        }
    };
    private static TaskMonitor monitorDB = new TaskMonitor(monitorSource);

    public static boolean isLoaded_allCards() {
        return allCards != null;
    }

    public static boolean isLoaded_ameSkills() {
        return ameSkills != null;
    }

    public static void registerLoaded(OnLoadState listener) {
        if (listener == null) return;

        Say.Log("Register");
        onDatabaseLoad.add(listener);
        notifyDatabaseState();

        monitorDB.registerClient(new TaskMonitor.OnTaskState() {
            @Override
            public void onTaskDone(int index) {
                Say.Log("V #%s task", index);
            }

            @Override
            public void onAllTaskDone() {
                Say.Log("W All tasks done");
            }
        });
    }

    private static void notifyDatabaseState() {
        Say.Log("+ onDB Load = %s", onDatabaseLoad);
        List<Integer> toRemove = new ArrayList<>();
        int n = onDatabaseLoad.size();
        for (int i = 0; i < n; i++) {
            int x = 0;
            OnLoadState li = onDatabaseLoad.get(i);
            if (isLoaded_ameSkills()) {
                li.onLoaded_ameSkills();
                x++;
            }
            if (isLoaded_allCards()) {
                li.onLoaded_allCards();
                x++;
            }
            if (x == ALL_DB) {
                li.onLoaded_All();
                toRemove.add(i);
            }
        }
        Say.Log("remove = %s", toRemove);

        n = toRemove.size();
        for (int i = n - 1; i >= 0; i--) {
            onDatabaseLoad.remove((int)toRemove.get(i));
        }
        Say.Log("- onDB %s lis, Load = %s", onDatabaseLoad.size(), onDatabaseLoad);
    }

    public interface OnLoadState {
        default void onLoaded_allCards() {}
        default void onLoaded_ameSkills() {}
        default void onLoaded_All() {}
    }

    public static TosCard[] allCards() {
        if (allCards == null) {
            return null;
        } else {
            return Arrays.copyOf(allCards, allCards.length);
        }
    }

    public static AmeSkill[] ameSkills() {
        if (ameSkills == null) {
            return null;
        } else {
            return Arrays.copyOf(ameSkills, ameSkills.length);
        }
    }

//    public static CardsDB cards() {
//        return tosCardDB;
//    }
//
//    public static AmeSkillsDB ameSkills() {
//        return ameSkillDB;
//    }
//
//    public static boolean isAllTasksDone() {
//        List<BaseTosDB> db = Arrays.asList(tosCardDB, ameSkillDB);
//        for (BaseTosDB d : db) {
//            if (!d.future.isDone()) {
//                return false;
//            }
//        }
//        return true;
//    }
}
