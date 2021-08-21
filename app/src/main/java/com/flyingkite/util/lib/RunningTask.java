package com.flyingkite.util.lib;

import com.flyingkite.library.util.ThreadUtil;

public interface RunningTask extends Runnable {

    @Override
    default void run() {
        ThreadUtil.runOnUiThread(this::onPreExecute);
        doInBackground();
        ThreadUtil.runOnUiThread(this::onPostExecute);
    }

    default void onPreExecute() {
    }

    public void doInBackground();

    default void onPostExecute() {
    }
}
