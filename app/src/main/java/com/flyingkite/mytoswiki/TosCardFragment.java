package com.flyingkite.mytoswiki;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.flyingkite.library.util.GsonUtil;
import com.flyingkite.library.util.ListUtil;
import com.flyingkite.library.util.MathUtil;
import com.flyingkite.mytoswiki.data.CardSort;
import com.flyingkite.mytoswiki.data.tos.TosCard;
import com.flyingkite.mytoswiki.dialog.CardDialog;
import com.flyingkite.mytoswiki.library.CardAdapter;
import com.flyingkite.mytoswiki.library.CardLibrary;
import com.flyingkite.mytoswiki.share.ShareHelper;
import com.flyingkite.mytoswiki.tos.TosWiki;
import com.flyingkite.mytoswiki.tos.query.TosCardCondition;
import com.flyingkite.mytoswiki.tos.query.TosSelectAttribute;
import com.flyingkite.util.TaskMonitor;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class TosCardFragment extends BaseFragment {
    public static final String TAG = "TosCardFragment";
    // Top Status bar
    private TextView tosInfo;
    // Main library
    private RecyclerView cardsRecycler;
    private CardLibrary cardLib;
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
    private CheckBox sortSpecialNo;
    private CheckBox sortSpecialFree;
    private CheckBox sortSpecialKeep;
    private CheckBox sortSpecialExplode;
    // 提升能力
    private ViewGroup sortImprove;
    private CheckBox sortImproveNo;
    private CheckBox sortImproveAme;
    private CheckBox sortImproveAwk;
    private CheckBox sortImprovePow;
    private CheckBox sortImproveVir;
    private CheckBox sortImproveTwo;
    private CheckBox sortImproveChg; // Skill change
    // Hide cards
    private ViewGroup sortHide;
    // Display card name
    private RadioGroup sortDisplay;


    private CardSort cardSort = new CardSort();

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
        cardLib = new CardLibrary(cardsRecycler);
        sortMenu = findViewById(R.id.tosSortMenu);
        initSortMenu();
        initToolIcons();

        new LoadDataAsyncTask().executeOnExecutor(sSingle);

        TosWiki.attendDatabaseTasks(onCardsReady);
    }

    private void initToolIcons() {
        findViewById(R.id.tosGoTop).setOnClickListener((v) -> {
            int index = 0;
            cardLib.recyclerView.scrollToPosition(index);
        });

        findViewById(R.id.tosGoBottom).setOnClickListener((v) -> {
            int index = cardLib.adapter.getItemCount() - 1;
            cardLib.recyclerView.scrollToPosition(index);
        });

        View tool = findViewById(R.id.tosTooBar);
        tool.setOnClickListener((v) -> {
            v.setSelected(!v.isSelected());
            if (toolOwner != null) {
                toolOwner.setToolsVisible(v.isSelected());
            }
        });
        boolean sel = false;
        if (toolOwner != null) {
            sel = toolOwner.isToolsVisible();
        }
        tool.setSelected(sel);
    }

    private TaskMonitor.OnTaskState onCardsReady = new TaskMonitor.OnTaskState() {
        @Override
        public void onTaskDone(int index, String tag) {
            runOnUiThread(() -> {
                if (TosWiki.TAG_ALL_CARDS.equals(tag)) {
                    onCardsReady(TosWiki.allCards());
                }
                log("#%s (%s) is done", index, tag);
            });
        }

        @Override
        public void onAllTaskDone() {
            log("All task OK");
        }
    };

    @Override
    public void log(String message) {
        //Log.v(LTag(), message);
    }

    private void onCardsReady(TosCard[] cards) {
        allCards = Arrays.asList(cards);
        int n = allCards.size();
        tosInfo.setText(getString(R.string.cards_selection, n, n));
        cardLib.setDataSet(allCards
                , (position, card) -> {
                    CardDialog d = new CardDialog();

                    Bundle b = new Bundle();
                    b.putParcelable(CardDialog.BUNDLE_CARD, card);
                    d.setArguments(b);
                    d.show(getFragmentManager(), CardDialog.TAG);

                }, (selected, total) ->  {
                    tosInfo.setText(getString(R.string.cards_selection, selected, total));
                }
        );
        updateHide();
        applySelection();
        test();
    }

    private void initShareImage(View parent) {
        parent.findViewById(R.id.tosSave).setOnClickListener((v) -> {
            View view = cardsRecycler;
            //File folder = Environment.getExternalStoragePublicDirectory()
            String name = ShareHelper.cacheName("1.png");
            LogE("Save to %s", name);

            ShareHelper.shareImage(getActivity(), view, name);
        });
    }

    private void test() {
        if (true) return;

        int cnt = 0;
        for (TosCard c : allCards) {
            String desc = c.skillDesc1 + " & " + c.skillDesc2;// + " & " + c.skillLeaderDesc;

            String[] names = {"可任意移動符石而不會發動消除", "任意移動符石", "不會發動消除"};

            boolean found = false;
            for (int i = 0; i < names.length && !found; i++) {
                if (desc.contains(names[i])) {
                    LogE("#%s -> @%d -> %s / %s", c.idNorm, i, c.name, desc);
                    found = true;
                    cnt++;
                }
            }
        }
        LogE("%s cards", cnt);
    }

    private void initSortMenu() {
        // Create MenuWindow
        View menu = LayoutInflater.from(getActivity()).inflate(R.layout.popup_tos_sort, (ViewGroup) getView(), false);
        int wrap = ViewGroup.LayoutParams.WRAP_CONTENT;
        sortWindow = new PopupWindow(menu, wrap, wrap, true);
        sortWindow.setOutsideTouchable(true);
        sortWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        sortMenu.setOnClickListener(v -> {
            sortWindow.showAsDropDown(v);
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
        sortSpecialNo = menu.findViewById(R.id.sortSpecialNo);
        sortSpecialFree = menu.findViewById(R.id.sortSpecialFreeMove);
        sortSpecialKeep = menu.findViewById(R.id.sortSpecialKeep);
        sortSpecialExplode = menu.findViewById(R.id.sortSpecialExplode);

        sortSpecial = initSortOf(menu, R.id.sortSpecialList, this::clickSpecial);
    }

    private void initSortByHide(View menu) {
        sortHide = initSortOf(menu, R.id.sortHide, this::clickHide);
    }

    private <T extends ViewGroup> T initSortOf(View menu, @IdRes int id, View.OnClickListener childClick) {
        T vg = menu.findViewById(id);
        setChildClick(vg, childClick);
        return vg;
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

        sortImprove = initSortOf(menu, R.id.sortImprove, this::clickImprove);
    }

    //------

    private void clickReset(View v) {
        ViewGroup[] vgs = {sortAttributes, sortRace, sortStar, sortRunestone};
        for (ViewGroup vg : vgs) {
            setAllChildrenSelected(vg, false);
        }
        sortCommon.check(R.id.sortCommonNo);
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
            sortCommon.check(R.id.sortCommonNo);
        }
        sortCommon.setEnabled(id != R.id.sortCassandraNo);

        applySelection();
    }

    private void clickTurnRuneStones(View v) {
        clickHide(v);
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

        int type = CardAdapter.NT_ID_NORM;
        switch (v.getId()) {
            case R.id.sortDisplayName:
                type = CardAdapter.NT_NAME;
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
        if (id != R.id.sortCommonNo) {
            sortCassandra.check(R.id.sortCassandraNo);
        }
        sortCassandra.setEnabled(id != R.id.sortCassandraNo);

        applySelection();
    }

    private void clickHide(View v) {
        v.setSelected(!v.isSelected());
        applySelection();
    }

    private void clickImprove(View v) {
        setCheckedIncludeNo(v, R.id.sortImproveNo, sortImprove);
        applySelection();
    }

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

        LogE("---------");
        LogE("sel T = %s", attrs);
        LogE("sel R = %s", races);
        LogE("sel S = %s", stars);

        if (cardLib.adapter != null) {
            TosCardCondition cond = new TosCardCondition().attr(attrs).race(races).star(stars);
            cardLib.adapter.setSelection(new TosSelect(allCards, cond));
        }
    }

    private void getSelectTags(ViewGroup vg, List<String> result, boolean addAllIfEmpty) {
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
        if (addAllIfEmpty && !added) {
            result.addAll(all);
        }
    }

    private void nonAllApply(View v, ViewGroup vg) {
        toggleSelection(v, vg);

        applySelection();
    }

    //-- View's helpers --
    private void setCheckedIncludeNo(View clicked, @IdRes int noId, ViewGroup parent) {
        View noView = null;
        // Find the noView
        int n = parent.getChildCount();
        for (int i = 0; i < n && noView == null; i++) {
            View v = parent.getChildAt(i);
            if (v.getId() == noId) {
                noView = v;
            }
        }

        int vid = clicked.getId();
        Checkable c;
        if (vid != noId) { // If select something, uncheck no
            setViewCheck(false, noView);
        } else {
            // selected no, uncheck all others except no
            for (int i = 0; i < n; i++) {
                setViewCheck(false, parent.getChildAt(i));
            }
            setViewCheck(true, noView);
        }
    }

    private void setViewCheck(boolean check, View v) {
        Checkable c;
        if (v instanceof Checkable) {
            c = (Checkable) v;
            c.setChecked(check);
        }
    }

    private void setChildClick(ViewGroup vg, View.OnClickListener clk) {
        int n = vg.getChildCount();
        for (int i = 0; i < n; i++) {
            View w = vg.getChildAt(i);
            w.setOnClickListener(clk);
        }
    }

    private void toggleSelection(View v, ViewGroup vg) {
        v.setSelected(!v.isSelected());
        // Deselect all if all selected
        if (isAllAsSelected(vg, true)) {
            setAllChildrenSelected(vg, false);
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
    //-- View helpers --

    private void updateHide() {
        sortHide.findViewById(R.id.sortHide6xxx).setSelected(cardSort.hideCard6xxx);
        sortHide.findViewById(R.id.sortHide8xxx).setSelected(cardSort.hideCard8xxx);
        sortHide.findViewById(R.id.sortHide9xxx).setSelected(cardSort.hideCard9xxx);
        int id = cardSort.displayByName ? R.id.sortDisplayName : R.id.sortDisplayNormId;
        clickDisplay(sortDisplay.findViewById(id));
    }

    private void toGsonHide() {
        cardSort.hideCard6xxx = sortHide.findViewById(R.id.sortHide6xxx).isSelected();
        cardSort.hideCard8xxx = sortHide.findViewById(R.id.sortHide8xxx).isSelected();
        cardSort.hideCard9xxx = sortHide.findViewById(R.id.sortHide9xxx).isSelected();
        cardSort.displayByName = sortDisplay.getCheckedRadioButtonId() == R.id.sortDisplayName;
        sSingle.submit(() -> {
            GsonUtil.writeFile(getTosCardSortFile(), new Gson().toJson(cardSort));
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        toGsonHide();
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

    private class TosSelect extends TosSelectAttribute {
        private final String[] commonRace = getResources().getStringArray(R.array.cards_common_keys);

        public TosSelect(List<TosCard> source, TosCardCondition condition) {
            super(source, condition);
        }

        @Override
        public boolean onSelect(TosCard c){
            List<String> attrs = select.getAttr();
            List<String> races = select.getRace();
            List<String> stars = select.getStar();

            return attrs.contains(c.attribute)
                    && races.contains(c.race)
                    && stars.contains("" + c.rarity)
                    && selectForTurnRunestones(c)
                    && selectForSpecial(c)
                    && selectForShow(c)
                    && selectForImprove(c)
            ;
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
            String key = c.skillDesc1 + " & " + c.skillDesc2;
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
            }
            return accept;
        }

        private boolean find(String key, @ArrayRes int dataId) {
            String[] data = getResources().getStringArray(dataId);
            return find(key, data);
        }

        private boolean find(String key, String[] data) {
            for (int i = 0; i < data.length; i++) {
                if (key.contains(data[i])) {
                    return true;
                }
            }
            return false;
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
            List<String> messages;
            messages = getCommonMessages(result);
            if (messages == null) {
                messages = getCassandraMessages(result);
            }
            return messages;
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
            if (id == RadioGroup.NO_ID || id == R.id.sortCommonNo) {
                return null;
            }
            return (o1, o2) -> {
                boolean dsc = true;
                TosCard c1 = data.get(o1);
                TosCard c2 = data.get(o2);
                long v1 = -1, v2 = -1;
                //logCard("#1", c1);
                //logCard("#2", c2);

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
}
