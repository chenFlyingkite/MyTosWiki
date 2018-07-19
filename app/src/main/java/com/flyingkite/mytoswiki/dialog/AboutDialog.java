package com.flyingkite.mytoswiki.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.flyingkite.firebase.RemoteConfig;
import com.flyingkite.firebase.RemoteConfigKey;
import com.flyingkite.mytoswiki.R;

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
        bulletin = findViewById(R.id.abd_message);
        String s = RemoteConfig.getString(RemoteConfigKey.DIALOG_BULLETIN_MESSAGE);
        bulletin.setText(Html.fromHtml(s));
    }
}
