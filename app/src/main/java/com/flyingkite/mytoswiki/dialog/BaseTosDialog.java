package com.flyingkite.mytoswiki.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;

import com.flyingkite.mytoswiki.R;
import com.flyingkite.util.DialogManager;

public abstract class BaseTosDialog {
    public interface DialogOwner {
        Activity getActivity();
    }

    protected DialogOwner owner;
    protected View mView;
    protected AlertDialog mDialog;

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
        mView = view;
        mDialog = dialog;
        onFinishInflate(view, dialog);
    }

    protected abstract void onFinishInflate(View view, AlertDialog dialog);

    protected final <T extends View> T findViewById(@IdRes int id) {
        View w = mView;
        if (w != null) {
            T v = w.findViewById(id);
            if (v != null) {
                return v;
            }
        }

        return getActivity().findViewById(id);
    }

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

    protected void initScrollTools(@IdRes int goTop, @IdRes int goBottom, RecyclerView recycler) {
        View w;
        w = findViewById(goTop);
        if (w != null) {
            w.setOnClickListener((v) -> {
                if (recycler != null) {
                    recycler.scrollToPosition(0);
                }
            });
        }

        w = findViewById(goBottom);
        if (w != null) {
            w.setOnClickListener((v) -> {
                if (recycler != null) {
                    RecyclerView.Adapter a = recycler.getAdapter();
                    int end = 0;
                    if (a != null) {
                        end = a.getItemCount() - 1;
                    }
                    recycler.scrollToPosition(end);
                }
            });
        }
    }
}
