package com.flyingkite.util;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flyingkite.library.log.Loggable;
import com.flyingkite.library.util.ListUtil;

import java.util.ArrayList;
import java.util.List;

public class PGAdapter<T> extends PagerAdapter implements Loggable {

    // Members & setters
    protected List<T> dataList = new ArrayList<>();

    @Deprecated // Unused
    public interface ItemListener<M, MVH> {
        void onClick(M item, MVH holder, int position);
    }

    //region Member setters
    public PGAdapter<T> setDataList(List<T> list) {
        dataList = nonNull(list);
        return this;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View v = inflateView(container, pageLayoutId(container, position));
        container.addView(v);
        onCreateView(v, position);
        return v;
    }

    public void onCreateView(View v, int position) {

    }

    @LayoutRes
    public int pageLayoutId(ViewGroup parent, int position) {
        return 0;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        log("- #%s", position);
        if (object instanceof View) {
            container.removeView((View) object);
        }
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void log(String message) {
        logE(message);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }

    //region Utility methods
    protected View inflateView(ViewGroup parent, @LayoutRes int layoutId) {
        if (parent == null) {
            return null;
        } else {
            return LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        }
    }

    public T itemOf(int index) {
        return ListUtil.itemOf(dataList, index);
    }

    protected <Z> List<Z> nonNull(List<Z> list) {
        return ListUtil.nonNull(list);
    }
}
