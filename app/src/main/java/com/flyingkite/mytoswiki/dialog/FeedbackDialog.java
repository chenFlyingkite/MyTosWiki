package com.flyingkite.mytoswiki.dialog;

import com.flyingkite.mytoswiki.R;

public class FeedbackDialog extends BaseTosDialog {
    @Override
    protected int getLayoutId() {
        return R.layout.dialog_feedback;
    }

    @Deprecated
    protected int ofTheme() {
        return R.style.CommonAlertDialog;
    }
}
