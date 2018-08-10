package com.flyingkite.mytoswiki.library;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.flyingkite.library.widget.Library;
import com.flyingkite.library.widget.RVAdapter;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.tos.Evolve;
import com.flyingkite.mytoswiki.data.tos.TosCard;
import com.flyingkite.mytoswiki.tos.TosWiki;
import com.flyingkite.mytoswiki.util.GlideUtil;
import com.flyingkite.mytoswiki.util.TosPageUtil;
import com.flyingkite.mytoswiki.util.ViewUtil;

import java.util.List;

public abstract class CardEvolveAdapter extends RVAdapter<Evolve, CardEvolveAdapter.EvolveVH, CardEvolveAdapter.ItemListener> implements TosPageUtil {
    public interface ItemListener extends RVAdapter.ItemListener<Evolve, EvolveVH> {

    }

    @NonNull
    @Override
    public EvolveVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EvolveVH(inflateView(parent, R.layout.view_card_row_evolve));
    }

    @Override
    public void onBindViewHolder(EvolveVH vh, int position) {
        Evolve ev = itemOf(position);
        vh.setEvolve(ev);
    }

    public class EvolveVH extends RecyclerView.ViewHolder implements GlideUtil, ViewUtil {
        private ImageView from;
        private ImageView to;
        private Library<CardLiteAdapter> needLibrary;

        public EvolveVH(View v) {
            super(v);
            from = v.findViewById(R.id.cardEvolveFrom);
            to = v.findViewById(R.id.cardEvolveTo);
            needLibrary = new Library<>(v.findViewById(R.id.cardEvolveNeed));
        }

        public void setEvolve(Evolve ev) {
            TosCard fc = TosWiki.getCardByIdNorm(ev.evolveFrom);
            TosCard tc = TosWiki.getCardByIdNorm(ev.evolveTo);
            setSimpleCard(from, fc);
            setSimpleCard(to, tc);
            List<TosCard> needs = getCardsByIdNorms(ev.evolveNeed);
            CardLiteAdapter a = new CardLiteAdapter();
            a.setDataList(needs);
            a.setItemListener((tosCard, cardLVH, i) -> {
                showCardDialog(tosCard);
            });
            needLibrary.setViewAdapter(a);
            RecyclerView rv = needLibrary.recyclerView;
            // To allow recycler view be scrollable inside ScrollView & HorizontalScrollView
            //rv.removeOnItemTouchListener(noIntercept);
            //rv.addOnItemTouchListener(noIntercept);
        }
    }
}
