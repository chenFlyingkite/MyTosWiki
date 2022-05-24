package com.flyingkite.mytoswiki;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.flyingkite.mytoswiki.data.tos.TosCard;
import com.flyingkite.mytoswiki.library.CardEvolvePathAdapter;
import com.flyingkite.mytoswiki.share.ShareHelper;
import com.flyingkite.mytoswiki.tos.TosWiki;
import com.flyingkite.mytoswiki.tos.query.AllCards;
import com.flyingkite.mytoswiki.tos.query.TosCondition;
import com.flyingkite.mytoswiki.util.ToolBarOwner;

import java.util.ArrayList;
import java.util.List;

import flyingkite.library.androidx.recyclerview.Library;

@Deprecated
public class TosCardEvolvePathFragment extends BaseFragment {
    public static final String TAG = "TosCardEvolvePathFragment";
    public interface Owner {
        CharSequence getInfoText();
        void getMessagePathItem(TosCard c, CardEvolvePathAdapter.CardVH vh);
        void getMessagePath(List<String> p, CardEvolvePathAdapter.EvolvePathVH vh);

        boolean acceptNonSlvMax(List<String> path);
    }

    private Library<CardEvolvePathAdapter> library;
    private RecyclerView recycler;
    private List<List<String>> evolvePath;
    private TextView tosInfo;
    private TextView tosInfo2;
    private View menuEntry;

    // Popup Menu tool bar
    private View sortReset;
    // 屬性 種族 星
    private ViewGroup sortAttributes;
    private ViewGroup sortRace;
    private ViewGroup sortStar;
    private ViewGroup sortPathLength;
    // Common Sorting order
    private RadioGroup sortCommon;
    // 特選
    private ViewGroup sortSpecial;
    private CheckBox sortSpecialNo;
    private CheckBox sortSpecialNonSlvMax;
    //private View sortSpecialFast;
    private View sortFastSkill;


