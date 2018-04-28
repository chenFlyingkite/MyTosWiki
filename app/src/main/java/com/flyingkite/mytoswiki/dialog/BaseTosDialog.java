package com.flyingkite.mytoswiki.dialog;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;

import com.flyingkite.mytoswiki.R;

public class BaseTosDialog extends Dialog {
    public BaseTosDialog(@NonNull Context context) {
        this(context, R.style.CommonAlertDialog);
        //this(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
    }

    public BaseTosDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layoutId = getLayoutId();
        if (layoutId > 0) {
            setContentView(layoutId);
        }
        setOnDismissListener(this::onDismiss);
        setOnCancelListener(this::onCancel);
        setOnKeyListener(this::onKey);
        setOnShowListener(this::onShow);
        onFinishInflate(null, this);
    }

    protected int getLayoutId() {
        return -1;
    }

    protected void onFinishInflate(View view, Dialog dialog) {

    }

    protected void onDismiss(DialogInterface dialog) {

    }

    protected void onCancel(DialogInterface dialog) {

    }

    protected void onShow(DialogInterface dialog) {

    }

    protected boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        return false;
    }

    protected String getString(@StringRes int sid) {
        return getContext().getString(sid);
    }

    protected String getString(@StringRes int sid, Object... args) {
        return getContext().getString(sid, args);
    }

    protected void shareString(String msg) {
        shareString(msg, getString(R.string.share_to));
    }

    protected void shareString(String msg, String chooser) {
        Intent it = new Intent(Intent.ACTION_SEND);
        it.putExtra(Intent.EXTRA_TEXT, msg);
        it.setType("text/plain");
        try {
            getContext().startActivity(Intent.createChooser(it, chooser));
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
