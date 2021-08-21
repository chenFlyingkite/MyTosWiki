package com.flyingkite.mytoswiki;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.flyingkite.fabric.FabricAnswers;
import com.flyingkite.library.TicTac2;
import com.flyingkite.library.preference.BasePreference;
import com.flyingkite.library.util.ListUtil;
import com.flyingkite.library.util.MathUtil;
import com.flyingkite.library.util.ThreadUtil;
import com.flyingkite.library.widget.Library;
import com.flyingkite.mytoswiki.data.PackSort;
import com.flyingkite.mytoswiki.data.pack.PackCard;
import com.flyingkite.mytoswiki.data.pack.PackInfoCard;
import com.flyingkite.mytoswiki.data.pack.response.PackRes;
import com.flyingkite.mytoswiki.data.pack.response.TokenRes;
import com.flyingkite.mytoswiki.data.tos.TosCard;
import com.flyingkite.mytoswiki.dialog.CommonDialog;
import com.flyingkite.mytoswiki.dialog.UsePackDialog;
import com.flyingkite.mytoswiki.library.CardEvolvePathAdapter;
import com.flyingkite.mytoswiki.share.ShareHelper;
import com.flyingkite.mytoswiki.tos.TosWiki;
import com.flyingkite.mytoswiki.tos.query.AllCards;
import com.flyingkite.mytoswiki.tos.query.TosCondition;
import com.flyingkite.mytoswiki.util.OkHttpUtil;
import com.flyingkite.mytoswiki.util.RegexUtil;
import com.flyingkite.mytoswiki.util.ToolBarOwner;
import com.flyingkite.mytoswiki.util.TosCardUtil;
import com.flyingkite.mytoswiki.util.TosPageUtil;
import com.flyingkite.util.TaskMonitor;
import com.flyingkite.util.WaitingDialog;
import com.flyingkite.util.lib.Gsons;
import com.flyingkite.util.lib.RunningTask;
import com.flyingkite.util.select.SelectedData;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import flyingkite.tool.StringUtil;

public class TosCardMyFragment extends BaseFragment implements TosPageUtil {
    public static final String TAG = "TosCardMyFragment";
    //public static final String TOS_REVIEW = "https://review.towerofsaviors.com/";
    public static final String TOS_REVIEW = "https://checkup.tosgame.com/";
    public static final String TOS_REVIEW_LOGIN = "https://checkupapi.tosgame.com/user/login";
    private static final String TOS_REVIEW_PACK = "https://checkupapi.tosgame.com/api/inventoryReview/getUserProfile";

    private RecyclerView cardRecycler;
    private TextView tosInfo;
    private TextView tosInfo2;
    private TextView uidText;
    private TextView verifyText;
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
    private ViewGroup sortPathLength;

    private CheckBox sortDisplayName;
    private CheckBox sortDisplayDetail;
    //----
    // Common Sorting order
    private RadioGroup sortCommon;
    // 特選
    private ViewGroup sortSpecial;
    //private View sortSpecialFast;
    //private View sortFastSkill;

    private CheckBox sortSpecialNo;
    private CheckBox sortSpecialNonSlvMax;
//    private CheckBox sortSpecialSkillEatable2;
//    private CheckBox sortSpecialSkillEatable3;
//    private CheckBox sortSpecialSkillEatable4;
//    private CheckBox sortSpecialSkillNotMax;
//    private CheckBox sortSpecialSkillEnough;
//    // 提升能力
//    private ViewGroup sortImprove;
//    private CheckBox sortImproveNo;
//    private CheckBox sortImproveAme;
//    private CheckBox sortImproveAwk;
//    private CheckBox sortImprovePow;
//    private CheckBox sortImproveVir;
//    private CheckBox sortImproveTwo;
//    private CheckBox sortImproveChg; // Skill change
//    private CheckBox sortImproveCom; // Combine cards
//    private CheckBox sortImproveVr2;
//    private CheckBox sortImproveSwt;
//    private CheckBox sortImproveDmx;
    // Hide cards
    private ViewGroup sortHide;
    private TextView sortHideEmpty;
    private TextView sortHideShowEmpty;
    private TextView sortHideShowUnowned;
    private TextView sortHideNormal;
    private TextView sortHide7xxx;
    private TextView sortHide8xxx;
    private TextView sortHide9xxx;
    // Display card name
    private ViewGroup sortDisplay;

    //-- core data
    private Library<CardEvolvePathAdapter> cardLib;
    private final List<List<String>> evolvePath = new ArrayList<>();

    // My Pack = Original all cards from json
    private final List<PackCard> myPack = new ArrayList<>();
    // Merged myPack by idNorm -> [c1, ..., cn]
    private final Map<String, PackInfoCard> myInfoPack = new TreeMap<>();
    // Omitted cards from myPack that did not show on adapter
    private final List<PackCard> myOmit = new ArrayList<>();

    private boolean showHint;
    private WaitingDialog waiting;
    private ToolBarOwner toolOwner;

    private AppPref appPref = new AppPref();

