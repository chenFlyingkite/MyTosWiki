package com.flyingkite.mytoswiki;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.view.View;

import com.flyingkite.library.IOUtil;
import com.flyingkite.library.Say;
import com.flyingkite.library.ThreadUtil;
import com.flyingkite.library.TicTac2;
import com.flyingkite.mytoswiki.dialog.FeedbackDialog;
import com.flyingkite.mytoswiki.dialog.MonsterLevelDialog;
import com.flyingkite.mytoswiki.dialog.SkillEatingDialog;
import com.flyingkite.mytoswiki.dialog.SummonerLevelDialog;
import com.flyingkite.mytoswiki.dialog.WebDialog;
import com.flyingkite.mytoswiki.library.IconAdapter;
import com.flyingkite.mytoswiki.library.Library;
import com.flyingkite.mytoswiki.room.ameskill.AmeSkill;
import com.flyingkite.mytoswiki.room.ameskill.AmeSkillDB;
import com.flyingkite.util.TextEditorDialog;
import com.google.gson.Gson;

import java.io.Reader;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseActivity implements TosCardFragment.ToolBarOwner {
    // Provide alternative bitmaps
    // https://developer.android.com/training/multiscreen/screendensities
    private List<Integer> tools = Arrays.asList(R.drawable.card_0617
            , R.drawable.logo_chrome
            , R.drawable.ic_send_black_48dp
            , R.drawable.logo_stamina
            //, R.mipmap.app_icon
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

    @Override
    protected void onResume() {
        super.onResume();
        loadAme();
    }

    private void loadAme() {
        final String assetName = "ameActiveSkills.json";
        Say.Log("parsing Cards");
        TicTac2 clk = new TicTac2();

        Gson gson = new Gson();
        Reader reader = null;
        AmeSkill[] cards = null;
        try {
            reader = TosWiki.getReader(assetName, getAssets());
            if (reader == null) {
                Say.Log("reader not found, %s", assetName);
            } else {
                clk.tic();
                cards = gson.fromJson(reader, AmeSkill[].class);
                int n = cards == null ? 0 : cards.length;
                clk.tac("%s ames read", n);
            }
        } finally {
            IOUtil.closeIt(reader);
        }

        final AmeSkill[] aa = cards;

        if (aa != null) {
            ThreadUtil.runOnWorkerThread(() -> {
                AmeSkillDB db = Room.inMemoryDatabaseBuilder(getApplicationContext(), AmeSkillDB.class).build();
                Say.Log("DB = %s", db.dao().getAll());
                for (AmeSkill a : aa) {
                    db.dao().insertAll(a);
                }
                Say.Log("DB = %s", db.dao().getAll());
                Say.Log("DB sel = %s", db.dao().getAll2(10));
            });
        }
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
                    //case R.mipmap.app_icon: // TODO
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
