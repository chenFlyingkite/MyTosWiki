package com.flyingkite.mytoswiki.library;

import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.flyingkite.mytoswiki.R;

public abstract class CardCombineAdapter extends CardTileAdapter {
    @NonNull
    @Override
    public CombineVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CombineVH(inflateView(parent, R.layout.view_card_combine_item));
    }

    @Override
    public void onBindViewHolder(CardLVH vh, int position) {
        super.onBindViewHolder(vh, position);
        if (vh instanceof CombineVH) {
            CombineVH c = (CombineVH) vh;
            if (position == 0) {
                c.plus.setVisibility(View.GONE);
            }
        }
    }

    public class CombineVH extends TileVH {
        protected ImageView plus;

        public CombineVH(View v) {
            super(v);
            plus = v.findViewById(R.id.itemPlus);
        }
    }
}
