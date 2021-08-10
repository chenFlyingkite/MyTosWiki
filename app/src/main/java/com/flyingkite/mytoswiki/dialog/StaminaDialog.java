package com.flyingkite.mytoswiki.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.flyingkite.fabric.FabricAnswers;
import com.flyingkite.library.util.GsonUtil;
import com.flyingkite.library.util.ThreadUtil;
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
    private static final int period = 8; // period minutes per 1 stamina
    // Data
    private Stamina sdData = new Stamina();
    private final Date dateNow = new Date();
    // Views
    private Library<StaminaAdapter> library;
    private StaminaUI sourceBar;
    private StaminaUI targetBar;
    private TextView nowTime;
    private TextView staminaShare;

    private static class StaminaUI {
        private SeekBar bar;
        private TextView text;
        private final int min = 0;
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
        initStamina();
        sSingle.submit(getLoadDataTask());

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
        int size = Math.max(70, (tail - head) * period / 30 + period / 2 + 1);
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
        sourceBar = makeBar(R.id.staminaSource, R.id.staminaSourceTxt, R.id.staminaSourcePlus, R.id.staminaSourceMinus, max);
        targetBar = makeBar(R.id.staminaTarget, R.id.staminaTargetTxt, R.id.staminaTargetPlus, R.id.staminaTargetMinus, max);
    }

    @SuppressLint("SetTextI18n")
    private StaminaUI makeBar(@IdRes int barID, @IdRes int txtID, @IdRes int addID, @IdRes int minusID, int max) {
        StaminaUI u = new StaminaUI();
        SeekBar b = findViewById(barID);
        TextView t = findViewById(txtID);
        u.bar = b;
        u.text = t;
        u.setStepper(findViewById(addID), findViewById(minusID));
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
        long dt = (target - source) * period * 60_000L;

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
        computeStamina();
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

    private Runnable getLoadDataTask() {
        return new Runnable() {
            @Override
            public void run() {
                Stamina data = get();
                if (getActivity() == null) return;

                ThreadUtil.runOnUiThread(() -> {
                    sdData = data != null ? data : new Stamina();
                    updateFromData();
                });
            }

            private Stamina get() {
                File f = getStaminaFile();
                if (f.exists()) {
                    return GsonUtil.loadFile(getStaminaFile(), Stamina.class);
                } else {
                    return null;
                }
            }
        };
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
