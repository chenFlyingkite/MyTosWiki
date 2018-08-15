package com.flyingkite.mytoswiki.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.flyingkite.fabric.FabricAnswers;
import com.flyingkite.firebase.RemoteConfig;
import com.flyingkite.firebase.RemoteConfigKey;
import com.flyingkite.mytoswiki.R;

import java.util.HashMap;
import java.util.Map;

public class StageMemoDialog extends BaseTosDialog {
    @Override
    protected int getLayoutId() {
        return R.layout.dialog_stage_memo;
    }

    @Override
    protected boolean useFloating() {
        return true;
    }

    private TextView stageMemo;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logImpression();
        dismissWhenClick(R.id.smd_header);
        stageMemo = findViewById(R.id.smd_message);
        // Set share
        View stageShare = findViewById(R.id.smd_share);
        stageShare.setOnClickListener((v) -> {
            shareString(stageMemo.getText().toString());
            logShare("text");
        });
        // Set text
        boolean html = RemoteConfig.getBoolean(RemoteConfigKey.DIALOG_STAGE_MEMO_USE_HTML);
        String s = RemoteConfig.getString(RemoteConfigKey.DIALOG_STAGE_MEMO_CONTENT);
        stageMemo.setText(html ? Html.fromHtml(s) : escapeNewLine(s));
    }

    //-- Events
    private void logShare(String type) {
        Map<String, String> m = new HashMap<>();
        m.put("share", type);
        FabricAnswers.logStageMemo(m);
    }

    private void logImpression() {
        Map<String, String> m = new HashMap<>();
        m.put("impression", "1");
        FabricAnswers.logStageMemo(m);
    }
    //-- Events

}
