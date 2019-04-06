package com.flyingkite.mytoswiki.dialog;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

import com.flyingkite.fabric.FabricAnswers;
import com.flyingkite.mytoswiki.R;

import java.util.HashMap;
import java.util.Map;

public class SkillEatSampleDialog extends BaseTosDialog {
    @Override
    protected int getLayoutId() {
        return R.layout.dialog_skill_eat_level;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logImpression();
        initShare();
        dismissWhenClick(R.id.sesBody);
    }

    private void initShare() {
        findViewById(R.id.sesShare).setOnClickListener((v) -> {
            shareImage(findViewById(R.id.sesContent));
            logShare("table");
        });
    }

    //-- Events
    private void logShare(String type) {
        Map<String, String> m = new HashMap<>();
        m.put("share", type);
        FabricAnswers.logSkillEatSample(m);
    }

    private void logImpression() {
        Map<String, String> m = new HashMap<>();
        m.put("impression", "1");
        FabricAnswers.logSkillEatSample(m);
    }
    //-- Events
}
