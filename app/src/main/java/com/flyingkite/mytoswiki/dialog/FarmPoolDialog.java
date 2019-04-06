package com.flyingkite.mytoswiki.dialog;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.flyingkite.fabric.FabricAnswers;
import com.flyingkite.firebase.RemoteConfig;
import com.flyingkite.firebase.RemoteConfigKey;
import com.flyingkite.mytoswiki.R;

import java.util.HashMap;
import java.util.Map;

public class FarmPoolDialog extends BaseTosDialog {
    @Override
    protected int getLayoutId() {
        return R.layout.dialog_farm_pool;
    }

    @Override
    protected boolean useFloating() {
        return true;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logImpression();
        dismissWhenClick(R.id.fpdTitle);
        TextView detail = findViewById(R.id.fpdDetail);
        findViewById(R.id.fpdShare).setOnClickListener((v) -> {
            shareString(detail.getText().toString());
            logShare("text");
        });
        String s = RemoteConfig.getString(RemoteConfigKey.DIALOG_FARM_POOL_CONTENT);
        detail.setText(escapeNewLine(s));
    }

    //-- Events
    private void logShare(String type) {
        Map<String, String> m = new HashMap<>();
        m.put("share", type);
        FabricAnswers.logFarmPool(m);
    }

    private void logImpression() {
        Map<String, String> m = new HashMap<>();
        m.put("impression", "1");
        FabricAnswers.logFarmPool(m);
    }
    //-- Events

}