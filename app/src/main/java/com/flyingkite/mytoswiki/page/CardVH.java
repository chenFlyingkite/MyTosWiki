package com.flyingkite.mytoswiki.page;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyingkite.library.Say;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.TosWiki;
import com.flyingkite.mytoswiki.data.TosCard;

class CardVH extends RecyclerView.ViewHolder {
    private TosCard card;
    ImageView thumb;
    TextView text;

    public CardVH(View v) {
        super(v);
        thumb = v.findViewById(R.id.squareImg);
        text = v.findViewById(R.id.squareText);
        v.setOnClickListener(w -> {
            Say.Log("click %s", text);
            TosWiki.ff();
        });
    }

    public void setCard(TosCard card) {
        this.card = card;
        //text.setText(card.name);
        text.setText(card.display_num);
    }
}
