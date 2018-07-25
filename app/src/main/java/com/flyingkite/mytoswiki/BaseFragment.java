package com.flyingkite.mytoswiki;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.flyingkite.library.log.Loggable;
import com.flyingkite.library.util.ThreadUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BaseFragment extends Fragment implements Loggable {
    protected static final ExecutorService sSingle = new ThreadPoolExecutor(0, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LogV("onAttach(%s)", context);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogV("onCreate(%s)", savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        int pageId = getPageLayoutId();
        if (pageId > 0) {
            return inflater.inflate(pageId, container, false);
        } else {
            return null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        LogV("onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogV("onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        LogV("onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        LogV("onStop()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogV("onDestroy()");
    }

    @LayoutRes
    protected int getPageLayoutId() {
        return -1;
    }

    protected final <T extends View> T findViewById(@IdRes int id) {
        View w = getView();
        if (w != null) {
            T v = w.findViewById(id);
            if (v != null) {
                return v;
            }
        }

        return getActivity().findViewById(id);
    }

    protected final void showToast(@StringRes int id, Object... args) {
        String s = getActivity().getString(id, args);
        showToast(s);
    }

    protected final void showToast(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }

    protected String getTagName() {
        return "Hi " + getClass().getSimpleName();
    }

    protected void LogV(String msg, Object... param) {
        LogV(String.format(msg, param));
    }

    protected void LogV(String msg) {
        Log.v(getTagName(), msg);
    }

    protected void LogE(String msg, Object... param) {
        LogE(String.format(msg, param));
    }

    protected void LogE(String msg) {
        Log.e(getTagName(), msg);
    }

    @Override
    public void log(String message) {
        Log.w(LTag(), message);
    }

    public final void runOnUiThread(@NonNull Runnable action) {
        Activity a = getActivity();
        if (a == null) {
            ThreadUtil.runOnUiThread(action);
        } else {
            a.runOnUiThread(action);
        }
    }
}
