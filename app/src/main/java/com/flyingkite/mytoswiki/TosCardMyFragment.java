package com.flyingkite.mytoswiki;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.flyingkite.library.util.ListUtil;
import com.flyingkite.library.util.MathUtil;
import com.flyingkite.library.util.ThreadUtil;
import com.flyingkite.library.widget.Library;
import com.flyingkite.mytoswiki.data.pack.PackCard;
import com.flyingkite.mytoswiki.data.tos.TosCard;
import com.flyingkite.mytoswiki.dialog.CommonDialog;
import com.flyingkite.mytoswiki.dialog.UsePackDialog;
import com.flyingkite.mytoswiki.library.CardAdapter;
import com.flyingkite.mytoswiki.library.CardPackAdapter;
import com.flyingkite.mytoswiki.library.Misc;
import com.flyingkite.mytoswiki.share.ShareHelper;
import com.flyingkite.mytoswiki.tos.TosWiki;
import com.flyingkite.mytoswiki.tos.query.AllCards;
import com.flyingkite.mytoswiki.tos.query.TosCondition;
import com.flyingkite.mytoswiki.util.JsoupUtil;
import com.flyingkite.mytoswiki.util.RegexUtil;
import com.flyingkite.mytoswiki.util.TosCardUtil;
import com.flyingkite.mytoswiki.util.TosPageUtil;
import com.flyingkite.util.TaskMonitor;
import com.flyingkite.util.WaitingDialog;

import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import androidx.annotation.ArrayRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import flyingkite.tool.StringUtil;

public class TosCardMyFragment extends BaseFragment implements TosPageUtil, JsoupUtil, TosCardUtil {
    public static final String TAG = "TosCardMyFragment";
    public static final String TOS_REVIEW = "https://review.towerofsaviors.com/";

    private RecyclerView cardsRecycler;
    private TextView tosInfo;
    private TextView uidText;
    private View uidLoad;
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
    // Hide cards
    private ViewGroup sortHide;
    // Display card name
    private RadioGroup sortDisplay;

    //private Library<CardAdapter> cardLib;
    private Library<CardPackAdapter> cardLib;
    private List<TosCard> myBag = new ArrayList<>();
    private List<PackCard> myPack = new ArrayList<>();
    private boolean showHint;
    private WaitingDialog waiting;

    @Override
    protected int getPageLayoutId() {
        return R.layout.fragment_tos_card_my;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cardsRecycler = findViewById(R.id.tosRecycler);
        tosInfo = findViewById(R.id.tosInfo);
        uidText = findViewById(R.id.tosMyUid);
        uidLoad = findViewById(R.id.tosLoad);
        menuEntry = findViewById(R.id.tosSortMenu);
        initSortMenu();
        initToolIcons();
        tosInfo.setOnClickListener((v) -> {
            AppPref p = new AppPref();
            p.setUserTosInventory("");
            p.setUserUid("");
            showToast("Cleared");
        });

        cardLib = new Library<>(cardsRecycler, 5);
        TosWiki.attendDatabaseTasks(onCardsReady);
    }

    // init sort menus and put onClickListeners --------
    private void initSortMenu() {
        // Create MenuWindow
        Pair<View, PopupWindow> pair = createPopupWindow(R.layout.popup_tos_sort_card_pack, (ViewGroup) getView());
        sortWindow = pair.second;
        View menu = pair.first;

        menuEntry.setOnClickListener(v -> {
            sortWindow.showAsDropDown(v);
            //logSelectCard();
        });

        initShareImage(menu);
        initSortReset(menu);
        initSortByAttribute(menu);
        initSortByRace(menu);
        initSortByStar(menu);
        initSortByCommon(menu);
        initSortByHide(menu);
        initDisplay(menu);
        initSortByImprove(menu);
    }

