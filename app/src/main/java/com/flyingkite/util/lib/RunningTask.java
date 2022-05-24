package com.flyingkite.util.lib;
//
//import flyingkite.library.android.util.ThreadUtil;
//
//@Deprecated
//public interface RunningTask extends Runnable {
//
//    @Override
//    default void run() {
//        ThreadUtil.runOnUiThread(this::onPreExecute);
//        doInBackground();
//        ThreadUtil.runOnUiThread(this::onPostExecute);
//    }
//
//    default void onPreExecute() {
//    }
//
//    public void doInBackground();
//
//    default void onPostExecute() {
//    }
//}
