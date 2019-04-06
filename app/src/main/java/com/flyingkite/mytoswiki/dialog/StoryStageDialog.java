package com.flyingkite.mytoswiki.dialog;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import com.flyingkite.fabric.FabricAnswers;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.stage.StageGroup;
import com.flyingkite.mytoswiki.library.StageGroupAdapter;
import com.flyingkite.mytoswiki.tos.TosWiki;
import com.flyingkite.util.TaskMonitor;

public class StoryStageDialog extends BaseTosDialog {
    private LinearLayout stageList;
    private StageGroup[] storyStages;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_story_stage;
    }

    @Override
    protected boolean useFloating() {
        return true;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logImpression();
        stageList = findViewById(R.id.ssd_main_stages);

        TosWiki.attendDatabaseTasks(onStagesReady);
    }

    private TaskMonitor.OnTaskState onStagesReady = new TaskMonitor.OnTaskState() {
        @Override
        public void onTaskDone(int index, String tag) {
            if (isActivityGone()) {
                return;
            }

            if (TosWiki.TAG_STORY_STAGE.equals(tag)) {
                storyStages = TosWiki.getStoryStages();
                initStages();
            }
        }
    };

    private void initStages() {
        StageGroupAdapter a = new StageGroupAdapter() {
            @Override
            public Activity getActivity() {
                return StoryStageDialog.this.getActivity();
            }
        };
        a.setDataList(Arrays.asList(storyStages));
        fillItems(stageList, a);
    }


    //-- Events
    private void logImpression() {
        Map<String, String> m = new HashMap<>();
        m.put("impression", "1");
        FabricAnswers.logStoryStage(m);
    }

    private void logShare(String type) {
        Map<String, String> m = new HashMap<>();
        m.put("share", type);
        FabricAnswers.logStoryStage(m);
    }
    //-- Events
}