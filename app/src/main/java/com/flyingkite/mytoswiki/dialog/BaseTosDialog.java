package com.flyingkite.mytoswiki.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flyingkite.library.Say;
import com.flyingkite.mytoswiki.R;

public class BaseTosDialog extends DialogFragment implements
    DialogInterface.OnKeyListener
    , DialogInterface.OnShowListener
    //, KeyEvent.Callback
{
    public BaseTosDialog() {
        // https://stackoverflow.com/a/15004206
        super();
        setStyle(
                //DialogFragment.STYLE_NORMAL
                //DialogFragment.STYLE_NO_TITLE
                DialogFragment.STYLE_NO_FRAME
                //, android.R.style.Theme_Translucent_NoTitleBar
                //, android.R.style.Theme_Holo_Light_Dialog
                //, R.style.AppTheme
                //, R.style.aaa
                //, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen
                , R.style.CommonAlertDialog
                //, android.R.style.Theme_DeviceDefault_Light_NoActionBar_TranslucentDecor
                //, getTheme()
        );
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
        if (isCancelable()) {
            onBackPressed();
        } else {
            dismissAllowingStateLoss();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = null;
        int layoutId = getLayoutId();
        if (layoutId > 0) {
            v = inflater.inflate(layoutId, container, false);
        }
        return v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog d = super.onCreateDialog(savedInstanceState);
        d.setOnKeyListener(this);
        d.setOnShowListener(this);
        return d;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        onFinishInflate(view, getDialog());
    }

//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        // The only reason you might override this method when using onCreateView() is
//        // to modify any dialog characteristics. For example, the dialog includes a
//        // title by default, but your custom layout might not need it. So here you can
//        // remove the dialog title, but you must call the superclass to get the Dialog.
//        Dialog dialog = super.onCreateDialog(savedInstanceState);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        return dialog;
//    }

    protected int getLayoutId() {
        return -1;
    }

    protected void onFinishInflate(View view, Dialog dialog) {

    }

    protected void shareString(String msg) {
        shareString(msg, getString(R.string.share_to));
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

    public void show(Activity activity) {
        Say.Log("name = %s", this.getClass().getCanonicalName());
        show(activity.getFragmentManager(), this.getClass().getCanonicalName());
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

    public void onBackPressed() {
    }

    protected <T extends View> T findViewById(@IdRes int id) {
        return getView().findViewById(id);
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public void onShow(DialogInterface dialog) {

    }

    // From android.app.Dialog
    /*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            event.startTracking();
            return true;
        }

        return false;
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking()
                && !event.isCanceled()) {
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int count, KeyEvent event) {
        return false;
    }
    */
}
