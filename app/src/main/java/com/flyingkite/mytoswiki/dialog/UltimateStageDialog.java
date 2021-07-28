package com.flyingkite.mytoswiki.dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.flyingkite.crashlytics.CrashReport;
import com.flyingkite.fabric.FabricAnswers;
import com.flyingkite.firebase.RemoteConfig;
import com.flyingkite.firebase.RemoteConfigKey;
import com.flyingkite.library.widget.Library;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.stage.Stage;
import com.flyingkite.mytoswiki.data.stage.StageGroup;
import com.flyingkite.mytoswiki.library.StageAdapter;
import com.flyingkite.mytoswiki.tos.TosWiki;
import com.flyingkite.util.TaskMonitor;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;

public class UltimateStageDialog extends BaseTosDialog {
    private StageGroup mainStages;
    private Library<StageAdapter> allLibrary;
    private Library<StageAdapter> selectLibrary;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_ultimate_stage;
    }

    @Override
    protected boolean useFloating() {
        return true;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logImpression();

        TosWiki.attendDatabaseTasks(onStagesReady);
    }

    private final TaskMonitor.OnTaskState onStagesReady = new TaskMonitor.OnTaskState() {
        @Override
        public void onTaskDone(int index, String tag) {
            if (isActivityGone()) {
                return;
            }

            if (TosWiki.TAG_ULTIM_STAGE.equals(tag)) {
                mainStages = TosWiki.getUltimateStages();
                getActivity().runOnUiThread(() -> {
                    initStages();
                });
            }
        }
    };

    private void initStages() {
        List<Stage> data;

        data = getWeeklyStage();
        // Weekly stage
        selectLibrary = new Library<>(findViewById(R.id.usd_select_stages), true);
        StageAdapter az = new StageAdapter();
        az.setDataList(data);
        az.setItemListener(new StageAdapter.ItemListener() {
            @Override
            public void onClick(Stage item, StageAdapter.StageVH holder, int position) {
                viewLinkAsWebDialog(item.link);
            }
        });
        selectLibrary.setViewAdapter(az);
        // And title name
        TextView q = findViewById(R.id.usd_select_title);
        q.setText(getString(R.string.week_stages, data.size()));


        data = mainStages.stages;
        // All ultimate stages
        allLibrary = new Library<>(findViewById(R.id.usd_main_stages), true);
        StageAdapter a = new StageAdapter() {
            @Override
            public int holderLayoutId() {
                return R.layout.view_stage_row_2;
            }
        };
        a.setDataList(data);
        a.setItemListener(new StageAdapter.ItemListener() {
            @Override
            public void onClick(Stage item, StageAdapter.StageVH holder, int position) {
                viewLinkAsWebDialog(item.link);
            }
        });
        allLibrary.setViewAdapter(a);
        // And title name
        TextView t = findViewById(R.id.usd_main_title);
        t.setText(getString(R.string.all_stages, data.size()));
    }

    private List<Stage> getWeeklyStage() {
        String json = RemoteConfig.getString(RemoteConfigKey.DIALOG_ULTIMATE_STAGE);
        try {
            Stage[] stage = new Gson().fromJson(json, Stage[].class);
            return Arrays.asList(stage);
        } catch (Exception e) {
            CrashReport.logException(e);
            return new ArrayList<>();
        }
    }

    //-- Events
    private void logImpression() {
        Map<String, String> m = new HashMap<>();
        m.put("impression", "1");
        FabricAnswers.logUltimateStage(m);
    }

    private void logShare(String type) {
        Map<String, String> m = new HashMap<>();
        m.put("share", type);
        FabricAnswers.logUltimateStage(m);
    }
    //-- Events
}
