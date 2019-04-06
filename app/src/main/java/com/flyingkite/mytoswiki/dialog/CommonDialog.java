package com.flyingkite.mytoswiki.dialog;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.flyingkite.mytoswiki.R;

public class CommonDialog extends BaseTosDialog {
    public interface Action {
        default void onConfirm(){};
        default void onCancel(){};
    }

    private Action action;
    private boolean cancelable = true;
    private CharSequence header;
    private CharSequence content;
    private CharSequence okText;
    private CharSequence ccText;

    @Override
    protected boolean useFloating() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_common;
    }

    public CommonDialog noCancel() {
        cancelable = false;
        return this;
    }

    public CommonDialog listener(Action listener) {
        action = listener;
        return this;
    }

    public CommonDialog title(CharSequence title) {
        header = title;
        return this;
    }

    public CommonDialog message(CharSequence msg) {
        content = msg;
        return this;
    }

    public CommonDialog okText(CharSequence ok) {
        okText = ok;
        return this;
    }

    public CommonDialog cancelText(CharSequence cc) {
        ccText = cc;
        return this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog d = super.onCreateDialog(savedInstanceState);
        d.setCancelable(cancelable);
        return d;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setTextOrHide(findViewById(R.id.cmd_title), header);
        setTextOrHide(findViewById(R.id.cmd_message), content);

        TextView ok = findViewById(R.id.cmd_ok);
        setTextIfExist(ok, okText);
        ok.setOnClickListener((v) -> {
            if (action != null) {
                action.onConfirm();
            }
            dismissAllowingStateLoss();
        });

        TextView cc = findViewById(R.id.cmd_cancel);
        setTextIfExist(cc, ccText);
        cc.setOnClickListener((v) -> {
            if (action != null) {
                action.onCancel();
            }
            dismissAllowingStateLoss();
        });
    }

    private void setTextIfExist(TextView view, CharSequence cs) {
        if (!TextUtils.isEmpty(cs)) {
            view.setText(cs);
        }
    }
}
