package com.flyingkite.mytoswiki.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.flyingkite.fabric.FabricAnswers;
import com.flyingkite.library.TicTac2;
import com.flyingkite.library.util.GsonUtil;
import com.flyingkite.library.widget.Library;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.Stamina;
import com.flyingkite.mytoswiki.library.StaminaAdapter;
import com.flyingkite.mytoswiki.share.ShareHelper;
import com.google.gson.Gson;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.IdRes;

public class StaminaDialog extends BaseTosDialog {

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_stamina;
    }

    private static final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US);
    private static final int period = 8;
    // Data
    private Stamina sdData = new Stamina();
    private Date dateNow = new Date();
    // Views
    private Library<StaminaAdapter> library;
    private StaminaUI sourceBar;
    private StaminaUI targetBar;
    private TextView nowTime;
    private TextView staminaShare;

    private static class StaminaUI {
        private SeekBar bar;
        private TextView text;

        private int getValue() {
            int ans = 0;
            if (bar != null) {
                ans += bar.getProgress();
            }
            return ans;
        }

        @SuppressLint("SetTextI18n")
        private void setValue(int v) {
            if (bar != null) {
                bar.setProgress(v);
            }
            text.setText("" + v);
        }
    }

    @Override
    protected void onFinishInflate(View view, Dialog dialog) {
        logImpression();
        initStamina();
        new LoadDataAsyncTask().executeOnExecutor(sSingle);

        initShare();
        initTable();
    }

    private void initShare() {
        staminaShare = findViewById(R.id.sdStaminaText);
        findViewById(R.id.sdShare).setOnClickListener((v) -> {
            String s = staminaShare.getText().toString();
            logW("share %s", s);
            shareString(s);
            logShare("share");
        });
    }

    private void initTable() {
        long now = dateNow.getTime();
        library = new Library<>(findViewById(R.id.sdStaminaTable), true);
        StaminaAdapter a = new StaminaAdapter();
        a.setStartTime(now);
        a.setDataList(getStaminaTable());
        library.setViewAdapter(a);
    }

    private List<String> getStaminaTable() {
        List<String> data = new ArrayList<>();
        int head = sourceBar.getValue();
        int tail = targetBar.getValue();
        int size = Math.max(50, (tail - head) * period / 30 + period / 2 + 1);
        for (int i = 0; i < size; i++) {
            data.add("" + (head + 30 * i / period));
        }

        return data;
    }

    private void initStamina() {
        // Now text
        nowTime = findViewById(R.id.staminaNow);
        nowTime.setText(getString(R.string.current_time, fmt.format(dateNow)));

        // Stamina select
        int max = 300;
        sourceBar = makeBar(R.id.staminaSource, R.id.staminaSourceTxt, max);
        targetBar = makeBar(R.id.staminaTarget, R.id.staminaTargetTxt, max);
    }

    @SuppressLint("SetTextI18n")
    private StaminaUI makeBar(@IdRes int barID, @IdRes int txtID, int max) {
        StaminaUI u = new StaminaUI();
        SeekBar b = findViewById(barID);
        TextView t = findViewById(txtID);
        u.bar = b;
        u.text = t;
        b.setMax(max);
        b.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                t.setText("" + progress);
                computeStamina();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return u;
    }

    @SuppressLint("SetTextI18n")
    private void computeStamina() {
        int source = sourceBar.getValue();
        int target = targetBar.getValue();
        long dt = (target - source) * period * 60_000;

        Date d2 = new Date(dateNow.getTime() + dt);
        staminaShare.setText(getString(R.string.stamina_desc
                , "" + source, fmt.format(dateNow)
                , "" + target, fmt.format(d2)));

        if (library != null) {
            library.adapter.setDataList(getStaminaTable());
            library.adapter.setTarget(target);
            library.adapter.notifyDataSetChanged();
        }
    }

    private void updateFromData() {
        sourceBar.setValue(sdData.sourceIndex);
        targetBar.setValue(sdData.targetIndex);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        saveStamina();
    }

    private void saveStamina() {
        sdData = new Stamina();
        sdData.sourceIndex = sourceBar.getValue();
        sdData.targetIndex = targetBar.getValue();
        sSingle.submit(() -> {
            GsonUtil.writeFile(getStaminaFile(), new Gson().toJson(sdData));
        });
    }

    // The file of dialog setting
    private File getStaminaFile() {
        return ShareHelper.extFilesFile("stamina.txt");
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadDataAsyncTask extends AsyncTask<Void, Void, Stamina> {
        private TicTac2 clk = new TicTac2();
        @Override
        protected void onPreExecute() {
            clk.tic();
        }

        @Override
        protected Stamina doInBackground(Void... voids) {
            File f = getStaminaFile();
            if (f.exists()) {
                return GsonUtil.loadFile(getStaminaFile(), Stamina.class);
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Stamina data) {
            clk.tac("sdData loaded");
            sdData = data != null ? data : new Stamina();
            updateFromData();
        }
    }

    //-- Events
    private void logImpression() {
        Map<String, String> m = new HashMap<>();
        m.put("impression", "1");
        FabricAnswers.logStamina(m);
    }

    private void logShare(String type) { //TODO
        Map<String, String> m = new HashMap<>();
        m.put("share", type);
        FabricAnswers.logStamina(m);
    }
    //-- Events
}
