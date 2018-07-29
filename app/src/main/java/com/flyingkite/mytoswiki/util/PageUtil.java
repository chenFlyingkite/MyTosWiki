package com.flyingkite.mytoswiki.util;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.flyingkite.library.log.Loggable;
import com.flyingkite.library.util.ThreadUtil;

public interface PageUtil extends Loggable {

    Activity getActivity();

    View getView();

    default void initScrollTools(@IdRes int goTop, @IdRes int goBottom, RecyclerView recycler) {
        View w;
        w = findViewById(goTop);
        if (w != null) {
            w.setOnClickListener((v) -> {
                if (recycler != null) {
                    recycler.scrollToPosition(0);
                }
            });
        }

        w = findViewById(goBottom);
        if (w != null) {
            w.setOnClickListener((v) -> {
                if (recycler != null) {
                    RecyclerView.Adapter a = recycler.getAdapter();
                    int end = 0;
                    if (a != null) {
                        end = a.getItemCount() - 1;
                    }
                    recycler.scrollToPosition(end);
                }
            });
        }
    }

    default void runOnUiThread(Runnable r) {
        Activity a = getActivity();
        if (a == null) {
            ThreadUtil.runOnUiThread(r);
        } else {
            a.runOnUiThread(r);
        }
    }

    default void showToast(@StringRes int id, Object... args) {
        String s = getActivity().getString(id, args);
        showToast(s);
    }

    default void showToast(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }

    default void setViewVisibility(@IdRes int parent, boolean show) {
        setViewVisibility(findViewById(parent), show);
    }

    default void setViewVisibility(View v, boolean show) {
        int vis = show ? View.VISIBLE : View.GONE;
        v.setVisibility(vis);
    }

    default void setVisibilities(int vis, int... ids) {
        for (int i : ids) {
            View v = findViewById(i);
            if (v != null) {
                v.setVisibility(vis);
            }
        }
    }

    default void setVisibilities(int vis, View... vs) {
        for (View v : vs) {
            if (v != null) {
                v.setVisibility(vis);
            }
        }
    }

    default void setOnClickListeners(View.OnClickListener lis, @IdRes int... ids) {
        for (int i : ids) {
            findViewById(i).setOnClickListener(lis);
        }
    }

    default <T extends View> T findViewById(@IdRes int id) {
        View w = getView();
        if (w != null) {
            T v = w.findViewById(id);
            if (v != null) {
                return v;
            }
        }

        return getActivity().findViewById(id);
    }

    //-- Logging -- start
    default String sig() {
        return this.getClass().getCanonicalName();
    }

    default String getTagName() {
        return "Hi " + getClass().getSimpleName();
    }

    default void LogV(String msg, Object... param) {
        LogV(String.format(msg, param));
    }

    default void LogV(String msg) {
        Log.v(getTagName(), msg);
    }

    default void LogE(String msg, Object... param) {
        LogE(String.format(msg, param));
    }

    default void LogE(String msg) {
        Log.e(getTagName(), msg);
    }
    //-- Logging -- end
}
