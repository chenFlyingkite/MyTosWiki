package com.flyingkite.mytoswiki.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;

import com.flyingkite.fabric.FabricAnswers;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.stage.RelicStage;
import com.flyingkite.mytoswiki.tos.TosWiki;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import flyingkite.library.androidx.recyclerview.Library;
import flyingkite.library.java.tool.TaskMonitor;

public class RelicStageDialog extends BaseTosDialog {
    private LinearLayout stageList;
    private RelicStage[][] relicStages;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_relic_stage;
    }

    @Override
    protected boolean useFloating() {
        return true;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logImpression();
        stageList = findViewById(R.id.rsd_main_stages);

        TosWiki.attendDatabaseTasks(onStagesReady);
    }

    private final TaskMonitor.OnTaskState onStagesReady = new TaskMonitor.OnTaskState() {
        @Override
        public void onTaskDone(int index, String tag) {
            if (isActivityGone()) {
                return;
            }

            if (TosWiki.TAG_RELIC_PASS.equals(tag)) {
                relicStages = TosWiki.getRelicStage();
                getActivity().runOnUiThread(() -> {
                    initStages();
                });
            }
        }
    };

    private void initStages() {
        stageList.removeAllViews();
        // 遠古遺跡 突發地獄遺跡（期間限定） 永久地獄遺跡（常設開放） 雙週遺跡

        addStages("遠古遺跡", relicStages[0], stageList);
        addStages("地獄遺跡", relicStages[1], stageList);
        addStages("雙週遺跡", relicStages[2], stageList);
    }

    private void addStages(String title, RelicStage[] stages, ViewGroup parent) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.view_relic_group_row, parent, false);
        // Set title
        TextView t = v.findViewById(R.id.relicGroupTitle);
        t.setText(title);
        // Set stages
        Library<RelicStageAdapter> b = new Library<>(v.findViewById(R.id.relicGroupItems), true);
        RelicStageAdapter a = new RelicStageAdapter() {
            @Override
            public Activity getActivity() {
                return RelicStageDialog.this.getActivity();
            }
        };
        a.setDataList(Arrays.asList(stages));
        a.setItemListener(new RelicStageAdapter.ItemListener() {
            @Override
            public void onClick(RelicStage item, RelicStageAdapter.RelicVH holder, int position) {
                viewLinkAsWebDialog(item.link);
            }
        });
        b.setViewAdapter(a);
        parent.addView(v);
    }

    //-- Events
    private void logImpression() {
        Map<String, String> m = new HashMap<>();
        m.put("impression", "1");
        FabricAnswers.logRelicStage(m);
    }

    private void logShare(String type) {
        Map<String, String> m = new HashMap<>();
        m.put("share", type);
        FabricAnswers.logRelicStage(m);
    }
    //-- Events
}
