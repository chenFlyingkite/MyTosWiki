package com.flyingkite.mytoswiki;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.flyingkite.fabric.FabricAnswers;
import com.flyingkite.library.TicTac2;
import com.flyingkite.library.log.Loggable;
import com.flyingkite.library.preference.BasePreference;
import com.flyingkite.library.util.ListUtil;
import com.flyingkite.library.util.MathUtil;
import com.flyingkite.library.util.ThreadUtil;
import com.flyingkite.library.widget.Library;
import com.flyingkite.mytoswiki.data.PackSort;
import com.flyingkite.mytoswiki.data.pack.PackCard;
import com.flyingkite.mytoswiki.data.pack.PackInfoCard;
import com.flyingkite.mytoswiki.data.tos.TosCard;
import com.flyingkite.mytoswiki.dialog.CommonDialog;
import com.flyingkite.mytoswiki.dialog.UsePackDialog;
import com.flyingkite.mytoswiki.library.CardPackInfoAdapter;
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
import com.google.gson.Gson;

import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

import androidx.annotation.ArrayRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import flyingkite.tool.StringUtil;

public class TosCardMyFragment extends BaseFragment implements TosPageUtil, JsoupUtil {
    public static final String TAG = "TosCardMyFragment";
    public static final String TOS_REVIEW = "https://review.towerofsaviors.com/";

    private RecyclerView cardsRecycler;
    private TextView tosInfo;
    private TextView tosInfo2;
    private TextView uidText;
    private View uidLoad;
    private View tosUsage;
    private TextView loading;
    private View howToUse;
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
    // 特選
    private ViewGroup sortSpecial;
    //private View sortSpecialFast;
    private View sortFastSkill;

    private CheckBox sortSpecialNo;
    private CheckBox sortSpecialSkillEatable2;
    private CheckBox sortSpecialSkillEatable3;
    private CheckBox sortSpecialSkillEatable4;
    private CheckBox sortSpecialSkillNotMax;
    private CheckBox sortSpecialSkillEnough;
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

    //private Library<CardAdapter> cardLib;
    //private Library<CardPackAdapter> cardLib;
    private Library<CardPackInfoAdapter> cardLib;

    private List<TosCard> myBag = new ArrayList<>();
    private List<PackCard> myPack = new ArrayList<>();
    private Map<String, PackInfoCard> myInfoPack = new TreeMap<>();
    private boolean showHint;
    private WaitingDialog waiting;
    private TosCardFragment.ToolBarOwner toolOwner;
    //private PackSort packSort = new PackSort();

    @Override
    protected int getPageLayoutId() {
        return R.layout.fragment_tos_card_my;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cardsRecycler = findViewById(R.id.tosRecycler);
        initSortMenu();
        initToolIcons();

        cardLib = new Library<>(cardsRecycler, 5);
        //new LoadDataAsyncTask().executeOnExecutor(sSingle);
        TosWiki.attendDatabaseTasks(onCardsReady);
    }

    // init sort menus and put onClickListeners --------
    private void initSortMenu() {
        // Create MenuWindow
        Pair<View, PopupWindow> pair = createPopupWindow(R.layout.popup_tos_sort_card_pack, (ViewGroup) getView());
        sortWindow = pair.second;
        View menu = pair.first;

        menuEntry = findViewById(R.id.tosSortMenu);
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
        initSortBySpecial(menu);
        initSortByImprove(menu);
    }

