package com.flyingkite.mytoswiki.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.view.KeyEvent;
import android.view.View;

import com.flyingkite.mytoswiki.R;
import com.flyingkite.util.DialogManager;

public abstract class BaseTosDialog {
    public interface DialogOwner {
        Activity getActivity();
    }

    protected DialogOwner owner;

    public BaseTosDialog(DialogOwner own) {
        owner = own;
    }


    protected final Activity getActivity() {
        return owner.getActivity();
    }

    public void show() {
        new DialogManager.GenericViewBuilder(owner.getActivity(), getLayoutId(), this::_onFinishInflate).buildAndShow();
    }

    protected abstract @LayoutRes int getLayoutId();

    private void _onFinishInflate(View view, AlertDialog dialog) {
        dialog.setOnDismissListener(this::onDismiss);
        dialog.setOnCancelListener(this::onCancel);
        dialog.setOnKeyListener(this::onKey);
        onFinishInflate(view, dialog);
    }

    protected abstract void onFinishInflate(View view, AlertDialog dialog);

    protected void onDismiss(DialogInterface dialog) {

    }

    protected void onCancel(DialogInterface dialog) {

    }

    protected boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        return false;
    }

    protected void shareString(String msg) {
        shareString(msg, getActivity().getString(R.string.share_to));
    }

    protected void shareString(String msg, String chooser) {
        Intent it = new Intent(Intent.ACTION_SEND);
        it.putExtra(Intent.EXTRA_TEXT, msg);
        it.setType("text/plain");
        try {
            getActivity().startActivity(Intent.createChooser(it, chooser));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
}
