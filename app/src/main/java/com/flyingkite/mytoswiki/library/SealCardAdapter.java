package com.flyingkite.mytoswiki.library;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyingkite.library.widget.RVAdapter;
import com.flyingkite.library.widget.RVSelectAdapter;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.tos.TosCard;
import com.flyingkite.mytoswiki.util.GlideUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SealCardAdapter extends RVSelectAdapter<TosCard, SealCardAdapter.SCardVH, SealCardAdapter.ItemListener> implements GlideUtil {
    public interface ItemListener extends RVAdapter.ItemListener<TosCard, SCardVH> {

    }

    private Set<Integer> drawnIndex = new HashSet<>();

    private boolean peekCard;

    public void setPeekCard(boolean peek) {
        peekCard = peek;
    }

    @Override
    public RVAdapter<TosCard, SCardVH, ItemListener> setDataList(List<TosCard> list) {
        drawnIndex.clear();
        return super.setDataList(list);
    }

    @NonNull
    @Override
    public SCardVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SCardVH(inflateView(parent, R.layout.view_tos_item_card_seal));
    }

    @Override
    public void onBindViewHolder(SCardVH vh, int position) {
        super.onBindViewHolder(vh, position);
        TosCard c = itemOf(position);
        vh.text.setText("# " + (position + 1));
        loadCardToImageView(vh.img2, c);
        vh.showCard(c, vh.isDrawn());
        vh.img2.setVisibility(peekCard ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onDidClickItem(TosCard item, SCardVH vh) {
        drawnIndex.add(vh.getAdapterPosition());
        vh.showCard(item, true);
    }

    public class SCardVH extends RecyclerView.ViewHolder {
        public ImageView img;
        private TextView text;
        private ImageView img2;

        public SCardVH(View v) {
            super(v);
            img = v.findViewById(R.id.tisImg);
            text = v.findViewById(R.id.tisText);
            img2 = v.findViewById(R.id.tisImg2);
        }

        private void showCard(TosCard c, boolean show) {
            if (c == null) return;

            int id = R.drawable.card_gold;
            if (show) {
                //loadCardToImageView(img, c);
                loadLinkToImageView(img, c.icon, img.getContext(), id);
            } else {
                img.setImageResource(id);
            }
        }

        public boolean isDrawn() {
            return drawnIndex.contains(getAdapterPosition());
        }
    }
}
