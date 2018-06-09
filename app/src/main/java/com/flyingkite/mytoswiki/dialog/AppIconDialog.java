package com.flyingkite.mytoswiki.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.share.ShareHelper;

public class AppIconDialog extends BaseTosDialog {
    @Override
    protected int getLayoutId() {
        return R.layout.dialog_app_icon;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        adjust1();
    }

    private void adjust1() {
        WebView w;
        WebSettings ws;
        w = findViewById(R.id.aicMathInt);
        ws = w.getSettings();
        ws.setTextZoom(1300);

        w = findViewById(R.id.aicMathDt);
        ws = w.getSettings();
        ws.setTextZoom(400);

        findViewById(R.id.aicIcon1).setOnClickListener((ic) -> {
            int s = 512;
            String name = ShareHelper.cacheName(s + ".png");
            ShareHelper.shareImage(getActivity(), ic, name, s, s);
        });
    }
}
