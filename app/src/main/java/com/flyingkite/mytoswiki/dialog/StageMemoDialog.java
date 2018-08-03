package com.flyingkite.mytoswiki.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.flyingkite.firebase.RemoteConfig;
import com.flyingkite.firebase.RemoteConfigKey;
import com.flyingkite.mytoswiki.R;

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
        stageMemo = findViewById(R.id.smd_message);
        // Set share
        View stageShare = findViewById(R.id.smd_share);
        stageShare.setOnClickListener((v) -> {
            shareString(stageMemo.getText().toString());
        });
        // Set text
        boolean html = RemoteConfig.getBoolean(RemoteConfigKey.DIALOG_STAGE_MEMO_USE_HTML);
        String s = RemoteConfig.getString(RemoteConfigKey.DIALOG_STAGE_MEMO_CONTENT);
        stageMemo.setText(html ? Html.fromHtml(s) : escapeNewLine(s));
    }

}
