package com.flyingkite.mytoswiki.library;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;

import com.flyingkite.mytoswiki.data.TosCard;

public class CardLibrary {
    public RecyclerView recycler;
    public CardAdapter cardAdapter;

    public CardLibrary(RecyclerView recyclerView) {
        recycler = recyclerView;

        recycler.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), 5));
        SnapHelper helper = new LinearSnapHelper();
        //helper.attachToRecyclerView(recycler);
    }

    public void setDataSet(TosCard[] cards, CardAdapter.OnClickCard click, CardAdapter.OnFilterCard filter) {
        cardAdapter = new CardAdapter();
        cardAdapter.setCards(cards);
        cardAdapter.setOnClickCard(click);
        cardAdapter.setOnFilter(filter);
        recycler.setAdapter(cardAdapter);
    }
}
