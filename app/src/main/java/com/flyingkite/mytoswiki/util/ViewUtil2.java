package com.flyingkite.mytoswiki.util;

import android.view.View;
import android.view.ViewGroup;

public interface ViewUtil2 {

    default void setViewVisibility(View v, boolean show) {
        if (v == null) return;

        int vis = show ? View.VISIBLE : View.GONE;
        v.setVisibility(vis);
    }

    default void toggleSelected(View v) {
        if (v == null) return;
        v.setSelected(!v.isSelected());
    }

    default void setVisibilityByChild(ViewGroup vg) {
        int n = vg.getChildCount();
        if (n == 0) return;

        boolean show = false;
        for (int i = 0; i < n && !show; i++) {
            show = vg.getChildAt(i).getVisibility() == View.VISIBLE;
        }

        setViewVisibility(vg, show);
    }
}
