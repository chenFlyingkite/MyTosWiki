package com.flyingkite.mytoswiki;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import androidx.viewpager.widget.ViewPager;

import com.flyingkite.mytoswiki.data.WebPin;
import com.flyingkite.mytoswiki.dialog.AboutDialog;
import com.flyingkite.mytoswiki.dialog.CardSealDialog;
import com.flyingkite.mytoswiki.dialog.CraftDialog;
import com.flyingkite.mytoswiki.dialog.DailyStageDialog;
import com.flyingkite.mytoswiki.dialog.FarmPoolDialog;
import com.flyingkite.mytoswiki.dialog.FeedbackDialog;
import com.flyingkite.mytoswiki.dialog.FixedStoneDialog;
import com.flyingkite.mytoswiki.dialog.FreeMoveSampleDialog;
import com.flyingkite.mytoswiki.dialog.HelpDialog;
import com.flyingkite.mytoswiki.dialog.MainStageDialog;
import com.flyingkite.mytoswiki.dialog.MonsterLevelDialog;
import com.flyingkite.mytoswiki.dialog.RealmStageDialog;
import com.flyingkite.mytoswiki.dialog.RelicStageDialog;
import com.flyingkite.mytoswiki.dialog.RunestoneEditorDialog;
import com.flyingkite.mytoswiki.dialog.SkillEatingDialog;
import com.flyingkite.mytoswiki.dialog.StageMemoDialog;
import com.flyingkite.mytoswiki.dialog.StaminaDialog;
import com.flyingkite.mytoswiki.dialog.StoryStageDialog;
import com.flyingkite.mytoswiki.dialog.SummonerLevelDialog;
import com.flyingkite.mytoswiki.dialog.TosEventDialog;
import com.flyingkite.mytoswiki.dialog.UltimateStageDialog;
import com.flyingkite.mytoswiki.dialog.WebDialog;
import com.flyingkite.mytoswiki.library.IconAdapter;
import com.flyingkite.mytoswiki.tos.TosWiki;
import com.flyingkite.mytoswiki.util.PageUtil;
import com.flyingkite.mytoswiki.util.ToolBarOwner;
import com.flyingkite.util.PGAdapter;
import com.flyingkite.util.WaitingDialog;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import flyingkite.library.android.util.BackPage;
import flyingkite.library.androidx.recyclerview.Library;
import flyingkite.library.java.tool.TaskMonitor;
import flyingkite.library.java.util.MathUtil;