    private void initToolIcons() {
        AppPref p = new AppPref();
        uidText.setText(p.getUserUid());

        initScrollTools(R.id.tosGoTop, R.id.tosGoBottom, cardsRecycler);
        uidLoad.setOnClickListener((v) -> {
            showKeyBoard(false, v);
            ThreadUtil.runOnWorkerThread(() -> {
                showHint = true;
                String uid = uidText.getText().toString();
                if (TextUtils.isEmpty(uid)) {
                    new CommonDialog().message(getString(R.string.enterUid)).show(getActivity());
                    return;
                }

                if (!App.isNetworkConnected()) {
                    new CommonDialog().message(getString(R.string.noNetwork)).show(getActivity());
                    return;
                }
                loadPack();
            });
        });
    }

    //----

    private void initShareImage(View parent) {
        parent.findViewById(R.id.tosSave).setOnClickListener((v) -> {
            View view = cardsRecycler;
            //File folder = Environment.getExternalStoragePublicDirectory()
            String name = ShareHelper.cacheName("1.png");

            ShareHelper.shareImage(getActivity(), view, name);
            //logShare("library"); // TODO
        });
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

    private void initSortByStar(View menu) {
        sortStar = initSortOf(menu, R.id.sortStar, this::clickStar);
    }

    private void initSortByCommon(View menu) {
        sortCommon = initSortOf(menu, R.id.sortCommonList, this::clickCommon);
    }

    private void initSortByHide(View menu) {
        sortHide = initSortOf(menu, R.id.sortHide, this::clickHide);
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

        sortImprove = initSortOf(menu, R.id.sortImprove, this::clickImprove);
    }


    private <T extends ViewGroup> T initSortOf(View menu, @IdRes int id, View.OnClickListener childClick) {
        return setTargetChildClick(menu, id, childClick);
    }
    // --------

    private void resetMenu() {
        ViewGroup[] vgs = {sortAttributes, sortRace, sortStar};
        for (ViewGroup vg : vgs) {
            setAllChildSelected(vg, false);
        }
        sortCommon.check(R.id.sortCommonNormId);
        setCheckedIncludeNo(sortImproveNo, R.id.sortImproveNo, sortImprove);
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

    private void clickStar(View v) {
        nonAllApply(v, sortStar);
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
            cardLib.adapter.setSelection(new TosSelectCard(myPack, cond));
        }
    }

    private void nonAllApply(View v, ViewGroup vg) {
        toggleAndClearIfAll(v, vg);

        applySelection();
    }
    //----

    @Override
    public void onPause() {
        super.onPause();

        if (uidText != null) {
            String uid = uidText.getText().toString();
            // Save to preference
            AppPref p = new AppPref();
            p.setUserUid(uid);
        }
    }

    private void showUsage() {
        if (showHint) {
            new UsePackDialog().show(getActivity());
        }
    }

    private void loadPack() {
        loadMyPack(uidText.getText().toString());
    }

    private void loadMyPack(String uid) {
        AppPref p = new AppPref();

        showCardsLoading();
        Document doc = getDocument(TOS_REVIEW + uid);
        String data = doc.toString();
        logE("data = %s", data);
        String inventory = find(data, 0, "inventory_str : '", "'.split(\",\")");
        // Load last time if not found
        if (TextUtils.isEmpty(inventory)) {
            inventory = p.getUserTosInventory();
        }

        // User did not provide
        if (TextUtils.isEmpty(inventory)) {
            hideCardsLoading();
            showUsage();
            return;
        }

        // Save data to preference
        p.setUserUid(uid);
        p.setUserTosInventory(inventory);

        inventory(inventory);


        getActivity().runOnUiThread(() -> {
            hideCardsLoading();
            CardAdapter a = new CardAdapter(); // TODO
            a.setDataList(myBag);
            CardPackAdapter b = new CardPackAdapter();
            b.setDataList(myPack);
            b.setItemListener(new CardPackAdapter.ItemListener() {
                @Override
                public void onFiltered(int selected, int total) {
                    tosInfo.setText(App.res().getString(R.string.cards_selection, selected, total));
                }

                @Override
                public void onClick(PackCard item, CardPackAdapter.PCardVH holder, int position) {
                    logE("item = %s", item.source);
                    showCardDialog(TosWiki.getCardByIdNorm(item.idNorm));
                }
            });
            cardLib.setViewAdapter(b);

            int n = myBag.size();
            n = myPack.size();
            tosInfo.setText(App.res().getString(R.string.cards_selection, n, n));

            if (showHint) {
                showToast(R.string.cards_read, n);
            }
        });
    }

    private void inventory(String inventory) {
        logE("inventory_str  = \n%s\n", inventory);
        String[] invs = inventory.split(",");
        //print(invs);
        myBag.clear();
        myPack.clear();
        for (int i = 0; i < invs.length; i++) {
            myPack.add(parseCard(invs[i]));
            String[] a = invs[i].split("[|]");
            myBag.add(TosWiki.getCardByIdNorm(idNorm(a[1])));
            int x = Integer.parseInt(a[7]);
            if (x != 0) {
                logE("QWE x = %s", x);
            }
        }
        logE("\n\n");
        //statistics("inventory", invs);
    }

    private <T> void print(T[] d) {
        if (d == null) return;

        logE("%s items", d.length);
        for (int i = 0; i < d.length; i++) {
            logE("#%04d : %s", i, d[i]);
        }
    }

    private void statistics(String prefix, String[] data) {
        // n records
        int n = data.length;
        if (n == 0) return;

        // m columns
        int m = data[0].split("[|]").length;
        List<Set<Integer>> sets = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            sets.add(new TreeSet<>());
        }

        for (int i = 0; i < n; i++) {
            String[] di = data[i].split("[|]");
            for (int j = 0; j < m; j++) {
                int xij = Integer.parseInt(di[j]);
                Set<Integer> sj = sets.get(j);
                sj.add(xij);
                if (j == 2 && xij == 10) {
                    logE("data = %s", data[i]);
                }
            }

            String idNorm = idNorm(di[1]);
            int slv = Integer.parseInt(di[4]);
//            TosCard c = allCards.get(idNorm);
//            // can train
//            boolean sk = MathUtil.isInRange(1, slv, c.skillCDMax1);
//            if (c.sameSkills.size() > 2 && sk) {
//                if (c.rarity >= 5) {
//                    logE("Skills = %s\n  %s\n", data[i], sc(c));
//                }
//            }
        }

        logE("For %s", prefix);
        for (int i = 0; i < m; i++) {
            Set<Integer> s = sets.get(i);
            logE("#%s has %s items = %s", i, s.size(), s);
        }
        //---
    }

