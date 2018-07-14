package com.flyingkite.util;

import android.support.annotation.NonNull;
import android.util.Log;

import com.flyingkite.library.logging.Loggable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TaskMonitor implements Loggable {
    /**
     * Our clients interested in those tasks source. </br>
     * Each client is eager to be notified {@link OnTaskState#onTaskDone(int)} or {@link OnTaskState#onAllTaskDone()}
     * After all task done, we will remove client.
     */
    private final List<OnTaskState> clients = Collections.synchronizedList(new ArrayList<>());

    private Source src;

    public TaskMonitor(@NonNull Source source) {
        src = source;
    }

    public interface Source {
        /**
         * How many tasks to be done, this value should be constant since we will notify {@link #isTaskDone(int)}
         */
        int taskCount();

        /**
         * Asking source whether the task[index] is done.
         */
        boolean isTaskDone(int index);
    }

    public interface OnTaskState {
        /**
         * Notify the task[index] is done
         */
        default void onTaskDone(int index) {}

        /**
         * Notify all the tasks are completed
         */
        default void onAllTaskDone() {}
    }

    public void registerClient(OnTaskState listener) {
        if (listener == null) return;

        log("Register");
        clients.add(listener);
        notifyClientsState();
    }

    public void notifyClientsState() {
        List<Integer> toRemove = new ArrayList<>();
        int n = clients.size();
        log("+ notify state to %s clients = %s", clients.size(), clients);
        for (int i = 0; i < n; i++) {
            int done = 0;
            OnTaskState ci = clients.get(i);
            // For each task[j], notify client[i] if task[j] is done
            for (int j = 0; j < src.taskCount(); j++) {
                if (src.isTaskDone(j)) {
                    ci.onTaskDone(j);
                    done++;
                }
            }
            // If all tasks are done, we plans to remove client[i]
            if (done == src.taskCount()) {
                ci.onAllTaskDone();
                toRemove.add(i);
            }
        }

        // Remove the clients, from largest index to smallest
        log("Remove clients = %s", toRemove);
        n = toRemove.size();
        for (int i = n - 1; i >= 0; i--) {
            int clientIndex = toRemove.get(i);
            clients.remove(clientIndex);
        }
        log("+ notify state to %s clients", clients.size(), clients);
    }

    @Override
    public void log(String message) {
        Log.i(LTag(), message);
    }
}
