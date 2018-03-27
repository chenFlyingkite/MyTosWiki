package com.flyingkite.mytoswiki.tos.query;

import android.support.annotation.NonNull;

import com.flyingkite.library.Say;
import com.flyingkite.mytoswiki.data.TosCard;

import java.util.ArrayList;
import java.util.List;

public interface TosCardSelection {

    default <T> List<T> nonEmpty(List<T> list) {
        return list == null ? new ArrayList<>() : list;
    }

    @NonNull
    default List<TosCard> from() {
        return new ArrayList<>();
    }

    @NonNull
    default List<Integer> select() {
        return new ArrayList<>();
    }

    @NonNull
    default List<Integer> sort(@NonNull List<Integer> result) {
        return result;
    }

    default List<Integer> query() {
        List<Integer> result = sort(select());
        Say.LogI("From %s selects %s items", from().size(), result.size());
        return result;
    }

    class All implements TosCardSelection {
        private List<TosCard> all;
        public All(List<TosCard> list) {
            all = list;
        }

        @NonNull
        @Override
        public List<TosCard> from() {
            return all;
        }

        @NonNull
        @Override
        public List<Integer> select() {
            List<Integer> result = new ArrayList<>();
            for (int i = 0; i < all.size(); i++) {
                result.add(i);
            }
            return result;
        }
    }
}
