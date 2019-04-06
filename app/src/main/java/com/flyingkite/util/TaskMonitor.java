package com.flyingkite.util;

import androidx.annotation.NonNull;

import com.flyingkite.library.log.Loggable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TaskMonitor implements Loggable {
    /**
     * Our clients interested in those tasks source. </br>
     * Each client is eager to be notified {@link OnTaskState#onTaskDone(int, String)} or {@link OnTaskState#onAllTaskDone()}
     * After all task done, we will remove client.
     */
    private final List<OnTaskState> clients = Collections.synchronizedList(new ArrayList<>());

    private TaskOwner taskOwner;

    public TaskMonitor(@NonNull TaskOwner source) {
        taskOwner = source;
    }

    public interface TaskOwner {
        /**
         * How many tasks to be done, this value should be constant since we will notify {@link #isTaskDone(int)}
         */
        int taskCount();

        /**
         * Asking source whether the task[index] is done.
         */
        boolean isTaskDone(int index);

        String getTaskTag(int index);
    }

    public interface OnTaskState {
        /**
         * Notify the task[index] is done
         */
        default void onTaskDone(int index, String tag) {}

        /**
         * Notify all the tasks are completed
         */
        default void onAllTaskDone() {}
    }

    public void registerClient(@NonNull OnTaskState listener) {
        log("Register");
        synchronized (clients) {
            clients.add(listener);
            notifyClientsState();
        }
    }

    public synchronized void notifyClientsState() {
        synchronized (clients) {
            List<Integer> toRemove = new ArrayList<>();
            int n = clients.size();
            log("+ notify state to %s clients = %s", clients.size(), clients);
            for (int i = 0; i < n; i++) {
                int done = 0;
                OnTaskState ci = clients.get(i);
                // For each task[j], notify client[i] if task[j] is done
                for (int j = 0; j < taskOwner.taskCount(); j++) {
                    if (taskOwner.isTaskDone(j)) {
                        ci.onTaskDone(j, taskOwner.getTaskTag(j));
                        done++;
                    }
                }
                // If all tasks are done, we plans to remove client[i]
                if (done == taskOwner.taskCount()) {
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
            log("- end with %s clients = %s", clients.size(), clients);
        }
    }

    @Override
    public void log(String message) {
        //Log.i(LTag(), message);
    }
}