    private void initToolIcons() {
        tosInfo = findViewById(R.id.tosInfo);
        tosInfo2 = findViewById(R.id.tosInfo2);
        uidText = findViewById(R.id.tosMyUid);
        uidLoad = findViewById(R.id.tosLoad);
        tosUsage = findViewById(R.id.tosUsage);
        tosUsage.setOnClickListener((v) -> {
            logHowToUse();
            new UsePackDialog().show(getActivity());
        });
        loading = findViewById(R.id.tosLoading);
        howToUse = findViewById(R.id.tosHow);
        howToUse.setOnClickListener((v) -> {
            tosUsage.callOnClick();
        });
        View clr = findViewById(R.id.tosClear);
        clr.setOnClickListener((v) -> {
            logClear();
            AppPref p = new AppPref();
            uidText.setText("");
            p.setUserTosInventory("");
            p.setUserUid("");
            showToast(getString(R.string.cleared));
        });

        AppPref p = new AppPref();
        uidText.setText(p.getUserUid());

        initScrollTools(R.id.tosGoTop, R.id.tosGoBottom, cardsRecycler);
        uidLoad.setOnClickListener((v) -> {
            showKeyBoard(false, v);
            showHint = true;
            String uid = uid();
            if (TextUtils.isEmpty(uid)) {
                new CommonDialog().message(getString(R.string.enterUid)).show(getActivity());
                return;
            }

            if (!App.isNetworkConnected()) {
                new CommonDialog().message(getString(R.string.noNetwork)).show(getActivity());
                return;
            }
            logDownload(uid);
            loadMyPack();
        });

        // Setup tool bar
        View tool = findViewById(R.id.tosToolBar);
        tool.setOnClickListener((v) -> {
            toggleSelected(v);

            boolean s = v.isSelected();
            if (toolOwner != null) {
                toolOwner.setToolsVisible(s);
                logAction(s ? "showTool" : "hideTool");
            }
            setViewVisibility(findViewById(R.id.tosUidBox), s);
        });
        boolean sel = false;
        if (toolOwner != null) {
            sel = toolOwner.isToolsVisible();
        }
        tool.setSelected(sel);

    }

    //----

