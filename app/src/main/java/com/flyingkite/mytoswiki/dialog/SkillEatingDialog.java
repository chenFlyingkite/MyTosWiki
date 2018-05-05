package com.flyingkite.mytoswiki.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.flyingkite.library.GsonUtil;
import com.flyingkite.library.Say;
import com.flyingkite.library.ThreadUtil;
import com.flyingkite.mytoswiki.App;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.SkillEat;
import com.flyingkite.mytoswiki.library.selectable.SelectableAdapter;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SkillEatingDialog extends BaseTosDialog {

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_skill_eat;
    }

    // Data
    private SkillEat skEat = new SkillEat();
    // Views
    private RecyclerView recycler;
    private SelectableAdapter adapter;
    private Spinner fromSpin;
    private Spinner toSpin;
    private Spinner pgsSpin;
    private TextView fromRnd;
    private TextView toRnd;
    private TextView needRnd;
    private CheckBox use600;
    private TextView eatCard;

    private static final int[] ROUNDS_SUM = {0
            // LV  1 ~  5, diff = 16, 19, 50, 75
            ,     0,    16,    35,    85,   160
            // LV  6 ~ 10, diff = 300, 500, 1800, 2500, 3000
            ,   460,   960,  2760,  5260,  8260
            // LV 11 ~ 15, diff = 3000
            , 11260, 14260, 17260, 20260, 23260
    };

    private List<String> tableData = new ArrayList<>();

    @Override
    protected void onFinishInflate(View view, Dialog dialog) {
        initSpinners();
        new LoadDataAsyncTask().executeOnExecutor(ThreadUtil.cachedThreadPool);

        initShare();
        initTable();
    }

    private void initShare() {
        findViewById(R.id.skillShareEat).setOnClickListener((v) -> {
            String shareText = getString(R.string.skill_share_eat_format
                    , fromSpin.getSelectedItem().toString().trim()
                    , pgsSpin.getSelectedItem().toString().trim()
                    , fromRnd.getText()
                    , toSpin.getSelectedItem().toString().trim()
                    , toRnd.getText()
                    , eatCard.getText());

            Say.Log("s = %s", shareText);
            shareString(shareText);
        });

        findViewById(R.id.skillShareTable).setOnClickListener((v) -> {
            StringBuilder s = new StringBuilder();

            int n = tableData.size() / 3;

            for (int i = 0; i < n; i++) {
                String s0 = tableData.get(3 * i);
                String s1 = tableData.get(3 * i + 1);
                String s2 = tableData.get(3 * i + 2);
                s.append(String.format(java.util.Locale.US, "%7s | %7s | %7s\n", s0, s1, s2));
            }
            Say.Log("s = %s", s);
            String shareText = getString(R.string.skill_share_eat_format
                    , fromSpin.getSelectedItem().toString()
                    , pgsSpin.getSelectedItem().toString()
                    , fromRnd.getText()
                    , toSpin.getSelectedItem().toString()
                    , toRnd.getText()
                    , eatCard.getText());

            shareString(s.toString());
        });
    }

    private void initTable() {
        recycler = findViewById(R.id.skillTable);
        int row = 3;
        recycler.setLayoutManager(new GridLayoutManager(getActivity(), row));
        adapter = new SelectableAdapter();
        adapter.setRow(row);

        List<String> data = new ArrayList<>();
        data.add(getString(R.string.cards_level));
        data.add(getString(R.string.skill_needRnd));
        data.add(getString(R.string.skill_sumRnd));
        for (int i = 2; i < 15; i++) {
            data.add("" + i);
            data.add("" + (ROUNDS_SUM[i] - ROUNDS_SUM[i - 1]));
            data.add("" + ROUNDS_SUM[i]);
        }
        data.add("15");
        data.add("-----");
        data.add("" + ROUNDS_SUM[15]);

        tableData = data;
        adapter.setDataList(tableData);
        recycler.setAdapter(adapter);
    }

    private void initSpinners() {
        fromRnd = findViewById(R.id.skillFromRound);
        toRnd = findViewById(R.id.skillToRound);
        needRnd = findViewById(R.id.skillNeedRound);
        use600 = findViewById(R.id.skillUse600);
        eatCard = findViewById(R.id.skillEatCard);
        use600.setOnClickListener((v1) -> {
            computeEatCard();
        });

        final int layoutId = android.R.layout.simple_spinner_dropdown_item;
        ArrayAdapter<String> levelsF = new ArrayAdapter<>(getActivity(), layoutId);
        ArrayAdapter<String> levelsT = new ArrayAdapter<>(getActivity(), layoutId);
        for (int i = 1; i <= 15; i++) {
            if (i < 15) {
                levelsF.add("   " + i + "   ");
            }
            levelsT.add("   " + i + "   ");
        }
        fromSpin = findViewById(R.id.skillFrom);
        toSpin = findViewById(R.id.skillTo);
        fromSpin.setAdapter(levelsF);
        toSpin.setAdapter(levelsT);
        fromSpin.setOnItemSelectedListener(adapterSelect);
        toSpin.setOnItemSelectedListener(adapterSelect);

        ArrayAdapter<String> progress = new ArrayAdapter<>(getActivity(), layoutId);
        for (int i = 0; i < 100; i++) {
            progress.add("   " + i + "   ");
        }
        pgsSpin = findViewById(R.id.skillPercent);
        pgsSpin.setAdapter(progress);
        pgsSpin.setOnItemSelectedListener(adapterSelect);
    }

    private AdapterView.OnItemSelectedListener adapterSelect = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            computeEatCard();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            computeEatCard();
        }
    };

    private void computeEatCard() {
        int lvFrom = fromSpin.getSelectedItemPosition() + 1;
        int pgs = pgsSpin.getSelectedItemPosition();
        int from = (int) Math.round(ROUNDS_SUM[lvFrom] + 0.01 * pgs * (ROUNDS_SUM[lvFrom + 1] - ROUNDS_SUM[lvFrom]));
        fromRnd.setText("" + from);

        int lvTo = toSpin.getSelectedItemPosition() + 1;
        int to = ROUNDS_SUM[lvTo];
        toRnd.setText("" + to);

        int need = to - from;
        needRnd.setText("" + need);

        // Evaluate card count to eat
        int q1, q2;
        int r = need % 200;
        if (use600.isChecked()) {
            q1 = need / 600;
            q2 = (need % 600) / 200;
        } else {
            q1 = 0;
            q2 = need / 200;
        }
        eatCard.setText(getString(R.string.skill_eat_card, q1, q2, r));
    }

    private void updateFromData() {
        fromSpin.setSelection(skEat.fromLevel);
        toSpin.setSelection(skEat.toLevel);
        pgsSpin.setSelection(skEat.progress);
        use600.setChecked(skEat.use600);
        computeEatCard();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        skEat = new SkillEat();
        skEat.fromLevel = fromSpin.getSelectedItemPosition();
        skEat.toLevel = toSpin.getSelectedItemPosition();
        skEat.progress = pgsSpin.getSelectedItemPosition();
        skEat.use600 = use600.isChecked();
        GsonUtil.writeFile(getSkillEatFile(), new Gson().toJson(skEat));
    }

    // The file of dialog setting
    private File getSkillEatFile() {
        File folder = App.me.getExternalCacheDir();
        return new File(folder, "skillEat.txt");
    }

    private class LoadDataAsyncTask extends AsyncTask<Void, Void, SkillEat> {
        @Override
        protected SkillEat doInBackground(Void... voids) {
            return GsonUtil.loadFile(getSkillEatFile(), SkillEat.class);
        }

        @Override
        protected void onPostExecute(SkillEat data) {
            skEat = data != null ? data : new SkillEat();
            updateFromData();
        }
    }
}
