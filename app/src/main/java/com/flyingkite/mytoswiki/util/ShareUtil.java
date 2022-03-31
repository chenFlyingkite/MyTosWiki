package com.flyingkite.mytoswiki.util;

import android.app.Activity;
import android.view.View;

import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.share.ShareHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.StringRes;

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

    default void shareString(String msg, CharSequence title) {
        ShareHelper.sendString(getActivity(), msg, title);
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

    default void shareImageCache(View v, String name) {
        String it = ShareHelper.cacheName(name);
        shareImage(v, it);
    }

    default void shareImageTime(View v) {
        Date now = new Date();
        SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd_hhmmssSSS");
        String name = String.format("%s.png", f.format(now));
        shareImageCache(v, name);
    }
}