    private void showCardsLoading() {
        if (isActivityGone() || !showHint) return;

        getActivity().runOnUiThread(() -> {
            waiting = new WaitingDialog.Builder(getActivity()).message(getString(R.string.cardsLoading)).buildAndShow();
        });
    }

    private void hideCardsLoading() {
        if (isActivityGone() || !showHint) return;

        getActivity().runOnUiThread(() -> {
            if (waiting != null) {
                waiting.dismiss();
                waiting = null;
            }
        });
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

            loadPack();
        }
    };


    // Actual implementation of TosSelectCard --------
    private class TosSelectCard extends AllCards<PackCard> {
        private final String[] commonRace = App.res().getStringArray(R.array.cards_common_keys_race);

        private TosCondition select;

        public TosSelectCard(List<PackCard> source, TosCondition condition) {
            super(source);
            select = condition;
        }

        private boolean[] selectForShow = new boolean[4];
        private Pattern farmSeries;
        private Pattern turnStoneRegex;

        // Find a good regex for 種族符石
        // Comma is for #1693 關師傅
        private Pattern raceStoneRegex1;
        private Pattern raceStoneRegex2;

        @Override
        public void onPrepare() {
            prepareShow();
            //prepareTurnRunestones();
            //prepareRaceStones();
        }

        private void prepareShow() {
            String farmKey = RegexUtil.toRegexOr(Arrays.asList(App.res().getStringArray(R.array.seriesOfFarm)));
            farmSeries = Pattern.compile(farmKey);

            selectForShow = new boolean[6];
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
                    case R.id.sortHideFarm: m = 4; break;
                    case R.id.sortHideNonFarm: m = 5; break;
                }
                if (MathUtil.isInRange(m, 0, selectForShow.length)) {
                    selectForShow[m] = v.isSelected();
                }
            }
        }
