package com.flyingkite.mytoswiki.tos.query;

import androidx.annotation.NonNull;

import java.util.List;

public class AllCards<T> implements TosSelection<T> {
    protected List<T> data;
    public AllCards(List<T> list) {
        data = list;
    }

    @NonNull
    @Override
    public List<T> from() {
        return data;
    }
}