package com.flyingkite.mytoswiki.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.flyingkite.library.log.Loggable;
import com.flyingkite.mytoswiki.dialog.WebDialog;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;
import flyingkite.math.Provider;

public interface PageUtil extends Loggable, ViewUtil {

    Activity getActivity();

    default View getView() {
        return null;
    }

    default void onToolScrollToPosition(RecyclerView rv, int position) {

    }

    default void viewLinkAsWebDialog(String link) {
        WebDialog d = new WebDialog();
        Bundle b = new Bundle();
        b.putString(WebDialog.BUNDLE_LINK, link);
        d.setArguments(b);
        d.show(getActivity());
    }

    default void initScrollTools(@IdRes int goTop, @IdRes int goBottom, Provider<RecyclerView> owner) {
        View w;
        w = findViewById(goTop);
        if (w != null) {
            w.setOnClickListener((v) -> {
                RecyclerView rv = null;
                if (owner != null) {
                    rv = owner.provide();
                }
                if (rv != null) {
                    rv.scrollToPosition(0);
                    onToolScrollToPosition(rv, 0);
                }
            });
        }

        w = findViewById(goBottom);
        if (w != null) {
            w.setOnClickListener((v) -> {
                RecyclerView rv = null;
                if (owner != null) {
                    rv = owner.provide();
                }
                if (rv != null) {
                    RecyclerView.Adapter a = rv.getAdapter();
                    int end = 0;
                    if (a != null) {
                        end = a.getItemCount() - 1;
                    }
                    rv.scrollToPosition(end);
                    onToolScrollToPosition(rv, end);
                }
            });
        }
    }

    default void initScrollTools(@IdRes int goTop, @IdRes int goBottom, RecyclerView recycler) {
        initScrollTools(goTop, goBottom, () -> {
            return recycler;
        });
    }

    /**
     * @return pair of inflated menu view & popup window
     */
    default Pair<View, PopupWindow> createPopupWindow(@LayoutRes int layoutId, ViewGroup root) {
        // Create MenuWindow
        View menu = LayoutInflater.from(getActivity()).inflate(layoutId, root, false);
        int wrap = ViewGroup.LayoutParams.WRAP_CONTENT;
        PopupWindow w = new PopupWindow(menu, wrap, wrap, true);
        w.setOutsideTouchable(true);
        w.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return new Pair<>(menu, w);
    }

    default void showToast(@StringRes int id, Object... args) {
        String s = getActivity().getString(id, args);
        showToast(s);
    }

    default void showToast(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }

    default void setViewVisibility(@IdRes int parent, boolean show) {
        setViewVisibility(findViewById(parent), show);
    }

    default void setVisibilities(int vis, int... ids) {
        for (int i : ids) {
            View v = findViewById(i);
            if (v != null) {
                v.setVisibility(vis);
            }
        }
    }

    default boolean isAllVisibilitiesOf(int vis, int... ids) {
        for (int i : ids) {
            View v = findViewById(i);
            if (v != null) {
                if (v.getVisibility() != vis) {
                    return false;
                }
            }
        }
        return true;
    }

    default void setOnClickListeners(View.OnClickListener lis, @IdRes int... ids) {
        for (int i : ids) {
            findViewById(i).setOnClickListener(lis);
        }
    }

    default <T extends View> T findViewById(@IdRes int id) {
        View w = getView();
        if (w != null) {
            T v = w.findViewById(id);
            if (v != null) {
                return v;
            }
        }

        if (getActivity() != null) {
            return getActivity().findViewById(id);
        }
        return null;
    }

    default String decodeURL(String s) {
        return UrlUtil.decodeURL(s);
    }

    default boolean isActivityGone() {
        Activity act = getActivity();
        if (act == null || act.isFinishing()) return true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return act.isDestroyed();
        }
        return false;
    }

    // https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
    default void showKeyBoard(boolean show, View view) {
        if (getActivity() == null) return;

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            if (show) {
                imm.showSoftInput(view, 0);
            } else {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    default void setMovementMethod(TextView t) {
        t.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
