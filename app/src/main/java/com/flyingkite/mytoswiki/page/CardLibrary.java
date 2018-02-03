package com.flyingkite.mytoswiki.page;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.flyingkite.TosCardN;
import com.flyingkite.mytoswiki.data.TosCard;

public class CardLibrary {
    private RecyclerView recycler;
    private CardAdapter cardAdapter;

    public CardLibrary(RecyclerView recyclerView) {
        recycler = recyclerView;

        recycler.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), 5));
        //recycler.setAdapter();
    }

    public void setDataSet(TosCard[] cards) {
        cardAdapter = new CardAdapter();
        cardAdapter.setCards(cards);
        recycler.setAdapter(cardAdapter);
    }

    public void setDataSet2(TosCardN[] cards, CardVH.OnClickCard click) {
        cardAdapter = new CardAdapter();
        cardAdapter.setCards2(cards);
        cardAdapter.setOnClickCard(click);
        recycler.setAdapter(cardAdapter);
    }

}
