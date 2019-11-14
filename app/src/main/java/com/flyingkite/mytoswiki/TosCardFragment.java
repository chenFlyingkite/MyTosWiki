package com.flyingkite.mytoswiki;

import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.flyingkite.library.util.ThreadUtil;
import com.flyingkite.library.widget.Library;
import com.flyingkite.library.widget.SimpleItemTouchHelper;
import com.flyingkite.mytoswiki.data.CardFavor;
import com.flyingkite.mytoswiki.data.CardSort;
import com.flyingkite.mytoswiki.data.tos.SkillLite;
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
import com.flyingkite.mytoswiki.util.RegexUtil;
import com.flyingkite.mytoswiki.util.TosCardUtil;
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
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import androidx.annotation.ArrayRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
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
    private View menuEntry;
    private PopupWindow sortWindow;
    // Popup Menu tool bar
    private View sortReset;
    // 屬性 種族 星
    private ViewGroup sortAttributes;
    private ViewGroup sortRace;
    private ViewGroup sortStar;
    // Common Sorting order
    private RadioGroup sortCommon;
    // 卡片攻擊力計算式
    private RadioGroup sortFormulaCard;
    private RadioGroup sortFormulaList;
    // 轉化符石
    private ViewGroup sortRunestone;
    // 種族符石
    private ViewGroup sortRaceStoneAttr;
    private ViewGroup sortRaceStoneRace;
    private View sortRaceStoneAddDetail;
    private View sortRaceStoneAddLeader;
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
    private CheckBox sortSpecialRegardlessInitialShield;
    private CheckBox sortSpecialClearAllEffect;
    private CheckBox sortSpecialStayUntil;
    private CheckBox sortSpecialStayUntilIf;
    private CheckBox sortSpecialExtraAttack;
    private CheckBox sortSpecialOriginalColor;
    private CheckBox sortSpecialRestoreNormal;
    private CheckBox sortSpecialRestoreDropRateTransfer;
    private CheckBox sortSpecialRestoreIntoEnchanted;
    private CheckBox sortSpecialRestoreAllAttrRandom;
    private CheckBox sortSpecialRestoreAllIntoRandom;
    private CheckBox sortSpecialRestoreAllInto;
    private CheckBox sortSpecialRestoreAllIntoEnchanted;
    private CheckBox sortSpecialDodge;
    private CheckBox sortSpecialOneDealDamage;
    private CheckBox sortSpecialOneDealDamageElement;
    private CheckBox sortSpecialAllDealDamage;
    private CheckBox sortSpecialAllDealDamageElement;
    private CheckBox sortSpecialTurnEnemyAttr;
    private CheckBox sortSpecialDelay;
    private CheckBox sortSpecialClearLock;
    private CheckBox sortSpecialAlsoHeartActive;
    private CheckBox sortSpecialAttackBonusCombo;
    private CheckBox sortSpecialAddComboCount;
    private CheckBox sortSpecialSkillCdMinus;
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
    private CheckBox sortImproveSwt;
    private CheckBox sortImproveDmx;
    // Hide cards
    private ViewGroup sortHide;
    // Display card name
    private RadioGroup sortDisplay;
    // Search
    private CheckBox search;
    private CheckBox searchRegex;
    private View searchApply;
    private TextView searchText;
    private View searchClear;
    private CheckBox searchRangeName;
    private CheckBox searchRangeSeries;
    private CheckBox searchRangeSkillActive;
    private CheckBox searchRangeSkillLeader;
    private CheckBox searchRangeDetail;
    // Search select
    private CheckBox searchOnHp;
    private CheckBox searchOnAttack;
    private CheckBox searchOnRecovery;

    private CardSort cardSort = new CardSort();
    private CardFavor cardFavor = new CardFavor();

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

        menuEntry = findViewById(R.id.tosSortMenu);
        initCardLibrary();
        initSortMenu();
        initToolIcons();
        resetMenu();

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
            toggleSelected(v);

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
            boolean s = !v.isSelected();
            updateFavor(s);
            new AppPref().setShowFavorite(s);
            logAction(s ? "showFavor" : "hideFavor");
        });
        updateFavor(new AppPref().getShowFavorite());
    }

    private void updateFavor(boolean b) {
        View favor = findViewById(R.id.tosFavor);
        favor.setSelected(b);
        favorBox.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    private void initFavorLib() {
        cardFavor = TosWiki.getCardFavor();

        if (favorLib == null) {
            favorLib = new Library<>(favorRecycler, -1);
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
                , ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
                , ItemTouchHelper.UP | ItemTouchHelper.DOWN
//                , ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
//                | ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0
        ) {
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
            if (isActivityGone()) {
                return;
            }

            getActivity().runOnUiThread(() -> {
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
        tosInfo.setText(App.res().getString(R.string.cards_selection, n, n));
        CardAdapter a = new CardAdapter();
        a.setDataList(allCards);
        a.setItemListener(new CardAdapter.ItemListener() {
            @Override
            public void onClick(TosCard card, CardAdapter.CardVH cardVH, int position) {
                showCardDialog(card);
            }

            @Override
            public void onFiltered(int selected, int total) {
                if (isActivityGone()) return;

                tosInfo.setText(getString(R.string.cards_selection, selected, total));
            }
        });
        cardLib.setViewAdapter(a);
        updateHide();
        applySelection();
        //testAllCardDialog();
    }

    /*
    private void testAllCardDialog() { //TODO : Hide me when release
        for (int i = 0; i < allCards.size(); i++) {
            TosCard ci = allCards.get(i);
            sSingle.submit(() -> {
                logE("+ #%s show %s", ci.idNorm, ci.name);
                CardDialog d = showCardDialog(ci);
                Say.sleepI(500);
                logE("- #%s hide %s", ci.idNorm, ci.name);
                d.dismiss();
            });
        }
    }
    */

    private void initShareImage(View parent) {
        parent.findViewById(R.id.tosSave).setOnClickListener((v) -> {
            View view = cardsRecycler;
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

        menuEntry.setOnClickListener(v -> {
            sortWindow.showAsDropDown(v);
            logSelectCard();
        });

        initShareImage(menu);
        initSortReset(menu);
        initSortByAttribute(menu);
        initSortByRace(menu);
        initSortByRaceRuneStones(menu);
        initSortByFormula(menu);
        initSortByTurnRuneStones(menu);
        initSortByStar(menu);
        initSortByCommon(menu);
        initSortBySpecial(menu);
        initSortByHide(menu);
        initDisplay(menu);
        initSortByImprove(menu);
        initSortBySearch(menu);
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

    private void initSortByRaceRuneStones(View menu) {
        sortRaceStoneAddDetail = menu.findViewById(R.id.sortRaceStoneAddDetail);
        sortRaceStoneAddLeader = menu.findViewById(R.id.sortRaceStoneAddLeader);
        sortRaceStoneAddDetail.setOnClickListener(this::clickRaceRuneStones);
        sortRaceStoneAddLeader.setOnClickListener(this::clickRaceRuneStones);
        sortRaceStoneAddDetail.setSelected(true);
        sortRaceStoneAddLeader.setSelected(true);

        sortRaceStoneAttr = initSortOf(menu, R.id.sortRaceStoneAttr, this::clickRaceRuneStones);
        sortRaceStoneRace = initSortOf(menu, R.id.sortRaceStoneRace, this::clickRaceRuneStones);
    }

    private void initSortByFormula(View menu) {
        sortFormulaCard = initSortOf(menu, R.id.sortFormulaCard, this::clickFormulaCard);
        sortFormulaList = initSortOf(menu, R.id.sortFormulaList, this::clickFormulaList);
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
        sortSpecialRestoreDropRateTransfer = menu.findViewById(R.id.sortSpecialRestoreDropRateTransfer);
        sortSpecialNoDefeat = menu.findViewById(R.id.sortSpecialNoDefeat);
        sortSpecialDamageLessActive = menu.findViewById(R.id.sortSpecialDamageLessActive);
        sortSpecialDamageLessLeader = menu.findViewById(R.id.sortSpecialDamageLessLeader);
        sortSpecialDamageToHp = menu.findViewById(R.id.sortSpecialDamageToHp);
        sortSpecialNonAttribute = menu.findViewById(R.id.sortSpecialNonAttribute);
        sortSpecialRegardlessDefense = menu.findViewById(R.id.sortSpecialRegardlessDefense);
        sortSpecialRegardlessAttribute = menu.findViewById(R.id.sortSpecialRegardlessAttribute);
        sortSpecialRegardlessInitialShield = menu.findViewById(R.id.sortSpecialRegardlessInitialShield);
        sortSpecialClearAllEffect = menu.findViewById(R.id.sortSpecialClearAllEffect);
        sortSpecialStayUntil = menu.findViewById(R.id.sortSpecialStayUntil);
        sortSpecialStayUntilIf = menu.findViewById(R.id.sortSpecialStayUntilIf);
        sortSpecialExtraAttack = menu.findViewById(R.id.sortSpecialExtraAttack);
        sortSpecialOriginalColor = menu.findViewById(R.id.sortSpecialOriginalColor);
        sortSpecialRestoreNormal = menu.findViewById(R.id.sortSpecialRestoreNormal);
        sortSpecialFix = menu.findViewById(R.id.sortSpecialFix);
        sortSpecialRestoreIntoEnchanted = menu.findViewById(R.id.sortSpecialRestoreIntoEnchanted);
        sortSpecialRestoreAllAttrRandom = menu.findViewById(R.id.sortSpecialRestoreAllAttrRandom);
        sortSpecialRestoreAllIntoRandom = menu.findViewById(R.id.sortSpecialRestoreAllIntoRandom);
        sortSpecialRestoreAllInto = menu.findViewById(R.id.sortSpecialRestoreAllInto);
        sortSpecialRestoreAllIntoEnchanted = menu.findViewById(R.id.sortSpecialRestoreAllIntoEnchanted);
        sortSpecialDodge = menu.findViewById(R.id.sortSpecialDodge);
        sortSpecialOneDealDamage = menu.findViewById(R.id.sortSpecialOneDealDamage);
        sortSpecialOneDealDamageElement = menu.findViewById(R.id.sortSpecialOneDealDamageElement);
        sortSpecialAllDealDamage = menu.findViewById(R.id.sortSpecialAllDealDamage);
        sortSpecialAllDealDamageElement = menu.findViewById(R.id.sortSpecialAllDealDamageElement);
        sortSpecialTurnEnemyAttr = menu.findViewById(R.id.sortSpecialTurnEnemyAttr);
        sortSpecialDelay = menu.findViewById(R.id.sortSpecialDelay);
        sortSpecialClearLock = menu.findViewById(R.id.sortSpecialClearLock);
        sortSpecialAlsoHeartActive = menu.findViewById(R.id.sortSpecialAlsoHeartActive);
        sortSpecialAttackBonusCombo = menu.findViewById(R.id.sortSpecialAttackBonusCombo);
        sortSpecialAddComboCount = menu.findViewById(R.id.sortSpecialAddComboCount);
        sortSpecialSkillCdMinus = menu.findViewById(R.id.sortSpecialSkillCdMinus);


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
        sortImproveSwt = menu.findViewById(R.id.sortImproveSwitch);
        sortImproveDmx = menu.findViewById(R.id.sortImproveDualMaxAdd);

        sortImprove = initSortOf(menu, R.id.sortImprove, this::clickImprove);
    }

    private void initSortBySearch(View menu) {
        search = menu.findViewById(R.id.sortSearchUse);
        searchRegex = menu.findViewById(R.id.sortSearchRegex);
        searchApply = menu.findViewById(R.id.sortSearch);
        searchText = menu.findViewById(R.id.sortSearchText);
        searchClear = menu.findViewById(R.id.sortSearchClear);
        searchRangeName = menu.findViewById(R.id.sortSearchName);
        searchRangeSeries = menu.findViewById(R.id.sortSearchSeries);
        searchRangeSkillActive = menu.findViewById(R.id.sortSearchSkillActive);
        searchRangeSkillLeader = menu.findViewById(R.id.sortSearchSkillLeader);
        searchRangeDetail = menu.findViewById(R.id.sortSearchDetails);
        searchOnHp = menu.findViewById(R.id.sortSearchHpImprove);
        searchOnAttack = menu.findViewById(R.id.sortSearchAttackImprove);
        searchOnRecovery = menu.findViewById(R.id.sortSearchRecoveryImprove);

        searchClear.setOnClickListener((v) -> {
            searchText.setText("");
            clickSearch(v);
        });
        searchApply.setOnClickListener((v) -> {
            logSearch(searchText.getText().toString());
            search.setChecked(true);
            clickSearch(v);
        });
        searchOnHp.setOnClickListener(this::clickSearchHar);
        searchOnAttack.setOnClickListener(this::clickSearchHar);
        searchOnRecovery.setOnClickListener(this::clickSearchHar);

        View[] vs = {search, searchRegex, searchRangeName, searchRangeSeries, searchRangeSkillActive, searchRangeSkillLeader, searchRangeDetail};
        for (View v : vs) {
            v.setOnClickListener(this::clickSearch);
        }
    }
    // --------

    private void resetMenu() {
        ViewGroup[] vgs = {sortAttributes, sortRace, sortStar, sortRunestone, sortRaceStoneAttr, sortRaceStoneRace};
        for (ViewGroup vg : vgs) {
            setAllChildSelected(vg, false);
        }
        sortCommon.check(R.id.sortCommonNormId);
        sortFormulaCard.check(R.id.sortFormulaCardNo);
        sortFormulaList.check(R.id.sortFormulaNo);
        setCheckedIncludeNo(sortSpecialNo, R.id.sortSpecialNo, sortSpecial);
        setCheckedIncludeNo(sortImproveNo, R.id.sortImproveNo, sortImprove);
        search.setChecked(false);
    }

    // click listeners for sort menus --------
    private void clickReset(View v) {
        resetMenu();
        applySelection();
    }

    private void clickAttr(View v) {
        nonAllApply(v, sortAttributes);
    }

    private void clickRace(View v) {
        nonAllApply(v, sortRace);
    }

    private void clickRaceRuneStones(View v) {
        toggleSelected(v);
        applySelection();
    }

    private void clickFormulaCard(View v) {
        final int noId = R.id.sortFormulaCardNo;
        int id = v.getId();
        sortFormulaCard.check(id);
        if (id != noId) {
            sortCommon.check(R.id.sortCommonNormId);
        }
        //sortCommon.setEnabled(id != noId);

        applySelection();
    }

    private void clickFormulaList(View v) {
        final int noId = R.id.sortFormulaNo;
        int id = v.getId();
        sortFormulaList.check(id);
        if (id != noId) {
            sortCommon.check(R.id.sortCommonNormId);
        }
        //sortCommon.setEnabled(id != noId);

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
            sortFormulaCard.check(R.id.sortFormulaCardNo);
            sortFormulaList.check(R.id.sortFormulaNo);
        }
        //sortFormulaList.setEnabled(id != R.id.sortFormulaNo);

        applySelection();
    }

    private void clickHide(View v) {
        toggleSelectApply(v);
    }

    private void toggleSelectApply(View v) {
        toggleSelected(v);
        applySelection();
    }

    private void clickImprove(View v) {
        setCheckedIncludeNo(v, R.id.sortImproveNo, sortImprove);
        applySelection();
    }

    private void clickSearch(View v) {
        String s = searchText.getText().toString();
        new AppPref().setCardsSearchText(s);
        applySelection();
    }

    private void clickSearchHar(View v) {
        // Prepare text and set to search text
        List<String> sa = new ArrayList<>();
        if (searchOnHp.isChecked()) {
            sa.add("\n" + getString(R.string.regex_searchOnHp));
        }
        if (searchOnAttack.isChecked()) {
            sa.add("\n" + getString(R.string.regex_searchOnAttack));
        }
        if (searchOnRecovery.isChecked()) {
            sa.add("\n" + getString(R.string.regex_searchOnRecovery));
        }
        String s = RegexUtil.toRegexOr(sa);
        searchText.setText(s);
        search.setChecked(true);
        searchRegex.setChecked(true);
        searchRangeName.setChecked(false);
        searchRangeSeries.setChecked(false);
        searchRangeSkillActive.setChecked(false);
        searchRangeSkillLeader.setChecked(false);
        searchRangeDetail.setChecked(true);
        clickSearch(v);
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
        toggleAndClearIfAll(v, vg);

        applySelection();
    }

    private void updateFromPref() {
        AppPref p = new AppPref();
        if (searchText != null) {
            searchText.setText(p.getCardsSearchText());
        }
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
        private final String[] commonRace = App.res().getStringArray(R.array.cards_common_keys_race);

        private TosCondition select;

        public TosSelectCard(List<TosCard> source, TosCondition condition) {
            super(source);
            select = condition;
        }

        private boolean[] selectForShow = new boolean[4];
        private Pattern turnStoneRegex;

        // Find a good regex for 種族符石
        // Comma is for #1693 關師傅
        private Pattern raceStoneRegex;
        private Pattern raceStoneRegexAttr;
        private Pattern raceStoneRegexRace;

        private boolean showRegexFail = false;

        @Override
        public void onPrepare() {
            prepareShow();
            prepareTurnRunestones();
            prepareRaceStones();
        }

        private void prepareShow() {
            selectForShow = new boolean[4];
            ViewGroup vg = sortHide;
            int n = vg.getChildCount();
            for (int i = 0; i < n; i++) {
                int m = -1;
                View v = vg.getChildAt(i);
                switch (v.getId()) {
                    case R.id.sortHide6xxx: m = 0; break;
                    case R.id.sortHide7xxx: m = 1; break;
                    case R.id.sortHide8xxx: m = 2; break;
                    case R.id.sortHide9xxx: m = 3; break;
                }
                if (MathUtil.isInRange(m, 0, selectForShow.length)) {
                    selectForShow[m] = v.isSelected();
                }
            }
        }

        private void prepareTurnRunestones() {
            List<String> stones = new ArrayList<>();
            getSelectTags(sortRunestone, stones, false);
            int n = stones.size();
            if (n == 0) {
                turnStoneRegex = null;
                return;
            }

            // 轉化(|為)(光|光強化)(|神族|魔族|人族|獸族|龍族|妖族|機械族)符石
            List<String> allRace = getRaces();
            allRace.add(0, ""); // No specified race
            String r = getString(R.string.cards_turn_into2) + "(|為)"
                    + RegexUtil.toRegexOr(stones)
                    + RegexUtil.toRegexOr(allRace)
                    + getString(R.string.cards_runestone);
            turnStoneRegex = Pattern.compile(r);
            logE("(T) = %s", turnStoneRegex);
        }

        private void prepareRaceStones() {
            List<String> attr = new ArrayList<>();
            getSelectTags(sortRaceStoneAttr, attr, false);
            List<String> race = new ArrayList<>();
            getSelectTags(sortRaceStoneRace, race, false);
            if (attr.isEmpty() && race.isEmpty()) {
                raceStoneRegex = null;
                raceStoneRegexAttr = null;
                raceStoneRegexRace = null;
                return;
            }

            // (火|光)(人族|獸族)(|強化)符石
            String attrs = toRegex(attr);
            String races = toRegex(race);

            String regex = attrs + races + "(|強化)符石";
            raceStoneRegex = Pattern.compile(regex);

            String raceK = races.replace("族", "");
            raceStoneRegexRace = Pattern.compile(raceK);
            raceStoneRegexAttr = Pattern.compile(attrs);
            logE("(All, Attr, Race) = %s  %s   %s", raceStoneRegex, raceStoneRegexAttr, raceStoneRegexRace);
        }

        private List<String> getRaces() {
            List<String> race = new ArrayList<>();
            getTagsWhen((w) -> true, sortRaceStoneRace, race, false);
            return race;
        }

        @Override
        public String typeName() {
            return "TosCard";
        }

        @Override
        public boolean onSelect(TosCard c){
            return selectForBasic(c)
                    && selectForTurnRunestones(c)
                    && selectForRaceRunestones(c)
                    && selectForImprove(c)
                    && selectForSpecial(c)
                    && selectForShow(c)
                    && selectForSearch(c)
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
            if (turnStoneRegex == null) {
                return true;
            } else {
                String key = c.skillsDesc();
                return turnStoneRegex.matcher(key).find();
            }
        }

        private boolean selectForRaceRunestones(TosCard c) {
            if (raceStoneRegex == null) {
                return true;
            }

            String key = c.skillsDesc() + " & " + c.skillAwkName;
            if (sortRaceStoneAddLeader.isSelected()) {
                key += " & " + c.skillLeaderDesc;
            }
            if (sortRaceStoneAddDetail.isSelected()) {
                key += " & " + c.cardDetails;
            }

            boolean b = raceStoneRegex.matcher(key).find();

            // Find 自身種族符石, special
            b = selectRaceRuneStoneSpecial(b, key, c);
            return b;
        }

        private boolean selectRaceRuneStoneSpecial(boolean value, String key, TosCard c) {
            boolean ans = value;
            int idNorm = Integer.parseInt(c.idNorm);
            //boolean noSel = !sortRaceStoneAddLeader.isSelected() && !sortRaceStoneAddDetail.isSelected();
            switch (idNorm) {
                case 1624: // 絲堤
                    ans = raceStoneRegexRace.matcher("神妖精").find();
                    break;
                case 1661: case 1662: case 1663: case 1664: case 1665: // 修道仙妖
                    ans = raceStoneRegexRace.matcher("妖精").find();
                    break;
                //case 1982: // 唐三藏
                case 2013: // 愛迪生
                case 2021: // 妮可
                    ans = true;
                    break;
                case 2081: // 艾莉亞
                    boolean a = raceStoneRegexAttr.matcher("水火木").find();
                    boolean r = raceStoneRegexRace.matcher("人").find();
                    ans = a && r;
                    break;
            }
            return ans;
        }

        private boolean selectForSearch(TosCard c) {
            if (!search.isChecked()) return true;

            List<String> keys = new ArrayList<>();
            if (searchRangeName.isChecked()) {
                keys.add(c.name);
            }
            if (searchRangeSeries.isChecked()) {
                keys.add(c.series);
            }
            if (searchRangeSkillActive.isChecked()) {
                keys.add(c.skillName1);
                keys.add(c.skillDesc1);
                keys.add(c.skillName2);
                keys.add(c.skillDesc2);
                keys.add(c.skillAwkName);
                if (c.skillChange.size() > 0) {
                    for (SkillLite s : c.skillChange) {
                        keys.add(s.name);
                        keys.add(s.effect);
                    }
                }
            }
            if (searchRangeSkillLeader.isChecked()) {
                keys.add(c.skillLeaderName);
                keys.add(c.skillLeaderDesc);
            }
            if (searchRangeDetail.isChecked()) {
                keys.add(c.cardDetails);
            }
            String key = RegexUtil.join("", "", "   ", keys);

            // Use text or regex to apply search
            String target = searchText.getText().toString();
            target = target.replaceAll("[ \n]", "");
            boolean res;
            if (searchRegex.isChecked()) {
                try {
                    Pattern p = Pattern.compile(target);
                    res = p.matcher(key).find();
                } catch (PatternSyntaxException e) {
                    res = key.contains(target);
                    e.printStackTrace();
                    if (!showRegexFail) {
                        showRegexFail = true;
                        ThreadUtil.runOnUiThread(() -> {
                            App.showToast(e.getMessage());
                        });
                    }
                }
            } else {
                res = key.contains(target);
            }
            return res;
        }

        private String toRegex(List<String> keys) {
            return RegexUtil.toRegexOr(keys);
        }

        private boolean selectForShow(TosCard c) {
            boolean accept = true;
            if (selectForShow[0]) {
                //noinspection ConstantConditions
                accept &= !TosCardUtil.isSkin(c);
            }
            if (selectForShow[1]) {
                accept &= !TosCardUtil.isTauFa(c);
            }
            if (selectForShow[2]) {
                accept &= !(TosCardUtil.isDisney(c) || TosCardUtil.isTunestone(c));
            }
            if (selectForShow[3]) {
                accept &= !TosCardUtil.is72Demon(c);
            }
            return accept;
        }

        private boolean selectForSpecial(TosCard c) {
            String key = activeSkill(c);
            boolean accept = true;
            if (!sortSpecialNo.isChecked()) {
                boolean element = c.race.matches(getString(R.string.cards_race_level) + "|" + getString(R.string.cards_race_evolve));
                //Though repeat, but fast.....
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
                if (sortSpecialRestoreDropRateTransfer.isChecked()) {
                    accept &= find(key, R.array.cards_runestone_drop_rate_transfer_keys);
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
                if (sortSpecialRegardlessInitialShield.isChecked()) {
                    accept &= find(key, R.array.cards_regardless_of_initial_shield_keys);
                }
                if (sortSpecialClearAllEffect.isChecked()) {
                    accept &= find(key, R.array.cards_clear_all_effect_keys);
                }
                if (sortSpecialStayUntil.isChecked()) {
                    accept &= find(key, R.array.cards_stay_until_keys);
                }
                if (sortSpecialStayUntilIf.isChecked()) {
                    accept &= find(key, R.array.cards_stay_until_if_keys);
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
                if (sortSpecialFix.isChecked()) {
                    accept &= find(key, R.array.cards_fix_keys);
                }
                if (sortSpecialRestoreIntoEnchanted.isChecked()) {
                    accept &= find(key, R.array.cards_runestone_into_enchanted_keys);
                }
                if (sortSpecialRestoreAllAttrRandom.isChecked()) {
                    accept &= find(key, R.array.cards_runestone_all_attr_random_keys);
                }
                if (sortSpecialRestoreAllIntoRandom.isChecked()) {
                    accept &= find(key, R.array.cards_runestone_all_into_random_keys);
                }
                if (sortSpecialRestoreAllInto.isChecked()) {
                    accept &= find(key, R.array.cards_runestone_all_into_keys);
                }
                if (sortSpecialRestoreAllIntoEnchanted.isChecked()) {
                    accept &= find(key, R.array.cards_runestone_all_into_enchanted_keys);
                }
                if (sortSpecialDodge.isChecked()) {
                    accept &= find(key, R.array.cards_dodge_keys);
                }
                if (sortSpecialOneDealDamage.isChecked()) {
                    accept &= !element & find(key, R.array.cards_one_deal_damage_keys);
                }
                if (sortSpecialOneDealDamageElement.isChecked()) {
                    accept &= element && find(key, R.array.cards_one_deal_damage_keys);
                }
                if (sortSpecialAllDealDamage.isChecked()) {
                    accept &= !element & find(key, R.array.cards_all_deal_damage_keys);
                }
                if (sortSpecialAllDealDamageElement.isChecked()) {
                    accept &= element && find(key, R.array.cards_all_deal_damage_keys);
                }
                if (sortSpecialTurnEnemyAttr.isChecked()) {
                    accept &= find(key, R.array.cards_turn_enemy_attr_keys);
                }
                if (sortSpecialDelay.isChecked()) {
                    accept &= find(key, R.array.cards_delay_keys);
                }
                if (sortSpecialClearLock.isChecked()) {
                    accept &= find(key, R.array.cards_clear_lock_keys);
                }
                if (sortSpecialAlsoHeartActive.isChecked()) {
                    key += " & " + c.skillLeaderDesc;
                    accept &= findRegex(key, R.array.cards_also_heart_keys);
                }
                if (sortSpecialAttackBonusCombo.isChecked()) {
                    accept &= find(key, R.array.cards_attack_bonus_combo_keys);
                }
                if (sortSpecialAddComboCount.isChecked()) {
                    accept &= find(key, R.array.cards_add_combo_count_keys);
                }
                if (sortSpecialSkillCdMinus.isChecked()) {
                    accept &= find(key, R.array.cards_skill_cd_minus_keys);
                }
            }
            return accept;
        }

        private String leaderSkill(TosCard c) {
            return joinKey(c.skillLeaderDesc, c);
        }

        private String activeSkill(TosCard c) {
            return joinKey(c.skillsDesc(), c);
        }

        private String joinKey(String key, TosCard c) {
            // Skill changes
            StringBuilder skills = new StringBuilder("");
            for (int i = 0; i < c.skillChange.size(); i++) {
                SkillLite sl = c.skillChange.get(i);
                if (i > 0) {
                    skills.append(" & ");
                }
                skills.append(sl.effect);
            }
            key += " & " + skills.toString();
            key += " & " + c.skillAwkName;

            if (sortSpecialAddDetail.isSelected()) {
                key += " & " + c.cardDetails;
            }
            return key;
        }

        private boolean find(String key, @ArrayRes int dataId) {
            String[] data = App.res().getStringArray(dataId);
            return find(key, data);
        }

        private boolean findRegex(String key, @ArrayRes int dataId) {
            List<String> data = ListUtil.nonNull(App.res().getStringArray(dataId));
            return findRegex(key, data);
        }

        private boolean findRegex(String key, List<String> data) {
            String reg = RegexUtil.toRegexOr(data);
            return Pattern.compile(reg).matcher(key).find();
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
                    accept &= c.skillPowBattle.size() > 0;
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
                if (sortImproveSwt.isChecked()) {
                    accept &= c.skillsDesc().contains("變身");
                }
                if (sortImproveDmx.isChecked()) {
                    accept &= c.maxAddAll();
                }
            }
            return accept;
        }

        @NonNull
        @Override
        public List<Integer> sort(@NonNull List<Integer> result) {
            Comparator<Integer> cmp;
            if (isSortFormula()) {
                cmp = getFormulaComparator();
            } else {
                cmp = getCommonComparator();
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
            if (isSortFormula()) {
                msgs = getFormulaMessages(result);
            } else {
                msgs = getCommonMessages(result);
            }
            return msgs;
        }

        private boolean isSortFormula() {
            int id = sortFormulaList.getCheckedRadioButtonId();
            return id > 0 && id != R.id.sortFormulaNo;
        }

        private double getAttackByCassandra(TosCard c) {
            return c.maxAttackAme + c.maxRecoveryAme * 3.5;
        }

        private double getAttackByCassandraRatio(TosCard c) {
            return getAttackByCassandra(c) / c.maxAttackAme;
        }

        private double getAttackByJade(TosCard c) {
            if (c.race.contains("妖")) {
                return c.maxAttackAme + c.maxRecoveryAme * 3;
            }
            return c.maxAttackAme;
        }

        private double getAttackByJadeRatio(TosCard c) {
            return getAttackByJade(c) / c.maxAttackAme;
        }

        private double getFormulaAttack(TosCard c) {
            int cardId = sortFormulaCard.getCheckedRadioButtonId();
            double atk = c.maxAttackAme;
            if (cardId == R.id.sortFormulaCardCassandra) {
                atk = getAttackByCassandra(c);
            } else if (cardId == R.id.sortFormulaCardJade) {
                atk = getAttackByJade(c);
            }
            return atk;
        }

        private double getFormulaAttackRatio(TosCard c) {
            int cardId = sortFormulaCard.getCheckedRadioButtonId();
            double atk = 1;
            if (cardId == R.id.sortFormulaCardCassandra) {
                atk = getAttackByCassandraRatio(c);
            } else if (cardId == R.id.sortFormulaCardJade) {
                atk = getAttackByJadeRatio(c);
            }
            return atk;
        }

        private Comparator<Integer> getFormulaComparator() {
            // Create comparator
            int id = sortFormulaList.getCheckedRadioButtonId();
            if (id == R.id.sortFormulaAttack) {
                return (o1, o2) -> {
                    boolean dsc = true;
                    // Evaluate formula
                    TosCard c1 = data.get(o1);
                    TosCard c2 = data.get(o2);
                    double atk1 = getFormulaAttack(c1);
                    double atk2 = getFormulaAttack(c2);

                    //logCard("#1", c1);
                    //logCard("#2", c2);
                    // Compare them
                    if (dsc) {
                        return Double.compare(atk2, atk1);
                    } else {
                        return Double.compare(atk1, atk2);
                    }
                };
            } else if (id == R.id.sortFormulaAttackRatio) {
                return (o1, o2) -> {
                    boolean dsc = true;
                    // Evaluate formula
                    TosCard c1 = data.get(o1);
                    TosCard c2 = data.get(o2);
                    double atk1 = getFormulaAttackRatio(c1);
                    double atk2 = getFormulaAttackRatio(c2);

                    // Compare them
                    if (dsc) {
                        return Double.compare(atk2, atk1);
                    } else {
                        return Double.compare(atk1, atk2);
                    }
                };
            } else {
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
                        v1 = c1.maxHPAme;
                        v2 = c2.maxHPAme;
                        break;
                    case R.id.sortCommonMaxAttack:
                        v1 = c1.maxAttackAme;
                        v2 = c2.maxAttackAme;
                        break;
                    case R.id.sortCommonMaxRecovery:
                        v1 = c1.maxRecoveryAme;
                        v2 = c2.maxRecoveryAme;
                        break;
                    case R.id.sortCommonMaxSum:
                        v1 = c1.maxHPAme + c1.maxAttackAme + c1.maxRecoveryAme;
                        v2 = c2.maxHPAme + c2.maxAttackAme + c2.maxRecoveryAme;
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
                    case R.id.sortCommonMaxTu:
                        v1 = c1.maxTUAllLevel;
                        v2 = c2.maxTUAllLevel;
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
            int c1 = c.skillCDMaxAme; // consider Amelioration, not max1
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

        private List<String> getFormulaMessages(List<Integer> result) {
            List<String> message = null;
            TosCard c;
            // Create Message
            int id = sortFormulaList.getCheckedRadioButtonId();
            switch (id) {
                case R.id.sortFormulaAttack:
                    message = new ArrayList<>();
                    for (int i = 0; i < result.size(); i++) {
                        c = data.get(result.get(i));
                        double atk = getFormulaAttack(c);
                        message.add(_fmt("%.1f", atk));
                    }
                    break;
                case R.id.sortFormulaAttackRatio:
                    message = new ArrayList<>();
                    for (int i = 0; i < result.size(); i++) {
                        c = data.get(result.get(i));
                        double atk = getFormulaAttackRatio(c);
                        message.add(_fmt("%.2f", atk));
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
                        msg = String.valueOf(c.maxHp());
                        if (c.ameAddHP()) {
                            msg += "^";
                        }
                        if (c.maxAddHp()) {
                            msg += "#";
                        }
                        break;
                    case R.id.sortCommonMaxAttack:
                        msg = String.valueOf(c.maxAttack());
                        if (c.ameAddAttack()) {
                            msg += "^";
                        }
                        if (c.maxAddAttack()) {
                            msg += "#";
                        }
                        break;
                    case R.id.sortCommonMaxRecovery:
                        msg = String.valueOf(c.maxRecovery());
                        if (c.ameAddRecovery()) {
                            msg += "^";
                        }
                        if (c.maxAddRecovery()) {
                            msg += "#";
                        }
                        break;
                    case R.id.sortCommonMaxSum:
                        msg = String.valueOf(c.maxHAR());
                        if (c.ameAddAll()) {
                            msg += "^";
                        }
                        if (c.maxAddAll()) {
                            msg += "#";
                        }
                        break;
                    case R.id.sortCommonSkillCDMax:
                        msg = "" + c.skillCDMaxAme;
                        if (c.ameMinusCD()) {
                            msg += "^";
                        }
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
                    case R.id.sortCommonMaxTu:
                        msg = String.valueOf(c.maxTUAllLevel);
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
                return GsonUtil.loadFile(f, CardSort.class);
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(CardSort data) {
            if (getActivity() == null) return;

            cardSort = data != null ? data : new CardSort();
            updateHide();
            updateFromPref();
            applySelection();
        }
    }

    //-- Events
    private void logShare(String type) {
        Map<String, String> m = new HashMap<>();
        m.put("share", type);
        FabricAnswers.logCardFragment(m);
    }

    private void logSearch(String s) {
        Map<String, String> m = new HashMap<>();
        m.put("search", s);
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
