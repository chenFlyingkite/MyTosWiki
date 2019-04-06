package com.flyingkite.mytoswiki.util;

import android.app.Activity;
import androidx.annotation.StringRes;
import android.view.View;

import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.share.ShareHelper;

public interface ShareUtil {
    Activity getActivity();

    default CharSequence getString(@StringRes int id) {
        Activity a = getActivity();
        if (a == null) {
            return null;
        } else {
            return a.getString(id);
        }
    }

    default void shareString(String msg) {
        shareString(msg, getString(R.string.share_to));
    }

    default void shareString(String msg, CharSequence chooser) {
        ShareHelper.shareString(getActivity(), msg, chooser);
    }

    default void viewLink(String link) {
        ShareHelper.viewLink(getActivity(), link);
    }

    default void shareImage(View view, String filename) {
        ShareHelper.shareImage(getActivity(), view, filename);
    }

    default void shareImage(View v) {
        String name = ShareHelper.cacheName("2.png");
        shareImage(v, name);
    }
}
