package com.flyingkite.mytoswiki.library;

import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;

import com.flyingkite.library.widget.Library;

public class CardLibrary extends Library<CardAdapter> {

    public CardLibrary(RecyclerView recyclerView) {
        super(recyclerView, 5);
        SnapHelper helper = new LinearSnapHelper();
        //helper.attachToRecyclerView(recycler);
    }
}
