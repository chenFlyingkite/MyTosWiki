package com.flyingkite.mytoswiki.library;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.flyingkite.library.log.Loggable;
import com.flyingkite.library.widget.RVAdapter;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.seal.SealItem;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SealSeriesAdapter extends RVAdapter<SealItem, SealSeriesAdapter.SealVH, SealSeriesAdapter.ItemListener> implements Loggable {

    public interface ItemListener extends RVAdapter.ItemListener<SealItem, SealVH> {

    }
    private int selected = 0;

    @Override
    public SealVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SealVH(inflateView(parent, R.layout.view_radio_button));
    }

    public void setSelectIndex(int p) {
        select(p);
    }

    @Override
    public void onBindViewHolder(SealVH h, int position) {
        //super.onBindViewHolder(h, position);
        SealItem s = itemOf(position);
        h.box.setText(s.strId);
        h.box.setChecked(selected == position);
        h.box.setOnClickListener((v) -> {
            setSelectIndex(position);
            if (onItem != null) {
                onItem.onClick(s, h, position);
            }
        });
    }

    private void select(int p) {
        int old = selected;
        selected = p;
        notifyItemChanged(old);
        notifyItemChanged(selected);
    }

    public static class SealVH extends RecyclerView.ViewHolder {
        private final RadioButton box;

        public SealVH(View v) {
            super(v);
            box = v.findViewById(R.id.item_radio);
        }
    }
}
