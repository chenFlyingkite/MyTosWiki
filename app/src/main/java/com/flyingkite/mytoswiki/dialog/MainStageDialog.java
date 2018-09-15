package com.flyingkite.mytoswiki.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.stage.MainStage;
import com.flyingkite.mytoswiki.library.MainStageAdapter;
import com.flyingkite.mytoswiki.tos.TosWiki;
import com.flyingkite.util.TaskMonitor;

import java.util.Arrays;

public class MainStageDialog extends BaseTosDialog {
    private LinearLayout stageList;
    private MainStage[] mainStages;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_main_stage;
    }

    @Override
    protected boolean useFloating() {
        return true;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        stageList = findViewById(R.id.msd_main_stages);

        TosWiki.attendDatabaseTasks(onStagesReady);
    }

    private TaskMonitor.OnTaskState onStagesReady = new TaskMonitor.OnTaskState() {
        @Override
        public void onTaskDone(int index, String tag) {
            if (TosWiki.TAG_MAIN_STAGE.equals(tag)) {
                mainStages = TosWiki.getMainStages();
                initStages();
            }
        }
    };

    private void initStages() {
        MainStageAdapter a = new MainStageAdapter() {
            @Override
            public Activity getActivity() {
                return MainStageDialog.this.getActivity();
            }
        };
        a.setDataList(Arrays.asList(mainStages));
        fillItemsLinearly(stageList, a);
    }
}
