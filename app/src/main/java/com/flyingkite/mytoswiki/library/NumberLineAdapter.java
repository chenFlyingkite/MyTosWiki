package com.flyingkite.mytoswiki.library;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.flyingkite.library.widget.RVAdapter;
import com.flyingkite.mytoswiki.R;

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
        private ImageView number;

        public NumberVH(View w) {
            super(w);
            number = w.findViewById(R.id.selrImage);
        }
    }
}
