package com.flyingkite.mytoswiki.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.annotation.LayoutRes;
import android.view.View;

import com.flyingkite.util.DialogManager;

public abstract class BaseTosDialog {
    public interface DialogOwner {
        Activity getActivity();
    }

    protected DialogOwner owner;

    public BaseTosDialog(DialogOwner own) {
        owner = own;
    }

    public void show() {
        new DialogManager.GenericViewBuilder(owner.getActivity(), getLayoutId(), this::onFinishInflate).buildAndShow();
    }

    protected abstract @LayoutRes int getLayoutId();

    protected abstract void onFinishInflate(View view, AlertDialog dialog);
}
