package com.flyingkite.mytoswiki.util;

import android.app.FragmentManager;
import android.os.Bundle;
import android.widget.ImageView;

import com.flyingkite.library.log.Loggable;
import com.flyingkite.mytoswiki.App;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.tos.BaseCraft;
import com.flyingkite.mytoswiki.data.tos.TosCard;
import com.flyingkite.mytoswiki.dialog.CardDialog;
import com.flyingkite.mytoswiki.dialog.CraftItemDialog;
import com.flyingkite.mytoswiki.tos.TosWiki;

public interface TosPageUtil extends Loggable, GlideUtil, TosWikiUtil {

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

    default CraftItemDialog showCraftDialog(BaseCraft craft) {
        CraftItemDialog d = new CraftItemDialog();
        Bundle b = new Bundle();
        b.putParcelable(CraftItemDialog.BUNDLE_CRAFT, craft);
        d.setArguments(b);
        d.show(getFragmentManager(), CraftItemDialog.TAG);
        return d;
    }

    default void showCraftDialog(String idNorm) {
        BaseCraft c = TosWiki.getCraftByIdNorm(idNorm);
        if (c == null) {
            logW("craft %s not ready", idNorm);
            return;
        }
        showCraftDialog(c);
    }

    default void setSimpleCard(ImageView view, String idNorm) {
        setSimpleCard(view, TosWiki.getCardByIdNorm(idNorm));
    }

    default void setSimpleCard(ImageView view, TosCard c) {
        loadCardToImageView(view, c);
        view.setOnClickListener((v) -> {
            if (c == null) return;
            showCardDialog(c);
        });
        view.setOnLongClickListener((v) -> {
            if (c == null) {
                App.showToastShort(R.string.unknown_card);
            } else {
                App.showToastShort("#" + c.idNorm + " " + c.name);
            }
            return true;
        });
    }

    default void setSimpleCraft(ImageView view, BaseCraft c) {
        if (c == null) return;

        loadCraftToImageView(view, c.icon.iconLink);
        view.setOnClickListener((v) -> {
            showCraftDialog(c);
        });
        view.setOnLongClickListener((v) -> {
            App.showToastShort("#" + c.idNorm + " " + c.name);
            return true;
        });
    }
}
