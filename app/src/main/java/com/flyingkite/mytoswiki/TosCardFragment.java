package com.flyingkite.mytoswiki;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.flyingkite.util.DialogManager;
import com.flyingkite.library.ThreadUtil;
import com.flyingkite.mytoswiki.data.TosCard;
import com.flyingkite.mytoswiki.library.CardLibrary;
import com.flyingkite.mytoswiki.tos.query.TosSelectAttribute;
import com.flyingkite.util.WaitingDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TosCardFragment extends BaseFragment {
    public static final String TAG = "TosCardFragment";
    private RecyclerView cardsRecycler;
    private CardLibrary cardLib;
    private View sortMenu;
    private PopupWindow sortWindow;
    private TextView tosInfo;
    private TosCard[] allCards;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        tosInfo = findViewById(R.id.tosInfo);

        cardsRecycler = findViewById(R.id.tosRecycler);
        cardLib = new CardLibrary(cardsRecycler);
        sortMenu = findViewById(R.id.tosSortMenu);
        initSortMenu();
        initScroll();

        new ParseCardsTask().executeOnExecutor(ThreadUtil.cachedThreadPool);
    }

    private void initScroll() {
        findViewById(R.id.tosGoTop).setOnClickListener((v) -> {
            int index = 0;
            cardLib.recycler.scrollToPosition(index);
        });

        findViewById(R.id.tosGoBottom).setOnClickListener((v) -> {
            int index = cardLib.cardAdapter.getItemCount() - 1;
            cardLib.recycler.scrollToPosition(index);
        });
    }

    private void initCardLibrary() {
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

                }, (selected, total) ->  {
                    tosInfo.setText(getString(R.string.cards_selection, selected, total));
                }
        );
    }

    private void viewLink(TosCard card) {
        Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(card.wikiLink));
        try {
            startActivity(it);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initSortMenu() {
        // Create MenuWindow
        View menu = LayoutInflater.from(getActivity()).inflate(R.layout.popup_tos_sort, (ViewGroup) getView(), false);
        int wrap = ViewGroup.LayoutParams.WRAP_CONTENT;
        sortWindow = new PopupWindow(menu, wrap, wrap);
        sortWindow.setOutsideTouchable(true);
        sortWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        sortMenu.setOnClickListener(v -> {
            if (sortWindow.isShowing()) {
                sortWindow.dismiss();
            } else {
                sortWindow.showAsDropDown(v);
            }
        });

        initSortByAttribute(menu);
    }

    private void initSortByAttribute(View menu) {
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
        List<String> sel = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            View w = vg.getChildAt(i);
            if (w.isSelected()) {
                sel.add(w.getTag().toString());
            }
        }
        LogE("sel = %s", sel);

        cardLib.cardAdapter.setSelection(new TosSelectAttribute(Arrays.asList(allCards), sel));

//        Map<String, String> map = new HashMap<>();
//        map.put(TosAttribute.KEY, s);
//        cardLib.cardAdapter.showSelection(map);
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

    private class ParseCardsTask extends AsyncTask<Void, Void, Void> {
        private WaitingDialog dialog;
        @Override
        protected void onPreExecute() {
            dialog = new WaitingDialog.Builder(getActivity()).buildAndShow();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            allCards = TosWiki.parseCards(getActivity().getAssets());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dialog.dismiss();
            dialog = null;
            initCardLibrary();
        }
    }
}
