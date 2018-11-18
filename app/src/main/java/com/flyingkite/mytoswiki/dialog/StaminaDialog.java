package com.flyingkite.mytoswiki.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
    private Spinner sourceSpin;
    private Spinner targetSpin;
    private TextView nowTime;
    private TextView staminaShare;

    @Override
    protected void onFinishInflate(View view, Dialog dialog) {
        logImpression();
        initSpinners();
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
        int s = Integer.parseInt(sourceSpin.getSelectedItem().toString());
        int end = Integer.parseInt(targetSpin.getSelectedItem().toString());
        int size = Math.max(50, (end - s) * period / 30 + 5);
        for (int i = 0; i < size; i++) {
            data.add("" + (s + 30 * i / period));
        }

        return data;
    }

    private void initSpinners() {
        // Now text
        nowTime = findViewById(R.id.staminaNow);
        nowTime.setText(getString(R.string.current_time, fmt.format(dateNow)));

        // Spiners
        int min = 0;
        int max = 300;
        targetSpin = makeSpin(R.id.staminaTarget, min, max);
        sourceSpin = makeSpin(R.id.staminaSource, min, max);
    }

    private Spinner makeSpin(@IdRes int spinnerID, int from, int to) {
        int downId = android.R.layout.simple_spinner_dropdown_item;
        int layoutId = R.layout.view_spinner_item;

        // Set up adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), layoutId);
        adapter.setDropDownViewResource(downId);
        for (int i = from; i <= to; i++) {
            adapter.add("" + i);
        }

        // Set up spinner
        Spinner spin = findViewById(spinnerID);
        spin.setAdapter(adapter);
        spin.setOnItemSelectedListener(adapterSelect);

        return spin;
    }

    private AdapterView.OnItemSelectedListener adapterSelect = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            computeStamina();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            computeStamina();
        }
    };

    @SuppressLint("SetTextI18n")
    private void computeStamina() {
        int source = Integer.parseInt(sourceSpin.getSelectedItem().toString());
        int target = Integer.parseInt(targetSpin.getSelectedItem().toString());
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
        sourceSpin.setSelection(sdData.sourceIndex);
        targetSpin.setSelection(sdData.targetIndex);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        sdData = new Stamina();
        sdData.sourceIndex = sourceSpin.getSelectedItemPosition();
        sdData.targetIndex = targetSpin.getSelectedItemPosition();
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
