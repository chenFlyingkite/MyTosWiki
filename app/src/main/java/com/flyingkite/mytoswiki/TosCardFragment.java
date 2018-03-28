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
import com.flyingkite.library.Say;
import com.flyingkite.library.ThreadUtil;
import com.flyingkite.mytoswiki.data.TosCard;
import com.flyingkite.mytoswiki.library.CardLibrary;
import com.flyingkite.mytoswiki.tos.query.TosCardCondition;
import com.flyingkite.mytoswiki.tos.query.TosSelectRaceAttribute;
import com.flyingkite.util.DialogManager;
import com.flyingkite.util.WaitingDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class TosCardFragment extends BaseFragment {
    public static final String TAG = "TosCardFragment";
    private RecyclerView cardsRecycler;
    private CardLibrary cardLib;
    private View sortMenu;
    private PopupWindow sortWindow;
    private TextView tosInfo;
    private TosCard[] allCards;
    private ViewGroup sortAttributes;
    private ViewGroup sortRace;

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
        initSortByRace(menu);
    }

    private void initSortByAttribute(View menu) {
        sortAttributes = menu.findViewById(R.id.sortAttributes);
        ViewGroup vg = sortAttributes;
        int n = vg.getChildCount();
        for (int i = 0; i < n; i++) {
            View w = vg.getChildAt(i);
            w.setOnClickListener(this::clickAttr);
        }
    }

    private void initSortByRace(View menu) {
        sortRace = menu.findViewById(R.id.sortRaces);

        ViewGroup vg = sortRace;
        int n = vg.getChildCount();
        for (int i = 0; i < n; i++) {
            View w = vg.getChildAt(i);
            w.setOnClickListener(this::clickRace);
        }
    }

    private void clickAttr(View v) {
        v.setSelected(!v.isSelected());
        // Deselect all if all selected
        if (isAllAsSelected(sortAttributes, true)) {
            setAllChildrenSelected(sortAttributes, false);
        }

        applySelection();
    }

    private void clickRace(View v) {
        v.setSelected(!v.isSelected());
        // Deselect all if all selected
        if (isAllAsSelected(sortRace, true)) {
            setAllChildrenSelected(sortRace, false);
        }

        applySelection();
    }

    private void applySelection() {
        // attribute
        List<String> attrs = new ArrayList<>();
        getSelectTags(sortAttributes, attrs);
        // race
        List<String> races = new ArrayList<>();
        getSelectTags(sortRace, races);

        LogE("---------");
        LogE("sel T = %s", attrs);
        LogE("sel R = %s", races);

        TosCardCondition cond = new TosCardCondition().attr(attrs).race(races);
        cardLib.cardAdapter.setSelection(new TosSelectRaceAttribute(Arrays.asList(allCards), cond));
    }

    private void getSelectTags(ViewGroup vg, List<String> result) {
        if (result == null) {
            result = new ArrayList<>();
        }
        int n = vg.getChildCount();

        List<String> all = new ArrayList<>();
        boolean added = false;
        for (int i = 0; i < n; i++) {
            View w = vg.getChildAt(i);
            String tag = w.getTag().toString();
            if (w.isSelected()) {
                added = true;
                result.add(tag);
            }
            all.add(tag);
        }
        // If no children is added, add all the child tags
        if (!added) {
            result.addAll(all);
        }
    }

    private void setAllChildrenSelected(ViewGroup vg, boolean sel) {
        if (vg == null) return;

        int n = vg.getChildCount();
        for (int i = 0; i < n; i++) {
            vg.getChildAt(i).setSelected(sel);
        }
    }

    private boolean isAllAsSelected(ViewGroup vg, boolean selected) {
        if (vg == null) return false;

        int n = vg.getChildCount();
        for (int i = 0; i < n; i++) {
            if (vg.getChildAt(i).isSelected() != selected) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
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
            check();
            return null;
        }

        private void check() {
            HashSet<String> attr = new HashSet<>();
            for (TosCard c : allCards) {
                attr.add(c.attribute);
            }
            Say.Log("attr = %s", attr);

            HashSet<String> race = new HashSet<>();
            for (TosCard c : allCards) {
                race.add(c.race);
            }
            Say.Log("race = %s", race);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dialog.dismiss();
            dialog = null;
            initCardLibrary();
        }
    }
}
