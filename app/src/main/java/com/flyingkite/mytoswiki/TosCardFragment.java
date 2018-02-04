package com.flyingkite.mytoswiki;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flyingkite.DialogManager;
import com.flyingkite.TosCardN;
import com.flyingkite.library.TicTac;
import com.flyingkite.mytoswiki.data.TosCard;
import com.flyingkite.mytoswiki.page.CardLibrary;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
            DialogManager.GenericViewBuilder.InflateListener onInflate = (v, dialog) -> {
                ImageView icon = v.findViewById(R.id.cardIcon);
                ImageView image = v.findViewById(R.id.cardImage);
                TextView info = v.findViewById(R.id.cardInfo);

                Glide.with(icon).load(card.icon).into(icon);
                Glide.with(image).load(card.bigImage).into(image);
                Gson g = new GsonBuilder().setPrettyPrinting().create();
                String s = g.toJson(card, TosCardN.class);
                info.setText(s);
            };


            new DialogManager.GenericViewBuilder(getActivity(),
                    R.layout.dialog_card, onInflate)
                    .buildAndShow();

//            Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(card.wikiLink));
//            try {
//                startActivity(it);
//            } catch (ActivityNotFoundException e) {
//                e.printStackTrace();
//            }
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
