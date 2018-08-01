package com.flyingkite.mytoswiki.dialog;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.flyingkite.library.util.GsonUtil;
import com.flyingkite.library.util.MathUtil;
import com.flyingkite.library.widget.Library;
import com.flyingkite.mytoswiki.BuildConfig;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.CraftSort;
import com.flyingkite.mytoswiki.data.tos.BaseCraft;
import com.flyingkite.mytoswiki.data.tos.CraftsNormal;
import com.flyingkite.mytoswiki.library.CraftAdapter;
import com.flyingkite.mytoswiki.share.ShareHelper;
import com.flyingkite.mytoswiki.tos.TosWiki;
import com.flyingkite.mytoswiki.tos.query.AllCards;
import com.flyingkite.mytoswiki.tos.query.TosCondition;
import com.flyingkite.util.TaskMonitor;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
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
    private ViewGroup sortAttribute;
    private ViewGroup sortRace;
    private ViewGroup sortStar;
    private ViewGroup sortHide;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_craft;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

    //region Init sorting menus
    private void initSortMenu() {
        sortMenu = findViewById(R.id.craftSortMenu);

        // Create MenuWindow
        Pair<View, PopupWindow> pair = createPopupWindow(R.layout.popup_tos_sort_craft, (ViewGroup) getView());
        sortWindow = pair.second;
        View menu = pair.first;

        sortMenu.setOnClickListener(v -> {
            sortWindow.showAsDropDown(v);
        });

        initShareImage(menu);
        initSortReset(menu);
        initSortByMode(menu);
        initSortByAttribute(menu);
        initSortByRace(menu);
        initSortByStar(menu);
        initSortByHide(menu);

    }

    private void initSortReset(View menu) {
        sortReset = menu.findViewById(R.id.sortReset);
        sortReset.setOnClickListener(this::clickReset);
    }

    private void initSortByAttribute(View menu) {
        sortAttribute = initSortOf(menu, R.id.sortAttributes, this::clickAttr);
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

    private void initSortByHide(View menu) {
        sortHide = initSortOf(menu, R.id.sortHide, this::clickHide);
    }

    private void initShareImage(View parent) {
        parent.findViewById(R.id.tosSave).setOnClickListener((v) -> {
            View view = craftLibrary.recyclerView;
            String name = ShareHelper.cacheName("1.png");
            ShareHelper.shareImage(getActivity(), view, name);
        });
    }

    private <T extends ViewGroup> T initSortOf(View menu, @IdRes int id, View.OnClickListener childClick) {
        T vg = menu.findViewById(id);
        setChildClick(vg, childClick);
        return vg;
    }
    //endregion

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

    //region click on sort items
    private void clickReset(View v) {
        ViewGroup[] vgs = {sortMode, sortAttribute, sortRace, sortStar};
        for (ViewGroup vg : vgs) {
            setAllChildrenSelected(vg, false);
        }
//        sortCommon.check(R.id.sortCommonNo);
//        sortCassandra.check(R.id.sortCassandraNo);
//        setCheckedIncludeNo(sortSpecialNo, R.id.sortSpecialNo, sortSpecial);
//        setCheckedIncludeNo(sortImproveNo, R.id.sortImproveNo, sortImprove);

        applySelection();
    }

    private void clickMode(View v) {
        nonAllApply(v, sortMode);
    }

    private void clickAttr(View v) {
        nonAllApply(v, sortAttribute);
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
    //endregion

    //region
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
        getSelectTags(sortAttribute, attrs, true);
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
    //endregion

    private void updateHide() {
        sortHide.findViewById(R.id.sortHideCraft0xxx).setSelected(craftSort.hideCraft0xxx);
        sortHide.findViewById(R.id.sortHideCraft2xxx).setSelected(craftSort.hideCraft2xxx);
        sortHide.findViewById(R.id.sortHideCraft3xxx).setSelected(craftSort.hideCraft3xxx);
        //int id = craftSort.displayByName ? R.id.sortDisplayName : R.id.sortDisplayNormId;
        //clickDisplay(sortDisplay.findViewById(id));
        if (craftLibrary.adapter != null) {
            craftLibrary.adapter.updateSelection();
        }
    }

    //region Saving preference as Gson
    private void toGsonHide() {
        craftSort.hideCraft0xxx = sortHide.findViewById(R.id.sortHideCraft0xxx).isSelected();
        craftSort.hideCraft2xxx = sortHide.findViewById(R.id.sortHideCraft2xxx).isSelected();
        craftSort.hideCraft3xxx = sortHide.findViewById(R.id.sortHideCraft3xxx).isSelected();
        //craftSort.displayByName = sortDisplay.getCheckedRadioButtonId() == R.id.sortDisplayName;
        sSingle.submit(() -> {
            GsonUtil.writeFile(getTosCardSortFile(), new Gson().toJson(craftSort));
        });
    }

    private File getTosCardSortFile() {
        return ShareHelper.extFilesFile("craftSort.txt");
    }
    //endregion

    // Sort implementation of TosSelectCraft to adapter
    private class TosSelectCraft extends AllCards<BaseCraft> {
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
                attr = "沒有限制".equals(cn.attrLimit) || find(cn.attrLimit, attrs);
                race = "沒有限制".equals(cn.raceLimit) || find(cn.raceLimit, races);
            }
            return mode && attr && race && star;
        }

        private boolean find(String key, List<String> data) {
            return containsAt(key, data) >= 0;
        }

        private int containsAt(String key, List<String> data) {
            return flyingkite.tool.StringUtil.containsAt(key, data);
        }

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
    }
    //endregion

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
