package com.flyingkite.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.flyingkite.mytoswiki.R;

public class WaitingDialog extends AlertDialog {
    protected WaitingDialog(Context context) {
        super(context);
        init();
    }

    protected WaitingDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    protected WaitingDialog(Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    private void init() {
        message = getContext().getString(R.string.loading);
    }

    public static class Builder {
        private final WaitingDialog dlg;

        public Builder(Activity act) {
            this(act, false);
        }

        public Builder(Activity act, boolean cancel) {
            dlg = new WaitingDialog(act, R.style.WaitingDialog);
            dlg.setCancelable(cancel);
        }

        public Builder delay(long ms) {
            dlg.setDelayDuration(ms);
            return this;
        }

        public Builder onCancel(OnCancelListener listener) {
            dlg.setOnCancelListener(listener);
            return this;
        }

        public Builder onDismiss(OnDismissListener listener) {
            dlg.setOnDismissListener(listener);
            return this;
        }

        public Builder message(CharSequence msg) {
            dlg.setLoading(msg);
            return this;
        }

        public WaitingDialog build() {
            return dlg;
        }

        public WaitingDialog buildAndShow() {
            WaitingDialog d = build();
            d.show();
            return d;
        }
    }

    private CharSequence message;
    private long delayDuration;
    private static final Handler ui = new Handler(Looper.getMainLooper());

    private WaitingDialog setDelayDuration(long ms) {
        delayDuration = ms;
        return this;
    }

    private WaitingDialog setLoading(CharSequence seq) {
        message = seq;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_NO_TITLE); // // https://stackoverflow.com/a/38007874
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_waiting_dialog);

        TextView t = findViewById(R.id.waitText);
        t.setText(message);

        if (delayDuration > 0) {
            setWindowAttr(0, 0);
            ui.postDelayed(showLoading, delayDuration);
        }
    }

    private final Runnable showLoading = new Runnable() {
        @Override
        public void run() {
            setWindowAttr(0.6F, 1F);
        }
    };

    private void setWindowAttr(float dim, float alpha) {
        Window w = getWindow();
        if (w == null) return;

        WindowManager.LayoutParams p = w.getAttributes();
        p.dimAmount = dim;
        w.setAttributes(p);
        w.getDecorView().setAlpha(alpha);
    }
}
