package com.flyingkite.mytoswiki.library;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.flyingkite.library.Say;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.TosCard;
import com.flyingkite.mytoswiki.data.TosCardRMDKVIR;
import com.flyingkite.mytoswiki.tos.TosCardFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

class CardAdapter extends RecyclerView.Adapter<CardVH> {
    @Deprecated
    private List<TosCardRMDKVIR> cardsRMD = new ArrayList<>();
    private List<TosCard> cards = new ArrayList<>();
    private CardVH.OnClickCard onClick;
    private boolean showFilter;
    private List<Integer> filterIndices = new ArrayList<>();
    private OnFilterCard onFilter;

    public interface OnFilterCard {
        void onFiltered(int selected, int total, Map<String, String> map);
    }

    @Deprecated
    public void setCardsRMD(TosCardRMDKVIR[] c) {
        cardsRMD = asList(c);
    }

    public void setCards(TosCard[] c) {
        cards = asList(c);
    }

    private <T> List<T> asList(T[] c) {
        return c == null ? new ArrayList<>() : Arrays.asList(c);
    }

    public void setOnClickCard(CardVH.OnClickCard listener) {
        onClick = listener;
    }

    public void setOnFilter(OnFilterCard listener) {
        onFilter = listener;
    }

    @Override
    public CardVH onCreateViewHolder(ViewGroup parent, int viewType) {
        //Say.LogF("create type #%s", viewType);
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_square_image, parent, false);
        return new CardVH(v);
    }

    @Override
    public void onBindViewHolder(CardVH holder, int position) {
        TosCard c;
        if (showFilter) {
            c = cards.get(filterIndices.get(position));
        } else {
            c = cards.get(position);
        }
        //Say.Log("bind #%d -> %s ,name = %s", position, c.id, c.name);

        holder.setCard(c);
        holder.setOnClickCard(onClick);
    }

    @Override
    public int getItemCount() {
        if (showFilter) {
            return filterIndices.size();
        } else {
            return cards.size();
        }
    }

    public void showSelection(Map<String, String> map) {
        showFilter = map != null && map.size() > 0;

        filterIndices.clear();
        if (showFilter) {
            filterIndices = TosCardFilter.me.filter(cards, map);
            notifyDataSetChanged();
            if (onFilter != null) {
                onFilter.onFiltered(filterIndices.size(), cards.size(), map);
            }
        }
    }
}

class CardVH extends RecyclerView.ViewHolder {
    private TosCardRMDKVIR cardRMDKVIR;
    private TosCard card;
    private ImageView thumb;
    private TextView text;
    private OnClickCard click;

    public interface OnClickCard {
        void onClick(int position, TosCard card);
    }

    public CardVH(View v) {
        super(v);
        thumb = v.findViewById(R.id.squareImg);
        text = v.findViewById(R.id.squareText);
    }

    public void setOnClickCard(OnClickCard onClick) {
        click = onClick;
    }

    public void setCard(TosCardRMDKVIR c) {
        cardRMDKVIR = c;
        text.setText(cardRMDKVIR.display_num);
    }

    public void setCard(TosCard c) {
        card = c;
        text.setText(card.id);
        itemView.setOnClickListener(w -> {
            Say.Log("click %s, %s", card.name, card.id);
            if (click != null) {
                click.onClick(getAdapterPosition(), card);
            }
        });
        loadImage(card.icon);
    }

    private void loadImage(String url) {
        Glide.with(thumb.getContext()).load(url)
                .apply(RequestOptions.centerCropTransform()
                        .placeholder(R.drawable.unknown_card))
                .into(thumb);
    }
}