public class MainActivity extends BaseActivity implements
        ToolBarOwner,
        WebDialog.OnWebAction,
        PageUtil
{
    // Provide alternative bitmaps
    // https://developer.android.com/training/multiscreen/screendensities
    private final List<Integer> tools = Arrays.asList(R.drawable.card_0617
            , R.drawable.logo_chrome
            //, R.drawable.rune_water
            , R.drawable.tos_app
            , R.drawable.gift_stamina
            , R.drawable.tos_enochian
            , R.drawable.card_0674
            , R.drawable.shop_card
            , R.drawable.s_i047
            , R.drawable.tos_lost_relic_pass
            , R.drawable.tos_void_realm
            , R.drawable.tos_story
            , R.drawable.tos_vestige
            //, R.drawable.card_1777
            , R.drawable.card_0096
            , R.drawable.card_1429
            , R.drawable.card_1089
            , R.drawable.q1
            , R.drawable.owl2
            , R.mipmap.app_icon
            , R.drawable.ic_send_black_48dp
            , R.drawable.logo_craft_1
            , R.drawable.logo_stamina
            , R.drawable.exp_eat
    );
    private Library<IconAdapter> iconLibrary;
    private ViewPager cardPager;
    private boolean pagerOK = false;
    private final List<String> homePagerTags = new ArrayList<>();

    private WaitingDialog waiting;
    private AppPref appPref = new AppPref();

    @Override
    protected boolean clearCacheWhenOnDestroy() {
        Date now = new Date();
        int min = now.getMinutes();
        boolean clean = MathUtil.isInRange(min, 30, 35);
        logE("clean = %s, min = %s, now = %s", clean, min, now);
        return clean;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolIcons();
        //addTosFragment();
        showCardsLoading();
        TosWiki.attendDatabaseTasks(onDatabaseState);
    }

    private void initPager() {
        if (pagerOK) return;
        pagerOK = true;
        cardPager = findViewById(R.id.cardPager);
        ViewPager p = cardPager;
        List<String> data = homePagerTags;
        data.add(TosCardFragment.TAG);
        if (1 > 0) { // use web
            data.add(WebDialog.TAG + "_1");
            data.add(WebDialog.TAG + "_2");
            data.add(WebDialog.TAG + "_3");
        }
        data.add(TosCardMyFragment.TAG);
        pagerAdapter.setDataList(data);

        p.setAdapter(pagerAdapter);
        p.setOffscreenPageLimit(data.size()); // keep all items

        TabLayout t;
        t = findViewById(R.id.cardTabBig);
        t.setupWithViewPager(p); // Major tab

        t = findViewById(R.id.cardTabLine);
        t.setupWithViewPager(p); // Thin tab
    }

    private final PGAdapter<String> pagerAdapter = new PGAdapter<String>() {
        @Override
        public int pageLayoutId(ViewGroup parent, int position) {
            return R.layout.view_box;
        }

        @Override
        public void onCreateView(View v, int position) {
            int id = View.generateViewId();

            Bundle b = new Bundle();
            Fragment f = null;
            FragmentManager fm = getFragmentManager();

            v.setId(id);
            if (isWeb(position)) {
                WebDialog d = new WebDialog();
                f = d;
                b.putString(WebDialog.BUNDLE_LINK, webPin.get(position));
                b.putBoolean(WebDialog.BUNDLE_PIN, true);
            } else {
                String tag = itemOf(position);
                switch (tag) {
                    case TosCardFragment.TAG:
                        TosCardFragment fg = new TosCardFragment();
                        f = fg;
                        break;
                    case TosCardMyFragment.TAG:
                        TosCardMyFragment g = new TosCardMyFragment();
                        f = g;
                        break;
                    default:
                }
            }

            if (f != null) {
                f.setArguments(b);
                fm.beginTransaction().replace(id, f, itemOf(position)).commitAllowingStateLoss();
                fm.executePendingTransactions();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            boolean w = isWeb(position);
            if (w) {
                return getString(R.string.web) + " " + position;
            } else {
                return getString(R.string.card);
            }
        }

        private boolean isWeb(int pos) {
            String tag = itemOf(pos);
            return tag.contains(WebDialog.TAG);
        }
    };

    @Override
    public void onPin(String link, int position) {
        Fragment f = findFragmentByTag(WebDialog.TAG + "_" + position);
        if (f instanceof WebDialog) {
            WebDialog w = (WebDialog) f;
            w.loadUrl(link);
            showToast(getString(R.string.web_pinned) + " " + position + " : " + decodeURL(link));
            webPin.set(position, link);
            TosWiki.saveWebPin(webPin);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (onBackCardPager()) {
            return;
        }
        super.onBackPressed();
    }

    private boolean onBackCardPager() {
        int page = cardPager.getCurrentItem();
        Fragment f = findFragmentByTag(homePagerTags.get(page));
        // dismiss shown dialogs of WebPage
        // dismiss the sort filter of main
        if (f instanceof BackPage) {
            BackPage p = (BackPage) f;
            if (p.onBackPressed()) {
                return true;
            }
        }

        // back to page of main
        if (page > 0) {
            cardPager.setCurrentItem(0);
            return true;
        }
        return false;
    }

    private void addTosFragment() {
        TosCardFragment f = new TosCardFragment();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fx = fm.beginTransaction();
        fx.replace(R.id.cardFragment, f, TosCardFragment.TAG);
        fx.commitAllowingStateLoss();

        fm.executePendingTransactions();
    }

    private void initToolIcons() {
        iconLibrary = new Library<>(findViewById(R.id.mainTools));
        IconAdapter adapter = new IconAdapter();
        adapter.setDataList(tools);
        adapter.setItemListener(new IconAdapter.ItemListener() {

            @Override
            public void onClick(Integer iconId, IconAdapter.IconVH vh, int position) {
                Activity a = getActivity();
                if (iconId == R.drawable.card_1429) {
                    new FixedStoneDialog().show(a);
                } else if (iconId == R.drawable.card_0674) {
                    new UltimateStageDialog().show(a);
                } else if (iconId == R.drawable.card_0617) {
                    new SkillEatingDialog().show(a);
                } else if (iconId == R.drawable.logo_stamina) {
                    new SummonerLevelDialog().show(a);
                } else if (iconId == R.drawable.exp_eat) {
                    new MonsterLevelDialog().show(a);
                } else if (iconId == R.drawable.logo_chrome) {
                    new WebDialog().show(a);
                } else if (iconId == R.drawable.ic_send_black_48dp) {
                    new FeedbackDialog().show(a);
                } else if (iconId == R.mipmap.app_icon) {
                    new AboutDialog().show(a);
                } else if (iconId == R.drawable.logo_craft_1) {
                    new CraftDialog().show(a);
                } else if (iconId == R.drawable.owl2) {
                    new StageMemoDialog().show(a);
                } else if (iconId == R.drawable.q1) {
                    new HelpDialog().show(a);
                } else if (iconId == R.drawable.shop_card) {
                    new CardSealDialog().show(a);
                } else if (iconId == R.drawable.tos_app) {
                    new TosEventDialog().show(a);
                } else if (iconId == R.drawable.tos_enochian) {
                    new MainStageDialog().show(a);
                } else if (iconId == R.drawable.tos_vestige) {
                    new DailyStageDialog().show(a);
                } else if (iconId == R.drawable.gift_stamina) {
                    new StaminaDialog().show(a);
                } else if (iconId == R.drawable.tos_lost_relic_pass) {
                    new RelicStageDialog().show(a);
                } else if (iconId == R.drawable.tos_story) {
                    new StoryStageDialog().show(a);
                } else if (iconId == R.drawable.tos_void_realm) {
                    new RealmStageDialog().show(a);
                } else if (iconId == R.drawable.card_0096) {
                    new FarmPoolDialog().show(a);
                //} else if (iconId == R.drawable.rune_water) {
                } else if (iconId == R.drawable.s_i047) {
                    new RunestoneEditorDialog().show(a);
                } else if (iconId == R.drawable.card_1089) {
                    new FreeMoveSampleDialog().show(a);
                } else if (iconId == 0){
                } else {
                }
            }
        });
        iconLibrary.setViewAdapter(adapter);
        updateTools(appPref.showAppTool.get());
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public View getView() {
        return getWindow().getDecorView();
    }

    private void updateTools(boolean visible) {
        iconLibrary.recyclerView.setVisibility(visible ? View.VISIBLE : View.GONE);
        setViewVisibility(findViewById(R.id.cardTabBig), visible);
    }

    @Override
    public void setToolsVisible(boolean visible) {
        updateTools(visible);
        appPref.showAppTool.set(visible);
    }

    @Override
    public boolean isToolsVisible() {
        return iconLibrary.recyclerView.getVisibility() == View.VISIBLE;
    }

    private void showCardsLoading() {
        waiting = new WaitingDialog.Builder(getActivity()).message(getString(R.string.cardsLoading)).buildAndShow();
    }

    private WebPin webPin = new WebPin();

    private final TaskMonitor.OnTaskState onDatabaseState = new TaskMonitor.OnTaskState() {
        @Override
        public void onTaskDone(int index, String tag) {
            logI("#%s (%s) is done", index, tag);
            if (isActivityGone()) return;

            runOnUiThread(() -> {
                if (TosWiki.TAG_WEB_PIN.equals(tag)) {
                    webPin = TosWiki.getWebPin();
                } else if (TosWiki.TAG_ALL_CARDS.equals(tag)) {
                    int n = TosWiki.getAllCardsCount();
                    showToast(R.string.cards_read, n);
                    if (waiting != null) {
                        waiting.dismiss();
                        waiting = null;
                    }

                    initPager();
                }
            });
        }

        @Override
        public void onAllTaskDone() {
            logI("All done");
        }
    };
}
