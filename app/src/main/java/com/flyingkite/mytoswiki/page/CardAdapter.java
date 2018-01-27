package com.flyingkite.mytoswiki.page;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.flyingkite.library.Say;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.TosCard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class CardAdapter extends RecyclerView.Adapter<CardVH> {
    private List<TosCard> cards = new ArrayList<>();

    public void setCards(TosCard[] cards) {
        this.cards = cards == null ? new ArrayList<>() : Arrays.asList(cards);
    }

    @Override
    public CardVH onCreateViewHolder(ViewGroup parent, int viewType) {
        //Say.LogF("create type #%s", viewType);
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_square_image, parent, false);
        return new CardVH(v);
    }

    @Override
    public void onBindViewHolder(CardVH holder, int position) {
        Context ctx = holder.thumb.getContext();
        TosCard c = cards.get(position);
        Say.Log("bind #%2d -> %s ,name = %s", position, c.icon_url, c.name);
        if (position % 2 == 0) {
//            Glide.with(ctx)
//                    //.load(Uri.parse())
//                    .load(c.icon_url).into(holder.thumb);
        } else {
            //Picasso.with(ctx).load(c.icon_url).into(holder.thumb);
        }

        Glide.with(ctx).load(c.icon_url)
                .apply(RequestOptions.centerCropTransform()
                        .placeholder(R.drawable.unkown_card))
                .into(holder.thumb);
        holder.setCard(c);
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }
}
