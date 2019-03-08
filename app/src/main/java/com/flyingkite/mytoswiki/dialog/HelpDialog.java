package com.flyingkite.mytoswiki.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.flyingkite.fabric.FabricAnswers;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.tos.TosWiki;

public class HelpDialog extends BaseTosDialog {
    @Override
    protected int getLayoutId() {
        return R.layout.dialog_help;
    }

    @Override
    protected boolean useFloating() {
        return true;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dismissWhenClick(R.id.help_main);
        FabricAnswers.logHelp(null);
        underline(findViewById(R.id.help_blue_text));
        init();
    }

    private void init() {
        findViewById(R.id.help_web).setOnClickListener((v) -> {
            new WebDialog().show(getActivity());
        });
        findViewById(R.id.help_save).setOnClickListener((v) -> {
            shareImage(v);
        });
        setSimpleCard(findViewById(R.id.help_card_icon), TosWiki.getCardByIdNorm("1777"));
        findViewById(R.id.help_select).setOnClickListener((v) -> {
            toggleSelected(v);
        });
        findViewById(R.id.help_yellow_text).setOnClickListener((v) -> {
            showToast("已點擊黃色字");
        });
        findViewById(R.id.help_blue_text).setOnClickListener((v) -> {
            showToast("已點擊藍色字");
        });
    }

    private void underline(TextView t) {
        CharSequence cs = Html.fromHtml("<u>" + t.getText() + "</u>");
        t.setText(cs);
    }
}

