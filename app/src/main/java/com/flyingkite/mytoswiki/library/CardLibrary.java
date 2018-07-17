package com.flyingkite.mytoswiki.library;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;

import com.flyingkite.library.widget.Library;
import com.flyingkite.mytoswiki.data.tos.TosCard;

import java.util.List;

public class CardLibrary extends Library<CardAdapter> {

    public CardLibrary(RecyclerView recyclerView) {
        super(recyclerView, new GridLayoutManager(recyclerView.getContext(), 5));
        SnapHelper helper = new LinearSnapHelper();
        //helper.attachToRecyclerView(recycler);
    }

    public void setDataSet(List<TosCard> cards, CardAdapter.OnClickCard click, CardAdapter.OnFilterCard filter) {
        adapter = new CardAdapter();
        adapter.setCards(cards);
        adapter.setOnClickCard(click);
        adapter.setOnFilter(filter);
        setViewAdapter(adapter);
    }
}
