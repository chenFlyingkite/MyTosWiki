package com.flyingkite.mytoswiki.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;

import com.flyingkite.fabric.FabricAnswers;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.stage.MainStage;
import com.flyingkite.mytoswiki.library.MainStageAdapter;
import com.flyingkite.mytoswiki.tos.TosWiki;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import flyingkite.library.java.tool.TaskMonitor;

public class RealmStageDialog extends BaseTosDialog {
    private LinearLayout magiStages;
    private LinearLayout heroStages;
    private LinearLayout ironStages;
    private MainStage[] realmStages;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_realm_stage;
    }

    @Override
    protected boolean useFloating() {
        return true;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logImpression();
        magiStages = findViewById(R.id.zsd_magic_stages);
        heroStages = findViewById(R.id.zsd_hero_stages);
        ironStages = findViewById(R.id.zsd_iron_stages);

        TosWiki.attendDatabaseTasks(onStagesReady);
    }

    private final TaskMonitor.OnTaskState onStagesReady = new TaskMonitor.OnTaskState() {
        @Override
        public void onTaskDone(int index, String tag) {
            if (isActivityGone()) {
                return;
            }

            if (TosWiki.TAG_MAIN_STAGE.equals(tag)) {
                realmStages = TosWiki.getRealmStages();
                getActivity().runOnUiThread(() -> {
                    initStages();
                });
            }
        }
    };

    private void initStages() {
        initMagicStages();
        initHeroStages();
        initIronStages();
    }

    private void initMagicStages() {
        //clickToShareSelf(findViewById(R.id.zsd_void3));
        // Stage content
        MainStageAdapter a = new MainStageAdapter() {
            @Override
            public Activity getActivity() {
                return RealmStageDialog.this.getActivity();
            }
        };
        a.setDataList(Arrays.asList(Arrays.copyOfRange(realmStages, 0, 1)));
        fillItems(magiStages, a);
    }

    private void initHeroStages() {
        clickToShareSelf(findViewById(R.id.zsd_void1));
        // Stage content
        MainStageAdapter a = new MainStageAdapter() {
            @Override
            public Activity getActivity() {
                return RealmStageDialog.this.getActivity();
            }
        };
        a.setDataList(Arrays.asList(Arrays.copyOfRange(realmStages, 1, 2)));
        fillItems(heroStages, a);
    }

    private void initIronStages() {
        clickToShareSelf(findViewById(R.id.zsd_void2));
        // Stage content
        MainStageAdapter a = new MainStageAdapter() {
            @Override
            public Activity getActivity() {
                return RealmStageDialog.this.getActivity();
            }
        };
        a.setDataList(Arrays.asList(Arrays.copyOfRange(realmStages, 2, 3)));
        fillItems(ironStages, a);
    }

    private void clickToShareSelf(ViewGroup vg) {
        if (vg == null) return;
        int n = vg.getChildCount();
        for (int i = 0; i < n; i++) {
            View v = vg.getChildAt(i);
            v.setOnClickListener(this::shareImage);
        }
    }


    //-- Events
    private void logImpression() {
        Map<String, String> m = new HashMap<>();
        m.put("impression", "1");
        FabricAnswers.logRealmStage(m);
    }

    private void logShare(String type) {
        Map<String, String> m = new HashMap<>();
        m.put("share", type);
        FabricAnswers.logRealmStage(m);
    }
    //-- Events
}
