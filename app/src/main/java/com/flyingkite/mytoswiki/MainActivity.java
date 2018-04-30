package com.flyingkite.mytoswiki;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;

import com.flyingkite.mytoswiki.dialog.SkillEatingDialog;
import com.flyingkite.mytoswiki.dialog.SummonerLevelDialog;
import com.flyingkite.mytoswiki.dialog.WebDialog;
import com.flyingkite.mytoswiki.library.IconAdapter;
import com.flyingkite.mytoswiki.library.Library;
import com.flyingkite.mytoswiki.tos.TCard;
import com.flyingkite.util.TextEditorDialog;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseActivity implements TosCardFragment.ToolBarOwner {
    private List<String> tools = Arrays.asList(
            TCard.Bird.url
            , App.getUriOfResource(R.drawable.logo_stamina).toString()
            , App.getUriOfResource(R.drawable.ic_description_black_48dp).toString()
            , App.getUriOfResource(R.drawable.logo_chrome).toString()
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
            public void onClick(String iconId, IconAdapter.IconVH vh, int position) {
                switch (position) {
                    case 0:
                        new SkillEatingDialog(getActivity()).show();
                        break;
                    case 1:
                        new SummonerLevelDialog(getActivity()).show();
                        break;
                    case 2:
                        new TextEditorDialog(MainActivity.this::getActivity).show();
                        break;
                    case 3:
                        new WebDialog(getActivity()).show();
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
