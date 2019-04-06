package com.flyingkite.mytoswiki.dialog;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.flyingkite.fabric.FabricAnswers;
import com.flyingkite.firebase.RemoteConfig;
import com.flyingkite.firebase.RemoteConfigKey;
import com.flyingkite.mytoswiki.R;

import java.util.HashMap;
import java.util.Map;

public class AboutDialog extends BaseTosDialog {
    @Override
    protected int getLayoutId() {
        return R.layout.dialog_about;
    }

    @Override
    protected boolean useFloating() {
        return true;
    }

    private TextView bulletin;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dismissWhenClick(R.id.abd_header);
        bulletin = findViewById(R.id.abd_message);
        String s = RemoteConfig.getString(RemoteConfigKey.DIALOG_BULLETIN_MESSAGE);
        bulletin.setText(Html.fromHtml(s));
        logImpression();
    }

    //-- Events
    private void logShare(String type) {
        Map<String, String> m = new HashMap<>();
        m.put("share", type);
        FabricAnswers.logCraft(m);
    }

    private void logImpression() {
        Map<String, String> m = new HashMap<>();
        m.put("impression", "1");
        FabricAnswers.logBulletin(m);
    }
    //-- Events
}
