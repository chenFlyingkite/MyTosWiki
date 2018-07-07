package com.flyingkite.mytoswiki;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;

import com.flyingkite.mytoswiki.dialog.FeedbackDialog;
import com.flyingkite.mytoswiki.dialog.MonsterLevelDialog;
import com.flyingkite.mytoswiki.dialog.SkillEatingDialog;
import com.flyingkite.mytoswiki.dialog.SummonerLevelDialog;
import com.flyingkite.mytoswiki.dialog.WebDialog;
import com.flyingkite.mytoswiki.library.IconAdapter;
import com.flyingkite.mytoswiki.library.Library;
import com.flyingkite.util.TextEditorDialog;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseActivity implements TosCardFragment.ToolBarOwner {
    // Provide alternative bitmaps
    // https://developer.android.com/training/multiscreen/screendensities
    private List<Integer> tools = Arrays.asList(R.drawable.card_0617
            , R.drawable.logo_chrome
            , R.drawable.ic_send_black_48dp
            , R.drawable.logo_stamina
            , R.mipmap.app_icon
            , R.drawable.exp_eat
            , R.drawable.ic_description_black_48dp
    );
    private Library<IconAdapter> iconLibrary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolIcons();
        addTosFragment();
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
                    case R.mipmap.app_icon: // TODO
                    case R.drawable.ic_send_black_48dp:
                        new FeedbackDialog().show(getActivity());
                        break;
                }
            }
        });
        iconLibrary.setViewAdapter(adapter);
    }

    private Activity getActivity() {
        return this;
    }

    @Override
    public void setToolsVisible(boolean visible) {
        iconLibrary.recyclerView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean isToolsVisible() {
        return iconLibrary.recyclerView.getVisibility() == View.VISIBLE;
    }
}
