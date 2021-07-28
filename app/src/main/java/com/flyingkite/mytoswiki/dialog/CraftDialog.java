package com.flyingkite.mytoswiki.dialog;

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
import com.flyingkite.library.util.MathUtil;
import com.flyingkite.library.widget.Library;
import com.flyingkite.mytoswiki.BuildConfig;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.CraftSort;
import com.flyingkite.mytoswiki.data.tos.BaseCraft;
import com.flyingkite.mytoswiki.data.tos.CraftSkill;
import com.flyingkite.mytoswiki.data.tos.CraftsNormal;
import com.flyingkite.mytoswiki.library.CraftAdapter;
import com.flyingkite.mytoswiki.library.Misc;
import com.flyingkite.mytoswiki.share.ShareHelper;
import com.flyingkite.mytoswiki.tos.TosWiki;
import com.flyingkite.mytoswiki.tos.query.AllCards;
import com.flyingkite.mytoswiki.tos.query.TosCondition;
import com.flyingkite.mytoswiki.util.RegexUtil;
import com.flyingkite.util.TaskMonitor;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import androidx.annotation.ArrayRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import flyingkite.tool.StringUtil;

public class CraftDialog extends BaseTosDialog {
    private Library<CraftAdapter> craftLibrary;
    private List<BaseCraft> allCraft;

    // Gson files
    private CraftSort craftSort = new CraftSort();

    // Popup Menus
    private View sortMenu;
    private PopupWindow sortWindow;
    // Popup Menu tool bar
    private View sortReset;
    // n / n crafts
    private TextView craftInfo;
    // 屬性 種族 星
    private ViewGroup sortMode;
    private ViewGroup sortAttr;
    private ViewGroup sortRace;
    private ViewGroup sortStar;
    private ViewGroup sortHide;
    // 限制屬性種族
    private ViewGroup sortLimit;
    private CheckBox sortLimitAttr;
    private CheckBox sortLimitRace;
    // 技能
    private ViewGroup sortSpecialList;
    private CheckBox sortSpecialNo;
    private CheckBox sortRecoveryInc;
    private CheckBox sortRegardlessAttr;
    private CheckBox sortHpAdd;
    private CheckBox sortRegardPuzzle;
    private CheckBox sortRegardAllAttr;
    private CheckBox sortRegardAttackFirst;
    private CheckBox sortRegardDamageLess;
    private CheckBox sortAllDealDamage;
    private CheckBox sortOneDealDamage;
    private CheckBox sortDodge;
    private CheckBox sortDamageToHp;
    private CheckBox sortDelayCD;
    private CheckBox sortSingleToFull;
    private CheckBox sortNoDefeat;
    private CheckBox sortRegardlessDefense;
    private CheckBox sortExtraAttack;

    private CheckBox sortAttackInc;
    private ViewGroup sortAttackIncCard;
    private CheckBox sortColdDownDec;
    private ViewGroup sortColdDownDecCard;
    private CheckBox sortRunestone;
    private ViewGroup sortRunestoneItem;
    // Common Sorting order
    private RadioGroup sortCommon;
    // Display card name
    private RadioGroup sortDisplay;

    private RecyclerView recycler;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_craft;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logImpression();

        initClose();
        initSortMenu();
        resetMenu();
        recycler = findViewById(R.id.craftRecycler);
        craftLibrary = new Library<>(recycler, 5);
        initScrollTools(R.id.craftGoTop, R.id.craftGoBottom, recycler);