    @Override
    protected int getPageLayoutId() {
        return R.layout.fragment_tos_card_my;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initSortMenu();
        initInputArea();
        initMyPackArea();

        TosWiki.attendDatabaseTasks(onCardsReady);
    }

    private void initInputArea() {
        uidText = findViewById(R.id.tosMyUid);
        verifyText = findViewById(R.id.tosMyVerify);
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
            clearInfo();
            fillInfoPref();
            showToast(getString(R.string.cleared));
        });

        uidLoad = findViewById(R.id.tosLoad);
        uidLoad.setOnClickListener((v) -> {
            showKeyBoard(false, v);
            showHint = true;
            String uid = uid();
            if (TextUtils.isEmpty(uid)) {
                new CommonDialog().message(getString(R.string.enterUid)).show(getActivity());
                return;
            }

            String verify = verify();
            if (TextUtils.isEmpty(verify)) {
                new CommonDialog().message(getString(R.string.enterVerify)).show(getActivity());
                return;
            }

            if (!App.isNetworkConnected()) {
                new CommonDialog().message(getString(R.string.noNetwork)).show(getActivity());
                return;
            }
            logDownload(uid);
            loginToken();
        });

        fillInfoPref();
        // test used
//        int key = appPref.getUserUid().isEmpty() ? 1 : 0;
//        if (key == 1) {
//            fillInfo("150372202", "471443");
//        } else if (key == 2) {
//            fillInfo("199215954", "836250");
//        }
    }

    private void initMyPackArea() {
        tosInfo = findViewById(R.id.tosInfo);
        tosInfo2 = findViewById(R.id.tosInfo2);
        initScrollTools(R.id.tosGoTop, R.id.tosGoBottom, () -> {
            return cardRecycler;
        });

        // tool bar
        View tool = findViewById(R.id.tosToolBar);
        tool.setOnClickListener((v) -> {
            toggleSelected(v);

            boolean s = v.isSelected();
            if (toolOwner != null) {
                toolOwner.setToolsVisible(s);
                logAction(s ? "showTool" : "hideTool");
            }
            setViewVisibility(findViewById(R.id.tosTopBar), s);
        });
        boolean sel = false;
        if (toolOwner != null) {
            sel = toolOwner.isToolsVisible();
        }
        tool.setSelected(sel);

        // library
        cardRecycler = findViewById(R.id.tosCardRecycler);
        cardLib = new Library<>(cardRecycler, true);
    }

    private void fillInfo(String uid, String verify) {
        uidText.setText(uid);
        verifyText.setText(verify);
    }

    private void fillInfoPref() {
        fillInfo(appPref.getUserUid(), appPref.getUserVerify());
    }

    private void clearInfo() {
        appPref.setUserTosInventory("");
        appPref.setUserUid("");
        appPref.setUserVerify("");
    }

    // init sort menus and put onClickListeners --------
    private void initSortMenu() {
        initSortMenuCard();
    }

    private void initSortMenuCard() {
        // Create MenuWindow
        Pair<View, PopupWindow> pair = createPopupWindow(R.layout.popup_tos_sort_card_pack, (ViewGroup) getView());
        sortWindow = pair.second;
        View menu = pair.first;

        menuEntry = findViewById(R.id.tosSortMenu);
        menuEntry.setOnClickListener(v -> {
            sortWindow.showAsDropDown(v);
        });

        makeMenu(menu);
    }

    private void makeMenu(View menu) {
        initShareImage(menu);
        initSortReset(menu);
        initSortByAttribute(menu);
        initSortByRace(menu);
        initSortByStar(menu);
        initSortByPathLength(menu);
        initDisplay(menu);
        initSortByHide(menu);
        initSortBySpecial(menu);
        initSortByCommon(menu);
        //initSortByImprove(menu);
    }

    //----

    private void initShareImage(View parent) {
        parent.findViewById(R.id.tosSave).setOnClickListener((v) -> {
            View view = cardRecycler;
            //File folder = Environment.getExternalStoragePublicDirectory()
            String name = ShareHelper.cacheName("1.png");

            ShareHelper.shareImage(getActivity(), view, name);
            logMenu("shareLibrary");
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

    private void initSortByPathLength(View menu) {
        sortPathLength = initSortOf(menu, R.id.sortPathLength, this::clickPathLength);
    }

    private void initSortByCommon(View menu) {
        sortCommon = initSortOf(menu, R.id.sortCommonList, this::clickCommon);
    }

    private void initSortByHide(View menu) {
        sortHide = initSortOf(menu, R.id.sortHide, this::clickHide);
        sortHideEmpty = menu.findViewById(R.id.sortHideEmpty);
        sortHideShowEmpty = menu.findViewById(R.id.sortHideShowEmpty);
        sortHideShowUnowned = menu.findViewById(R.id.sortHideShowUnowned);
        sortHideNormal = menu.findViewById(R.id.sortHideNormal);
        sortHide7xxx = menu.findViewById(R.id.sortHide7xxx);
        sortHide8xxx = menu.findViewById(R.id.sortHide8xxx);
        sortHide9xxx = menu.findViewById(R.id.sortHide9xxx);
    }

    private void initDisplay(View menu) {
        sortDisplay = initSortOf(menu, R.id.sortDisplayList, this::clickDisplay);
        sortDisplayName = menu.findViewById(R.id.sortDisplayName);
        sortDisplayDetail = menu.findViewById(R.id.sortDisplayDetail);
    }

//    private void initSortByImprove(View menu) {
//        sortImproveNo = menu.findViewById(R.id.sortImproveNo);
//        sortImproveAme = menu.findViewById(R.id.sortImproveAmelioration);
//        sortImproveAwk = menu.findViewById(R.id.sortImproveAwakenRecall);
//        sortImprovePow = menu.findViewById(R.id.sortImprovePowerRelease);
//        sortImproveVir = menu.findViewById(R.id.sortImproveVirtualRebirth);
//        sortImproveTwo = menu.findViewById(R.id.sortImproveTwoSkill);
//        sortImproveChg = menu.findViewById(R.id.sortImproveSkillChange);
//        sortImproveCom = menu.findViewById(R.id.sortImproveCombine);
//        sortImproveVr2 = menu.findViewById(R.id.sortImproveVirtualRebirthChange);
//        sortImproveSwt = menu.findViewById(R.id.sortImproveSwitch);
//        sortImproveDmx = menu.findViewById(R.id.sortImproveDualMaxAdd);
//
//        sortImprove = initSortOf(menu, R.id.sortImprove, this::clickImprove);
//    }

    private void initSortBySpecial(View menu) {
        sortSpecialNo = menu.findViewById(R.id.sortSpecialNo);
        sortSpecialNonSlvMax = menu.findViewById(R.id.sortSpecialNonSlvMax);
//        sortSpecialSkillEatable2 = menu.findViewById(R.id.sortSpecialSkillEatable2);
//        sortSpecialSkillEatable3 = menu.findViewById(R.id.sortSpecialSkillEatable3);
//        sortSpecialSkillEatable4 = menu.findViewById(R.id.sortSpecialSkillEatable4);
//        sortSpecialSkillNotMax = menu.findViewById(R.id.sortSpecialSkillNotMax);
//        sortSpecialSkillEnough = menu.findViewById(R.id.sortSpecialSkillEnough);
        //--
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

//        sortFastSkill = menu.findViewById(R.id.sortFastSkill);
//        sortFastSkill.setOnClickListener((v) -> {
//            logMenu("5 星可練技卡");
//            // Find 5 star and click
//            View target = findChildTag(sortStar, "5");
//            if (target != null) {
//                target.setSelected(true);
//            }
//
//            sortSpecialSkillEatable4.setChecked(true);
//            sortSpecialSkillNotMax.setChecked(true);
//            sortSpecialSkillEnough.setChecked(true);
//            setCheckedIncludeNo(sortSpecialSkillEnough, R.id.sortSpecialNo, sortSpecial);
//            applySelection();
//        });
        sortSpecial = initSortOf(menu, R.id.sortSpecialList, this::clickSpecial);
    }


    private <T extends ViewGroup> T initSortOf(View menu, @IdRes int id, View.OnClickListener childClick) {
        return setTargetChildClick(menu, id, childClick);
    }
    // --------

    private void resetMenu() {
        ViewGroup[] vgs = {sortAttributes, sortRace, sortStar, sortPathLength};
        for (ViewGroup vg : vgs) {
            setAllChildSelected(vg, false);
        }
        sortCommon.check(R.id.sortCommonNormId);
        setCheckedIncludeNo(sortSpecialNo, R.id.sortSpecialNo, sortSpecial);
        //setCheckedIncludeNo(sortImproveNo, R.id.sortImproveNo, sortImprove);
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

    private void clickPathLength(View v) {
        nonAllApply(v, sortPathLength);
    }

    private void clickDisplay(View v) {
        if (cardLib.adapter != null) {
            int n = cardLib.adapter.getItemCount();
            cardLib.adapter.notifyItemRangeChanged(0, n);
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
        //setCheckedIncludeNo(v, R.id.sortImproveNo, sortImprove);
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

        logE("-----Cards-----");
        logE("A = %s", attrs);
        logE("R = %s", races);
        logE("S = %s", stars);

        if (cardLib.adapter != null) {
            TosCondition cond = new TosCondition().attr(attrs).race(races).star(stars);
            cardLib.adapter.setSelection(new TosSelectCard(evolvePath, cond));
        }
    }

    private void nonAllApply(View v, ViewGroup vg) {
        toggleAndClearIfAll(v, vg);

        applySelection();
    }

    private void updateHide() {
        PackSort p = new MyPackPref().getPref();

        sortHideEmpty.setSelected(p.ownExist);
        sortHideShowEmpty.setSelected(p.ownZero);
        sortHideShowUnowned.setSelected(p.unownedPath);
        sortHideNormal.setSelected(p.normal);
        sortHide7xxx.setSelected(p.tauFa);
        sortHide8xxx.setSelected(p.disney);
        sortHide9xxx.setSelected(p.demon72);

        int k = (int) MathUtil.makeInRange(p.display, 0, sortDisplay.getChildCount());
        clickDisplay(sortDisplay.getChildAt(k));
    }
    // --------

    // Saving preference as Gson --------
    private void toGsonHide() {
        PackSort p = new PackSort();
        p.ownExist    = sortHideEmpty.isSelected();
        p.ownZero     = sortHideShowEmpty.isSelected();
        p.unownedPath = sortHideShowUnowned.isSelected();
        p.normal      = sortHideNormal.isSelected();
        p.tauFa       = sortHide7xxx.isSelected();
        p.disney      = sortHide8xxx.isSelected();
        p.demon72     = sortHide9xxx.isSelected();

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
            appPref.setUserUid(uid);
        }
    }

    @Override
    public void onToolScrollToPosition(RecyclerView rv, int position) {
        String s = position == 0 ? "Scroll Head" : "Scroll Tail";
        logAction(s);
    }

    @Override
    public boolean onBackPressed() {
//        if (evolvePathFilterArea.getVisibility() == View.VISIBLE) {
//            evolvePathFilterArea.performClick();
//            return true;
//        }
        return false;
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

    private void showUsage() {
        if (showHint) {
            new UsePackDialog().show(getActivity());
        } else {
            howToUse.setVisibility(View.VISIBLE);
        }
    }

    private void loginToken() {
        loginToken(uid(), verify());
    }

    private void fetchPack() {
        String token = appPref.getUserPackToken();
        fetchPack(uid(), verify(), token);
    }

    private String uid() {
        return uidText.getText().toString();
    }

    private String verify() {
        return verifyText.getText().toString();
    }

    private void parseMyPack() {
        String data = appPref.getUserTosInventory();
        if (TextUtils.isEmpty(data)) {
            showUsage();
        } else {
            ThreadUtil.cachedThreadPool.submit(new ParsePackTask(data));
        }
    }

    private void fetchPack(String uid, String verify, String token) {
        ThreadUtil.cachedThreadPool.submit(new FetchPackTask(uid, verify, token));
    }

    private void loginToken(String uid, String verify) {
        ThreadUtil.cachedThreadPool.submit(new LoginTokenTask(uid, verify));
    }

    // Core method to exam each card
    private void listPack(List<PackCard> cards) {
        myPack.clear();
        myInfoPack.clear();
        TicTac2 t = new TicTac2();
        t.tic();
        t.tic();
        // Building info pack
        myPack.addAll(cards);
        // myInfoPack = card ids we show in myPack
        TosCard[] all = TosWiki.allCards();
        for (int i = 0; i < all.length; i++) {
            TosCard d = all[i];
            if (TosCardUtil.isInBag(d)) {
                PackInfoCard c = PackInfoCard.from(d);
                myInfoPack.put(d.idNorm, c);
            }
        }
        t.tac("Building info pack");

        t.tic();
        // Omit combined cards in path
        evolvePath.clear();
        boolean useAllPath = false;
        if (useAllPath) {
            // setup adapter
            evolvePath.addAll(TosWiki.getEvolvePath());
        } else {
            List<List<String>> valid = new ArrayList<>();
            List<List<String>> allPath = TosWiki.getEvolvePath();
            // choose path[0] is in bag
            for (int i = 0; i < allPath.size(); i++) {
                List<String> s = allPath.get(i);
                TosCard d = TosWiki.getCardByIdNorm(s.get(0));
                if (TosCardUtil.isInBag(d)) {
                    valid.add(s);
                }
            }
            evolvePath.addAll(valid);
        }
        t.tac("Make evolve path");

        // Add cards from my bag to infoPack as
        // idNorm = [c1, c2, c3, ...]
        t.tic();
        myOmit.clear();
        for (int i = 0; i < cards.size(); i++) {
            PackCard c = cards.get(i);
            String idNorm = TosCardUtil.idNorm("" + c.id);
            PackInfoCard q = myInfoPack.get(idNorm);
            if (q != null) {
                q.packs.add(c);
            } else {
                //TosCard x = TosWiki.getCardByIdNorm(idNorm);
                //logE("Omit : %s => %s", c, x);
                myOmit.add(c);
            }
        }
        t.tac("Make infoPacks & omit");

        t.tic();
        List<PackInfoCard> pks = new ArrayList<>(myInfoPack.values());
        for (int i = 0; i < pks.size(); i++) {
            List<PackCard> owned = pks.get(i).packs;
            // sort owned cards
            Collections.sort(owned, (p1, p2) -> {
                long[] k1s = {p1.skillLevel, p1.level, p1.enhanceLevel, p1.acquiredAt};
                long[] k2s = {p2.skillLevel, p2.level, p1.enhanceLevel, p2.acquiredAt};

                for (int j = 0; j < k1s.length; j++) {
                    long k1 = k1s[j];
                    long k2 = k2s[j];
                    if (k1 != k2) {
                        return Long.compare(k2, k1); // desc
                    }
                }
                return 0;
            });
        }
        t.tac("Sort info pack cards");

        if (0 > 0) {
            logE("\n\n");

            Set<String> keys = myInfoPack.keySet();
            logE("%s keys", keys.size());
            for (String k : keys) {
                PackInfoCard c = myInfoPack.get(k);
                logE("%s => %s", k, c);
            }
            logE("%s omitted = %s", myOmit.size(), myOmit);
        }
        t.tac("listPack OK");
    }

    private <T> void print(T[] d) {
        if (d == null) return;

        logE("%s items", d.length);
        for (int i = 0; i < d.length; i++) {
            logE("#%04d : %s", i, d[i]);
        }
    }

    private void showCardsLoading(String msg) {
        if (isActivityGone()) return;

        uidLoad.setEnabled(false);
        if (showHint) {
            waiting = new WaitingDialog.Builder(getActivity()).message(msg).buildAndShow();
        } else {
            loading.setVisibility(View.VISIBLE);
        }
    }

    private void hideCardsLoading() {
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

    private final TaskMonitor.OnTaskState onCardsReady = new TaskMonitor.OnTaskState() {
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

            evolvePath.clear();
            evolvePath.addAll(TosWiki.getEvolvePath());
            updateHide();
            parseMyPack();
            setupAdapter(true);
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
    private class TosSelectCard extends AllCards<List<String>> {
        private final String[] commonRace = App.res().getStringArray(R.array.cards_common_keys_race);

        private final TosCondition select;
        private final List<String> wantPathLength = new ArrayList<>();

        public TosSelectCard(List<List<String>> source, TosCondition condition) {
            super(source);
            select = condition;
        }

        private Pattern farmSeries;

        @Override
        public void onPrepare() {
            wantPathLength.clear();
            getSelectTags(sortPathLength, wantPathLength, true);
            prepareShow();
        }

        @Override
        public void onSelected() {
        }

        private void prepareShow() {
            String farmKey = RegexUtil.toRegexOr(Arrays.asList(App.res().getStringArray(R.array.seriesOfFarm)));
            farmSeries = Pattern.compile(farmKey);
        }

        @Override
        public String typeName() {
            return "TosCardPack";
        }

        @Override
        public boolean onSelect(List<String> p) {
            if (ListUtil.isEmpty(p)) return true;
            // make [id] to be [card]
            List<TosCard> cards = new ArrayList<>();
            for (int i = 0; i < p.size(); i++) {
                TosCard c = TosWiki.getCardByIdNorm(p.get(i));
                cards.add(c);
            }
            //logE("select %s", p);

            return selectForBasic(cards.get(0))
                    && selectForPathLength(p)
                    && selectForStar(cards)
                    //&& selectForImprove(c)
                    && selectForSpecial(cards, p)
                    && selectForShow(cards, p)
                    ;
        }

        private boolean selectForBasic(TosCard c) {
            List<String> attrs = select.getAttr();
            List<String> races = select.getRace();
            return attrs.contains(c.attribute) && races.contains(c.race);
        }

        // any one of card
        private boolean selectForStar(List<TosCard> cards) {
            List<String> stars = select.getStar();
            for (TosCard c : cards) {
                if (stars.contains("" + c.rarity)) {
                    return true;
                }
            }
            return false;
        }

        private boolean selectForPathLength(List<String> p) {
            if (p == null) {
                return true;
            }
            int z = p.size();
            return wantPathLength.contains("" + z);
        }

        private boolean isFarm(TosCard c) {
            boolean a = farmSeries.matcher(c.series).matches();
            return a;
        }

        private boolean selectForShow(List<TosCard> cs, List<String> ps) {
            TosCard c = cs.get(0);
            boolean accept = true;
            if (sortHideEmpty.isSelected()) {
                int n = cardCount(c.idNorm);
                //noinspection ConstantConditions
                accept &= n > 0;
            }
            if (sortHideShowEmpty.isSelected()) {
                int n = cardCount(c.idNorm);
                accept &= n == 0;
            }
            if (sortHideShowUnowned.isSelected()) {
                int n = cardCount(ps);
                accept &= n == 0;
            }
            if (sortHideNormal.isSelected()) {
                accept &= !isFarm(c);
            }
            if (sortHide7xxx.isSelected()) {
                accept &= !TosCardUtil.isTauFa(c);
            }
            if (sortHide8xxx.isSelected()) {
                accept &= !TosCardUtil.isDisney(c);
            }
            if (sortHide9xxx.isSelected()) {
                accept &= !TosCardUtil.is72Demon(c);
            }
            return accept;
        }

        private boolean isNonSlvMax(TosCard c) {
            if (c != null) {
                PackInfoCard p = myInfoPack.get(c.idNorm);
                if (p != null && p.packs.size() > 0) {
                    PackCard first = p.packs.get(0);
                    int slvMax = c.skillCDMin1 - c.skillCDMax1 + 1;
                    return first.skillLevel < slvMax;
                }
            }
            return false;
        }

        private boolean selectForSpecial(List<TosCard> cards, List<String> p) {
            boolean accept = true;
            if (!sortSpecialNo.isChecked()) {
                //Though repeat, but fast.....
                if (sortSpecialNonSlvMax.isChecked()) {
                    accept &= cardCount(p) > 0 && isNonSlvMax(cards.get(0));
                }
            }
            return accept;
        }
        /*
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
        */


        @NonNull
        @Override
        public List<SelectedData> sort(@NonNull List<SelectedData> result) {
            Comparator<SelectedData> cmp = null;
            cmp = getCommonComparator();
            // Apply the comparator on result
            if (cmp != null) {
                Collections.sort(result, cmp);
            }
            return result;
        }

        @Override
        public String getMessage(List<String> idNorms) {
            //logE("msg of %s", idNorms);
            return getCommonMessage(idNorms);
        }

        private Comparator<SelectedData> getCommonComparator() {
            // Create comparator
            int id = sortCommon.getCheckedRadioButtonId();
            if (id == RadioGroup.NO_ID || id == R.id.sortCommonNormId) {
                return null;
            }
            return (o1, o2) -> {
                int k1 = o1.index;
                int k2 = o2.index;
                boolean dsc = true;
                String s1 = data.get(k1).get(0);
                String s2 = data.get(k2).get(0);
                TosCard c1 = TosWiki.getCardByIdNorm(s1);
                TosCard c2 = TosWiki.getCardByIdNorm(s2);
                long v1 = -1, v2 = -1;
                //logE("c1 = %s", c1);
                //logE("c2 = %s", c2);

                if (id == R.id.sortCommonMaxHP) {
                    v1 = c1.maxHPAme;
                    v2 = c2.maxHPAme;
                } else if (id == R.id.sortCommonMaxAttack) {
                    v1 = c1.maxAttackAme;
                    v2 = c2.maxAttackAme;
                } else if (id == R.id.sortCommonMaxRecovery) {
                    v1 = c1.maxRecoveryAme;
                    v2 = c2.maxRecoveryAme;
                } else if (id == R.id.sortCommonMaxSum) {
                    v1 = c1.maxHPAme + c1.maxAttackAme + c1.maxRecoveryAme;
                    v2 = c2.maxHPAme + c2.maxAttackAme + c2.maxRecoveryAme;
                } else if (id == R.id.sortCommonSkillCDMax) {
                    dsc = false;
                    v1 = normSkillCD(c1);
                    v2 = normSkillCD(c2);
                } else if (id == R.id.sortCommonSpace) {
                    dsc = false;
                    v1 = c1.cost;
                    v2 = c2.cost;
                } else if (id == R.id.sortCommonRace) {
                    dsc = false;
                    v1 = ListUtil.indexOf(commonRace, c1.race);
                    v2 = ListUtil.indexOf(commonRace, c2.race);
                } else if (id == R.id.sortCommonOwnCount) {
                    List<String> ss1 = data.get(k1);
                    List<String> ss2 = data.get(k2);
                    v1 = cardCount(ss1);
                    v2 = cardCount(ss2);
                } else if (id == 0) {
                } else {
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
            logE("%s %s -> %4s + %4s * 3.5 = %7.1f => %s"
                    , prefix, c.idNorm, c.maxAttack, c.maxRecovery
                    , c.maxAttack + c.maxRecovery * 3.5, c.name
            );
        }

        private String getCommonMessage(List<String> cs) {
            // Create Message
            int id = sortCommon.getCheckedRadioButtonId();

            String idNorm = cs.get(0);
            TosCard c = TosWiki.getCardByIdNorm(idNorm);
            String msg = c.idNorm;
            if (id == R.id.sortCommonMaxHP) {
                msg = String.valueOf(c.maxHPAme);
                if (c.ameAddHP()) {
                    msg += "^";
                }
            } else if (id == R.id.sortCommonMaxAttack) {
                msg = String.valueOf(c.maxAttackAme);
                if (c.ameAddAttack()) {
                    msg += "^";
                }
            } else if (id == R.id.sortCommonMaxRecovery) {
                msg = String.valueOf(c.maxRecoveryAme);
                if (c.ameAddRecovery()) {
                    msg += "^";
                }
            } else if (id == R.id.sortCommonMaxSum) {
                msg = String.valueOf(c.maxHPAme + c.maxAttackAme + c.maxRecoveryAme);
                if (c.ameAddAll()) {
                    msg += "^";
                }
            } else if (id == R.id.sortCommonSkillCDMax) {
                msg = "" + c.skillCDMaxAme;
                if (c.ameMinusCD()) {
                    msg += "^";
                }
                if (c.skillCDMax2 > 0) {
                    msg += " & " + c.skillCDMax2;
                }
            } else if (id == R.id.sortCommonSpace) {
                msg = String.valueOf(c.cost);
            } else if (id == R.id.sortCommonRace) {
                String name = c.idNorm;
                if (cardLib.adapter != null) {
                    //name = cardLib.adapter.name(c);
                }
                msg = name + "\n" + c.race;
            } else if (id == R.id.sortDisplayName) {
            } else {
            }
            return msg;
        }
    }

    private void setupAdapter(boolean ok) {
        //-- path
        CardEvolvePathAdapter a = new CardEvolvePathAdapter();
        a.setDataList(evolvePath);
        a.setItemListener(new CardEvolvePathAdapter.ItemListener() {
            @Override
            public void onFiltered(int selected, int total) {
                tosInfo.setText(App.res().getString(R.string.cards_evolution, selected, total));
            }

            // count all cards
            // since the path may overlaps on
            // 木獸機械 長度4
            // 梅杜莎(獸) : 0853 <- 0184 <- 0183 <- 0182
            // 梅杜莎(機) : 1993 <- 0184 <- 0183 <- 0182
            private int getPathCardCount(List<Integer> indices) {
                int sum = 0;
                Set<String> seen = new HashSet<>(); // seen ids
                for (int i = 0; i < indices.size(); i++) {
                    int x = indices.get(i);
                    if (x < evolvePath.size()) {
                        List<String> ids = evolvePath.get(x);
                        for (int j = 0; j < ids.size(); j++) {
                            String id = ids.get(j);
                            if (!seen.contains(id)) {
                                PackInfoCard c = myInfoPack.get(id);
                                if (c != null) {
                                    seen.add(id);
                                    sum += c.packs.size();
                                }
                            }
                        }
                    }
                }
                return sum;
            }

            @Override
            public void onFilteredIndex(List<Integer> indices) {
                int sum = getPathCardCount(indices);
                int n = myPack.size();
                // todo why first time gets sum < n? strange
                tosInfo2.setText(App.res().getString(R.string.cards_selection, sum, n));
            }

            @Override
            public void onClick(List<String> item, CardEvolvePathAdapter.EvolvePathVH holder, int position) {
                logE("item = %s", item);
                //showCardDialog(TosWiki.getCardByIdNorm(item.idNorm));
            }

            @Override
            public void onClickEach(int at, String item, CardEvolvePathAdapter.EvolvePathVH vh, int position) {
                showCardDialog(TosWiki.getCardByIdNorm(item));
            }

            @Override
            public void onPathItem(int at, TosCard c, CardEvolvePathAdapter.CardVH vh) {
                getMessagePathItem(c, vh);
            }

            @Override
            public void onPath(List<String> path, CardEvolvePathAdapter.EvolvePathVH vh) {
                getMessagePath(path, vh);
            }
        });

        cardLib.setViewAdapter(a);

        setInfoTexts();

        if (showHint) {
            int n;
            n = myPack.size();
            showToast(R.string.cards_read, n);
            n = evolvePath.size();
            showToast(R.string.cards_path_read, n);
        }

        // todo
//        if (ok) {
//            howToUse.setVisibility(View.GONE);
//        } else {
//            showUsage();
//        }
        howToUse.setVisibility(View.GONE);
        applySelection();
    }

    private void setInfoTexts() {
        List<PackInfoCard> list = new ArrayList<>(myInfoPack.values());
        int n = myInfoPack.size();
        tosInfo.setText(App.res().getString(R.string.cards_evolution, n, n));
        int n2 = 0;
        for (int i = 0; i < list.size(); i++) {
            n2 += list.get(i).packs.size();
        }
        tosInfo2.setText(App.res().getString(R.string.cards_selection, n2, n2));
    }

    private void logTime(long time) {
        String t = StringUtil.MMSSFFF(time) + "s";
        String s = getString(R.string.cards_pack_ok, t);
        App.showToastShort(s);
        logE(s);
    }

    // count card ids
    private int cardCount(List<String> cs) {
        int n = 0;
        for (String id : cs) {
            n += cardCount(id);
        }
        return n;
    }

    private int cardCount(String id) {
        PackInfoCard p = myInfoPack.get(id);
        return p == null ? 0 : p.packs.size();
    }

    private void getMessagePathItem(TosCard c, CardEvolvePathAdapter.CardVH vh) {
        if (c == null) return;

        String id = c.idNorm;
        StringBuilder msg = new StringBuilder(id);
        int z = cardCount(id);
        msg.append("\n").append(App.me.getString(R.string.cards_n_card, z));

        vh.text.setText(msg);
        vh.text.setVisibility(sortDisplayName.isChecked() ? View.VISIBLE : View.GONE);
        vh.thumb.setImageAlpha(z == 0 ? 0x99 : 0xFF);
    }

    private void getMessagePath(List<String> p, CardEvolvePathAdapter.EvolvePathVH vh) {
        if (!sortDisplayDetail.isChecked()) {
            vh.message.setVisibility(View.GONE);
            return;
        }
        vh.message.setVisibility(View.VISIBLE);

        StringBuilder sb = new StringBuilder(App.me.getString(R.string.cards_filter_skill_max) + " = ");
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < p.size(); i++) {
            String id = p.get(i);
            TosCard c = TosWiki.getCardByIdNorm(id);
            // make string : slv = max1, max2, ...
            int slvMax = 0;
            if (c != null) {
                slvMax = c.skillCDMin1 - c.skillCDMax1 + 1;
            }
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(slvMax);

            // make string : #... = (...), (..), ..
            PackInfoCard pi = myInfoPack.get(id);
            String m = peekPack(pi);
            String mi = App.me.getString(R.string.cards_evolution_info, id, m);

            if (i > 0) {
                s.append("\n");
            }
            s.append(mi);
        }
        sb.append("\n").append(s);

        vh.message.setText(sb);
    }

    private String peekPack(PackInfoCard k) {
        if (k == null || k.packs.size() == 0) {
            return "-";
        }
        // make to be
        // (x1, y1), (x2, y2), (x3, y3), ...
        //         |         |         |     | cut points
        StringBuilder s = new StringBuilder();
        int peek = 3;
        int user = k.packs.size();
        int n = Math.min(peek, user);
        for (int i = 0; i < n; i++) {
            PackCard p = k.packs.get(i);
            if (i > 0) {
                s.append(", ");
            }
            s.append(App.me.getString(R.string.cards_evolution_info_each, p.level, p.skillLevel));
        }
        if (peek < user) {
            s.append(", ...");
        }
        return s.toString();
    }

    // Given (uid, aid), login to get token
    // After we have token, we can send (uid, aid, token) to get PackRes
    private class LoginTokenTask implements RunningTask {
        private final String uid;
        private final String aid;// verify code
        private TokenRes result;

        public LoginTokenTask(String id, String code) {
            uid = id;
            aid = code;
        }

        private Map<String, String> param() {
            Map<String, String> m = new HashMap<>();
            m.put("aid", aid);
            m.put("uid", uid);
            m.put("labels", "{\"serviceType\":\"tosCampaign\"}");
            return m;
        }

        @Override
        public void onPreExecute() {
            result = null;
            showCardsLoading(getString(R.string.fetchNetworkData));
        }

        @Override
        public void doInBackground() {
            if (TextUtils.isEmpty(uid) || TextUtils.isEmpty(aid)) {
                return;
            }

            String link = TOS_REVIEW_LOGIN;
            logE("fetch %s", link);
            TicTac2 t = new TicTac2();
            t.tic();
            String src = OkHttpUtil.getResponse(link, param());
            t.tac("fetched");

            if (TextUtils.isEmpty(src)) {
                return; // fail
            }
            //logE("login src = %s", src);
            result = Gsons.from(src, TokenRes.class);
        }

        @Override
        public void onPostExecute() {
            if (isActivityGone()) return;

            TokenRes tr = result;
            hideCardsLoading();
            AppPref pref = appPref;
            // save to preference and parse pack
            pref.setUserUid(uid);
            pref.setUserVerify(aid);
            if (tr != null) {
                if (tr.isSuccess > 0) {
                    pref.setUserPackToken(tr.token);
                    fetchPack();
                } else {
                    String msg = tr.errorMessage + "\nerrorCode = " + tr.errorCode;
                    new CommonDialog().message(msg).show(getActivity());
                }
            }
        }
    }

    // Given (uid, aid, token) -> get PackList
    private class FetchPackTask implements RunningTask {
        private final String uid;
        private final String aid;
        private final String token;
        private PackRes result;
        private TicTac2 clock = new TicTac2();

        public FetchPackTask(String id, String code, String tokens) {
            uid = id;
            aid = code;
            token = tokens;
        }

        @Override
        public void onPreExecute() {
            showCardsLoading(getString(R.string.cardsLoading));
            clock.tic();
        }

        private Map<String, String> param() {
            Map<String, String> m = new HashMap<>();
            m.put("token", token);
            m.put("aid", aid);
            m.put("uid", uid);
            m.put("includeInventory", "true");
            return m;
        }

        @Override
        public void doInBackground() {
            if (TextUtils.isEmpty(uid) || TextUtils.isEmpty(aid) || TextUtils.isEmpty(token)) {
                return;
            }

            // Fetch response as raw from link
            String link = TOS_REVIEW_PACK;
            logE("fetch %s", link);
            clock.tic();
            String src = OkHttpUtil.getResponse(link, param());
            clock.tac("fetched");
            //logE("source = %s", src);

            // Find inventory and parse to PackRes
            if (TextUtils.isEmpty(src)) {
                return;
            }

            PackRes r = Gsons.from(src, PackRes.class);
            if (r != null) {
                appPref.setUserTosInventory(src);
                listPack(r.card);
            }
            result = r;
        }

        @Override
        public void onPostExecute() {
            if (isActivityGone()) return;

            PackRes r = result;
            boolean ok = r != null;

            long time = clock.tacL();
            logTime(time);
            hideCardsLoading();
            setupAdapter(ok);
        }
    }

    // Parse String to json of PackRes, and setup adapter
    private class ParsePackTask implements RunningTask {
        private final String src;
        private PackRes res;
        public ParsePackTask(String data) {
            src = data;
        }

        @Override
        public void onPreExecute() {
            res = null;
        }

        @Override
        public void doInBackground() {
            PackRes r = Gsons.from(src, PackRes.class);
            if (r != null) {
                listPack(r.card);
            }
            res = r;
        }

        @Override
        public void onPostExecute() {
            setupAdapter(res != null);
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