package com.flyingkite.mytoswiki.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.share.ShareHelper;
import com.flyingkite.mytoswiki.util.PageUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BaseTosDialog extends DialogFragment implements
        PageUtil,
        DialogInterface.OnKeyListener,
        DialogInterface.OnShowListener {
    protected static final ExecutorService sSingle = new ThreadPoolExecutor(0, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    public BaseTosDialog() {
        // https://stackoverflow.com/a/15004206
        super();
        setStyle(
                //DialogFragment.STYLE_NORMAL
                //DialogFragment.STYLE_NO_TITLE
                DialogFragment.STYLE_NO_FRAME
                //, android.R.style.Theme
                //, android.R.style.Theme_Translucent
                //, android.R.style.Theme_Translucent_NoTitleBar
                //, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen // No status bar
                //, android.R.style.Theme_Holo_Light_Dialog
                //, R.style.AppTheme
                , ofTheme()
                //, R.style.CommonAlertDialog_noFloating
                //, R.style.CommonAlertDialog
                //, android.R.style.Theme_DeviceDefault_Light_NoActionBar_TranslucentDecor
                //, getTheme()
        );
    }

    protected int ofTheme() {
        return useFloating() ? R.style.CommonAlertDialog : R.style.CommonAlertDialog_noFloating;
    }

    protected boolean useFloating() {
        return false;
    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (savedInstanceState != null) {
//            // For activity re-create case (Don't keep activity)
//            // We directly dismiss it, since we need the listener of AudioAdder be set
//            // see {@link Fragment#setRetainInstance(boolean)}
//            dismissAllowingStateLoss();
//        } else {
//            //setPickerFragment();
//        }
//    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    protected void dismissWhenClick(@IdRes int... ids) {
        setOnClickListeners((v) -> {
            dismissAllowingStateLoss();
        }, ids);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = null;
        int layoutId = getLayoutId();
        if (layoutId > 0) {
            v = inflater.inflate(layoutId, container, false);
        }
        // Dismiss if click on view container
        if (v != null) {
            v.setOnClickListener((v1) -> {
                dismissAllowingStateLoss();
            });
        }
        return v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog d = super.onCreateDialog(savedInstanceState);
        d.setOnKeyListener(this);
        d.setOnShowListener(this);
        //d.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        return d;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        onFinishInflate(view, getDialog());
    }

    protected int getLayoutId() {
        return -1;
    }

    protected void onFinishInflate(View view, Dialog dialog) {

    }

    protected void shareString(String msg) {
        shareString(msg, getString(R.string.share_to));
    }

    protected void shareString(String msg, String chooser) {
        ShareHelper.shareString(getActivity(), msg, chooser);
    }

    protected void viewLink(String link) {
        ShareHelper.viewLink(getActivity(), link);
    }

    protected void shareImage(View view, String filename) {
        ShareHelper.shareImage(getActivity(), view, filename);
    }

    protected void shareImage(View v) {
        String name = ShareHelper.cacheName("2.png");
        shareImage(v, name);
    }

    protected void viewLinkAsWebDialog(String link) {
        WebDialog d = new WebDialog();
        Bundle b = new Bundle();
        b.putString(WebDialog.BUNDLE_LINK, link);
        d.setArguments(b);
        d.show(getActivity());
    }

    public void show(Activity activity) {
        log("show %s", sig());
        show(activity.getFragmentManager(), sig());
    }

    /**
     *
     * @return true if this event was consumed.
     * @see Dialog#dispatchKeyEvent(KeyEvent)
     */
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        int action = event.getAction();
        switch (action) {
            case KeyEvent.ACTION_UP:
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (onBackPressed()) {
                        return true;
                    }
                }
                break;
        }
        return false;
    }

    @Override
    public void onShow(DialogInterface dialog) {

    }

}
