package com.flyingkite.mytoswiki;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flyingkite.mytoswiki.util.BackPage;
import com.flyingkite.mytoswiki.util.PageUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

public class BaseFragment extends Fragment implements PageUtil, BackPage {
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

    /**
     * @return true if this event was consumed
     */
    public boolean onBackPressed() {
        return false;
    }

    @LayoutRes
    protected int getPageLayoutId() {
        return -1;
    }

    @Override
    public void log(String message) {
        Log.w(LTag(), message);
    }

}
