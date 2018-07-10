package com.flyingkite.mytoswiki.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.flyingkite.crashlytics.CrashReport;
import com.flyingkite.firebase.CloudMessaging;
import com.flyingkite.mytoswiki.App;
import com.flyingkite.mytoswiki.R;

public class FeedbackDialog extends BaseTosDialog {
    @Override
    protected int getLayoutId() {
        return R.layout.dialog_feedback;
    }

    private TextView userContent;
    private View send;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userContent = findViewById(R.id.feedback_content);
        send = findViewById(R.id.feedback_send);
        send.setOnClickListener((v) -> {
            try {
                String s = null;
                s.length();
            } catch (NullPointerException e) {
                FeedbackException fe = new FeedbackException(userContent.getText());
                App.showToast(R.string.feedback_thanks);
                CrashReport.logException(fe);
                dismissAllowingStateLoss();
            }
        });
    }

    @Override
    public boolean onBackPressed() {
        boolean ask = userContent.length() > 0;
        if (ask) {
            new CommonDialog().message(getString(R.string.leave)).listener(new CommonDialog.Action() {
                @Override
                public void onConfirm() {
                    dismissAllowingStateLoss();
                }
            }).show(getActivity());
            return true;
        } else {
            return super.onBackPressed();
        }
    }

    private class FeedbackException extends Exception {
        public FeedbackException(CharSequence s) {
            super("\n" + s + "\n\nFCM Token= " + CloudMessaging.getToken() + "\n");
        }
    }
}