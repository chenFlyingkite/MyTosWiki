package com.flyingkite.mytoswiki.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.util.BackPage;
import com.flyingkite.mytoswiki.util.PageUtil;
import com.flyingkite.mytoswiki.util.ShareUtil;
import com.flyingkite.mytoswiki.util.TosPageUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;

public class BaseTosDialog extends DialogFragment implements
        PageUtil, TosPageUtil, ShareUtil,
        BackPage,
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
//
//    protected void shareString(String msg) {
//        shareString(msg, getString(R.string.share_to));
//    }
//
//    protected void shareString(String msg, String chooser) {
//        ShareHelper.shareString(getActivity(), msg, chooser);
//    }
//
//    protected void viewLink(String link) {
//        ShareHelper.viewLink(getActivity(), link);
//    }
//
//    protected void shareImage(View view, String filename) {
//        ShareHelper.shareImage(getActivity(), view, filename);
//    }
//
//    protected void shareImage(View v) {
//        String name = ShareHelper.cacheName("2.png");
//        shareImage(v, name);
//    }

    public void show(Activity activity) {
        log("show %s", sig());
        show(activity.getFragmentManager(), sig());
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

    // We replace the "\n" in s again as "\n", Since remote config's value has \n
    // And we directly setText it will expose "\n", not a new line, so we add here
    protected String escapeNewLine(String s) {
        String[] t = s.split("\\\\n");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < t.length; i++) {
            if (i != 0) {
                sb.append("\n");
            }
            sb.append(t[i]);
        }
        return sb.toString();
    }

    // VG = R.layout.view_teamSimple
    public void setTeamSimple(View vg, String[] idNorms) {
        if (idNorms == null) return;
        int n = idNorms.length;

        final int[] ids = {R.id.tsLeader, R.id.tsMember1, R.id.tsMember2
                , R.id.tsMember3, R.id.tsMember4, R.id.tsFriend};
        ImageView v;
        int m = Math.min(n, ids.length);
        for (int i = 0; i < m; i++) {
            v = vg.findViewById(ids[i]);
            setSimpleCard(v, idNorms[i]);
        }
    }

}
