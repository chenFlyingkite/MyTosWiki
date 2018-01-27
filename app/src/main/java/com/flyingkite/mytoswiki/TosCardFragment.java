package com.flyingkite.mytoswiki;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.flyingkite.library.TicTac;
import com.flyingkite.mytoswiki.data.TosCard;
import com.flyingkite.mytoswiki.page.CardLibrary;

public class TosCardFragment extends BaseFragment {
    private RecyclerView cardsRecycler;
    private CardLibrary cardLib;
    private TosCard[] allCard;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        cardsRecycler = findViewById(R.id.tosRecycler);

        cardLib = new CardLibrary(cardsRecycler);
        //parseCards()

        TicTac.tic();
        allCard = TosWiki.parseCards(getActivity().getAssets());
        cardLib.setDataSet(allCard);
        TicTac.tac("parseCards");

    }

    @Override
    protected int getPageLayoutId() {
        return R.layout.fragment_tos_card;
    }
}
