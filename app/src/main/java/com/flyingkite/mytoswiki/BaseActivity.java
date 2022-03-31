package com.flyingkite.mytoswiki;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.flyingkite.mytoswiki.util.PageUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

public abstract class BaseActivity extends Activity implements
        PageUtil
{
    private static final int REQ_PERMISSION = 1;
    private static final String[] RESULT_STATE = {"OK", "Cancel"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("onCreate(%s)", savedInstanceState);
        requestPermissions();
    }

    @Override
    protected void onStart() {
        super.onStart();
        log("onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        log("onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        log("onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        log("onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        log("onDestroy()");
        if (clearCacheWhenOnDestroy()) {
            App.me.clearAppCache();
        }
    }

    protected boolean clearCacheWhenOnDestroy() {
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        log("result : %s", RESULT_STATE[resultCode + 1]);
    }

    protected Fragment findFragmentById(@IdRes int fragmentId) {
        FragmentManager fm = getFragmentManager();
        if (fm == null) {
            return null;
        } else {
            return fm.findFragmentById(fragmentId);
        }
    }

    protected Fragment findFragmentByTag(String tag) {
        FragmentManager fm = getFragmentManager();
        if (fm == null) {
            return null;
        } else {
            return fm.findFragmentByTag(tag);
        }
    }


    protected String[] neededPermissions() {
        return new String[0];
    }

    protected final void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> perm = new ArrayList<>();
            String[] permissions = neededPermissions();
            perm.addAll(Arrays.asList(permissions));
            for (int i = perm.size() - 1; i >= 0; i--) {
                if (checkSelfPermission(perm.get(i)) == PackageManager.PERMISSION_GRANTED) {
                    perm.remove(i);
                }
            }
            if (perm.size() > 0) {
                requestPermissions(perm.toArray(new String[perm.size()]), REQ_PERMISSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQ_PERMISSION:
                log("Request permissions = " + Arrays.toString(permissions));
                log("and returns results = " + Arrays.toString(grantResults));
                break;
        }
    }

    protected final void showToast(@StringRes int id) {
        showToast(getString(id));
    }
}
