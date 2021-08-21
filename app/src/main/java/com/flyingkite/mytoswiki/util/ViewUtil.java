package com.flyingkite.mytoswiki.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.IdRes;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import kotlin.jvm.functions.Function1;

public interface ViewUtil extends ViewUtil2 {

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

    default void setCheckChildClick(ViewGroup vg, View.OnClickListener clk) {
        int n = vg.getChildCount();
        for (int i = 0; i < n; i++) {
            View w = vg.getChildAt(i);
            if (w instanceof Checkable) {
                w.setOnClickListener(clk);
            }
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

    default <T extends ViewGroup> T setTargetCheckChildClick(View parent, @IdRes int targetId, View.OnClickListener childClick) {
        T vg = parent.findViewById(targetId);
        setCheckChildClick(vg, childClick);
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

    default void toggleAndClearIfAll(View v, ViewGroup vg) {
        toggleSelected(v);
        clearIfAllSelected(vg);
    }

    default void clearIfAllSelected(ViewGroup vg) {
        // Deselect all if all selected
        if (isAllAsSelected(vg, true)) {
            setAllChildSelected(vg, false);
        }
    }

    default void setAllChildSelected(ViewGroup vg, boolean sel) {
        if (vg == null) return;

        int n = vg.getChildCount();
        for (int i = 0; i < n; i++) {
            vg.getChildAt(i).setSelected(sel);
        }
    }

    /**
     * Set parent as checked if any child is selected
     */
    default void checkParentIfSelectAnyItem(ViewGroup items, Checkable parent, Checkable noView) {
        // select parent if any child selected
        boolean any = isAnyAsSelected(items, true);
        if (any) {
            if (parent != null) {
                parent.setChecked(true);
            }
            if (noView != null) {
                noView.setChecked(false);
            }
        }
    }

    default void clearChildIfParentUnchecked(ViewGroup items, Checkable parent) {
        if (parent != null) {
            if (!parent.isChecked()) {
                setAllChildSelected(items, false);
            }
        }
    }

    /**
     * @return true if ANY child's isSelected() == selected,
     *         false otherwise
     */
    default boolean isAnyAsSelected(ViewGroup vg, boolean selected) {
        if (vg == null) return false;

        int n = vg.getChildCount();
        for (int i = 0; i < n; i++) {
            if (vg.getChildAt(i).isSelected() == selected) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return true if ALL children's isSelected() == selected,
     *         false otherwise
     */
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

    default void getTagsWhen(Function1<View, Boolean> fn, ViewGroup vg, List<String> result, boolean addAllIfEmpty) {
        if (result == null) {
            result = new ArrayList<>();
        }
        int n = vg == null ? 0 : vg.getChildCount();

        List<String> all = new ArrayList<>();
        boolean added = false;
        for (int i = 0; i < n; i++) {
            View w = vg.getChildAt(i);
            String tag = w.getTag().toString();
            if (fn.invoke(w)) {
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

    default void getSelectTags(ViewGroup vg, List<String> result, boolean addAllIfEmpty) {
        getTagsWhen(View::isSelected, vg, result, addAllIfEmpty);
    }

    default void setTextOrHide(TextView t, CharSequence cs) {
        if (t == null) return;

        if (TextUtils.isEmpty(cs)) {
            t.setVisibility(View.GONE);
        } else {
            t.setVisibility(View.VISIBLE);
            t.setText(cs);
        }
    }

    default View findChildTag(ViewGroup vg, Object tag) {
        if (vg == null) return null;

        int n = vg.getChildCount();
        for (int i = 0; i < n; i++) {
            View v = vg.getChildAt(i);
            Object vt = v.getTag();
            if (tag.equals(vt)) {
                return v;
            }
        }
        return null;
    }
    //-- View helpers --
}
