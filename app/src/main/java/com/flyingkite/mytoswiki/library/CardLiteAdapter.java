package com.flyingkite.mytoswiki.library;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.tos.TosCard;
import com.flyingkite.mytoswiki.util.GlideUtil;

import flyingkite.library.androidx.recyclerview.RVAdapter;

public class CardLiteAdapter extends RVAdapter<TosCard, CardLiteAdapter.CardLVH, CardLiteAdapter.ItemListener> {

    public interface ItemListener extends RVAdapter.ItemListener<TosCard, CardLVH> {

    }

    @NonNull
    @Override
    public CardLVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CardLVH(inflateView(parent, getItemLayout()));
    }

    @LayoutRes
    public int getItemLayout() {
        return R.layout.view_simple_icon;
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
