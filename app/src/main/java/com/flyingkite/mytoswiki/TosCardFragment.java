package com.flyingkite.mytoswiki;

import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.flyingkite.fabric.FabricAnswers;
import com.flyingkite.library.util.GsonUtil;
import com.flyingkite.library.util.ListUtil;
import com.flyingkite.library.util.MathUtil;
import com.flyingkite.library.widget.Library;
import com.flyingkite.library.widget.SimpleItemTouchHelper;
import com.flyingkite.mytoswiki.data.CardFavor;
import com.flyingkite.mytoswiki.data.CardSort;
import com.flyingkite.mytoswiki.data.tos.TosCard;
import com.flyingkite.mytoswiki.dialog.OnAction;
import com.flyingkite.mytoswiki.library.CardAdapter;
import com.flyingkite.mytoswiki.library.CardLibrary;
import com.flyingkite.mytoswiki.library.CardTileAdapter;
import com.flyingkite.mytoswiki.library.Misc;
import com.flyingkite.mytoswiki.share.ShareHelper;
import com.flyingkite.mytoswiki.tos.TosWiki;
import com.flyingkite.mytoswiki.tos.query.AllCards;
import com.flyingkite.mytoswiki.tos.query.TosCondition;
import com.flyingkite.mytoswiki.util.TosPageUtil;
import com.flyingkite.util.TaskMonitor;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import flyingkite.tool.StringUtil;

public class TosCardFragment extends BaseFragment implements TosPageUtil {
    public static final String TAG = "TosCardFragment";
    // Top Status bar
    private TextView tosInfo;
    // Main library
    private RecyclerView cardsRecycler;
    private CardLibrary cardLib;
    // Favorite library
    private View favorBox;
    private RecyclerView favorRecycler;
    private Library<CardTileAdapter> favorLib;
    // Popup Menus
    private View sortMenu;
    private PopupWindow sortWindow;
    // Popup Menu tool bar
    private View sortReset;
    // 屬性 種族 星
    private ViewGroup sortAttributes;
    private ViewGroup sortRace;
    private ViewGroup sortStar;
    // Common Sorting order
    private RadioGroup sortCommon;
    private RadioGroup sortCassandra;
    // 轉化符石
    private ViewGroup sortRunestone;
    // 特選
    private ViewGroup sortSpecial;
    private View sortSpecialAddDetail;

    private CheckBox sortSpecialNo;
    private CheckBox sortSpecialFree;
    private CheckBox sortSpecialKeep;
    private CheckBox sortSpecialExplode;
    private CheckBox sortSpecialMoreCoin;
    private CheckBox sortSpecialExtend;
    private CheckBox sortSpecialAlsoActive;
    private CheckBox sortSpecialAlsoLeader;
    private CheckBox sortSpecialFix;
    private CheckBox sortSpecialNoDefeat;
    private CheckBox sortSpecialDamageLessActive;
    private CheckBox sortSpecialDamageLessLeader;
    private CheckBox sortSpecialDamageToHp;
    private CheckBox sortSpecialNonAttribute;
    private CheckBox sortSpecialRegardlessDefense;
    private CheckBox sortSpecialRegardlessAttribute;
    private CheckBox sortSpecialClearAllEffect;
    private CheckBox sortSpecialStayUntil;
    private CheckBox sortSpecialExtraAttack;
    private CheckBox sortSpecialOriginalColor;
    private CheckBox sortSpecialRestoreNormal;
    // 提升能力
    private ViewGroup sortImprove;
    private CheckBox sortImproveNo;
    private CheckBox sortImproveAme;
    private CheckBox sortImproveAwk;
    private CheckBox sortImprovePow;
    private CheckBox sortImproveVir;
    private CheckBox sortImproveTwo;
    private CheckBox sortImproveChg; // Skill change
    private CheckBox sortImproveCom; // Combine cards
    private CheckBox sortImproveVr2;
    // Hide cards
    private ViewGroup sortHide;
    // Display card name
    private RadioGroup sortDisplay;

    private CardSort cardSort = new CardSort();
    private CardFavor cardFavor = new CardFavor();
    private boolean cardReady;

    // Major components
    private List<TosCard> allCards;

    private ToolBarOwner toolOwner;

    public interface ToolBarOwner {
        void setToolsVisible(boolean visible);
        boolean isToolsVisible();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        tosInfo = findViewById(R.id.tosInfo);

        cardsRecycler = findViewById(R.id.tosRecycler);
        favorBox = findViewById(R.id.tosFavorBox);
        favorRecycler = findViewById(R.id.tosFavorites);

        sortMenu = findViewById(R.id.tosSortMenu);
        initCardLibrary();
        initSortMenu();
        initToolIcons();

