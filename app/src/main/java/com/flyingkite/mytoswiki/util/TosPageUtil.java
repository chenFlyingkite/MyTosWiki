package com.flyingkite.mytoswiki.util;

import android.app.FragmentManager;
import android.os.Bundle;
import android.widget.ImageView;

import com.flyingkite.library.log.Loggable;
import com.flyingkite.mytoswiki.data.tos.TosCard;
import com.flyingkite.mytoswiki.dialog.CardDialog;
import com.flyingkite.mytoswiki.tos.TosWiki;

import java.util.ArrayList;
import java.util.List;

public interface TosPageUtil extends Loggable, GlideUtil {

    FragmentManager getFragmentManager();

    default CardDialog showCardDialog(TosCard card) {
        CardDialog d = new CardDialog();
        Bundle b = new Bundle();
        b.putParcelable(CardDialog.BUNDLE_CARD, card);
        d.setArguments(b);
        d.show(getFragmentManager(), CardDialog.TAG);
        return d;
    }

    default void showCardDialog(String idNorm) {
        TosCard c = TosWiki.getCardByIdNorm(idNorm);
        if (c == null) {
            logW("card %s not ready", idNorm);
            return;
        }
        showCardDialog(c);
    }

    default void setSimpleCard(ImageView view, TosCard c) {
        loadCardToImageView(view, c.icon);
        view.setOnClickListener((v) -> {
            showCardDialog(c);
        });
    }

    default List<TosCard> getCardsByIdNorms(List<String> idNorms) {
        List<TosCard> ans = new ArrayList<>();
        for (int i = 0; i < idNorms.size(); i++) {
            String idNorm = idNorms.get(i);
            ans.add(TosWiki.getCardByIdNorm(idNorm));
        }
        return ans;
    }
}
