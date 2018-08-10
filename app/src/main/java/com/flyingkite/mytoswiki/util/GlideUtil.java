package com.flyingkite.mytoswiki.util;

import android.content.Context;
import android.widget.ImageView;

import com.flyingkite.mytoswiki.GlideApp;
import com.flyingkite.mytoswiki.R;

public interface GlideUtil {

    default void loadCraftToImageView(ImageView img, String link) {
        loadLinkToImageView(img, link, img.getContext(), R.drawable.unknown_craft);
    }

    default void loadCardToImageView(ImageView img, String link) {
        loadLinkToImageView(img, link, img.getContext(), R.drawable.unknown_card);
    }

    default void loadLinkToImageView(ImageView img, String link, Context context, int holderId) {
        GlideApp.with(context).load(link).centerCrop().placeholder(holderId).into(img);
    }

    default void loadLinkToImageView(ImageView img, String link, Context context) {
        GlideApp.with(context).load(link).into(img);
    }

}
