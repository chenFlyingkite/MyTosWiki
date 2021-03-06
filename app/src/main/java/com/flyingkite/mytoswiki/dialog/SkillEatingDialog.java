package com.flyingkite.mytoswiki.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import com.flyingkite.fabric.FabricAnswers;
import com.flyingkite.library.Say;
import com.flyingkite.library.TicTac2;
import com.flyingkite.library.util.GsonUtil;
import com.flyingkite.library.widget.Library;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.SkillEat;
import com.flyingkite.mytoswiki.library.CardTileAdapter;
import com.flyingkite.mytoswiki.library.selectable.SelectableAdapter;
import com.flyingkite.mytoswiki.share.ShareHelper;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.IdRes;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private RoundUI fromRndUI;
    private RoundUI toRndUI;
    private RoundUI pgsRndUI;
    private TextView fromRnd;
    private TextView toRnd;
    private TextView needRnd;
    private CheckBox use600;
    private CheckBox use50;
    private TextView eatCard;
    private Library<CardTileAdapter> card600;

    private static final int[] ROUNDS_SUM = {0
            // LV  1 ~  5, diff = 16, 19, 50, 75
            ,     0,    16,    35,    85,   160
            // LV  6 ~ 10, diff = 300, 500, 1800, 2500, 3000
            ,   460,   960,  2760,  5260,  8260
            // LV 11 ~ 15, diff = 3000
            , 11260, 14260, 17260, 20260, 23260
    };

    private List<String> tableData = new ArrayList<>();

    private static class RoundUI {
        private SeekBar bar;
        private TextView text;
        private int min;
        private View add;
        private View minus;

        private int getValue() {
            int ans = min;
            if (bar != null) {
                ans += bar.getProgress();
            }
            return ans;
        }

        @SuppressLint("SetTextI18n")
        private void setValue(int v) {
            if (bar != null) {
                bar.setProgress(v - min);
            }
            text.setText("" + v);
        }

        private void step(int dx) {
            int v = getValue() + dx;
            if (bar != null) {
                v = Math.min(Math.max(min, v), min + bar.getMax());
            }
            setValue(v);
        }

        private void setStepper(View add1, View minus1) {
            add1.setOnClickListener((v) -> {
                step(+1);
            });
            add = add1;

            minus1.setOnClickListener((v) -> {
                step(-1);
            });
            minus = minus1;
        }
    }

    @Override
    protected void onFinishInflate(View view, Dialog dialog) {
        logImpression();
        initRounds();
        initCard600();
        new LoadDataAsyncTask().executeOnExecutor(sSingle);

        initShare();
        initTable();
    }

    private void initShare() {
        findViewById(R.id.skillShareEat).setOnClickListener((v) -> {
            String shareText = getString(R.string.skill_share_eat_format
                    , fromRndUI.text.getText()
                    , pgsRndUI.text.getText()
                    , fromRnd.getText()
                    , toRndUI.text.getText()
                    , toRnd.getText()
                    , eatCard.getText());

            Say.Log("s = %s", shareText);
            shareString(shareText);
            logShare("eat");
        });

        findViewById(R.id.skillShareTable).setOnClickListener((v) -> {
            StringBuilder s = new StringBuilder();

            int n = tableData.size() / 3;

            for (int i = 0; i < n; i++) {
                String s0 = tableData.get(3 * i);
                String s1 = tableData.get(3 * i + 1);
                String s2 = tableData.get(3 * i + 2);
                s.append(_fmt("%7s | %7s | %7s\n", s0, s1, s2));
            }
            Say.Log("s = %s", s);
            shareString(s.toString());
            logShare("table");
        });

        dismissWhenClick(R.id.selConcept, R.id.sed_roundNeed);
        findViewById(R.id.skillSample).setOnClickListener((v) -> {
            new SkillEatSampleDialog().show(getActivity());
        });
    }

    private void initCard600() {
        card600 = new Library<>(findViewById(R.id.skillCard600));
        CardTileAdapter a = new CardTileAdapter() {
            @Override
            public FragmentManager getFragmentManager() {
                return getActivity().getFragmentManager();
            }

            @Override
            public int getItemLayout() {
                return R.layout.view_small_image;
            }
        };
        List<String> c600 = Arrays.asList("1709", "1735", "1777", "1801", "1897", "1972", "2077", "2078", "2128", "2170", "2201", "2202", "2328"
                , "2427", "2428", "2429", "2462", "2471", "2472", "2509", "2535", "2633"
        );
        a.setDataList(getCardsByIdNorms(c600));
        card600.setViewAdapter(a);
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

    private void initRounds() {
        fromRnd = findViewById(R.id.skillFromRound);
        toRnd = findViewById(R.id.skillToRound);
        needRnd = findViewById(R.id.skillNeedRound);
        use600 = findViewById(R.id.skillUse600);
        use50 = findViewById(R.id.skillUse50);
        eatCard = findViewById(R.id.skillEatCard);
        use50.setOnClickListener((v1) -> {
            computeEatCard();
        });
        use600.setOnClickListener((v1) -> {
            computeEatCard();
        });

        toRndUI = makeBar(R.id.skillTo, R.id.sed_to, R.id.sed_to_plus, R.id.sed_to_minus, 2, 15);
        pgsRndUI = makeBar(R.id.skillPercent, R.id.sed_percent, R.id.sed_pgs_plus, R.id.sed_pgs_minus, 0, 99);
        fromRndUI = makeBar(R.id.skillFrom, R.id.sed_from, R.id.sed_from_plus, R.id.sed_from_minus, 1, 14);
    }

    private RoundUI makeBar(@IdRes int barID, @IdRes int valueID, @IdRes int addID, @IdRes int minusID, int from, int to) {
        TextView txt = findViewById(valueID);
        SeekBar bar = findViewById(barID);
        bar.setMax(to - from);

        RoundUI r = new RoundUI();
        r.text = txt;
        r.bar = bar;
        r.min = from;
        r.setStepper(findViewById(addID), findViewById(minusID));

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String s = "" + (from + progress);
                txt.setText(s);
                computeEatCard();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return r;
    }

//    private Spinner makeSpin(@IdRes int spinnerID, int from, int to) {
//        int downId = android.R.layout.simple_spinner_dropdown_item;
//        int layoutId = R.layout.view_spinner_item;
//
//        // Set up adapter
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), layoutId);
//        adapter.setDropDownViewResource(downId);
//        for (int i = from; i <= to; i++) {
//            adapter.add("" + i);
//        }
//
//        // Set up spinner
//        Spinner spinner = findViewById(spinnerID);
//        spinner.setAdapter(adapter);
//        spinner.setOnItemSelectedListener(adapterSelect);
//
//        return spinner;
//    }
//
//    private AdapterView.OnItemSelectedListener adapterSelect = new AdapterView.OnItemSelectedListener() {
//        @Override
//        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//            computeEatCard();
//        }
//
//        @Override
//        public void onNothingSelected(AdapterView<?> parent) {
//            computeEatCard();
//        }
//    };

    @SuppressLint("SetTextI18n")
    private void computeEatCard() {
        int lvFrom = fromRndUI.getValue();
        int pgs = pgsRndUI.getValue();
        int from = (int) Math.round(ROUNDS_SUM[lvFrom] + 0.01 * pgs * (ROUNDS_SUM[lvFrom + 1] - ROUNDS_SUM[lvFrom]));
        fromRnd.setText("" + from);

        int lvTo = toRndUI.getValue();
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
        if (use50.isChecked()) {
            int q3 = r / 50;
            r = need % 50;
            eatCard.setText(getString(R.string.skill_eat_card_2, q1, q2, q3, r));
        } else {
            eatCard.setText(getString(R.string.skill_eat_card, q1, q2, r));
        }
    }

    private void updateFromData() {
        fromRndUI.setValue(skEat.fromLevel);
        pgsRndUI.setValue(skEat.progress);
        toRndUI.setValue(skEat.toLevel);
        use600.setChecked(skEat.use600);
        computeEatCard();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        saveEat();
    }

    private void saveEat() {
        skEat = new SkillEat();
        skEat.fromLevel = fromRndUI.getValue();
        skEat.progress = pgsRndUI.getValue();
        skEat.toLevel = toRndUI.getValue();
        skEat.use600 = use600.isChecked();
        sSingle.submit(() -> {
            GsonUtil.writeFile(getSkillEatFile(), new Gson().toJson(skEat));
        });
    }

    // The file of dialog setting
    private File getSkillEatFile() {
        return ShareHelper.extFilesFile("skillEat.txt");
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadDataAsyncTask extends AsyncTask<Void, Void, SkillEat> {
        private TicTac2 clk = new TicTac2();
        @Override
        protected void onPreExecute() {
            clk.tic();
        }

        @Override
        protected SkillEat doInBackground(Void... voids) {
            File f = getSkillEatFile();
            if (f.exists()) {
                return GsonUtil.loadFile(getSkillEatFile(), SkillEat.class);
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(SkillEat data) {
            clk.tac("skEat loaded");
            skEat = data != null ? data : new SkillEat();
            updateFromData();
        }
    }

    //-- Events
    private void logImpression() {
        Map<String, String> m = new HashMap<>();
        m.put("impression", "1");
        FabricAnswers.logSkillEat(m);
    }

    private void logShare(String type) {
        Map<String, String> m = new HashMap<>();
        m.put("share", type);
        FabricAnswers.logSkillEat(m);
    }
    //-- Events
}
