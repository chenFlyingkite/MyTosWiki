package com.flyingkite.mytoswiki;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.flyingkite.TosCardN;
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

//        TicTac.tic();
//        allCard = TosWiki.parseCards(getActivity().getAssets());
//        cardLib.setDataSet(allCard);
//        TicTac.tac("parseCards");

        TicTac.tic();
        TosCardN[] crd = TosWiki.parseCards2(getActivity().getAssets());
        cardLib.setDataSet2(crd, (position, card) -> {
            Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(card.wikiLink));
            try {
                startActivity(it);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        });
        TicTac.tac("parseCards");
    }

    @Override
    public void onResume() {
        super.onResume();
        //TosWiki.ff();
    }

    @Override
    protected int getPageLayoutId() {
        return R.layout.fragment_tos_card;
    }
}
