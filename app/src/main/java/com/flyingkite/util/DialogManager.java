package com.flyingkite.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Build;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;

import com.flyingkite.mytoswiki.R;

public class DialogManager {

    private static void setTextOrHide(View dialogView, @IdRes int textViewResId, String text) {
        if (TextUtils.isEmpty(text)) {
            dialogView.findViewById(textViewResId).setVisibility(View.GONE);
        } else {
            setText(dialogView, textViewResId, text);
        }
    }


    private static void setText(View dialogView, @IdRes int textViewResId, @NonNull CharSequence text) {
        ((TextView) dialogView.findViewById(textViewResId)).setText(text);
    }

    private static void setMovementMethod(View dialogView, @IdRes int textViewResId) {
        ((TextView) dialogView.findViewById(textViewResId)).setMovementMethod(LinkMovementMethod.getInstance());
    }

    private static boolean isDead(Activity activity) {
        if (activity.isFinishing()) return true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return activity.isDestroyed();
        }
        return false;
    }

    public static class GenericViewBuilder {
        public interface InflateListener {
            void onFinishInflate(View view, AlertDialog dialog);
        }

        private final Activity activity;
        @LayoutRes
        private final int viewLayoutId;
        private final InflateListener onViewInflated;
        @StyleRes
        private final int themeResId;

        public GenericViewBuilder(@NonNull Activity activity, @LayoutRes int layoutId, InflateListener onInflate) {
            this(activity, layoutId, R.style.CommonAlertDialog, onInflate);
        }

        public GenericViewBuilder(@NonNull Activity activity, @LayoutRes int layoutId, @StyleRes int themeId, InflateListener onInflate) {
            this.activity = activity;
            viewLayoutId = layoutId;
            onViewInflated = onInflate;
            themeResId = themeId;
        }

        public void buildAndShow() {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    _buildAndShow();
                }
            });
        }

        private void _buildAndShow() {
            if (isDead(activity)) return;

            View dialogView = LayoutInflater.from(activity).inflate(viewLayoutId, null);
            final AlertDialog dialog = new AlertDialog.Builder(activity, themeResId).setView(dialogView).create();
            dialog.setCanceledOnTouchOutside(true);
            dialog.setCancelable(true);

            if (onViewInflated != null) {
                onViewInflated.onFinishInflate(dialogView, dialog);
            }
            dialog.show();
        }
    }
}
