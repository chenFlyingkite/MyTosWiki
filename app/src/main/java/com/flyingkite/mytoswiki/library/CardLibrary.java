package com.flyingkite.mytoswiki.library;

import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.flyingkite.library.widget.Library;

public class CardLibrary extends Library<CardAdapter> {

    public CardLibrary(RecyclerView recyclerView) {
        super(recyclerView, 5);
        SnapHelper helper = new LinearSnapHelper();
        //helper.attachToRecyclerView(recycler);
    }
}
