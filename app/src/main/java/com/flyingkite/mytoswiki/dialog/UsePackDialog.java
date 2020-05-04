package com.flyingkite.mytoswiki.dialog;

import android.app.Dialog;
import android.view.View;

import com.flyingkite.mytoswiki.R;

public class UsePackDialog extends BaseTosDialog {

    @Override
    protected boolean useFloating() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_use_pack;
    }

    @Override
    protected void onFinishInflate(View view, Dialog dialog) {
        dismissWhenClick(R.id.cmd_ok);
        //setMovementMethod(msg);
    }
}
