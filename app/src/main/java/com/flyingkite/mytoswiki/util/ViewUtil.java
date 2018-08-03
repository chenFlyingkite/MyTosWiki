package com.flyingkite.mytoswiki.util;

import android.support.annotation.IdRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;

import java.util.ArrayList;
import java.util.List;

public interface ViewUtil {


    default void setViewVisibility(View v, boolean show) {
        int vis = show ? View.VISIBLE : View.GONE;
        v.setVisibility(vis);
    }

    default void setVisibilities(int vis, View... vs) {
        for (View v : vs) {
            if (v != null) {
                v.setVisibility(vis);
            }
        }
    }

    default void setChildClick(ViewGroup vg, View.OnClickListener clk) {
        int n = vg.getChildCount();
        for (int i = 0; i < n; i++) {
            View w = vg.getChildAt(i);
            w.setOnClickListener(clk);
        }
    }

    default void setViewCheck(boolean check, View v) {
        Checkable c;
        if (v instanceof Checkable) {
            c = (Checkable) v;
            c.setChecked(check);
        }
    }

    default <T extends ViewGroup> T setTargetChildChick(View parent, @IdRes int targetId, View.OnClickListener childClick) {
        T vg = parent.findViewById(targetId);
        setChildClick(vg, childClick);
        return vg;
    }

    //-- View's helpers --
    default void setCheckedIncludeNo(View clicked, @IdRes int noId, ViewGroup parent) {
        View noView = null;
        // Find the noView
        int n = parent.getChildCount();
        for (int i = 0; i < n && noView == null; i++) {
            View v = parent.getChildAt(i);
            if (v.getId() == noId) {
                noView = v;
            }
        }

        int vid = clicked.getId();
        Checkable c;
        if (vid != noId) { // If select something, uncheck no
            setViewCheck(false, noView);
        } else {
            // selected no, uncheck all others except no
            for (int i = 0; i < n; i++) {
                setViewCheck(false, parent.getChildAt(i));
            }
            setViewCheck(true, noView);
        }
    }

    default void toggleSelection(View v, ViewGroup vg) {
        v.setSelected(!v.isSelected());
        // Deselect all if all selected
        if (isAllAsSelected(vg, true)) {
            setAllChildrenSelected(vg, false);
        }
    }

    default void setAllChildrenSelected(ViewGroup vg, boolean sel) {
        if (vg == null) return;

        int n = vg.getChildCount();
        for (int i = 0; i < n; i++) {
            vg.getChildAt(i).setSelected(sel);
        }
    }

    default boolean isAllAsSelected(ViewGroup vg, boolean selected) {
        if (vg == null) return false;

        int n = vg.getChildCount();
        for (int i = 0; i < n; i++) {
            if (vg.getChildAt(i).isSelected() != selected) {
                return false;
            }
        }
        return true;
    }

    default void getSelectTags(ViewGroup vg, List<String> result, boolean addAllIfEmpty) {
        if (result == null) {
            result = new ArrayList<>();
        }
        int n = vg.getChildCount();

        List<String> all = new ArrayList<>();
        boolean added = false;
        for (int i = 0; i < n; i++) {
            View w = vg.getChildAt(i);
            String tag = w.getTag().toString();
            if (w.isSelected()) {
                added = true;
                result.add(tag);
            }
            all.add(tag);
        }
        // If no children is added, add all the child tags
        if (addAllIfEmpty && !added) {
            result.addAll(all);
        }
    }
    //-- View helpers --
}
