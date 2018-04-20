package com.flyingkite.mytoswiki.library;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class Library<T extends RecyclerView.Adapter> {
    public RecyclerView recyclerView;
    public T adapter;

    public Library(RecyclerView view) {
        recyclerView = view;
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    public void setViewAdapter(T rvAdapter) {
        adapter = rvAdapter;
        recyclerView.setAdapter(rvAdapter);
    }
}