//
//        private void prepareTurnRunestones() {
//            List<String> stones = new ArrayList<>();
//            getSelectTags(sortRunestone, stones, false);
//            int n = stones.size();
//            if (n == 0) {
//                turnStoneRegex = null;
//                return;
//            }
//
//            // 轉化(|為)(光|光強化)(|神族|魔族|人族|獸族|龍族|妖族|機械族)符石
//            List<String> allRace = getRaces();
//            allRace.add(0, ""); // No specified race
//            String r = getString(R.string.cards_turn_into2) + "(|為)"
//                    + RegexUtil.toRegexOr(stones)
//                    + RegexUtil.toRegexOr(allRace)
//                    + getString(R.string.cards_runestone);
//            turnStoneRegex = Pattern.compile(r);
//            logE("(T) = %s", turnStoneRegex);
//        }
//
//        private void prepareRaceStones() {
//            List<String> attr = new ArrayList<>();
//            getSelectTags(sortRaceStoneAttr, attr, false);
//            List<String> race = new ArrayList<>();
//            getSelectTags(sortRaceStoneRace, race, false);
//            if (attr.isEmpty() && race.isEmpty()) {
//                raceStoneRegex1 = null;
//                raceStoneRegex2 = null;
//                return;
//            }
//
//            // (火|光)(人族|獸族)(|強化)符石
//            String attrs = toRegex(attr);
//            String races = toRegex(race);
//
//            String regex = attrs + races + "(|強化)符石";
//            raceStoneRegex1 = Pattern.compile(regex);
//
//            String raceK = toRegex(race).replace("族", "");
//            raceStoneRegex2 = Pattern.compile(raceK);
//            logE("(R1, R2) = %s   %s", raceStoneRegex1, raceStoneRegex2);
//        }
//
//        private List<String> getRaces() {
//            List<String> race = new ArrayList<>();
//            getTagsWhen((w) -> true, sortRaceStoneRace, race, false);
//            return race;
//        }

        @Override
        public String typeName() {
            return "TosCardPack";
        }

        @Override
        public boolean onSelect(PackCard p) {
            TosCard c = TosWiki.getCardByIdNorm(p.idNorm);
            return selectForBasic(c)
                    //&& selectForTurnRunestones(c)
                    //&& selectForRaceRunestones(c)
                    && selectForImprove(c)
                    //&& selectForSpecial(c)
                    && selectForShow(c)
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
//
//        private boolean selectForTurnRunestones(TosCard c) {
//            // Still fail for 轉化為固定數量及位置 & X及Y符石轉化為強化符石
//            // like, 洛可可(1169) & 龍葵(0900) & 1772 (尼特羅)
//            // Runestones keys as st
//            if (turnStoneRegex == null) {
//                return true;
//            } else {
//                String key = c.skillsDesc();
//                return turnStoneRegex.matcher(key).find();
//            }
//        }
//
//        private boolean selectForRaceRunestones(TosCard c) {
//            if (raceStoneRegex1 == null) {
//                return true;
//            }
//
//            String key = c.skillsDesc() + " & " + c.skillAwkName;
//            if (sortRaceStoneAddLeader.isSelected()) {
//                key += " & " + c.skillLeaderDesc;
//            }
//            if (sortRaceStoneAddDetail.isSelected()) {
//                key += " & " + c.cardDetails;
//            }
//
//            boolean b = raceStoneRegex1.matcher(key).find();
//
//            // Find 自身種族符石, special
//            b = selectRaceRuneStoneSpecial(b, key, c);
////            boolean z = raceStoneRegexSp.matcher(key).find();
////            if (z) {
////                b |= raceStoneRegex2.matcher(c.race).find();
////            }
//            return b;
//        }
//
//        private boolean selectRaceRuneStoneSpecial(boolean value, String key, TosCard c) {
//            boolean ans = value;
//            int idNorm = Integer.parseInt(c.idNorm);
//            //boolean noSel = !sortRaceStoneAddLeader.isSelected() && !sortRaceStoneAddDetail.isSelected();
//            switch (idNorm) {
//                case 1624: // God & elf, 絲堤
//                    ans = raceStoneRegex2.matcher("神妖精").find();
//                    break;
//                case 1661: case 1662: case 1663: case 1664: case 1665: // 修道仙妖
//                    ans = raceStoneRegex2.matcher("妖精").find();
//                    break;
//                //case 1982: // 唐三藏
//                case 2013: // 愛迪生
//                case 2021: // 妮可
//                    ans = true;
//                    break;
//            }
//            return ans;
//        }
//
//        private String toRegex(List<String> keys) {
//            return RegexUtil.toRegexOr(keys);
//        }

        private boolean selectForShow(TosCard c) {
            int idNorm = Integer.parseInt(c.idNorm);
            boolean accept = true;
            if (selectForShow[0]) {
                //noinspection ConstantConditions
                accept &= !MathUtil.isInRange(idNorm, 6000, 7000);
            }
            if (selectForShow[1]) {
                accept &= !MathUtil.isInRange(idNorm, 7000, 8000);
            }
            if (selectForShow[2]) {
                accept &= !MathUtil.isInRange(idNorm, 8000, 9000);
            }
            if (selectForShow[3]) {
                accept &= !MathUtil.isInRange(idNorm, 9000, 10000);
            }
            if (selectForShow[4]) {
                accept &= farmSeries.matcher(c.series).find();//!MathUtil.isInRange(idNorm, 9000, 10000);
            }
            if (selectForShow[5]) {
                accept &= !farmSeries.matcher(c.series).find();//!MathUtil.isInRange(idNorm, 9000, 10000);
            }
            return accept;
        }
//
//        private boolean selectForSpecial(TosCard c) {
//            String key = activeSkill(c);
//            boolean accept = true;
//            if (!sortSpecialNo.isChecked()) {
//                boolean element = c.race.matches(getString(R.string.cards_race_level) + "|" + getString(R.string.cards_race_evolve));
//                //Though repeat, but fast.....
//                if (sortSpecialFree.isChecked()) {
//                    //noinspection ConstantConditions
//                    accept &= find(key, R.array.cards_freemove_keys);
//                }
//                if (sortSpecialKeep.isChecked()) {
//                    accept &= find(key, R.array.cards_keep_keys);
//                }
//                if (sortSpecialExplode.isChecked()) {
//                    accept &= find(key, R.array.cards_explode_keys);
//                }
//                if (sortSpecialMoreCoin.isChecked()) {
//                    key += " & " + c.skillLeaderDesc;
//                    accept &= find(key, R.array.cards_morecoin_keys);
//                }
//                if (sortSpecialExtend.isChecked()) {
//                    accept &= find(key, R.array.cards_extend_keys);
//                }
//                if (sortSpecialAlsoActive.isChecked()) {
//                    accept &= find(key, R.array.cards_also_keys);
//                }
//                if (sortSpecialAlsoLeader.isChecked()) {
//                    key = leaderSkill(c);
//                    accept &= find(key, R.array.cards_also_keys);
//                }
//                if (sortSpecialRestoreDropRateTransfer.isChecked()) {
//                    accept &= find(key, R.array.cards_runestone_drop_rate_transfer_keys);
//                }
//                if (sortSpecialNoDefeat.isChecked()) {
//                    accept &= find(key, R.array.cards_no_defeat_keys);
//                }
//                if (sortSpecialDamageLessActive.isChecked()) {
//                    accept &= find(key, R.array.cards_damage_less_keys);
//                }
//                if (sortSpecialDamageLessLeader.isChecked()) {
//                    key = leaderSkill(c);
//                    accept &= find(key, R.array.cards_damage_less_leader_keys);
//                }
//                if (sortSpecialDamageToHp.isChecked()) {
//                    accept &= find(key, R.array.cards_damage_to_hp_keys);
//                }
//                if (sortSpecialNonAttribute.isChecked()) {
//                    accept &= find(key, R.array.cards_non_attribute_keys);
//                }
//                if (sortSpecialRegardlessDefense.isChecked()) {
//                    accept &= find(key, R.array.cards_regardless_of_defense_keys);
//                }
//                if (sortSpecialRegardlessAttribute.isChecked()) {
//                    accept &= find(key, R.array.cards_regardless_of_attribute_keys);
//                }
//                if (sortSpecialClearAllEffect.isChecked()) {
//                    accept &= find(key, R.array.cards_clear_all_effect_keys);
//                }
//                if (sortSpecialStayUntil.isChecked()) {
//                    accept &= find(key, R.array.cards_stay_until_keys);
//                }
//                if (sortSpecialStayUntilIf.isChecked()) {
//                    accept &= find(key, R.array.cards_stay_until_if_keys);
//                }
//                if (sortSpecialExtraAttack.isChecked()) {
//                    accept &= find(key, R.array.cards_extra_attack_keys);
//                }
//                if (sortSpecialOriginalColor.isChecked()) {
//                    accept &= find(key, R.array.cards_black_white_original_keys);
//                }
//                if (sortSpecialRestoreNormal.isChecked()) {
//                    accept &= find(key, R.array.cards_restore_runestone_normal_keys);
//                }
//                if (sortSpecialFix.isChecked()) {
//                    accept &= find(key, R.array.cards_fix_keys);
//                }
//                if (sortSpecialRestoreIntoEnchanted.isChecked()) {
//                    accept &= find(key, R.array.cards_runestone_into_enchanted_keys);
//                }
//                if (sortSpecialRestoreAllAttrRandom.isChecked()) {
//                    accept &= find(key, R.array.cards_runestone_all_attr_random_keys);
//                }
//                if (sortSpecialRestoreAllIntoRandom.isChecked()) {
//                    accept &= find(key, R.array.cards_runestone_all_into_random_keys);
//                }
//                if (sortSpecialRestoreAllInto.isChecked()) {
//                    accept &= find(key, R.array.cards_runestone_all_into_keys);
//                }
//                if (sortSpecialRestoreAllIntoEnchanted.isChecked()) {
//                    accept &= find(key, R.array.cards_runestone_all_into_enchanted_keys);
//                }
//                if (sortSpecialDodge.isChecked()) {
//                    accept &= find(key, R.array.cards_dodge_keys);
//                }
//                if (sortSpecialOneDealDamage.isChecked()) {
//                    accept &= !element & find(key, R.array.cards_one_deal_damage_keys);
//                }
//                if (sortSpecialOneDealDamageElement.isChecked()) {
//                    accept &= element && find(key, R.array.cards_one_deal_damage_keys);
//                }
//                if (sortSpecialAllDealDamage.isChecked()) {
//                    accept &= !element & find(key, R.array.cards_all_deal_damage_keys);
//                }
//                if (sortSpecialAllDealDamageElement.isChecked()) {
//                    accept &= element && find(key, R.array.cards_all_deal_damage_keys);
//                }
//                if (sortSpecialTurnEnemyAttr.isChecked()) {
//                    accept &= find(key, R.array.cards_turn_enemy_attr_keys);
//                }
//                if (sortSpecialDelay.isChecked()) {
//                    accept &= find(key, R.array.cards_delay_keys);
//                }
//            }
//            return accept;
//        }
//
//        private String leaderSkill(TosCard c) {
//            return joinKey(c.skillLeaderDesc, c);
//        }
//
//        private String activeSkill(TosCard c) {
//            return joinKey(c.skillsDesc(), c);
//        }
//
//        private String joinKey(String key, TosCard c) {
//            if (sortSpecialAddDetail.isSelected()) {
//                key += " & " + c.cardDetails;
//            }
//            return key;
//        }

        private boolean find(String key, @ArrayRes int dataId) {
            String[] data = App.res().getStringArray(dataId);
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
            }
            return accept;
        }

        @NonNull
        @Override
        public List<Integer> sort(@NonNull List<Integer> result) {
            Comparator<Integer> cmp;
            cmp = getCommonComparator();
//            if (cmp == null) {
//                cmp = getCassandraComparator();
//            }

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
//            if (msgs == null) {
//                msgs = getCassandraMessages(result);
//            }
            return msgs;
        }
