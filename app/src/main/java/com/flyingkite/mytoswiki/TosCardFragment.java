package com.flyingkite.mytoswiki;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flyingkite.DialogManager;
import com.flyingkite.mytoswiki.data.TosCard;
import com.flyingkite.mytoswiki.library.CardLibrary;
import com.flyingkite.mytoswiki.tos.TosAttribute;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

public class TosCardFragment extends BaseFragment {
    public static final String TAG = "TosCardFragment";
    private RecyclerView cardsRecycler;
    private CardLibrary cardLib;
    private View sortMenu;
    private PopupWindow sortWindow;
    private TextView tosInfo;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        tosInfo = findViewById(R.id.tosInfo);
        initCardLibrary();
        initSortMenu();
    }

    private void initCardLibrary() {
        cardsRecycler = findViewById(R.id.tosRecycler);
        cardLib = new CardLibrary(cardsRecycler);
        //parseCardsRMD()

//        TicTac.tic();
//        TosCardRMDKVIR[] allCard = TosWiki.parseCardsRMD(getActivity().getAssets());
//        cardLib.setDataSetRMD(allCard);
//        TicTac.tac("parseCardsRMD");

        TosCard[] allCards = TosWiki.me.parseCards(getActivity().getAssets());
        int n = allCards == null ? 0 : allCards.length;
        showToast(R.string.cards_read, n);
        tosInfo.setText(getString(R.string.cards_selection, n, n));
        cardLib.setDataSet(allCards
                , (position, card) -> {
                    DialogManager.GenericViewBuilder.InflateListener onInflate = (v, dialog) -> {
                        ImageView icon = v.findViewById(R.id.cardIcon);
                        ImageView image = v.findViewById(R.id.cardImage);
                        TextView info = v.findViewById(R.id.cardInfo);

                        Glide.with(icon).load(card.icon).into(icon);
                        Glide.with(image).load(card.bigImage).into(image);
                        Gson g = new GsonBuilder().setPrettyPrinting().create();
                        String s = g.toJson(card, TosCard.class);
                        info.setText(s);
                    };

                    new DialogManager.GenericViewBuilder(getActivity(), R.layout.dialog_card, onInflate).buildAndShow();

        //            Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(card.wikiLink));
        //            try {
        //                startActivity(it);
        //            } catch (ActivityNotFoundException e) {
        //                e.printStackTrace();
        //            }
                }, (selected, total, map) ->  {
                    tosInfo.setText(getString(R.string.cards_selection, selected, total));

                }
        );
    }

    private void initSortMenu() {
        sortMenu = findViewById(R.id.tosSortMenu);
        // Create MenuWindow
        View menu = LayoutInflater.from(getActivity()).inflate(R.layout.popup_tos_sort, (ViewGroup) getView(), false);
        int wrap = ViewGroup.LayoutParams.WRAP_CONTENT;
        sortWindow = new PopupWindow(menu, wrap, wrap);
        sortWindow.setOutsideTouchable(true);
        sortWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        sortMenu.setOnClickListener(v -> {
            sortWindow.showAsDropDown(v);
        });

        ViewGroup vg = menu.findViewById(R.id.sortAttributes);
        setAllChildrenSelected(vg, true);
        int n = vg.getChildCount();
        for (int i = 0; i < n; i++) {
            View w = vg.getChildAt(i);
            w.setOnClickListener(this::clickAttr);
        }
    }

    private void clickAttr(View v) {
        v.setSelected(!v.isSelected());
        ViewGroup vg = (ViewGroup) v.getParent();
        if (isAllNonSelected(vg)) {
            setAllChildrenSelected(vg, true);
        }

        // Log the tag
        int n = vg.getChildCount();
        String s = "";
        for (int i = 0; i < n; i++) {
            View w = vg.getChildAt(i);
            if (w.isSelected()) {
                s += w.getTag().toString();
            }
        }
        LogE("sel = %s", s);

        Map<String, String> map = new HashMap<>();
        map.put(TosAttribute.KEY, s);
        cardLib.showSelection(map);
    }

    private void setAllChildrenSelected(ViewGroup vg, boolean sel) {
        if (vg == null) return;

        int n = vg.getChildCount();
        for (int i = 0; i < n; i++) {
            vg.getChildAt(i).setSelected(sel);
        }
    }

    private boolean isAllNonSelected(ViewGroup vg) {
        if (vg == null) return true;

        int n = vg.getChildCount();
        for (int i = 0; i < n; i++) {
            if (vg.getChildAt(i).isSelected()) {
                return false;
            }
        }
        return true;
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
