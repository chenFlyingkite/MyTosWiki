package com.flyingkite.mytoswiki.library;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.flyingkite.library.widget.RVAdapter;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.tos.TosCard;
import com.flyingkite.mytoswiki.util.GlideUtil;

public class CardLiteAdapter extends RVAdapter<TosCard, CardLiteAdapter.CardLVH, CardLiteAdapter.ItemListener> {

    public interface ItemListener extends RVAdapter.ItemListener<TosCard, CardLVH> {

    }

    @NonNull
    @Override
    public CardLVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CardLVH(inflateView(parent, R.layout.view_simple_icon));
    }

    @Override
    public void onBindViewHolder(CardLVH vh, int position) {
        super.onBindViewHolder(vh, position);
        vh.setCard(itemOf(position));
    }

    public static class CardLVH extends RecyclerView.ViewHolder implements GlideUtil {
        protected ImageView img;

        public CardLVH(View v) {
            super(v);
            img = v.findViewById(R.id.itemIcon);
        }

        public void setCard(TosCard c) {
            loadCardToImageView(img, c);
        }
    }
}
