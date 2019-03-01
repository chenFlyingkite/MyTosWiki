package com.flyingkite.mytoswiki.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.IdRes;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public interface ViewUtil {

    RecyclerView.OnItemTouchListener noIntercept = new RecyclerView.OnItemTouchListener() {

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            int action = e.getAction();
            switch (action) {
                case MotionEvent.ACTION_MOVE:
                    rv.getParent().requestDisallowInterceptTouchEvent(true);
                    break;
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    };

    default DividerItemDecoration getRVDivider(Context context, boolean vertical, Drawable drawable) {
        int r = vertical ? DividerItemDecoration.VERTICAL : DividerItemDecoration.HORIZONTAL;
        DividerItemDecoration d = new DividerItemDecoration(context, r);
        d.setDrawable(drawable);
        return d;
    }

    default void setViewVisibility(View v, boolean show) {
        int vis = show ? View.VISIBLE : View.GONE;
        v.setVisibility(vis);
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

    default <T extends RecyclerView.ViewHolder> void fillItems(ViewGroup linear, RecyclerView.Adapter<T> adapter) {
        linear.removeAllViews();
        int n = adapter.getItemCount();
        for (int i = 0; i < n; i++) {
            T vg = adapter.onCreateViewHolder(linear, adapter.getItemViewType(i));
            adapter.onBindViewHolder(vg, i);
            linear.addView(vg.itemView);
        }
        linear.requestLayout();
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

    default <T extends ViewGroup> T setTargetChildClick(View parent, @IdRes int targetId, View.OnClickListener childClick) {
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
        if (vid != noId) {
            // uncheck no first since select other
            setViewCheck(false, noView);
            boolean non = isAllAsChecked(parent, false);
            // check no if all unchecked
            if (non) {
                setViewCheck(true, noView);
            }
        } else {
            // selected no, uncheck all others except no
            for (int i = 0; i < n; i++) {
                setViewCheck(false, parent.getChildAt(i));
            }
            setViewCheck(true, noView);
        }
    }

    default boolean isAllAsChecked(ViewGroup vg, boolean checked) {
        if (vg == null) return false;

        int n = vg.getChildCount();
        View v;
        Checkable c;
        for (int i = 0; i < n; i++) {
            v = vg.getChildAt(i);
            if (v instanceof Checkable) {
                c = (Checkable) v;
                if (c.isChecked() != checked) {
                    return false;
                }
            }
        }
        return true;
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

    default void setTextOrHide(TextView t, CharSequence cs) {
        if (TextUtils.isEmpty(cs)) {
            t.setVisibility(View.GONE);
        } else {
            t.setVisibility(View.VISIBLE);
            t.setText(cs);
        }
    }
    //-- View helpers --
}
