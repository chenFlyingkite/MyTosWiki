package com.flyingkite.mytoswiki.tos.query;

import android.support.annotation.NonNull;

import com.flyingkite.library.util.ListUtil;
import com.flyingkite.library.TicTac2;
import com.flyingkite.mytoswiki.data.TosCard;

import java.util.ArrayList;
import java.util.List;

public interface TosCardSelection {
    @NonNull
    default List<TosCard> from() {
        return new ArrayList<>();
    }

    @NonNull
    default List<Integer> select() {
        List<TosCard> data = ListUtil.nonNull(from());
        List<Integer> index = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            TosCard c = data.get(i);
            if (onSelect(c)) {
                index.add(i);
            }
        }
        return index;
    }

    default boolean onSelect(TosCard c) {
        return true;
    }

    @NonNull
    default List<Integer> sort(@NonNull List<Integer> result) {
        return result;
    }

    default List<Integer> query() {
        TicTac2 t = new TicTac2();
        t.tic();
        List<Integer> result = sort(select());
        t.tac("From %s selects %s items", from().size(), result.size());
        return result;
    }

    default List<String> getMessages(List<Integer> result) {
        return null; // No messages
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
