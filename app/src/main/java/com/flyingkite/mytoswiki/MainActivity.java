package com.flyingkite.mytoswiki;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.flyingkite.library.widget.Library;
import com.flyingkite.mytoswiki.dialog.AboutDialog;
import com.flyingkite.mytoswiki.dialog.CardSealDialog;
import com.flyingkite.mytoswiki.dialog.CraftDialog;
import com.flyingkite.mytoswiki.dialog.DailyStageDialog;
import com.flyingkite.mytoswiki.dialog.FeedbackDialog;
import com.flyingkite.mytoswiki.dialog.HelpDialog;
import com.flyingkite.mytoswiki.dialog.MainStageDialog;
import com.flyingkite.mytoswiki.dialog.MonsterLevelDialog;
import com.flyingkite.mytoswiki.dialog.SkillEatSampleDialog;
import com.flyingkite.mytoswiki.dialog.SkillEatingDialog;
import com.flyingkite.mytoswiki.dialog.StageMemoDialog;
import com.flyingkite.mytoswiki.dialog.StaminaDialog;
import com.flyingkite.mytoswiki.dialog.SummonerLevelDialog;
import com.flyingkite.mytoswiki.dialog.TosEventDialog;
import com.flyingkite.mytoswiki.dialog.WebDialog;
import com.flyingkite.mytoswiki.library.IconAdapter;
import com.flyingkite.mytoswiki.tos.TosWiki;
import com.flyingkite.mytoswiki.util.PageUtil;
import com.flyingkite.util.TaskMonitor;
import com.flyingkite.util.TextEditorDialog;
import com.flyingkite.util.WaitingDialog;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseActivity implements
        TosCardFragment.ToolBarOwner,
        PageUtil
{
    // Provide alternative bitmaps
    // https://developer.android.com/training/multiscreen/screendensities
    private List<Integer> tools = Arrays.asList(R.drawable.card_0617
            , R.drawable.logo_chrome
            , R.drawable.tos_app
            , R.drawable.tos_vestige
            , R.drawable.tos_enochian
            , R.drawable.owl2
            , R.drawable.shop_card
            , R.drawable.card_1777
            , R.drawable.q1
            , R.drawable.gift_stamina
            , R.mipmap.app_icon
            , R.drawable.ic_send_black_48dp
            , R.drawable.logo_craft_1
            , R.drawable.logo_stamina
            , R.drawable.exp_eat
            , R.drawable.ic_description_black_48dp
    );
    private Library<IconAdapter> iconLibrary;

    @Override
    public void log(String message) {
        Log.e(LTag(), message);
    }

    private WaitingDialog waiting;
    private boolean isDestroyed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolIcons();
        addTosFragment();
        showCardsLoading();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroyed = true;
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
                switch (iconId) {
                    case R.drawable.card_0617:
                        new SkillEatingDialog().show(getActivity());
                        break;
                    case R.drawable.logo_stamina:
                        new SummonerLevelDialog().show(getActivity());
                        break;
                    case R.drawable.exp_eat:
                        new MonsterLevelDialog().show(getActivity());
                        break;
                    case R.drawable.ic_description_black_48dp:
                        new TextEditorDialog(MainActivity.this::getActivity).show();
                        break;
                    case R.drawable.logo_chrome:
                        new WebDialog().show(getActivity());
                        break;
                    case R.drawable.ic_send_black_48dp:
                        new FeedbackDialog().show(getActivity());
                        break;
                    case R.mipmap.app_icon:
                        new AboutDialog().show(getActivity());
                        break;
                    case R.drawable.logo_craft_1:
                        new CraftDialog().show(getActivity());
                        break;
                    case R.drawable.owl2:
                        new StageMemoDialog().show(getActivity());
                        break;
                    case R.drawable.q1:
                        new HelpDialog().show(getActivity());
                        break;
                    case R.drawable.card_1777:
                        new SkillEatSampleDialog().show(getActivity());
                        break;
                    case R.drawable.shop_card:
                        new CardSealDialog().show(getActivity());
                        break;
                    case R.drawable.tos_app:
                        new TosEventDialog().show(getActivity());
                        break;
                    case R.drawable.tos_enochian:
                        new MainStageDialog().show(getActivity());
                        break;
                    case R.drawable.tos_vestige:
                        new DailyStageDialog().show(getActivity());
                        break;
                    case R.drawable.gift_stamina:
                        new StaminaDialog().show(getActivity());
                        break;
                }
            }
        });
        iconLibrary.setViewAdapter(adapter);
    }

    public Activity getActivity() {
        return this;
    }

    @Override
    public View getView() {
        return getWindow().getDecorView();
    }

    @Override
    public void setToolsVisible(boolean visible) {
        iconLibrary.recyclerView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean isToolsVisible() {
        return iconLibrary.recyclerView.getVisibility() == View.VISIBLE;
    }

    private void showCardsLoading() {
        waiting = new WaitingDialog.Builder(getActivity()).message(getString(R.string.cardsLoading)).buildAndShow();
        TosWiki.attendDatabaseTasks(onDatabaseState);
    }

    private TaskMonitor.OnTaskState onDatabaseState = new TaskMonitor.OnTaskState() {
        @Override
        public void onTaskDone(int index, String tag) {
            log("#%s (%s) is done", index, tag);
            if (isFinishing() || isDestroyed) return;

            runOnUiThread(() -> {
                if (TosWiki.TAG_ALL_CARDS.equals(tag)) {

                    int n = TosWiki.getAllCardsCount();
                    showToast(R.string.cards_read, n);
                    if (waiting != null) {
                        waiting.dismiss();
                        waiting = null;
                    }
                }
            });
        }

        @Override
        public void onAllTaskDone() {
            log("All done");
        }
    };
}