        new LoadDataAsyncTask().executeOnExecutor(sSingle);
        TosWiki.attendDatabaseTasks(onCraftsReady);
    }

    private void initLibrary(List<BaseCraft> list) {
        allCraft = list;
        int n = list.size();
        craftInfo = findViewById(R.id.craftInfo);
        craftInfo.setText(getString(R.string.craft_selection, n, n));
        craftLibrary = new Library<>(recycler, 5);
        CraftAdapter a = new CraftAdapter();
        a.setDataList(list);
        a.setItemListener(new CraftAdapter.ItemListener() {
            @Override
            public void onFiltered(int selected, int total) {
                if (isDetached()) return;

                craftInfo.setText(getString(R.string.craft_selection, selected, n));
            }

            @Override
            public void onClick(BaseCraft craft, CraftAdapter.CraftVH craftVH, int position) {
                CraftItemDialog d = new CraftItemDialog();
                Bundle b = new Bundle();
                b.putParcelable(CraftItemDialog.BUNDLE_CRAFT, craft);

                d.setArguments(b);
                d.show(getActivity());
            }
        });
        craftLibrary.setViewAdapter(a);
        applySelection();
        //countSet();
        //testAllCraftDialog();
    }

    /*
    private void testAllCraftDialog() { // TODO : Hide me when release
        for (int i = 0; i < allCraft.size(); i++) {
            BaseCraft ci = allCraft.get(i);
            sSingle.submit(() -> {
                logE("+ #%s show %s", ci.idNorm, ci.name);
                CraftItemDialog d = showCraftDialog(ci);
                Say.sleepI(500);
                logE("- #%s hide %s", ci.idNorm, ci.name);
                d.dismiss();
            });
        }
    }
    */

    private void countSet(List<BaseCraft> list) {
        //noinspection PointlessBooleanExpression
        if (false == BuildConfig.DEBUG) return;


        Set<String> s = new HashSet<>();
        int n;
        // Count mode
        n = 0;
        s.clear();
        for (BaseCraft c : list) {
            s.add(c.mode);
            n++;
        }
        logE("%s, mode = %s", n, s);

        n = 0;
        s.clear();
        for (BaseCraft c : list) {
            if (c instanceof CraftsNormal) {
                CraftsNormal cn = (CraftsNormal) c;
                s.add(cn.attrLimit);
                n++;
            }
        }
        logE("%s, attrLimit = %s", n, s);

        n = 0;
        s.clear();
        for (BaseCraft c : list) {
            if (c instanceof CraftsNormal) {
                CraftsNormal cn = (CraftsNormal) c;
                s.add(cn.raceLimit);
                n++;
            }
        }
        logE("%s, raceLimit = %s", n, s);
    }

    private void initClose() {
        findViewById(R.id.craftClose).setOnClickListener((v) -> {
            dismissAllowingStateLoss();
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

    private final TaskMonitor.OnTaskState onCraftsReady = new TaskMonitor.OnTaskState() {
        private boolean normalOK = false;
        private boolean armOK = false;
        @Override
        public void onTaskDone(int index, String tag) {
            if (isActivityGone()) return;

            switch (tag) {
                case TosWiki.TAG_ARM_CRAFTS:
                    armOK = true;
                    break;
                case TosWiki.TAG_NORMAL_CRAFTS:
                    normalOK = true;
                    break;
            }
            if (armOK && normalOK) {
                getActivity().runOnUiThread(() -> {
                    BaseCraft[] arm = TosWiki.allArmCrafts();
                    BaseCraft[] normal = TosWiki.allNormalCrafts();
                    List<BaseCraft> li = new ArrayList<>();
                    Collections.addAll(li, normal);
                    Collections.addAll(li, arm);
                    initLibrary(li);
                });
            }

            //log("#%s (%s) is done", index, tag);
        }

        @Override
        public void onAllTaskDone() {
            //log("All is done");
        }
    };

    // Init sorting menus --------
    private void initSortMenu() {
        sortMenu = findViewById(R.id.craftSortMenu);

        // Create MenuWindow
        Pair<View, PopupWindow> pair = createPopupWindow(R.layout.popup_tos_sort_craft, (ViewGroup) getView());
        sortWindow = pair.second;
        View menu = pair.first;

        sortMenu.setOnClickListener(v -> {
            sortWindow.showAsDropDown(v);
            logSelectCraft();
        });

        initShareImage(menu);
        initSortReset(menu);
        initSortByMode(menu);
        initSortByStar(menu);
        initSortByAttribute(menu);
        initSortByRace(menu);
        initSortLimit(menu);
        initSortBySpecial(menu);
        initSortByCommon(menu);
        initSortByHide(menu);
        initDisplay(menu);
    }

    private void initSortReset(View menu) {
        sortReset = menu.findViewById(R.id.sortReset);
        sortReset.setOnClickListener(this::clickReset);
    }

    private void initSortByAttribute(View menu) {
        sortAttr = initSortOf(menu, R.id.sortAttributes, this::clickAttr);
    }

    private void initSortByRace(View menu) {
        sortRace = initSortOf(menu, R.id.sortRaces, this::clickRace);
    }

    private void initSortByStar(View menu) {
        sortStar = initSortOf(menu, R.id.sortStar, this::clickStar);
    }

    private void initSortByMode(View menu) {
        sortMode = initSortOf(menu, R.id.sortModes, this::clickMode);
    }

    private void initSortLimit(View menu) {
        sortLimitAttr = menu.findViewById(R.id.sortAllAttr);
        sortLimitRace = menu.findViewById(R.id.sortAllRace);
        sortLimit = initSortOf(menu, R.id.sortLimitList, this::clickLimit);
    }

    private void initSortByCommon(View menu) {
        sortCommon = initSortOf(menu, R.id.sortCommonList, this::clickCommon);
        sortCommon.check(R.id.sortCommonNormId);
    }

    private void initSortByHide(View menu) {
        sortHide = initSortOf(menu, R.id.sortHide, this::clickHide);
    }

    private void initDisplay(View menu) {
        sortDisplay = initSortOf(menu, R.id.sortDisplayList, this::clickDisplay);
        sortDisplay.check(R.id.sortDisplayName);
    }

    private void initSortBySpecial(View menu) {
        sortSpecialNo = menu.findViewById(R.id.sortSpecialNo);
        sortRegardlessAttr = menu.findViewById(R.id.sortRegardlessAttr);
        sortAttackInc = menu.findViewById(R.id.sortAttackInc);
        sortRecoveryInc = menu.findViewById(R.id.sortRecoveryInc);
        sortColdDownDec = menu.findViewById(R.id.sortColdDownDec);
        sortHpAdd = menu.findViewById(R.id.sortHpAdd);
        sortRegardPuzzle = menu.findViewById(R.id.sortRegardPuzzle);
        sortRegardAllAttr = menu.findViewById(R.id.sortRegardAllAttr);
        sortRegardAttackFirst = menu.findViewById(R.id.sortRegardAttackFirst);
        sortRegardDamageLess = menu.findViewById(R.id.sortRegardDamageLess);
        sortAllDealDamage = menu.findViewById(R.id.sortAllDealDamage);
        sortOneDealDamage = menu.findViewById(R.id.sortOneDealDamage);
        sortDodge = menu.findViewById(R.id.sortDodge);
        sortDamageToHp = menu.findViewById(R.id.sortDamageToHp);
        sortDelayCD = menu.findViewById(R.id.sortDelayCD);
        sortSingleToFull = menu.findViewById(R.id.sortSingleToFull);
        sortNoDefeat = menu.findViewById(R.id.sortNoDefeat);
        sortRegardlessDefense = menu.findViewById(R.id.sortRegardlessDefense);
        sortRunestone = menu.findViewById(R.id.sortRunestone);
        sortExtraAttack = menu.findViewById(R.id.sortExtraAttack);

        sortAttackIncCard = initSortOf(menu, R.id.sortAttackIncCard, this::clickAttackInc);
        sortColdDownDecCard = initSortOf(menu, R.id.sortColdDownDecCard, this::clickColdDownDec);
        sortRunestoneItem = initSortOf(menu, R.id.sortRunestoneItem, this::clickStoneItem);
        sortSpecialList = initSortCheckOf(menu, R.id.sortSpecialList, this::clickSpecial);
    }

    private void initShareImage(View parent) {
        parent.findViewById(R.id.tosSave).setOnClickListener((v) -> {
            View view = craftLibrary.recyclerView;
            String name = ShareHelper.cacheName("1.png");
            shareImage(view, name);
            logShare("library");
        });
    }

    private <T extends ViewGroup> T initSortCheckOf(View menu, @IdRes int id, View.OnClickListener childClick) {
        return setTargetCheckChildClick(menu, id, childClick);
    }

    private <T extends ViewGroup> T initSortOf(View menu, @IdRes int id, View.OnClickListener childClick) {
        return setTargetChildClick(menu, id, childClick);
    }
    // --------

    private void resetMenu() {
        ViewGroup[] vgs = {sortMode, sortAttr, sortRace, sortStar};
        for (ViewGroup vg : vgs) {
            setAllChildSelected(vg, false);
        }
        sortCommon.check(R.id.sortCommonNormId);
        checkSpecialView(sortSpecialNo);
    }

    // click on sort items  --------
    private void clickReset(View v) {
        resetMenu();
        applySelection();
    }

    private void clickMode(View v) {
        nonAllApply(v, sortMode);
    }

    private void clickAttr(View v) {
        nonAllApply(v, sortAttr);
    }

    private void clickRace(View v) {
        nonAllApply(v, sortRace);
    }

    private void clickStar(View v) {
        nonAllApply(v, sortStar);
    }

    private void clickHide(View v) {
        toggleSelected(v);
        applySelection();
    }

    private void checkSpecialView(View v) {
        setCheckedIncludeNo(v, R.id.sortSpecialNo, sortSpecialList);
        clearChildIfParentUnchecked(sortAttackIncCard, sortAttackInc);
        clearChildIfParentUnchecked(sortColdDownDecCard, sortColdDownDec);
        clearChildIfParentUnchecked(sortRunestoneItem, sortRunestone);
    }

    private void clickSpecial(View v) {
        checkSpecialView(v);

        applySelection();
    }

    private void clickAttackInc(View v) {
        toggleSelected(v);
        checkParentIfSelectAnyItem(sortAttackIncCard, sortAttackInc, sortSpecialNo);
        applySelection();
    }

    private void clickColdDownDec(View v) {
        toggleSelected(v);
        checkParentIfSelectAnyItem(sortColdDownDecCard, sortColdDownDec, sortSpecialNo);
        applySelection();
    }

    private void clickStoneItem(View v) {
        toggleSelected(v);
        checkParentIfSelectAnyItem(sortRunestoneItem, sortRunestone, sortSpecialNo);
        applySelection();
    }

    private void clickCommon(View v) {
        int id = v.getId();
        sortCommon.check(id);

        applySelection();
    }

    private void clickDisplay(View v) {
        sortDisplay.check(v.getId());

        int type = Misc.NT_NAME;
        int id = v.getId();
        if (id == R.id.sortDisplayNormId) {
            type = Misc.NT_ID_NORM;
        } else if (id == 0) {
        } else {
        }
        if (craftLibrary.adapter != null) {
            craftLibrary.adapter.setNameType(type);
            craftLibrary.adapter.updateSelection();
        }
    }

    private void clickLimit(View v) {
        applySelection();
    }
    // --------

    // Apply to adapter --------
    private void nonAllApply(View v, ViewGroup vg) {
        toggleAndClearIfAll(v, vg);

        applySelection();
    }

    private void applySelection() {
        // Mode
        List<String> modes = new ArrayList<>();
        getSelectTags(sortMode, modes, true);
        // Attribute
        List<String> attrs = new ArrayList<>();
        getSelectTags(sortAttr, attrs, true);
        // Race
        List<String> races = new ArrayList<>();
        getSelectTags(sortRace, races, true);
        // Star
        List<String> stars = new ArrayList<>();
        getSelectTags(sortStar, stars, true);

        logE("----- Craft ----");
        logE("M = %s", modes);
        logE("A = %s", attrs);
        logE("R = %s", races);
        logE("S = %s", stars);

        if (craftLibrary.adapter != null) {
            TosCondition cond = new TosCondition().mode(modes).attr(attrs).race(races).star(stars);
            craftLibrary.adapter.setSelection(new TosSelectCraft(allCraft, cond));
        }
    }
    // --------

    private void updateHide() {
        sortHide.findViewById(R.id.sortHideCraft0xxx).setSelected(craftSort.hideCraft0xxx);
        sortHide.findViewById(R.id.sortHideCraft2xxx).setSelected(craftSort.hideCraft2xxx);
        sortHide.findViewById(R.id.sortHideCraft3xxx).setSelected(craftSort.hideCraft3xxx);
        int id = craftSort.displayById ? R.id.sortDisplayNormId : R.id.sortDisplayName;
        clickDisplay(sortDisplay.findViewById(id));
        if (craftLibrary.adapter != null) {
            craftLibrary.adapter.updateSelection();
        }
    }

    // Saving preference as Gson ----
    private void toGsonHide() {
        craftSort.hideCraft0xxx = sortHide.findViewById(R.id.sortHideCraft0xxx).isSelected();
        craftSort.hideCraft2xxx = sortHide.findViewById(R.id.sortHideCraft2xxx).isSelected();
        craftSort.hideCraft3xxx = sortHide.findViewById(R.id.sortHideCraft3xxx).isSelected();
        craftSort.displayById = sortDisplay.getCheckedRadioButtonId() == R.id.sortDisplayNormId;
        sSingle.submit(() -> {
            GsonUtil.writeFile(getTosCardSortFile(), new Gson().toJson(craftSort));
        });
    }

    private File getTosCardSortFile() {
        return ShareHelper.extFilesFile("craftSort.txt");
    }
    // ------

    // Sort implementation of TosSelectCraft to adapter
    private class TosSelectCraft extends AllCards<BaseCraft> {
        private final String[] commonMode = getResources().getStringArray(R.array.craft_common_keys_mode);
        private final String[] commonAttr = getResources().getStringArray(R.array.craft_common_keys_attr);
        private final String[] commonRace = getResources().getStringArray(R.array.craft_common_keys_race);
        private final String noLimit = getString(R.string.craft_no_limit);
        private final TosCondition select;
        private Pattern attackIncRegex;
        private Pattern coldDownDecRegex;
        private Pattern runestoneRegex;

        public TosSelectCraft(List<BaseCraft> list, TosCondition condition) {
            super(list);
            select = condition;
        }

        @Override
        public void onPrepare() {
            List<String> card = new ArrayList<>();
            // attack increase
            card.clear();
            if (sortAttackInc.isChecked()) {
                getSelectTags(sortAttackIncCard, card, false);
                String r = sortAttackInc.getText().toString();
                if (!card.isEmpty()) {
                    card = addAttr(card);
                    r = RegexUtil.toRegexOr(card) + "成員的" + r;
                }
                attackIncRegex = Pattern.compile(r);
            } else {
                attackIncRegex = null;
            }

            // Cold down decrease
            card.clear();
            if (sortColdDownDec.isChecked()) {
                String r = sortColdDownDec.getText().toString();
                getSelectTags(sortColdDownDecCard, card, false);
                if (!card.isEmpty()) {
                    card = addAttr(card);
                    r = RegexUtil.toRegexOr(card) + "成員的" + r;
                }
                coldDownDecRegex = Pattern.compile(r);
            } else {
                coldDownDecRegex = null;
            }

            // turn into runestone
            card.clear();
            if (sortRunestone.isChecked()) {
                //String r = sortColdDownDec.getText().toString();
                getSelectTags(sortRunestoneItem, card, false);
                String r = "符石轉化為";
                if (!card.isEmpty()) {
                    card = addEnchanted(card);
                    r = r + RegexUtil.toRegexOr(card) + "符石";
                }
                runestoneRegex = Pattern.compile(r);
            } else {
                runestoneRegex = null;
            }

            logE("FindCraft A %s", attackIncRegex);
            logE("FindCraft C %s", coldDownDecRegex);
            logE("FindCraft R %s", runestoneRegex);
        }

        @Override
        public String typeName() {
            return "BaseCraft";
        }

        @Override
        public boolean onSelect(BaseCraft c) {
            return selectForBasic(c)
                    && selectForShow(c)
                    && selectForSpecial(c)
            ;
        }

        private boolean selectForSpecial(BaseCraft c) {
            String key = keyOfSpecial(c);
            boolean accept = true;
            if (!sortSpecialNo.isChecked()) {
                if (sortRegardlessAttr.isChecked()) {
                    accept &= find(key, R.array.craft_regardless_of_attribute_keys);
                }
                if (sortAttackInc.isChecked()) {
                    accept &= findRegexKey(key, R.array.craft_attack_increase_keys, attackIncRegex);
                }
                if (sortRecoveryInc.isChecked()) {
                    accept &= find(key, R.array.craft_recovery_increase_keys);
                }
                if (sortColdDownDec.isChecked()) {
                    accept &= findRegexKey(key, R.array.craft_cd_decrease_keys, coldDownDecRegex);
                }
                if (sortHpAdd.isChecked()) {
                    accept &= find(key, R.array.craft_hp_add_keys);
                }
                if (sortRegardPuzzle.isChecked()) {
                    accept &= find(key, R.array.craft_regard_puzzle_keys);
                }
                if (sortRegardAllAttr.isChecked()) {
                    accept &= find(key, R.array.craft_regard_all_attr_keys);
                }
                if (sortRegardAttackFirst.isChecked()) {
                    accept &= find(key, R.array.craft_regard_attack_first_keys);
                }
                if (sortRegardDamageLess.isChecked()) {
                    accept &= find(key, R.array.craft_damage_less_keys);
                }
                if (sortAllDealDamage.isChecked()) {
                    accept &= find(key, R.array.craft_all_deal_damage_keys);
                }
                if (sortOneDealDamage.isChecked()) {
                    accept &= find(key, R.array.craft_one_deal_damage_keys);
                }
                if (sortExtraAttack.isChecked()) {
                    accept &= find(key, R.array.cards_extra_attack_keys);
                }
                if (sortDodge.isChecked()) {
                    accept &= find(key, R.array.craft_dodge_keys);
                }
                if (sortDamageToHp.isChecked()) {
                    accept &= find(key, R.array.craft_damage_to_hp_keys);
                }
                if (sortDelayCD.isChecked()) {
                    accept &= find(key, R.array.craft_delay_cd_keys);
                }
                if (sortSingleToFull.isChecked()) {
                    accept &= find(key, R.array.craft_single_to_full_keys);
                }
                if (sortNoDefeat.isChecked()) {
                    accept &= find(key, R.array.cards_no_defeat_keys);
                }
                if (sortRegardlessDefense.isChecked()) {
                    accept &= find(key, R.array.craft_regardless_of_defense_keys);
                }
                if (sortRunestone.isChecked()) {
                    accept &= findRegexKey(key, R.array.craft_turn_runestone_keys, runestoneRegex);
                }
            }

            return accept;
        }

        private boolean findRegexKey(String key, @ArrayRes int dataId, Pattern regex) {
            if (regex != null) {
                return regex.matcher(key).find();
            } else {
                return find(key, dataId);
            }
        }

        private String keyOfSpecial(BaseCraft c) {
            StringBuilder s = new StringBuilder();
            for (CraftSkill cs : c.craftSkill) {
                if (s.length() > 0) {
                    s.append(" & ");
                }
                s.append(cs.detail);
            }
            return s.toString();
        }

        private List<String> addAttr(List<String> list) {
            List<String> all = new ArrayList<>(list);
            for (int i = 0; i < list.size(); i++) {
                String s = list.get(i);
                if (s.matches("(水|火|木|光|暗)")) {
                    all.add(i, s + "屬性");
                }
            }
            return all;
        }

        // Similar with addAttr
        private List<String> addEnchanted(List<String> list) {
            List<String> all = new ArrayList<>(list);
            for (int i = 0; i < list.size(); i++) {
                String s = list.get(i);
                if (s.matches("(水|火|木|光|暗|心)")) {
                    all.add(i, s + "強化");
                }
            }
            return all;
        }

        private boolean selectForBasic(BaseCraft c) {
            List<String> attrs = select.getAttr();
            List<String> races = select.getRace();
            List<String> stars = select.getStar();
            List<String> modes = select.getMode();

            boolean star = stars.contains("" + c.rarity);
            boolean mode = find(c.mode, modes);

            // Apply attribute & race
            boolean race = true;
            boolean attr = true;
            if (c instanceof CraftsNormal) {
                CraftsNormal cn = (CraftsNormal) c;
                // Attribute
                attr = findNoLimit(cn.attrLimit, attrs, sortLimitAttr);
                //Race
                race = findNoLimit(cn.raceLimit, races, sortLimitRace);
            }
            return mode && attr && race && star;
        }

        @NonNull
        @Override
        public List<Integer> sort(@NonNull List<Integer> result) {
            Comparator<Integer> cmp;
            cmp = getCommonComparator();
            if (cmp == null) {
                //cmp = getCassandraComparator();
            }

            // Apply the comparator on result
            if (cmp != null) {
                Collections.sort(result, cmp);
            }
            return result;
        }

        private Comparator<Integer> getCommonComparator() {
            // Create comparator
            int id = sortCommon.getCheckedRadioButtonId();
            if (id == RadioGroup.NO_ID || id == R.id.sortCommonNormId) {
                return null;
            }
            return (o1, o2) -> {
                boolean dsc = false;
                BaseCraft c1 = data.get(o1);
                BaseCraft c2 = data.get(o2);
                long v1 = -1, v2 = -1;

                if (id == R.id.sortCommonMode) {
                    v1 = asCompareIndex(modeN(c1), starN(c1), attrN(c1), raceN(c1));
                    v2 = asCompareIndex(modeN(c2), starN(c2), attrN(c2), raceN(c2));

                } else if (id == R.id.sortCommonAttr) {
                    v1 = asCompareIndex(attrN(c1), modeN(c1), starN(c1), raceN(c1));
                    v2 = asCompareIndex(attrN(c2), modeN(c2), starN(c2), raceN(c2));

                } else if (id == R.id.sortCommonRace) {
                    v1 = asCompareIndex(raceN(c1), modeN(c1), starN(c1), attrN(c1));
                    v2 = asCompareIndex(raceN(c2), modeN(c2), starN(c2), attrN(c2));
                } else if (id == 0) {
                } else {
                }
                //logE("v1 = %s -> %s", v1, c1);
                //logE("v2 = %s -> %s", v2, c2);

                if (dsc) {
                    return Long.compare(v2, v1);
                } else {
                    return Long.compare(v1, v2);
                }
            };
        }

        private boolean find(String key, @ArrayRes int dataId) {
            String[] data = getResources().getStringArray(dataId);
            return find(key, data);
        }

        private boolean find(String key, String[] data) {
            return StringUtil.containsAt(key, data) >= 0;
        }

        //---- Normalize all the compare attribute to be int
        private int asCompareIndex(int... order) {
            int n = order.length;
            int radix = 100;
            int sum = 0;
            for (int i = 0; i < n; i++) {
                sum = sum * radix + order[i];
            }
            return sum;
        }

        private int modeN(BaseCraft c) {
            return 1 + containsAt(c.mode, commonMode); // Make to fall in [0, len]
        }

        private int starN(BaseCraft c) {
            return c.rarity;
        }

        private int attrN(BaseCraft c) {
            String[] keys = commonAttr;
            int a = keys.length + 1;
            if (c instanceof CraftsNormal) {
                CraftsNormal cn = (CraftsNormal) c;
                a = containsAt(cn.attrLimit, keys);
                // Not found in list
                if (a < 0) {
                    a = keys.length;
                }
            }
            return a;
        }

        private int raceN(BaseCraft c) {
            String[] keys = commonRace;
            int a = keys.length + 1;
            if (c instanceof CraftsNormal) {
                CraftsNormal cn = (CraftsNormal) c;
                a = containsAt(cn.raceLimit, keys);
                // Not found in list
                if (a < 0) {
                    a = keys.length;
                }
            }
            return a;
        }
        //------

        private boolean selectForShow(BaseCraft c) {
            int idNorm = Integer.parseInt(c.idNorm);
            boolean accept = true;

            ViewGroup vg = sortHide;
            int n = vg.getChildCount();
            for (int i = 0; i < n; i++) {
                View v = vg.getChildAt(i);
                if (v.isSelected()) {
                    int id = v.getId();
                    if (id == R.id.sortHideCraft0xxx) {
                        accept &= !MathUtil.isInRange(idNorm,    0, 1000);
                    } else if (id == R.id.sortHideCraft2xxx) {
                        accept &= !MathUtil.isInRange(idNorm, 2000, 3000);
                    } else if (id == R.id.sortHideCraft3xxx) {
                        accept &= !MathUtil.isInRange(idNorm, 3000, 4000);
                    } else {
                    }
                }
            }
            return accept;
        }

        private boolean findNoLimit(String key, List<String> data, CheckBox box) {
            boolean ans = find(key, data);
            if (box.isChecked()) {
                ans |= noLimit.equals(key); // = "沒有限制"
            }
            return ans;
        }

        private boolean find(String key, List<String> data) {
            return containsAt(key, data) >= 0;
        }

        private int containsAt(String key, List<String> data) {
            return flyingkite.tool.StringUtil.containsAt(key, data);
        }

        private int containsAt(String key, String[] data) {
            return flyingkite.tool.StringUtil.containsAt(key, data);
        }

    }
    // --------

    private class LoadDataAsyncTask extends AsyncTask<Void, Void, CraftSort> {
        @Override
        protected CraftSort doInBackground(Void... voids) {
            File f = getTosCardSortFile();
            if (f.exists()) {
                return GsonUtil.loadFile(getTosCardSortFile(), CraftSort.class);
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(CraftSort data) {
            craftSort = data != null ? data : new CraftSort();
            updateHide();
            applySelection();
        }
    }

    //-- Events
    private void logShare(String type) {
        Map<String, String> m = new HashMap<>();
        m.put("share", type);
        FabricAnswers.logCraftDialog(m);
    }

    @Override
    public void onToolScrollToPosition(RecyclerView rv, int position) {
        String s = position == 0 ? "Scroll Head" : "Scroll Tail";
        logCraftDialog(s);
    }

    private void logCraftDialog(String act) {
        Map<String, String> m = new HashMap<>();
        m.put("action", act);
        FabricAnswers.logCraftDialog(m);
    }

    private void logSelectCraft() {
        Map<String, String> m = new HashMap<>();
        m.put("craft", "1");
        FabricAnswers.logSelectCraft(m);
    }

    private void logImpression() {
        Map<String, String> m = new HashMap<>();
        m.put("impression", "1");
        FabricAnswers.logCraftDialog(m);
    }
    //-- Events
}
