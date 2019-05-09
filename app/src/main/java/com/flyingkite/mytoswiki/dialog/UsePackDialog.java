package com.flyingkite.mytoswiki.dialog;

import android.app.Dialog;
import android.view.View;
import android.widget.TextView;

import com.flyingkite.mytoswiki.AppPref;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.TosCardMyFragment;

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
        TextView msg = view.findViewById(R.id.cmd_review);
        msg.setText(getString(R.string.cards_pack) + "\n" + TosCardMyFragment.TOS_REVIEW + new AppPref().getUserUid());
        //setMovementMethod(msg);

    }
}
