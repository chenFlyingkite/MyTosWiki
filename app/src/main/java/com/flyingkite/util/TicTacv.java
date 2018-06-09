package com.flyingkite.util;

import android.util.Log;

import com.flyingkite.library.TicTac2;

public class TicTacv extends TicTac2 {
    private static final String TAG = "TicTacv";

    public TicTacv() {
        super();
    }

    public TicTacv(boolean show) {
        showLog(show);
    }

    @Override
    protected void logTac(String s) {
        if (showLog) {
            Log.v(TAG, s);
        }
    }

    protected void logError(long tac, String msg) {
        if (showLog) {
            Log.e(TAG, "X_X [tic = N/A, tac = " + tac + "] : " + msg);
        }
    }
}
