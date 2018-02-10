package com.flyingkite.mytoswiki;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class BaseFragment extends Fragment {

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

    protected final void showToast(@StringRes int id) {
        Toast.makeText(getActivity(), id, Toast.LENGTH_LONG).show();
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
}
