package com.flyingkite.mytoswiki.library;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flyingkite.mytoswiki.R;

import flyingkite.library.androidx.recyclerview.RVAdapter;

public class NumberLineAdapter extends RVAdapter<Integer, NumberLineAdapter.NumberVH, NumberLineAdapter.ItemListener> {
    public interface ItemListener extends RVAdapter.ItemListener<Integer, NumberVH> {

    }

    @NonNull
    @Override
    public NumberVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NumberVH(inflateView(parent, R.layout.view_skill_eat_row));
    }

    @Override
    public void onBindViewHolder(NumberVH vh, int position) {
        super.onBindViewHolder(vh, position);
        int resId = itemOf(position);
        vh.number.setImageResource(resId);
    }

    public class NumberVH extends RecyclerView.ViewHolder {
        private final ImageView number;

        public NumberVH(View w) {
            super(w);
            number = w.findViewById(R.id.selrImage);
        }
    }
}
