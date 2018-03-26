package com.flyingkite.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.view.WindowManager;

import com.flyingkite.mytoswiki.R;

public class WaitingDialog extends AlertDialog {
    protected WaitingDialog(Context context) {
        super(context);
    }

    protected WaitingDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected WaitingDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {
        private Activity activity;
        private boolean cancelable;
        private long delay;
        private OnCancelListener onCancel;
        private OnDismissListener onDismiss;

        public Builder(Activity act) {
            this(act, false);
        }

        public Builder(Activity act, boolean cancel) {
            activity = act;
            cancelable = cancel;
        }

        public Builder delay(long ms) {
            delay = ms;
            return this;
        }

        public Builder onCancel(OnCancelListener listener) {
            onCancel = listener;
            return this;
        }

        public Builder onDismiss(OnDismissListener listener) {
            onDismiss = listener;
            return this;
        }

        public WaitingDialog build() {
            WaitingDialog d = new WaitingDialog(activity, R.style.WaitingDialog);
            d.setDelayDuration(delay);
            d.setCancelable(cancelable);
            d.setOnCancelListener(onCancel);
            d.setOnDismissListener(onDismiss);
            return d;
        }

        public WaitingDialog buildAndShow() {
            WaitingDialog d = build();
            d.show();
            return d;
        }
    }

    private long delayDuration;
    private static final Handler ui = new Handler(Looper.getMainLooper());

    private WaitingDialog setDelayDuration(long ms) {
        delayDuration = ms;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE); // // https://stackoverflow.com/a/38007874
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_waiting_dialog);

        if (delayDuration > 0) {
            setWindowAttr(0, 0);
            ui.postDelayed(showLoading, delayDuration);
        }
    }

    private Runnable showLoading = new Runnable() {
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
