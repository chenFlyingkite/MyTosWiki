package com.flyingkite.mytoswiki.library;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class RVAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    /**
     * Item listener for RVAdapter(RecyclerViewAdapter)
     */
    public interface ItemListener<M, MVH> {
        void onClick(M item, MVH holder, int position);
    }

    // Members & setters
    protected List<T> dataList = new ArrayList<>();
    protected ItemListener<T, VH> onItem;

    public RVAdapter setDataList(List<T> list) {
        dataList = nonNull(list);
        return this;
    }

    public RVAdapter setItemListener(ItemListener<T, VH> listener) {
        onItem = listener;
        return this;
    }

    // We left the creation to be abstract
    /*
    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }
    */

    @Override
    public void onBindViewHolder(VH holder, int position) {
        T item = itemOf(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickItem(item, holder);
                if (onItem != null) {
                    onItem.onClick(item, holder, holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }



    protected View inflateView(ViewGroup parent, @LayoutRes int layoutId) {
        if (parent == null) {
            return null;
        } else {
            return LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        }
    }

    public T itemOf(int index) {
        if (index < 0 || dataList.size() <= index) {
            return null;
        } else {
            return dataList.get(index);
        }
    }

    /**
     * Called when {@link RecyclerView.ViewHolder#itemView itemView}
     * is clicked
     */
    protected void onClickItem(T item, VH holder) {

    }

    protected <Z> List<Z> nonNull(List<Z> list) {
        return list == null ? new ArrayList<>() : list;
    }


}