    private void initShareImage(View parent) {
        parent.findViewById(R.id.tosSave).setOnClickListener((v) -> {
            View view = cardsRecycler;
            //File folder = Environment.getExternalStoragePublicDirectory()
            String name = ShareHelper.cacheName("1.png");

            ShareHelper.shareImage(getActivity(), view, name);
            logMenu("shareLibrary"); // TODO
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
        sortImproveDmx = menu.findViewById(R.id.sortImproveDualMaxAdd);

        sortImprove = initSortOf(menu, R.id.sortImprove, this::clickImprove);
    }

    private void initSortBySpecial(View menu) {
        sortSpecialNo = menu.findViewById(R.id.sortSpecialNo);
        sortSpecialSkillEatable2 = menu.findViewById(R.id.sortSpecialSkillEatable2);
        sortSpecialSkillEatable3 = menu.findViewById(R.id.sortSpecialSkillEatable3);
        sortSpecialSkillEatable4 = menu.findViewById(R.id.sortSpecialSkillEatable4);
        sortSpecialSkillNotMax = menu.findViewById(R.id.sortSpecialSkillNotMax);
        sortSpecialSkillEnough = menu.findViewById(R.id.sortSpecialSkillEnough);
//        sortSpecialMoreCoin = menu.findViewById(R.id.sortSpecialMoreCoin);
//        sortSpecialExtend = menu.findViewById(R.id.sortSpecialExtend);
//        sortSpecialAlsoActive = menu.findViewById(R.id.sortSpecialAlsoActive);
//        sortSpecialAlsoLeader = menu.findViewById(R.id.sortSpecialAlsoLeader);
//        sortSpecialRestoreDropRateTransfer = menu.findViewById(R.id.sortSpecialRestoreDropRateTransfer);
//        sortSpecialNoDefeat = menu.findViewById(R.id.sortSpecialNoDefeat);
//        sortSpecialDamageLessActive = menu.findViewById(R.id.sortSpecialDamageLessActive);
//        sortSpecialDamageLessLeader = menu.findViewById(R.id.sortSpecialDamageLessLeader);
//        sortSpecialDamageToHp = menu.findViewById(R.id.sortSpecialDamageToHp);
//        sortSpecialNonAttribute = menu.findViewById(R.id.sortSpecialNonAttribute);
//        sortSpecialRegardlessDefense = menu.findViewById(R.id.sortSpecialRegardlessDefense);
//        sortSpecialRegardlessAttribute = menu.findViewById(R.id.sortSpecialRegardlessAttribute);
//        sortSpecialClearAllEffect = menu.findViewById(R.id.sortSpecialClearAllEffect);
//        sortSpecialStayUntil = menu.findViewById(R.id.sortSpecialStayUntil);
//        sortSpecialStayUntilIf = menu.findViewById(R.id.sortSpecialStayUntilIf);
//        sortSpecialExtraAttack = menu.findViewById(R.id.sortSpecialExtraAttack);
//        sortSpecialOriginalColor = menu.findViewById(R.id.sortSpecialOriginalColor);
//        sortSpecialRestoreNormal = menu.findViewById(R.id.sortSpecialRestoreNormal);
//        sortSpecialFix = menu.findViewById(R.id.sortSpecialFix);
//        sortSpecialRestoreIntoEnchanted = menu.findViewById(R.id.sortSpecialRestoreIntoEnchanted);
//        sortSpecialRestoreAllAttrRandom = menu.findViewById(R.id.sortSpecialRestoreAllAttrRandom);
//        sortSpecialRestoreAllIntoRandom = menu.findViewById(R.id.sortSpecialRestoreAllIntoRandom);
//        sortSpecialRestoreAllInto = menu.findViewById(R.id.sortSpecialRestoreAllInto);
//        sortSpecialRestoreAllIntoEnchanted = menu.findViewById(R.id.sortSpecialRestoreAllIntoEnchanted);
//        sortSpecialDodge = menu.findViewById(R.id.sortSpecialDodge);
//        sortSpecialOneDealDamage = menu.findViewById(R.id.sortSpecialOneDealDamage);
//        sortSpecialOneDealDamageElement = menu.findViewById(R.id.sortSpecialOneDealDamageElement);
//        sortSpecialAllDealDamage = menu.findViewById(R.id.sortSpecialAllDealDamage);
//        sortSpecialAllDealDamageElement = menu.findViewById(R.id.sortSpecialAllDealDamageElement);
//        sortSpecialTurnEnemyAttr = menu.findViewById(R.id.sortSpecialTurnEnemyAttr);
//        sortSpecialDelay = menu.findViewById(R.id.sortSpecialDelay);

        sortFastSkill = menu.findViewById(R.id.sortFastSkill);
        sortFastSkill.setOnClickListener((v) -> {
            logMenu("5 星可練技卡");
            // Find 5 star and click
            View target = findChildTag(sortStar, "5");
            if (target != null) {
                target.setSelected(true);
            }

            sortSpecialSkillEatable4.setChecked(true);
            sortSpecialSkillNotMax.setChecked(true);
            sortSpecialSkillEnough.setChecked(true);
            setCheckedIncludeNo(sortSpecialSkillEnough, R.id.sortSpecialNo, sortSpecial);
            applySelection();
        });
        sortSpecial = initSortOf(menu, R.id.sortSpecialList, this::clickSpecial);
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
        setCheckedIncludeNo(sortSpecialNo, R.id.sortSpecialNo, sortSpecial);
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
            case R.id.sortDisplayNone:
                type = Misc.NT_NONE;
                break;
            case R.id.sortDisplayNameNormId:
                type = Misc.NT_NAME_ID_NORM;
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

    private void clickSpecial(View v) {
        setCheckedIncludeNo(v, R.id.sortSpecialNo, sortSpecial);
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

            List<PackInfoCard> list = new ArrayList<>(myInfoPack.values());
            //cardLib.adapter.setSelection(new TosSelectCard(myPack, cond));
            cardLib.adapter.setSelection(new TosSelectCard(list, cond));
        }
    }

    private void nonAllApply(View v, ViewGroup vg) {
        toggleAndClearIfAll(v, vg);

        applySelection();
    }

    private void updateHide() {
        PackSort p = new MyPackPref().getPref();

        sortHide.findViewById(R.id.sortHideEmpty ).setSelected(p.ownExist);
        sortHide.findViewById(R.id.sortHideFarm  ).setSelected(p.farm);
        sortHide.findViewById(R.id.sortHideNormal).setSelected(p.normal);
        sortHide.findViewById(R.id.sortHide7xxx  ).setSelected(p.tauFa);
        sortHide.findViewById(R.id.sortHide8xxx  ).setSelected(p.disney);
        sortHide.findViewById(R.id.sortHide9xxx  ).setSelected(p.demon72);

        int k = (int) MathUtil.makeInRange(p.display, 0, sortDisplay.getChildCount());
        clickDisplay(sortDisplay.getChildAt(k));
    }
    // --------

    // Saving preference as Gson --------
    private void toGsonHide() {
        PackSort p = new PackSort();
        p.ownExist = sortHide.findViewById(R.id.sortHideEmpty ).isSelected();
        p.farm     = sortHide.findViewById(R.id.sortHideFarm  ).isSelected();
        p.normal   = sortHide.findViewById(R.id.sortHideNormal).isSelected();
        p.tauFa    = sortHide.findViewById(R.id.sortHide7xxx  ).isSelected();
        p.disney   = sortHide.findViewById(R.id.sortHide8xxx  ).isSelected();
        p.demon72  = sortHide.findViewById(R.id.sortHide9xxx  ).isSelected();

        p.display = findCheckedIndex(sortDisplay);
        new MyPackPref().setPref(p);
    }
    //----

    private int findCheckedIndex(ViewGroup vg) {
        int n = vg.getChildCount();
        for (int i = 0; i < n; i++) {
            View v = vg.getChildAt(i);
            if (v instanceof Checkable) {
                Checkable r = (Checkable) v;
                if (r.isChecked()) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public void onPause() {
        super.onPause();
        toGsonHide();

        if (uidText != null) {
            String uid = uid();
            // Save to preference
            AppPref p = new AppPref();
            p.setUserUid(uid);
        }
    }

    @Override
    public void onToolScrollToPosition(RecyclerView rv, int position) {
        String s = position == 0 ? "Scroll Head" : "Scroll Tail";
        logAction(s);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TosCardFragment.ToolBarOwner) {
            toolOwner = (TosCardFragment.ToolBarOwner) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        toolOwner = null;
    }

    private void showUsage() {
        if (showHint) {
            new UsePackDialog().show(getActivity());
        } else {
            howToUse.setVisibility(View.VISIBLE);
        }
    }

    private void loadMyPack() {
        loadPack(uid());
    }

    private String uid() {
        return uidText.getText().toString();
    }

    private void loadPack(String uid) {
        new LoadPackTask(uid).executeOnExecutor(ThreadUtil.cachedThreadPool);
    }

    private void inventory(String inventory) {
        //logE("inventory_str  = \n%s\n", inventory);
        String[] invs = inventory.split(",");
        myBag.clear();
        myPack.clear();
        myInfoPack.clear();
        TosCard[] all = TosWiki.allCards();
        for (int i = 0; i < all.length; i++) {
            // Make PackInfoCard from TosCard
            TosCard d = all[i];
            if (TosCardUtil.isInBag(d)) {
                PackInfoCard c = PackInfoCard.from(d);
                myInfoPack.put(d.idNorm, c);
            }
        }

        for (int i = 0; i < invs.length; i++) {
            PackCard p = TosCardUtil.parseCard(invs[i]);
            myPack.add(TosCardUtil.parseCard(invs[i]));
            String[] a = invs[i].split("[|]");
            myBag.add(TosWiki.getCardByIdNorm(TosCardUtil.idNorm(a[1])));

            PackInfoCard q = myInfoPack.get(p.idNorm);
            if (q != null) {
                q.packs.add(p);
            }
        }
        logE("\n\n");

        if (false) { // TODO print
            logE("---- PackInfoCard ----- %s items", myInfoPack.size());
            List<PackInfoCard> p = new ArrayList<>(myInfoPack.values());
            for (int i = 0; i < p.size(); i++) {
                PackInfoCard pi = p.get(i);
                String name = TosWiki.getCardByIdNorm(pi.idNorm).name;
                logE("#%04d : %s %-20s -> %s", i, pi.idNorm, name, pi);
            }
            logE("---------");
        }

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

            String idNorm = TosCardUtil.idNorm(di[1]);
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
        if (isActivityGone()) return;

        uidLoad.setEnabled(false);
        if (showHint) {
            waiting = new WaitingDialog.Builder(getActivity()).message(getString(R.string.cardsLoading)).buildAndShow();
        } else {
            loading.setVisibility(View.VISIBLE);
        }

    }

    private void hideCardsLoading() {
        if (isActivityGone()) return;

        uidLoad.setEnabled(true);
        if (showHint) {
            if (waiting != null) {
                waiting.dismiss();
                waiting = null;
            }
        } else {
            loading.setVisibility(View.GONE);
        }
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

            updateHide();
            loadMyPack();
        }
    };

    //-- Events
    private void logMenu(String type) {
        Map<String, String> m = new HashMap<>();
        m.put("menu", type);
        FabricAnswers.logMyPack(m);
    }

    private void logClear() {
        Map<String, String> m = new HashMap<>();
        m.put("clear", "1");
        FabricAnswers.logMyPack(m);
    }

    private void logAction(String act) {
        Map<String, String> m = new HashMap<>();
        m.put("action", act);
        FabricAnswers.logMyPack(m);
    }

    private void logDownload(String id) {
        Map<String, String> m = new HashMap<>();
        m.put("DownloadUid", id);
        FabricAnswers.logMyPack(m);
    }

    private void logHowToUse() {
        Map<String, String> m = new HashMap<>();
        m.put("HowToUse", "1");
        FabricAnswers.logMyPack(m);
    }
    //-- Events


    // Actual implementation of TosSelectCard --------
    private class TosSelectCard extends AllCards<PackInfoCard> {
        private final String[] commonRace = App.res().getStringArray(R.array.cards_common_keys_race);

        private TosCondition select;

        public TosSelectCard(List<PackInfoCard> source, TosCondition condition) {
            super(source);
            select = condition;
        }

        private SparseBooleanArray selectForShow = new SparseBooleanArray();
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

            selectForShow.clear();
            ViewGroup vg = sortHide;
            int n = vg.getChildCount();
            for (int i = 0; i < n; i++) {
                int m = -1;
                View v = vg.getChildAt(i);
                switch (v.getId()) {
                    case R.id.sortHideEmpty:   m = 0; break;
                    case R.id.sortHideFarm:    m = 1; break;
                    case R.id.sortHideNormal:  m = 2; break;
                    case R.id.sortHide7xxx:    m = 3; break;
                    case R.id.sortHide8xxx:    m = 4; break;
                    case R.id.sortHide9xxx:    m = 5; break;
                }
                selectForShow.put(m, v.isSelected());
            }
            logE("prepared show as %s", selectForShow);
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
        public boolean onSelect(PackInfoCard p) {
            TosCard c = TosWiki.getCardByIdNorm(p.idNorm);
            return selectForBasic(c)
                    //&& selectForTurnRunestones(c)
                    //&& selectForRaceRunestones(c)
                    && selectForImprove(c)
                    && selectForSpecial(c, p)
                    && selectForShow(c, p)
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

        private boolean selectForShow(TosCard c, PackInfoCard p) {
            boolean accept = true;

            if (selectForShow.get(0)) {
                //noinspection ConstantConditions
                accept &= p.packs.size() > 0;
            }
            if (selectForShow.get(1)) {
                accept &= isFarm(c);
            }
            if (selectForShow.get(2)) {
                accept &= !isFarm(c);
            }
            if (selectForShow.get(3)) {
                accept &= !TosCardUtil.isTauFa(c);
            }
            if (selectForShow.get(4)) {
                accept &= !TosCardUtil.isDisney(c);
            }
            if (selectForShow.get(5)) {
                accept &= !TosCardUtil.is72Demon(c);
            }
            return accept;
        }

        private boolean isFarm(TosCard c) {
            boolean a = farmSeries.matcher(c.series).matches();
            boolean b = c.rarity <= 4;
            return a && b;
        }


        private boolean selectForSpecial(TosCard c, PackInfoCard p) {
            boolean accept = true;
            if (!sortSpecialNo.isChecked()) {
                //Though repeat, but fast.....
                if (sortSpecialSkillEatable2.isChecked()) {
                    //noinspection ConstantConditions
                    accept &= c.sameSkills.size() >= 2;
                }
                if (sortSpecialSkillEatable3.isChecked()) {
                    accept &= c.sameSkills.size() >= 3;
                }
                if (sortSpecialSkillEatable4.isChecked()) {
                    accept &= c.sameSkills.size() >= 4;
                }
                if (sortSpecialSkillNotMax.isChecked()) {
                    boolean yes = false;
                    int n = p.packs.size();
                    for (int i = 0; i < n && !yes; i++) {
                        PackCard q = p.packs.get(i);
                        if (q.skillLv < c.skillCDMax1) {
                            yes = true;
                        }
                    }

                    accept &= yes;//find(key, R.array.cards_keep_keys);
                }
                if (sortSpecialSkillEnough.isChecked()) {
                    boolean yes = false;
                    int n = 0;
                    for (int i = 0; i < c.sameSkills.size() && !yes; i++) {
                        String id = c.sameSkills.get(i);
                        PackInfoCard q = myInfoPack.get(id);
                        if (q != null) {
                            TosCard qi = TosWiki.getCardByIdNorm(q.idNorm);
                            if (qi != null && qi.rarity <= 4) {
                                n += q.packs.size();
                            }
                        }
                        if (n >= 5) {
                            yes = true;
                        }
                    }
                    accept &= yes;//find(key, R.array.cards_explode_keys);
                    if (accept) {
                        logE("Yes eat %s\n%s", p, TosCardUtil.strCard(c));
                    }
                }
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
            }
            return accept;
        }
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
                PackInfoCard p1 = data.get(o1);
                PackInfoCard p2 = data.get(o2);
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
                    case R.id.sortCommonOwnCount:
                        v1 = p1.packs.size();
                        v2 = p2.packs.size();
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
            PackInfoCard p;
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
    private class LoadPackTask extends AsyncTask<Void, Void, Boolean> implements Loggable {

        private String uid = "";
        private AppPref pref = new AppPref();
        private long tic;

        public LoadPackTask(String id) {
            uid = id;
        }

        @Override
        protected void onPreExecute() {
            showCardsLoading();
            tic = System.currentTimeMillis();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (TextUtils.isEmpty(uid)) {
                return false;
            }

            String link = TOS_REVIEW + uid;
            logE("fetch %s", link);
            TicTac2 t = new TicTac2();
            t.tic();
            Document doc = getDocument(TOS_REVIEW + uid);
            t.tac("fetched");
            // Fail to fetch Document
            if (doc == null) {
                return false; // fail
            }

            String data = doc.toString();

            String inventory = find(data, 0, "inventory_str : '", "'.split(\",\")");
            // Load last time if not found
            if (TextUtils.isEmpty(inventory)) {
                inventory = pref.getUserTosInventory();
            }

            // User did not provide
            if (TextUtils.isEmpty(inventory)) {
                return false; // fail
            }

            // Save data to preference
            pref.setUserUid(uid);
            pref.setUserTosInventory(inventory);

            inventory(inventory);
            return true;
        }

        private void logTime() {
            long tac = System.currentTimeMillis();
            String t = StringUtil.MMSSFFF(tac - tic);
            String s = getString(R.string.cards_pack_ok, t + "s");
            App.showToastShort(s);
            logE(s);
        }

        @Override
        protected void onPostExecute(Boolean ok) {
            if (isActivityGone()) return;

            logTime();
            hideCardsLoading();
            if (ok) {
                CardPackInfoAdapter d = new CardPackInfoAdapter();
                List<PackInfoCard> list = new ArrayList<>(myInfoPack.values());
                d.setDataList(list);
                d.setItemListener(new CardPackInfoAdapter.ItemListener() {
                    @Override
                    public void onFiltered(int selected, int total) {
                        tosInfo.setText(App.res().getString(R.string.cards_selection_kind, selected, total));
                    }

                    @Override
                    public void onFilteredAll(int selected, int total) {
                        tosInfo2.setText(App.res().getString(R.string.cards_selection, selected, total));
                    }

                    @Override
                    public void onClick(PackInfoCard item, CardPackInfoAdapter.PCardVH holder, int position) {
                        logE("item = %s %s", item.idNorm, item.name);
                        showCardDialog(TosWiki.getCardByIdNorm(item.idNorm));
                    }
                });

                cardLib.setViewAdapter(d);

                int n = myInfoPack.size();
                tosInfo.setText(App.res().getString(R.string.cards_selection_kind, n, n));
                int n2 = 0;
                for (int i = 0; i < list.size(); i++) {
                    n2 += list.get(i).packs.size();
                }
                tosInfo2.setText(App.res().getString(R.string.cards_selection, n2, n2));

                if (showHint) {
                    showToast(R.string.cards_read, n);
                }

                howToUse.setVisibility(View.GONE);
            } else {
                showUsage();
            }
            applySelection();
        }
    }

    private class MyPackPref extends BasePreference {
        public MyPackPref() {
            super(App.me, "MyPackPref");
        }

        private final Gson gson = new Gson();

        private static final String pref = "pref";
        public void setPref(PackSort src) {
            String s = gson.toJson(src);
            //logE(">> set pack.Pref = %s", s);
            edit().putString(pref, s).apply();
        }
        public PackSort getPref() {
            String s = getString(pref, "");
            //logE("<< get pack.Pref = %s", s);
            PackSort p = gson.fromJson(s, PackSort.class);
            if (p == null) {
                p = new PackSort();
            }
            return p;
        }
    }

}
