package com.flyingkite.mytoswiki.tos.query;

import com.flyingkite.util.select.Selector;

import java.util.List;

import androidx.annotation.NonNull;

public class AllCards<T> implements Selector<T> {
    protected List<T> data;
    private boolean isCancelled = false;

    public AllCards(List<T> list) {
        data = list;
    }

    @NonNull
    @Override
    public List<T> from() {
        return data;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

}