        new LoadDataAsyncTask().executeOnExecutor(sSingle);

        TosWiki.attendDatabaseTasks(onCardsReady);
    }

    private void initCardLibrary() {
        cardLib = new CardLibrary(cardsRecycler);
    }

    private void initToolIcons() {
        initScrollTools(R.id.tosGoTop, R.id.tosGoBottom, cardLib.recyclerView);

        // Setup tool bar
        View tool = findViewById(R.id.tosToolBar);
        tool.setOnClickListener((v) -> {
            v.setSelected(!v.isSelected());

            boolean s = v.isSelected();
            if (toolOwner != null) {
                toolOwner.setToolsVisible(s);
                logAction(s ? "showTool" : "hideTool");
            }
        });
        boolean sel = false;
        if (toolOwner != null) {
            sel = toolOwner.isToolsVisible();
        }
        tool.setSelected(sel);

        // Setup favorite
        View favor = findViewById(R.id.tosFavor);
        favor.setOnClickListener((v) -> {
            v.setSelected(!v.isSelected());
            boolean s = v.isSelected();

            favorBox.setVisibility(s ? View.VISIBLE : View.GONE);
            logAction(s ? "showFavor" : "hideFavor");
        });
        //favor.setSelected(true);
    }

    private void initFavorLib() {
        cardFavor = TosWiki.getCardFavor();

        if (favorLib == null) {
            favorLib = new Library<>(favorRecycler);
        }
        CardTileAdapter a = new CardTileAdapter() {
            @Override
            public FragmentManager getFragmentManager() {
                return TosCardFragment.this.getFragmentManager();
            }
        };
        final List<TosCard> favorCards = getCardsByIdNorms(cardFavor.favors);
        a.setDataList(favorCards);
        favorLib.setViewAdapter(a);

        if (helper != null) {
            helper.getHelper().attachToRecyclerView(null);
        }
        helper = new SimpleItemTouchHelper(a
                , ItemTouchHelper.LEFT | ItemTouchHelper. RIGHT
                , ItemTouchHelper.UP | ItemTouchHelper.DOWN) {
            @Override
            public List getList() {
                return favorCards;
            }

            @Override
            protected ItemTouchHelper.SimpleCallback initCallback(int dragDirs, int swipeDirs) {
                return new HelperCallback(dragDirs, swipeDirs) {
                    @Override
                    public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
                        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
                        save();
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        super.onSwiped(viewHolder, direction);
                        save();
                    }

                    private void save() {
                        cardFavor.favors = idNorms(favorCards);
                        toGsonFavor();
                    }

                    private List<String> idNorms(List<TosCard> list) {
                        List<String> a = new ArrayList<>();
                        for (TosCard c : list) {
                            a.add(c.idNorm);
                        }
                        return a;
                    }
                };
            }
        };
        helper.getHelper().attachToRecyclerView(favorLib.recyclerView);
    }

    private SimpleItemTouchHelper helper;

    private OnAction favorAction = new OnAction() {
        @Override
        public void onChanged() {
            initFavorLib();
        }
    };

    @Override
    public void onToolScrollToPosition(RecyclerView rv, int position) {
        String s = position == 0 ? "Scroll Head" : "Scroll Tail";
        logAction(s);
    }

    private TaskMonitor.OnTaskState onCardsReady = new TaskMonitor.OnTaskState() {
        @Override
        public void onTaskDone(int index, String tag) {
            log("#%s (%s) is done", index, tag);
        }

        @Override
        public void onAllTaskDone() {
            log("All task OK");
            runOnUiThread(() -> {
                cardReady = true;
                onCardsReady(TosWiki.allCards());
                TosWiki.joinFavorAction(favorAction);
            });
        }
    };

    @Override
    public void log(String message) {
        if (BuildConfig.DEBUG) {
            logI(message);
        }
    }

    private void onCardsReady(TosCard[] cards) {
        allCards = Arrays.asList(cards);
        int n = allCards.size();
        tosInfo.setText(getString(R.string.cards_selection, n, n));
        CardAdapter a = new CardAdapter();
        a.setDataList(allCards);
        a.setItemListener(new CardAdapter.ItemListener() {
            @Override
            public void onClick(TosCard card, CardAdapter.CardVH cardVH, int position) {
                showCardDialog(card);
            }

            @Override
            public void onFiltered(int selected, int total) {
                if (isDetached()) return;

                tosInfo.setText(getString(R.string.cards_selection, selected, total));
            }
        });
        cardLib.setViewAdapter(a);
        updateHide();
        applySelection();
        //testAllCardDialog();
    }

    /*private void testAllCardDialog() { //TODO : Hide me when release
        for (int i = 0; i < allCards.size(); i++) {
            TosCard ci = allCards.get(i);
            sSingle.submit(() -> {
                Say.Log("+ #%s show %s", ci.idNorm, ci.name);
                CardDialog d = showCardDialog(ci);
                Say.sleepI(500);
                Say.Log("- #%s hide %s", ci.idNorm, ci.name);
                d.dismiss();
            });
        }
    }*/

    private void initShareImage(View parent) {
        parent.findViewById(R.id.tosSave).setOnClickListener((v) -> {
            View view = cardsRecycler;
            //File folder = Environment.getExternalStoragePublicDirectory()
            String name = ShareHelper.cacheName("1.png");
            LogE("Save to %s", name);

            ShareHelper.shareImage(getActivity(), view, name);
            logShare("library");
        });
    }

    // init sort menus and put onClickListeners --------
    private void initSortMenu() {
        // Create MenuWindow
        Pair<View, PopupWindow> pair = createPopupWindow(R.layout.popup_tos_sort_card, (ViewGroup) getView());
        sortWindow = pair.second;
        View menu = pair.first;

        sortMenu.setOnClickListener(v -> {
            sortWindow.showAsDropDown(v);
            logSelectCard();
        });

        initShareImage(menu);
        initSortReset(menu);
        initSortByAttribute(menu);
        initSortByRace(menu);
        initSortByCassandra(menu);
        initSortByTurnRuneStones(menu);
        initSortByStar(menu);
        initSortByCommon(menu);
        initSortBySpecial(menu);
        initSortByHide(menu);
        initDisplay(menu);
        initSortByImprove(menu);
    }

    private void initSortReset(View menu) {
        sortReset = menu.findViewById(R.id.sortReset);
        sortReset.setOnClickListener(this::clickReset);
    }

    private void initSortByAttribute(View menu) {
        sortAttributes = initSortOf(menu, R.id.sortAttributes, this::clickAttr);
    }

    private void initSortByRace(View menu) {
        sortRace = initSortOf(menu, R.id.sortRaces, this::clickRace);
    }

    private void initSortByCassandra(View menu) {
        sortCassandra = initSortOf(menu, R.id.sortCassandraList, this::clickCassandra);
    }

    private void initSortByTurnRuneStones(View menu) {
        sortRunestone = initSortOf(menu, R.id.sortRunestone, this::clickTurnRuneStones);
    }

    private void initSortByStar(View menu) {
        sortStar = initSortOf(menu, R.id.sortStar, this::clickStar);
    }

    private void initSortByCommon(View menu) {
        sortCommon = initSortOf(menu, R.id.sortCommonList, this::clickCommon);
    }

    private void initSortBySpecial(View menu) {
        sortSpecialAddDetail = menu.findViewById(R.id.sortSpecialAddDetail);
        sortSpecialAddDetail.setOnClickListener(this::toggleSelectApply);

        sortSpecialNo = menu.findViewById(R.id.sortSpecialNo);
        sortSpecialFree = menu.findViewById(R.id.sortSpecialFreeMove);
        sortSpecialKeep = menu.findViewById(R.id.sortSpecialKeep);
        sortSpecialExplode = menu.findViewById(R.id.sortSpecialExplode);
        sortSpecialMoreCoin = menu.findViewById(R.id.sortSpecialMoreCoin);
        sortSpecialExtend = menu.findViewById(R.id.sortSpecialExtend);
        sortSpecialAlsoActive = menu.findViewById(R.id.sortSpecialAlsoActive);
        sortSpecialAlsoLeader = menu.findViewById(R.id.sortSpecialAlsoLeader);
        sortSpecialFix = menu.findViewById(R.id.sortSpecialFix);
        sortSpecialNoDefeat = menu.findViewById(R.id.sortSpecialNoDefeat);
        sortSpecialDamageLessActive = menu.findViewById(R.id.sortSpecialDamageLessActive);
        sortSpecialDamageLessLeader = menu.findViewById(R.id.sortSpecialDamageLessLeader);
        sortSpecialDamageToHp = menu.findViewById(R.id.sortSpecialDamageToHp);
        sortSpecialNonAttribute = menu.findViewById(R.id.sortSpecialNonAttribute);
        sortSpecialRegardlessDefense = menu.findViewById(R.id.sortSpecialRegardlessDefense);
        sortSpecialRegardlessAttribute = menu.findViewById(R.id.sortSpecialRegardlessAttribute);
        sortSpecialClearAllEffect = menu.findViewById(R.id.sortSpecialClearAllEffect);
        sortSpecialStayUntil = menu.findViewById(R.id.sortSpecialStayUntil);
        sortSpecialExtraAttack = menu.findViewById(R.id.sortSpecialExtraAttack);
        sortSpecialOriginalColor = menu.findViewById(R.id.sortSpecialOriginalColor);
        sortSpecialRestoreNormal = menu.findViewById(R.id.sortSpecialRestoreNormal);

        sortSpecial = initSortOf(menu, R.id.sortSpecialList, this::clickSpecial);
    }

    private void initSortByHide(View menu) {
        sortHide = initSortOf(menu, R.id.sortHide, this::clickHide);
    }

    private <T extends ViewGroup> T initSortOf(View menu, @IdRes int id, View.OnClickListener childClick) {
        return setTargetChildClick(menu, id, childClick);
    }

    private void initDisplay(View menu) {
        sortDisplay = initSortOf(menu, R.id.sortDisplayList, this::clickDisplay);
        sortDisplay.check(R.id.sortDisplayNormId);
    }

    private void initSortByImprove(View menu) {
        sortImproveNo = menu.findViewById(R.id.sortImproveNo);
        sortImproveAme = menu.findViewById(R.id.sortImproveAmelioration);
        sortImproveAwk = menu.findViewById(R.id.sortImproveAwakenRecall);
        sortImprovePow = menu.findViewById(R.id.sortImprovePowerRelease);
        sortImproveVir = menu.findViewById(R.id.sortImproveVirtualRebirth);
        sortImproveTwo = menu.findViewById(R.id.sortImproveTwoSkill);
        sortImproveChg = menu.findViewById(R.id.sortImproveSkillChange);
        sortImproveCom = menu.findViewById(R.id.sortImproveCombine);
        sortImproveVr2 = menu.findViewById(R.id.sortImproveVirtualRebirthChange);

        sortImprove = initSortOf(menu, R.id.sortImprove, this::clickImprove);
    }
    // --------

    // click listeners for sort menus --------
    private void clickReset(View v) {
        ViewGroup[] vgs = {sortAttributes, sortRace, sortStar, sortRunestone};
        for (ViewGroup vg : vgs) {
            setAllChildrenSelected(vg, false);
        }
        sortCommon.check(R.id.sortCommonNormId);
        sortCassandra.check(R.id.sortCassandraNo);
        setCheckedIncludeNo(sortSpecialNo, R.id.sortSpecialNo, sortSpecial);
        setCheckedIncludeNo(sortImproveNo, R.id.sortImproveNo, sortImprove);

        applySelection();
    }

    private void clickAttr(View v) {
        nonAllApply(v, sortAttributes);
    }

    private void clickRace(View v) {
        nonAllApply(v, sortRace);
    }

    private void clickCassandra(View v) {
        int id = v.getId();
        sortCassandra.check(id);
        if (id != R.id.sortCassandraNo) {
            sortCommon.check(R.id.sortCommonNormId);
        }
        sortCommon.setEnabled(id != R.id.sortCassandraNo);

        applySelection();
    }

    private void clickTurnRuneStones(View v) {
        toggleSelectApply(v);
    }

    private void clickStar(View v) {
        nonAllApply(v, sortStar);
    }

    private void clickSpecial(View v) {
        setCheckedIncludeNo(v, R.id.sortSpecialNo, sortSpecial);

        applySelection();
    }

    private void clickDisplay(View v) {
        sortDisplay.check(v.getId());

        int type = Misc.NT_ID_NORM;
        switch (v.getId()) {
            case R.id.sortDisplayName:
                type = Misc.NT_NAME;
                break;
        }
        if (cardLib.adapter != null) {
            cardLib.adapter.setNameType(type);
            cardLib.adapter.updateSelection();
            cardLib.adapter.notifyDataSetChanged();
        }
    }

    private void clickCommon(View v) {
        int id = v.getId();
        sortCommon.check(id);
        if (id != R.id.sortCommonNormId) {
            sortCassandra.check(R.id.sortCassandraNo);
        }
        sortCassandra.setEnabled(id != R.id.sortCassandraNo);

        applySelection();
    }

    private void clickHide(View v) {
        toggleSelectApply(v);
    }

    private void toggleSelectApply(View v) {
        v.setSelected(!v.isSelected());
        applySelection();
    }

    private void clickImprove(View v) {
        setCheckedIncludeNo(v, R.id.sortImproveNo, sortImprove);
        applySelection();
    }
    // --------

    // Apply selection to adapter --------
    private void applySelection() {
        // Attribute
        List<String> attrs = new ArrayList<>();
        getSelectTags(sortAttributes, attrs, true);
        // Race
        List<String> races = new ArrayList<>();
        getSelectTags(sortRace, races, true);
        // Star
        List<String> stars = new ArrayList<>();
        getSelectTags(sortStar, stars, true);

        LogE("-----Cards-----");
        LogE("A = %s", attrs);
        LogE("R = %s", races);
        LogE("S = %s", stars);

        if (cardLib.adapter != null) {
            TosCondition cond = new TosCondition().attr(attrs).race(races).star(stars);
            cardLib.adapter.setSelection(new TosSelectCard(allCards, cond));
        }
    }

    private void nonAllApply(View v, ViewGroup vg) {
        toggleSelection(v, vg);

        applySelection();
    }

    private void updateHide() {
        sortHide.findViewById(R.id.sortHide6xxx).setSelected(cardSort.hideCard6xxx);
        sortHide.findViewById(R.id.sortHide7xxx).setSelected(cardSort.hideCard7xxx);
        sortHide.findViewById(R.id.sortHide8xxx).setSelected(cardSort.hideCard8xxx);
        sortHide.findViewById(R.id.sortHide9xxx).setSelected(cardSort.hideCard9xxx);
        int id = cardSort.displayByName ? R.id.sortDisplayName : R.id.sortDisplayNormId;
        clickDisplay(sortDisplay.findViewById(id));
    }
    // --------

    // Saving preference as Gson --------
    private void toGsonHide() {
        cardSort.hideCard6xxx = sortHide.findViewById(R.id.sortHide6xxx).isSelected();
        cardSort.hideCard7xxx = sortHide.findViewById(R.id.sortHide7xxx).isSelected();
        cardSort.hideCard8xxx = sortHide.findViewById(R.id.sortHide8xxx).isSelected();
        cardSort.hideCard9xxx = sortHide.findViewById(R.id.sortHide9xxx).isSelected();
        cardSort.displayByName = sortDisplay.getCheckedRadioButtonId() == R.id.sortDisplayName;
        sSingle.submit(() -> {
            GsonUtil.writeFile(getTosCardSortFile(), new Gson().toJson(cardSort));
        });
    }

    private void toGsonFavor() {
        sSingle.submit(() -> {
            TosWiki.saveCardFavor(cardFavor);
        });
    }
    // --------

    @Override
    public void onPause() {
        super.onPause();
        toGsonHide();
        toGsonFavor();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected int getPageLayoutId() {
        return R.layout.fragment_tos_card;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ToolBarOwner) {
            toolOwner = (ToolBarOwner) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        toolOwner = null;
    }

    // Actual implementation of TosSelectCard --------
    private class TosSelectCard extends AllCards<TosCard> {
        private final String[] commonRace = getResources().getStringArray(R.array.cards_common_keys_race);

        private TosCondition select;

        public TosSelectCard(List<TosCard> source, TosCondition condition) {
            super(source);
            select = condition;
        }

        @Override
        public String typeName() {
            return "TosCard";
        }

        @Override
        public boolean onSelect(TosCard c){
            return selectForBasic(c)
                    && selectForTurnRunestones(c)
                    && selectForSpecial(c)
                    && selectForShow(c)
                    && selectForImprove(c)
            ;
        }

        private boolean selectForBasic(TosCard c) {
            List<String> attrs = select.getAttr();
            List<String> races = select.getRace();
            List<String> stars = select.getStar();
            return attrs.contains(c.attribute)
                    && races.contains(c.race)
                    && stars.contains("" + c.rarity);
        }

        private boolean selectForTurnRunestones(TosCard c) {
            // Still fail for 轉化為固定數量及位置 & X及Y符石轉化為強化符石
            // like, 洛可可(1169) & 龍葵(0900) & 1772 (尼特羅)
            // Runestones keys as st
            List<String> stones = new ArrayList<>();
            getSelectTags(sortRunestone, stones, false);
            int n = stones.size();
            if (n == 0) {
                return true;
            } else {
                String[] st = new String[n];
                for (int i = 0; i < n; i++) {
                    st[i] = getString(R.string.cards_turn_into) + "" + stones.get(i);
                }

                String key = c.skillDesc1 + " & " + c.skillDesc2;
                return find(key, st);
            }
        }

        private boolean selectForShow(TosCard c) {
            int idNorm = Integer.parseInt(c.idNorm);
            boolean accept = true;

            ViewGroup vg = sortHide;
            int n = vg.getChildCount();
            for (int i = 0; i < n; i++) {
                View v = vg.getChildAt(i);
                if (v.isSelected()) {
                    switch (v.getId()) {
                        case R.id.sortHide6xxx:
                            accept &= !MathUtil.isInRange(idNorm, 6000, 7000);
                            break;
                        case R.id.sortHide7xxx:
                            accept &= !MathUtil.isInRange(idNorm, 7000, 8000);
                            break;
                        case R.id.sortHide8xxx:
                            accept &= !MathUtil.isInRange(idNorm, 8000, 9000);
                            break;
                        case R.id.sortHide9xxx:
                            accept &= !MathUtil.isInRange(idNorm, 9000, 10000);
                            break;
                    }
                }
            }
            return accept;
        }

        private boolean selectForSpecial(TosCard c) {
            String key = activeSkill(c);
            boolean accept = true;
            if (!sortSpecialNo.isChecked()) {
                if (sortSpecialFree.isChecked()) {
                    //noinspection ConstantConditions
                    accept &= find(key, R.array.cards_freemove_keys);
                }
                if (sortSpecialKeep.isChecked()) {
                    accept &= find(key, R.array.cards_keep_keys);
                }
                if (sortSpecialExplode.isChecked()) {
                    accept &= find(key, R.array.cards_explode_keys);
                }
                if (sortSpecialMoreCoin.isChecked()) {
                    key += " & " + c.skillLeaderDesc;
                    accept &= find(key, R.array.cards_morecoin_keys);
                }
                if (sortSpecialExtend.isChecked()) {
                    accept &= find(key, R.array.cards_extend_keys);
                }
                if (sortSpecialAlsoActive.isChecked()) {
                    accept &= find(key, R.array.cards_also_keys);
                }
                if (sortSpecialAlsoLeader.isChecked()) {
                    key = leaderSkill(c);
                    accept &= find(key, R.array.cards_also_keys);
                }
                if (sortSpecialFix.isChecked()) {
                    accept &= find(key, R.array.cards_fix_keys);
                }
                if (sortSpecialNoDefeat.isChecked()) {
                    accept &= find(key, R.array.cards_no_defeat_keys);
                }
                if (sortSpecialDamageLessActive.isChecked()) {
                    accept &= find(key, R.array.cards_damage_less_keys);
                }
                if (sortSpecialDamageLessLeader.isChecked()) {
                    key = leaderSkill(c);
                    accept &= find(key, R.array.cards_damage_less_leader_keys);
                }
                if (sortSpecialDamageToHp.isChecked()) {
                    accept &= find(key, R.array.cards_damage_to_hp_keys);
                }
                if (sortSpecialNonAttribute.isChecked()) {
                    accept &= find(key, R.array.cards_non_attribute_keys);
                }
                if (sortSpecialRegardlessDefense.isChecked()) {
                    accept &= find(key, R.array.cards_regardless_of_defense_keys);
                }
                if (sortSpecialRegardlessAttribute.isChecked()) {
                    accept &= find(key, R.array.cards_regardless_of_attribute_keys);
                }
                if (sortSpecialClearAllEffect.isChecked()) {
                    accept &= find(key, R.array.cards_clear_all_effect_keys);
                }
                if (sortSpecialStayUntil.isChecked()) {
                    accept &= find(key, R.array.cards_stay_until_keys);
                }
                if (sortSpecialExtraAttack.isChecked()) {
                    accept &= find(key, R.array.cards_extra_attack_keys);
                }
                if (sortSpecialOriginalColor.isChecked()) {
                    accept &= find(key, R.array.cards_black_white_original_keys);
                }
                if (sortSpecialRestoreNormal.isChecked()) {
                    accept &= find(key, R.array.cards_restore_runestone_normal_keys);
                }
            }
            return accept;
        }

        private String leaderSkill(TosCard c) {
            return joinKey(c.skillLeaderDesc, c);
        }

        private String activeSkill(TosCard c) {
            return joinKey(c.skillDesc1 + " & " + c.skillDesc2, c);
        }

        private String joinKey(String key, TosCard c) {
            if (sortSpecialAddDetail.isSelected()) {
                key += " & " + c.cardDetails;
            }
            return key;
        }

        private boolean find(String key, @ArrayRes int dataId) {
            String[] data = getResources().getStringArray(dataId);
            return find(key, data);
        }

        private boolean find(String key, String[] data) {
            return StringUtil.containsAt(key, data) >= 0;
        }

        private boolean selectForImprove(TosCard c) {
            boolean accept = true;
            if (!sortImproveNo.isChecked()) {
                if (sortImproveAme.isChecked()) {
                    //noinspection ConstantConditions
                    accept &= c.skillAmeCost1 > 0;
                }
                if (sortImproveAwk.isChecked()) {
                    accept &= !c.skillAwkName.isEmpty();
                }
                if (sortImprovePow.isChecked()) {
                    accept &= !c.skillPowBattleName.isEmpty();
                }
                if (sortImproveVir.isChecked()) {
                    accept &= !c.skillVirBattleName.isEmpty();
                }
                if (sortImproveTwo.isChecked()) {
                    accept &= !c.skillName2.isEmpty();
                }
                if (sortImproveChg.isChecked()) {
                    accept &= c.skillChange.size() > 0;
                }
                if (sortImproveCom.isChecked()) {
                    accept &= c.combineFrom.size() > 0 && c.combineTo.size() > 0;
                }
                if (sortImproveVr2.isChecked()) {
                    accept &= c.rebirthChange.length() > 0;
                }
            }
            return accept;
        }

        @NonNull
        @Override
        public List<Integer> sort(@NonNull List<Integer> result) {
            Comparator<Integer> cmp;
            cmp = getCommonComparator();
            if (cmp == null) {
                cmp = getCassandraComparator();
            }

            // Apply the comparator on result
            if (cmp != null) {
                Collections.sort(result, cmp);
            }
            return result;
        }

        @Override
        public List<String> getMessages(List<Integer> result) {
            List<String> msgs;
            msgs = getCommonMessages(result);
            if (msgs == null) {
                msgs = getCassandraMessages(result);
            }
            return msgs;
        }

        private Comparator<Integer> getCassandraComparator() {
            // Create comparator
            int id = sortCassandra.getCheckedRadioButtonId();
            switch (id) {
                case R.id.sortCassandraAttack:
                    return (o1, o2) -> {
                        boolean dsc = true;
                        TosCard c1 = data.get(o1);
                        TosCard c2 = data.get(o2);
                        double atk1 = c1.maxAttack + c1.maxRecovery * 3.5;
                        double atk2 = c2.maxAttack + c2.maxRecovery * 3.5;
                        //logCard("#1", c1);
                        //logCard("#2", c2);
                        if (dsc) {
                            return Double.compare(atk2, atk1);
                        } else {
                            return Double.compare(atk1, atk2);
                        }
                    };
                case R.id.sortCassandraRatio:
                    return (o1, o2) -> {
                        boolean dsc = true;
                        TosCard c1 = data.get(o1);
                        TosCard c2 = data.get(o2);
                        double atk1 = 1 + c1.maxRecovery * 3.5 / c1.maxAttack;
                        double atk2 = 1 + c2.maxRecovery * 3.5 / c2.maxAttack;
                        if (dsc) {
                            return Double.compare(atk2, atk1);
                        } else {
                            return Double.compare(atk1, atk2);
                        }
                    };
                default:
                    break;
            }
            return null;
        }

        private Comparator<Integer> getCommonComparator() {
            // Create comparator
            int id = sortCommon.getCheckedRadioButtonId();
            if (id == RadioGroup.NO_ID || id == R.id.sortCommonNormId) {
                return null;
            }
            return (o1, o2) -> {
                boolean dsc = true;
                TosCard c1 = data.get(o1);
                TosCard c2 = data.get(o2);
                long v1 = -1, v2 = -1;

                switch (id) {
                    case R.id.sortCommonMaxHP:
                        v1 = c1.maxHP;
                        v2 = c2.maxHP;
                        break;
                    case R.id.sortCommonMaxAttack:
                        v1 = c1.maxAttack;
                        v2 = c2.maxAttack;
                        break;
                    case R.id.sortCommonMaxRecovery:
                        v1 = c1.maxRecovery;
                        v2 = c2.maxRecovery;
                        break;
                    case R.id.sortCommonMaxSum:
                        v1 = c1.maxHP + c1.maxAttack + c1.maxRecovery;
                        v2 = c2.maxHP + c2.maxAttack + c2.maxRecovery;
                        break;
                    case R.id.sortCommonSkillCDMax:
                        dsc = false;
                        v1 = normSkillCD(c1);
                        v2 = normSkillCD(c2);
                        break;
                    case R.id.sortCommonSpace:
                        dsc = false;
                        v1 = c1.cost;
                        v2 = c2.cost;
                        break;
                    case R.id.sortCommonRace:
                        dsc = false;
                        v1 = ListUtil.indexOf(commonRace, c1.race);
                        v2 = ListUtil.indexOf(commonRace, c2.race);
                        break;
                }

                if (dsc) {
                    return Long.compare(v2, v1);
                } else {
                    return Long.compare(v1, v2);
                }
            };
        }

        private long normSkillCD(TosCard c) {
            long norm;
            final long radix = 100;
            int c1 = c.skillCDMax1;
            int c2 = c.skillCDMax2;
            if (c1 == 0) { // No skills
                norm = radix * radix;
            } else if (c2 == 0) { // single skill
                norm = c1 * radix;
            } else { // two skills
                // minCD * radix + maxCD
                if (c1 < c2) {
                    norm = c1 * radix + c2;
                } else {
                    norm = c2 * radix + c1;
                }
            }
            return norm;
        }

        private void logCard(String prefix, TosCard c) {
            // https://stackoverflow.com/questions/16946694/how-do-i-align-the-decimal-point-when-displaying-doubles-and-floats
            // Align float point is %(x+y+1).yf
            LogE("%s %s -> %4s + %4s * 3.5 = %7.1f => %s"
                , prefix, c.idNorm, c.maxAttack, c.maxRecovery
                , c.maxAttack + c.maxRecovery * 3.5, c.name
            );
        }

        private List<String> getCassandraMessages(List<Integer> result) {
            List<String> message = null;
            TosCard c;
            String msg;
            // Create Message
            int id = sortCassandra.getCheckedRadioButtonId();
            switch (id) {
                case R.id.sortCassandraAttack:
                    message = new ArrayList<>();
                    for (int i = 0; i < result.size(); i++) {
                        c = data.get(result.get(i));
                        double atk = c.maxAttack + c.maxRecovery * 3.5;
                        msg = String.format(Locale.US, "%.1f", atk);
                        message.add(msg);
                    }
                    break;
                case R.id.sortCassandraRatio:
                    message = new ArrayList<>();
                    for (int i = 0; i < result.size(); i++) {
                        c = data.get(result.get(i));
                        double atk = 1 + c.maxRecovery * 3.5 / c.maxAttack;
                        msg = String.format(Locale.US, "%.2f", atk);
                        message.add(msg);
                    }
                    break;
                default:
                    break;
            }
            return message;
        }

        private List<String> getCommonMessages(List<Integer> result) {
            List<String> message = new ArrayList<>();
            TosCard c;
            String msg;
            // Create Message
            boolean added = false;
            int id = sortCommon.getCheckedRadioButtonId();

            for (int i = 0; i < result.size(); i++) {
                c = data.get(result.get(i));
                msg = null;
                switch (id) {
                    case R.id.sortCommonMaxHP:
                        msg = String.valueOf(c.maxHP);
                        break;
                    case R.id.sortCommonMaxAttack:
                        msg = String.valueOf(c.maxAttack);
                        break;
                    case R.id.sortCommonMaxRecovery:
                        msg = String.valueOf(c.maxRecovery);
                        break;
                    case R.id.sortCommonMaxSum:
                        msg = String.valueOf(c.maxHP + c.maxAttack + c.maxRecovery);
                        break;
                    case R.id.sortCommonSkillCDMax:
                        msg = "" + c.skillCDMax1;
                        if (c.skillCDMax2 > 0) {
                             msg += " & " + c.skillCDMax2;
                        }
                        break;
                    case R.id.sortCommonSpace:
                        msg = String.valueOf(c.cost);
                        break;
                    case R.id.sortCommonRace:
                        String name = c.id;
                        if (cardLib.adapter != null) {
                            name = cardLib.adapter.name(c);
                        }
                        msg = name + "\n" + c.race;
                        break;
                    default:
                }

                if (msg != null) {
                    added = true;
                    message.add(msg);
                }
            }

            if (added) {
                return message;
            } else {
                return null;
            }
        }
    }
    // --------

    private void logSelectCard() {
        Map<String, String> m = new HashMap<>();
        //String id = craft == null ? "--" : craft.idNorm;
        //m.put("craft", id);
        FabricAnswers.logSelectCard(null);
    }

    // The file of dialog setting
    private File getTosCardSortFile() {
        return ShareHelper.extFilesFile("cardSort.txt");
    }

    private class LoadDataAsyncTask extends AsyncTask<Void, Void, CardSort> {
        @Override
        protected CardSort doInBackground(Void... voids) {
            File f = getTosCardSortFile();
            if (f.exists()) {
                return GsonUtil.loadFile(getTosCardSortFile(), CardSort.class);
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(CardSort data) {
            cardSort = data != null ? data : new CardSort();
            updateHide();
            applySelection();
        }
    }

    //-- Events
    private void logShare(String type) {
        Map<String, String> m = new HashMap<>();
        m.put("share", type);
        FabricAnswers.logCardFragment(m);
    }

    private void logAction(String act) {
        Map<String, String> m = new HashMap<>();
        m.put("action", act);
        FabricAnswers.logCardFragment(m);
    }

    private void logShowCard() {
        Map<String, String> m = new HashMap<>();
        FabricAnswers.logCardFragment(m);
    }

    private void logImpression() {
        Map<String, String> m = new HashMap<>();
        m.put("impression", "1");
        FabricAnswers.logCardFragment(m);
    }
    //-- Events
}