//
//        private Comparator<Integer> getCassandraComparator() {
//            // Create comparator
//            int id = sortCassandra.getCheckedRadioButtonId();
//            switch (id) {
//                case R.id.sortCassandraAttack:
//                    return (o1, o2) -> {
//                        boolean dsc = true;
//                        TosCard c1 = data.get(o1);
//                        TosCard c2 = data.get(o2);
//                        double atk1 = c1.maxAttack + c1.maxRecovery * 3.5;
//                        double atk2 = c2.maxAttack + c2.maxRecovery * 3.5;
//                        //logCard("#1", c1);
//                        //logCard("#2", c2);
//                        if (dsc) {
//                            return Double.compare(atk2, atk1);
//                        } else {
//                            return Double.compare(atk1, atk2);
//                        }
//                    };
//                case R.id.sortCassandraRatio:
//                    return (o1, o2) -> {
//                        boolean dsc = true;
//                        TosCard c1 = data.get(o1);
//                        TosCard c2 = data.get(o2);
//                        double atk1 = 1 + c1.maxRecovery * 3.5 / c1.maxAttack;
//                        double atk2 = 1 + c2.maxRecovery * 3.5 / c2.maxAttack;
//                        if (dsc) {
//                            return Double.compare(atk2, atk1);
//                        } else {
//                            return Double.compare(atk1, atk2);
//                        }
//                    };
//                default:
//                    break;
//            }
//            return null;
//        }

        private Comparator<Integer> getCommonComparator() {
            // Create comparator
            int id = sortCommon.getCheckedRadioButtonId();
            if (id == RadioGroup.NO_ID || id == R.id.sortCommonNormId) {
                return null;
            }
            return (o1, o2) -> {
                boolean dsc = true;
                PackCard p1 = data.get(o1);
                PackCard p2 = data.get(o2);
                TosCard c1 = TosWiki.getCardByIdNorm(p1.idNorm);
                TosCard c2 = TosWiki.getCardByIdNorm(p2.idNorm);
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
//
//        private List<String> getCassandraMessages(List<Integer> result) {
//            List<String> message = null;
//            TosCard c;
//            String msg;
//            // Create Message
//            int id = sortCassandra.getCheckedRadioButtonId();
//            switch (id) {
//                case R.id.sortCassandraAttack:
//                    message = new ArrayList<>();
//                    for (int i = 0; i < result.size(); i++) {
//                        c = data.get(result.get(i));
//                        double atk = c.maxAttack + c.maxRecovery * 3.5;
//                        message.add(_fmt("%.1f", atk));
//                    }
//                    break;
//                case R.id.sortCassandraRatio:
//                    message = new ArrayList<>();
//                    for (int i = 0; i < result.size(); i++) {
//                        c = data.get(result.get(i));
//                        double atk = 1 + c.maxRecovery * 3.5 / c.maxAttack;
//                        message.add(_fmt("%.2f", atk));
//                    }
//                    break;
//                default:
//                    break;
//            }
//            return message;
//        }

        private List<String> getCommonMessages(List<Integer> result) {
            List<String> message = new ArrayList<>();
            PackCard p;
            TosCard c;
            String msg;
            // Create Message
            boolean added = false;
            int id = sortCommon.getCheckedRadioButtonId();

            for (int i = 0; i < result.size(); i++) {
                p = data.get(result.get(i));
                c = TosWiki.getCardByIdNorm(p.idNorm);
                msg = null;
                switch (id) {
                    case R.id.sortCommonMaxHP:
                        msg = String.valueOf(c.maxHPAme);
                        if (c.ameAddHP()) {
                            msg += "^";
                        }
                        break;
                    case R.id.sortCommonMaxAttack:
                        msg = String.valueOf(c.maxAttackAme);
                        if (c.ameAddAttack()) {
                            msg += "^";
                        }
                        break;
                    case R.id.sortCommonMaxRecovery:
                        msg = String.valueOf(c.maxRecoveryAme);
                        if (c.ameAddRecovery()) {
                            msg += "^";
                        }
                        break;
                    case R.id.sortCommonMaxSum:
                        msg = String.valueOf(c.maxHPAme + c.maxAttackAme + c.maxRecoveryAme);
                        if (c.ameAddAll()) {
                            msg += "^";
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

}
