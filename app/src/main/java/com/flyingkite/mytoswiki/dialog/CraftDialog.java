package com.flyingkite.mytoswiki.dialog;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.flyingkite.mytoswiki.data.tos.CraftsNormal;
import com.flyingkite.mytoswiki.library.CraftAdapter;
import com.flyingkite.mytoswiki.library.Misc;
import com.flyingkite.mytoswiki.share.ShareHelper;
import com.flyingkite.mytoswiki.tos.TosWiki;
import com.flyingkite.mytoswiki.tos.query.AllCards;
import com.flyingkite.mytoswiki.tos.query.TosCondition;
import com.flyingkite.util.TaskMonitor;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    // Common Sorting order
    private RadioGroup sortCommon;
    // Display card name
    private RadioGroup sortDisplay;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_craft;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logShowCraft();

        initClose();
        initSortMenu();
        initScrollTools(R.id.craftGoTop, R.id.craftGoBottom, findViewById(R.id.craftRecycler));

        new LoadDataAsyncTask().executeOnExecutor(sSingle);
        TosWiki.attendDatabaseTasks(onCraftsReady);
    }

    private void initLibrary(List<BaseCraft> list) {
        allCraft = list;
        int n = list.size();
        craftInfo = findViewById(R.id.craftInfo);
        craftInfo.setText(getString(R.string.craft_selection, n, n));
        craftLibrary = new Library<>(findViewById(R.id.craftRecycler), 5);
        CraftAdapter a = new CraftAdapter();
        a.setDataList(list);
        a.setItemListener(new CraftAdapter.ItemListener() {
            @Override
            public void onFiltered(int selected, int total) {
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

        countSet(list);
    }

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

    private TaskMonitor.OnTaskState onCraftsReady = new TaskMonitor.OnTaskState() {
        private boolean normalOK = false;
        private boolean armOK = false;
        @Override
        public void onTaskDone(int index, String tag) {
            switch (tag) {
                case TosWiki.TAG_ARM_CRAFTS:
                    armOK = true;
                    break;
                case TosWiki.TAG_NORMAL_CRAFTS:
                    normalOK = true;
                    break;
            }
            if (armOK && normalOK) {
                runOnUiThread(() -> {
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
        initSortByAttribute(menu);
        initSortByRace(menu);
        initSortLimit(menu);
        initSortByStar(menu);
        initSortByHide(menu);
        initSortByCommon(menu);
        initDisplay(menu);
    }

    private void logSelectCraft() {
        //Map<String, String> m = new HashMap<>();
        //String id = craft == null ? "--" : craft.idNorm;
        //m.put("craft", id);
        FabricAnswers.logSelectCraft(null);
    }

    private void logShowCraft() {
        //Map<String, String> m = new HashMap<>();
        FabricAnswers.logCraftDialog(null);
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
    }

    private void initSortByHide(View menu) {
        sortHide = initSortOf(menu, R.id.sortHide, this::clickHide);
    }

    private void initDisplay(View menu) {
        sortDisplay = initSortOf(menu, R.id.sortDisplayList, this::clickDisplay);
        sortDisplay.check(R.id.sortDisplayName);
    }

    private void initShareImage(View parent) {
        parent.findViewById(R.id.tosSave).setOnClickListener((v) -> {
            View view = craftLibrary.recyclerView;
            String name = ShareHelper.cacheName("1.png");
            ShareHelper.shareImage(getActivity(), view, name);
        });
    }

    private <T extends ViewGroup> T initSortOf(View menu, @IdRes int id, View.OnClickListener childClick) {
        return setTargetChildChick(menu, id, childClick);
    }
    // --------

//    private void addCraftFragment() {
//        TosCraftFragment f = new TosCraftFragment();
//
//        FragmentManager fm = null;//getFragmentManager();
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            fm = getChildFragmentManager();
//            FragmentTransaction fx = fm.beginTransaction();
//            fx.replace(R.id.craftFragment, f, TosCraftFragment.TAG);
//            fx.commitAllowingStateLoss();
//
//            fm.executePendingTransactions();
//        }
//    }

    // click on sort items  --------
    private void clickReset(View v) {
        ViewGroup[] vgs = {sortMode, sortAttr, sortRace, sortStar};
        for (ViewGroup vg : vgs) {
            setAllChildrenSelected(vg, false);
        }
        sortCommon.check(R.id.sortCommonNormId);
//        sortCassandra.check(R.id.sortCassandraNo);
//        setCheckedIncludeNo(sortSpecialNo, R.id.sortSpecialNo, sortSpecial);
//        setCheckedIncludeNo(sortImproveNo, R.id.sortImproveNo, sortImprove);

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
        v.setSelected(!v.isSelected());
        applySelection();
    }

    private void clickCommon(View v) {
        int id = v.getId();
        sortCommon.check(id);
//        if (id != R.id.sortCommonNormId) {
//            sortCassandra.check(R.id.sortCassandraNo);
//        }
//        sortCassandra.setEnabled(id != R.id.sortCassandraNo);

        applySelection();
    }

    private void clickDisplay(View v) {
        sortDisplay.check(v.getId());

        int type = Misc.NT_NAME;
        switch (v.getId()) {
            case R.id.sortDisplayNormId:
                type = Misc.NT_ID_NORM;
                break;
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
        toggleSelection(v, vg);

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
        private TosCondition select;

        public TosSelectCraft(List<BaseCraft> list, TosCondition condition) {
            super(list);
            select = condition;
        }

        @Override
        public String typeName() {
            return "BaseCraft";
        }

        @Override
        public boolean onSelect(BaseCraft c) {
            return selectForBasic(c)
                    && selectForShow(c)
            ;
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

                switch (id) {
                    case R.id.sortCommonMode:
                        v1 = asCompareIndex(modeN(c1), starN(c1), attrN(c1), raceN(c1));
                        v2 = asCompareIndex(modeN(c2), starN(c2), attrN(c2), raceN(c2));
                        break;
                    case R.id.sortCommonAttr:
                        v1 = asCompareIndex(attrN(c1), modeN(c1), starN(c1), raceN(c1));
                        v2 = asCompareIndex(attrN(c2), modeN(c2), starN(c2), raceN(c2));
                        break;
                    case R.id.sortCommonRace:
                        v1 = asCompareIndex(raceN(c1), modeN(c1), starN(c1), attrN(c1));
                        v2 = asCompareIndex(raceN(c2), modeN(c2), starN(c2), attrN(c2));
                        break;
                        /*
                    case R.id.sortCommonMaxSum:
                        v1 = c1.maxHP + c1.maxAttack + c1.maxRecovery;
                        v2 = c2.maxHP + c2.maxAttack + c2.maxRecovery;
                        break;
                    case R.id.sortCommonRace:
                        dsc = false;
                        v1 = ListUtil.indexOf(commonRace, c1.race);
                        v2 = ListUtil.indexOf(commonRace, c2.race);
                        break;
                        */
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
                    switch (v.getId()) {
                        case R.id.sortHideCraft0xxx:
                            accept &= !MathUtil.isInRange(idNorm,    0, 1000);
                            break;
                        case R.id.sortHideCraft2xxx:
                            accept &= !MathUtil.isInRange(idNorm, 2000, 3000);
                            break;
                        case R.id.sortHideCraft3xxx:
                            accept &= !MathUtil.isInRange(idNorm, 3000, 4000);
                            break;
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
}
