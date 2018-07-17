package com.flyingkite.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.flyingkite.mytoswiki.R;

public class DialogManager {
    public static class Builder {
        private final Activity activity;
        private String title;
        private CharSequence message;
        private String positiveText;
        private String negativeText;
        private String checkBoxText;
        private Runnable positiveRunnable;
        private Runnable negativeRunnable;
        private CheckBox.OnCheckedChangeListener checkboxListener;
        private boolean isChecked = false;
        private boolean cancelOnTouchOutside = true;
        private boolean isCancelable = true;
        private Runnable cancelRunnable;

        public Builder(@NonNull Activity activity, CharSequence message) {
            this.activity = activity;
            this.message = message;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder positiveText(String positiveText) {
            this.positiveText = positiveText;
            return this;
        }

        public Builder negativeText(String negativeText) {
            this.negativeText = negativeText;
            return this;
        }

        public Builder positiveRunnable(Runnable positiveRunnable) {
            this.positiveRunnable = positiveRunnable;
            return this;
        }

        public Builder negativeRunnable(Runnable negativeRunnable) {
            this.negativeRunnable = negativeRunnable;
            return this;
        }

        public Builder cancelOnTouchOutside(boolean cancel) {
            this.cancelOnTouchOutside = cancel;
            return this;
        }

        public Builder isCancelable(boolean isCancelable) {
            this.isCancelable = isCancelable;
            return this;
        }

        public Builder cancelRunnable(Runnable cancelRunnable) {
            this.cancelRunnable = cancelRunnable;
            return this;
        }

        public Builder setCheckBoxText(String checkBoxText, boolean isChecked, CheckBox.OnCheckedChangeListener checkboxListener) {
            this.checkBoxText = checkBoxText;
            this.isChecked = isChecked;
            this.checkboxListener = checkboxListener;
            return this;
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
            // TODO :
//
//            View dialogView = LayoutInflater.from(activity).inflate(R.layout.view_customize_alert_dialog, null);
//            final AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CommonAlertDialogStyle)
//                    .setView(dialogView).create();
//
//            setTextOrHide(dialogView, R.id.dialog_title, title);
//
//            setText(dialogView, R.id.dialog_message, message);
//            setMovementMethod(dialogView, R.id.dialog_message);
//            dialogView.findViewById(R.id.dialog_positive).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (positiveRunnable != null) {
//                        positiveRunnable.run();
//                    }
//                    alertDialog.dismiss();
//                }
//            });
//
//            if (!StringUtils.isEmpty(positiveText)) {
//                setText(dialogView, R.id.dialog_positive, positiveText);
//            }
//
//            dialogView.findViewById(R.id.dialog_negative).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (negativeRunnable != null) {
//                        negativeRunnable.run();
//                    }
//                    alertDialog.dismiss();
//                }
//            });
//
//            setTextOrHide(dialogView, R.id.dialog_negative, negativeText);
//
//            if (checkBoxText != null) {
//                CheckBox box = (CheckBox) dialogView.findViewById(R.id.dialog_checkbox);
//                box.setText(checkBoxText);
//                box.setChecked(isChecked);
//                box.setOnCheckedChangeListener(checkboxListener);
//            } else {
//                dialogView.findViewById(R.id.dialog_checkbox).setVisibility(View.GONE);
//            }
//
//            alertDialog.setCanceledOnTouchOutside(cancelOnTouchOutside);
//            alertDialog.setCancelable(isCancelable);
//            if (cancelRunnable != null) {
//                alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//                    @Override
//                    public void onCancel(DialogInterface dialog) {
//                        cancelRunnable.run();
//                    }
//                });
//            }
//
//            alertDialog.show();
        }
    }

    /**
     * Create and show a simple dialog with specific message and default OK button.
     */
    public static void showSimpleDialog(Activity activity, CharSequence msg) {
        new DialogManager.Builder(activity, msg).buildAndShow();
    }

    public static class InputTextBuilder {
        public interface InputListener {
            void input(@NonNull String text);
        }

        private final Activity activity;
        private final InputListener inputListener;

        private String title;
        private String hint;
        private String defaultText;
        private boolean singleLine = true;
        private int counterMaxLength = -1;
        private String positiveText;
        private String negativeText;

        public InputTextBuilder(@NonNull Activity activity, @NonNull InputListener inputListener) {
            this.activity = activity;
            this.inputListener = inputListener;
        }

        public InputTextBuilder title(String title) {
            this.title = title;
            return this;
        }

        public InputTextBuilder hint(String hint) {
            this.hint = hint;
            return this;
        }

        public InputTextBuilder defaultText(String defaultText) {
            this.defaultText = defaultText;
            return this;
        }

        public InputTextBuilder singleLine(boolean singleLine) {
            this.singleLine = singleLine;
            return this;
        }

        public InputTextBuilder counterMaxLength(int counterMaxLength) {
            this.counterMaxLength = counterMaxLength;
            return this;
        }

        public InputTextBuilder positiveText(String positiveText) {
            this.positiveText = positiveText;
            return this;
        }

        public InputTextBuilder negativeText(String negativeText) {
            this.negativeText = negativeText;
            return this;
        }

        /**
         * Create a dialog instance and show it up.
         * <p/>
         * The instance won't expose to client since we would do some checking internally to avoid
         * client to invoke dialog method at unexpected situation.
         */
        public void createAndShow() {
            if (isDead(activity)) return;
            // TODO :
//
//            final View dialogView = LayoutInflater.from(activity).inflate(R.layout.view_customize_input_dialog, null);
//            final AlertDialog dialog = new AlertDialog.Builder(activity, R.style.CommonAlertDialogStyle)
//                    .setView(dialogView).create();
//
//            setTextOrHide(dialogView, R.id.dialog_title, title);
//            setTextOrHide(dialogView, R.id.dialog_edithint, hint);
//
//            TextInputLayout textInputLayout = (TextInputLayout) dialogView.findViewById(R.id.dialog_text_inputlayout);
//            textInputLayout.setCounterEnabled(counterMaxLength > 0);
//            textInputLayout.setCounterMaxLength(counterMaxLength);
//
//            final EditText editText = (EditText) dialogView.findViewById(R.id.dialog_edittext);
//            editText.setText(defaultText);
//            editText.setHint(hint);
//            editText.setSingleLine(singleLine);
//            if (counterMaxLength > 0) {
//                editText.setFilters(new InputFilter[] {
//                        new InputFilter.LengthFilter(counterMaxLength)
//                });
//            }
//            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//                @Override
//                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                     if (EditorInfo.IME_ACTION_DONE == actionId) {
//                         dialogView.findViewById(R.id.dialog_positive).performClick();
//                         return true;
//                     }
//
//                    return false;
//                }
//            });
//
//            if (!StringUtils.isEmpty(positiveText)) {
//                setText(dialogView, R.id.dialog_positive, positiveText);
//            }
//            if (!StringUtils.isEmpty(negativeText)) {
//                setText(dialogView, R.id.dialog_negative, negativeText);
//            }
//
//            dialogView.findViewById(R.id.dialog_positive).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (editText.length() > 0) {
//                        inputListener.input(editText.getText().toString()); // Callback with input string.
//                        dialog.dismiss();
//                    }
//                }
//            });
//            dialogView.findViewById(R.id.dialog_negative).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dialog.cancel();
//                }
//            });
//
//            dialog.setCanceledOnTouchOutside(true);
//            dialog.setCancelable(true);
//            // Show keyboard for input dialog
//            if (dialog.getWindow() != null) {
//                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//            }
//            dialog.show();
        }
    }

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
            if (activity.isDestroyed()) return true;
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
