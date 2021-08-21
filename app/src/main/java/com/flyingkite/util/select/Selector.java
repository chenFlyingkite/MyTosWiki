package com.flyingkite.util.select;

import android.util.Log;

import com.flyingkite.library.TicTac2;
import com.flyingkite.library.util.ListUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * Mainly perform the selection by query, it will load from data list and perform the select
 * with calling onPrepare(), and onSelect() on each and then add the index & message
 */
public interface Selector<T> {

    default String typeName() {
        return "";
    }

    // -- Listeners
    default void onPrepare() {

    }

    default boolean onSelect(T c) {
        return true;
    }

    default void onSelected() {

    }

    // -- data set
    @NonNull
    default List<T> from() {
        return new ArrayList<>();
    }

    default List<SelectedData> query() {
        List<SelectedData> sorted = null;
        try {
            TicTac2 t = new TicTac2();
            t.tic();
            //t.tic();
            List<SelectedData> chosen = select();
            //t.tac("1. chosen");
            //Log.e("Sel", "chosen = " + chosen);
            //t.tic();
            sorted = sort(chosen);
            //t.tac("2. sorted");
            //Log.e("Sel", "sorted = " + sorted);
            t.tac("From %s selects %s items (%s)", from().size(), sorted.size(), typeName());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Selector", "failed", e);
        }
        return sorted;
    }

    @NonNull
    default List<SelectedData> select() {
        List<T> data = ListUtil.nonNull(from());
        List<SelectedData> ans = new ArrayList<>();
        onPrepare();
        for (int i = 0; i < data.size() && !isCancelled(); i++) {
            T c = data.get(i);
            SelectedData d = null;
            if (onSelect(c)) {
                d = new SelectedData();
                d.index = i;
                d.message = getMessage(c);
            }
            if (d != null) {
                ans.add(d);
            }
        }
        onSelected();
        return ans;
    }

    @NonNull
    default List<SelectedData> sort(@NonNull List<SelectedData> result) {
        return result;
    }

    default String getMessage(T t) {
        return null;
    }

    default void setCancelled(boolean cancel) {
    }

    default boolean isCancelled() {
        return false;
    }

}
