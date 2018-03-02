package com.flyingkite.mytoswiki.library;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;

import com.flyingkite.mytoswiki.data.TosCard;
import com.flyingkite.mytoswiki.data.TosCardRMDKVIR;

import java.util.Map;

public class CardLibrary {
    private RecyclerView recycler;
    private CardAdapter cardAdapter;

    public CardLibrary(RecyclerView recyclerView) {
        recycler = recyclerView;

        recycler.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), 5));
        SnapHelper helper = new LinearSnapHelper();
        //helper.attachToRecyclerView(recycler);
    }

    @Deprecated
    public void setDataSetRMD(TosCardRMDKVIR[] cards) {
        cardAdapter = new CardAdapter();
        cardAdapter.setCardsRMD(cards);
        recycler.setAdapter(cardAdapter);
    }

    public void setDataSet(TosCard[] cards, CardVH.OnClickCard click, CardAdapter.OnFilterCard filter) {
        cardAdapter = new CardAdapter();
        cardAdapter.setCards(cards);
        cardAdapter.setOnClickCard(click);
        cardAdapter.setOnFilter(filter);
        recycler.setAdapter(cardAdapter);
    }

    public void showSelection(Map<String, String> map) {
        cardAdapter.showSelection(map);
    }

}