    private ToolBarOwner toolOwner;
    private Owner thisOwner;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI();
    }

    private void initUI() {
        initToolIcons();
        initLibrary();
        initSortMenu();
    }

    private void initToolIcons() {
        tosInfo = findViewById(R.id.tosInfo);
        tosInfo2 = findViewById(R.id.tosInfo2);
        logE("parent = %s", getParentFragment());

        findViewById(R.id.tosFavor).setOnClickListener((v) -> {
            toggleSelected(v);
        });

        initScrollTools(R.id.tosGoTop, R.id.tosGoBottom, () -> {
            return recycler;
        });

        // Setup tool bar
        View tool = findViewById(R.id.tosToolBar);
        tool.setOnClickListener((v) -> {
            toggleSelected(v);
            boolean s = v.isSelected();
            if (toolOwner != null) {
                toolOwner.setToolsVisible(s);
            }
        });
        boolean sel = false;
        if (toolOwner != null) {
            sel = toolOwner.isToolsVisible();
        }
        tool.setSelected(sel);
    }

    private void initLibrary() {
        recycler = findViewById(R.id.tosPathRecycler);
        library = new Library<>(recycler, true);

        evolvePath = new ArrayList<>(TosWiki.getEvolvePath());
        if (thisOwner != null) {
            tosInfo.setText(thisOwner.getInfoText());
        } else {
            tosInfo.setText("");
        }
        int n = evolvePath.size();
        tosInfo2.setText(getString(R.string.cards_evolution, n, n));
        //-- path
        CardEvolvePathAdapter a = new CardEvolvePathAdapter();
        //a.setPackInfo(myInfoPack);
        a.setDataList(evolvePath);
        a.setItemListener(new CardEvolvePathAdapter.ItemListener() {
            @Override
            public void onFiltered(int selected, int total) {
                tosInfo.setText(App.res().getString(R.string.cards_selection_kind, selected, total));
            }

            @Override
            public void onClick(List<String> item, CardEvolvePathAdapter.EvolvePathVH holder, int position) {
                logE("item = %s", item);
                //showCardDialog(TosWiki.getCardByIdNorm(item.idNorm));
            }

            @Override
            public void onPathItem(int at, TosCard c, CardEvolvePathAdapter.CardVH vh) {
                if (thisOwner != null) {
                    thisOwner.getMessagePathItem(c, vh);
                }
            }

            @Override
            public void onPath(List<String> path, CardEvolvePathAdapter.EvolvePathVH vh) {
                if (thisOwner != null) {
                    thisOwner.getMessagePath(path, vh);
                }
            }
        });

        library.setViewAdapter(a);
        showToast(R.string.cards_path_read, n);
    }

    public void reloadLibrary() {
        if (library != null && library.adapter != null) {
            int n = library.adapter.getItemCount();
            library.adapter.notifyItemRangeChanged(0, n);
        }
    }

    private void initSortMenu() {
        menuEntry = findViewById(R.id.tosSortMenu);
        View menu = findViewById(R.id.tosFilterMenu);
        View pop = findViewById(R.id.tosFilter);
        pop.setOnClickListener((v) -> {
            pop.setVisibility(View.INVISIBLE);
        });
        menuEntry.setOnClickListener((v) -> {
            pop.setVisibility(View.VISIBLE);
        });

        initMenu(menu);
    }

    private void initMenu(View v) {
        initShareImage(v);
        initSortReset(v);
        initSortByAttribute(v);
        initSortByRace(v);
        initSortByStar(v);
        initSortByPathLength(v);
        initSortBySpecial(v);
    }

    private void initShareImage(View menu) {
        menu.findViewById(R.id.tosSave).setOnClickListener((v) -> {
            View view = recycler;
            String name = ShareHelper.cacheName("1.png");

            ShareHelper.shareImage(getActivity(), view, name);
            //logMenu("shareLibrary");
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

    private void initSortBySpecial(View menu) {
        sortSpecialNo = menu.findViewById(R.id.sortSpecialNo);
        sortSpecialNonSlvMax = menu.findViewById(R.id.sortSpecialNonSlvMax);
//        sortSpecialSkillEatable2 = menu.findViewById(R.id.sortSpecialSkillEatable2);
//        sortSpecialSkillEatable3 = menu.findViewById(R.id.sortSpecialSkillEatable3);
//        sortSpecialSkillEatable4 = menu.findViewById(R.id.sortSpecialSkillEatable4);
//        sortSpecialSkillNotMax = menu.findViewById(R.id.sortSpecialSkillNotMax);
//        sortSpecialSkillEnough = menu.findViewById(R.id.sortSpecialSkillEnough);
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
//            //logMenu("5 星可練技卡");
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

//    private void initSortByHide(View menu) {
//        sortHide = initSortOf(menu, R.id.sortHide, this::clickHide);
//    }
//
//    private void initDisplay(View menu) {
//        sortDisplay = initSortOf(menu, R.id.sortDisplayList, this::clickDisplay);
//        sortDisplay.check(R.id.sortDisplayNormId);
//    }

    private <T extends ViewGroup> T initSortOf(View menu, @IdRes int id, View.OnClickListener childClick) {
        return setTargetChildClick(menu, id, childClick);
    }

    private void resetMenu() {
        ViewGroup[] vgs = {sortAttributes, sortRace, sortStar};
        for (ViewGroup vg : vgs) {
            setAllChildSelected(vg, false);
        }
        setCheckedIncludeNo(sortSpecialNo, R.id.sortSpecialNo, sortSpecial);
//        setCheckedIncludeNo(sortImproveNo, R.id.sortImproveNo, sortImprove);
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
        toggleSelected(v);
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

        if (library.adapter != null) {
            TosCondition cond = new TosCondition().attr(attrs).race(races).star(stars);

            library.adapter.setSelection(new TosSelectCard(evolvePath, cond));
        }
    }

    private void nonAllApply(View v, ViewGroup vg) {
        toggleAndClearIfAll(v, vg);
        applySelection();
    }


    // Actual implementation of TosSelectCard --------
    private class TosSelectCard extends AllCards<List<String>> {
        private final TosCondition select;
        private final List<String> pathLength = new ArrayList<>();

        public TosSelectCard(List<List<String>> source, TosCondition condition) {
            super(source);
            select = condition;
        }

        @Override
        public String typeName() {
            return "TosEvolvePath";
        }

        @Override
        public void onPrepare() {
            // pathLength
            pathLength.clear();
            getSelectTags(sortPathLength, pathLength, false);
        }

        @Override
        public boolean onSelect(List<String> p) {
            TosCard c = TosWiki.getCardByIdNorm(p.get(0));
            int n = p.size();
            boolean meetStar = true;
            List<String> stars = select.getStar();
            if (stars.size() > 0) {
                meetStar = stars.contains("" + c.rarity);
            }

            return selectForBasic(c)
                    && selectForSpecial(p)
                    && meetStar
                    && pathLength.contains("" + n)
                    //&& stars.contains("" + n)
//                    && selectForImprove(c)
//                    && selectForShow(c, p)
                    ;
        }

        private boolean selectForBasic(TosCard c) {
            List<String> attrs = select.getAttr();
            List<String> races = select.getRace();
            return attrs.contains(c.attribute)
                    && races.contains(c.race);
        }


        private boolean selectForSpecial(List<String> p) {
            boolean accept = true;
            if (!sortSpecialNo.isChecked()) {
                //Though repeat, but fast.....
                if (sortSpecialNonSlvMax.isChecked()) {
                    if (thisOwner != null) {
                        accept &= thisOwner.acceptNonSlvMax(p);
                    }
                }
            }
            return accept;
        }
    }
    //---
    @Override
    protected int getPageLayoutId() {
        return R.layout.fragment_tos_card_evolve_path;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ToolBarOwner) {
            toolOwner = (ToolBarOwner) context;
        }

        if (getParentFragment() instanceof Owner) {
            thisOwner = (Owner) getParentFragment();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        toolOwner = null;
    }

}
