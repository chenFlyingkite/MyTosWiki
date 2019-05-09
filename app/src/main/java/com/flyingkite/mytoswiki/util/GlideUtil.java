package com.flyingkite.mytoswiki.util;

import android.content.Context;
import android.widget.ImageView;

import com.flyingkite.mytoswiki.GlideApp;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.tos.TosCard;

public interface GlideUtil {

    default void loadCraftToImageView(ImageView img, String link) {
        loadLinkToImageView(img, link, img.getContext(), R.drawable.unknown_craft);
    }

//    default void loadCardToImageView(ImageView img, String link) {
//        loadLinkToImageView(img, link, img.getContext(), R.drawable.unknown_card);
//    }

    default void loadCardToImageView(ImageView img, TosCard card) {
        int h = R.drawable.unknown_card;

        if (card == null) {
            img.setImageResource(h);
            return;
        }

        switch (card.attribute) {
            case "水": h = R.drawable.empty_w; break;
            case "火": h = R.drawable.empty_f; break;
            case "木": h = R.drawable.empty_e; break;
            case "光": h = R.drawable.empty_l; break;
            case "暗": h = R.drawable.empty_d; break;
        }

        loadLinkToImageView(img, card.icon, img.getContext(), h);
    }

    default void loadLinkToImageView(ImageView img, String link, Context context, int holderId) {
        //Glide.with(context).load(link).centerCrop().placeholder(holderId).animate(R.anim.fadein).into(img);
        GlideApp.with(context).load(link).centerCrop().placeholder(holderId).into(img);
    }

    default void loadLinkToImageView(ImageView img, String link, Context context) {
        //Glide.with(context).load(link).animate(R.anim.fadein).into(img);
        GlideApp.with(context).load(link).into(img);
    }

}
