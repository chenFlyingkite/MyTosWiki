package com.flyingkite.mytoswiki.tos.query;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.flyingkite.library.TicTac2;
import com.flyingkite.library.util.ListUtil;

import java.util.ArrayList;
import java.util.List;

public interface TosSelection<T> {

    @NonNull
    default List<T> from() {
        return new ArrayList<>();
    }

    @NonNull
    default List<Integer> select() {
        List<T> data = ListUtil.nonNull(from());
        List<Integer> index = new ArrayList<>();
        onPrepare();
        for (int i = 0; i < data.size(); i++) {
            T c = data.get(i);
            if (onSelect(c)) {
                index.add(i);
            } else {
                index.size();
            }
        }
        return index;
    }

    default void onPrepare() {

    }

    default boolean onSelect(T c) {
        return true;
    }

    @NonNull
    default List<Integer> sort(@NonNull List<Integer> result) {
        return result;
    }

    default List<Integer> query() {
        String s = "";
        if (!TextUtils.isEmpty(typeName())) {
            s = "(" + typeName() + ")";
        }
        TicTac2 t = new TicTac2();
        t.tic();
        List<Integer> result = sort(select());
        t.tac("From %s selects %s items %s", from().size(), result.size(), s);
        return result;
    }

    default List<String> getMessages(List<Integer> result) {
        return null; // No messages
    }

//    default void a() {
//        Say.Log("class = %s", getClass());
//        Say.Log("class = %s", Arrays.toString(getClass().getGenericInterfaces()));
//        Say.Log("class = %s", Arrays.toString(getClass().getInterfaces()));
//        Say.Log("class = %s", getClass());
//        Say.Log("class = %s", getClass());
//        Say.Log("class = %s", getClass());
//    }

    default String typeName() {
        return "";
    }
}
