package com.flyingkite.mytoswiki.page;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyingkite.TosCardN;
import com.flyingkite.library.Say;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.TosCard;

public class CardVH extends RecyclerView.ViewHolder {
    private TosCard card;
    private TosCardN cardN;
    ImageView thumb;
    TextView text;
    private OnClickCard click;

    public interface OnClickCard {
        void onClick(int position, TosCardN card);
    }

    public CardVH(View v) {
        super(v);
        thumb = v.findViewById(R.id.squareImg);
        text = v.findViewById(R.id.squareText);
    }

    public void setCard(TosCard card) {
        this.card = card;
        //text.setText(card.name);
        text.setText(card.display_num);
    }

    public void setOnClickCard(OnClickCard onClick) {
        click = onClick;
    }

    public void setCardN(TosCardN card) {
        cardN = card;
        text.setText(card.id);
        itemView.setOnClickListener(w -> {
            Say.Log("click %s, %s", cardN.name, cardN.id);
            if (click != null) {
                click.onClick(getAdapterPosition(), cardN);
            }
        });
    }
}
