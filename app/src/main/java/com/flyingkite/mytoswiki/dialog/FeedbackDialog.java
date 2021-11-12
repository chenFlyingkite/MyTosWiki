package com.flyingkite.mytoswiki.dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.flyingkite.crashlytics.CrashReport;
import com.flyingkite.fabric.FabricAnswers;
import com.flyingkite.mytoswiki.App;
import com.flyingkite.mytoswiki.R;

import androidx.annotation.Nullable;

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
        FabricAnswers.logFeedback(null);
        userContent = findViewById(R.id.feedback_content);
        send = findViewById(R.id.feedback_send);
        dismissWhenClick(R.id.feedback_header);
        send.setOnClickListener((v) -> {
            String t = userContent.getText().toString().trim();
            if (t.length() == 0) {
                return;
            }

            try {
                String s = null;
                s.length();
            } catch (NullPointerException e) {
                FeedbackException fe = new FeedbackException(t);
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
}
