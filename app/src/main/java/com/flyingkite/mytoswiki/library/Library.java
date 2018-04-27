package com.flyingkite.mytoswiki.library;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class Library<T extends RecyclerView.Adapter> {
    public RecyclerView recyclerView;
    public T adapter;

    public Library(RecyclerView view) {
        this(view, false);
    }

    public Library(RecyclerView view, boolean vertical) {
        recyclerView = view;
        int orient = vertical ? LinearLayoutManager.VERTICAL : LinearLayoutManager.HORIZONTAL;
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), orient, false));
    }

    public Library(RecyclerView view, RecyclerView.LayoutManager layout) {
        recyclerView = view;
        recyclerView.setLayoutManager(layout);
    }

    public void setViewAdapter(T rvAdapter) {
        adapter = rvAdapter;
        recyclerView.setAdapter(rvAdapter);
    }
}
