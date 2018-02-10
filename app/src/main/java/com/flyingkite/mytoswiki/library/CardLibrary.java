package com.flyingkite.mytoswiki.library;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.flyingkite.mytoswiki.data.TosCard;
import com.flyingkite.mytoswiki.data.TosCardRMDKVIR;

public class CardLibrary {
    private RecyclerView recycler;
    private CardAdapter cardAdapter;

    public CardLibrary(RecyclerView recyclerView) {
        recycler = recyclerView;

        recycler.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), 5));
    }

    @Deprecated
    public void setDataSetRMD(TosCardRMDKVIR[] cards) {
        cardAdapter = new CardAdapter();
        cardAdapter.setCardsRMD(cards);
        recycler.setAdapter(cardAdapter);
    }

    public void setDataSet(TosCard[] cards, CardVH.OnClickCard click) {
        cardAdapter = new CardAdapter();
        cardAdapter.setCards(cards);
        cardAdapter.setOnClickCard(click);
        recycler.setAdapter(cardAdapter);
    }

}
