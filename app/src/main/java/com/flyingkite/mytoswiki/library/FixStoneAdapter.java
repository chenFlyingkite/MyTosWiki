package com.flyingkite.mytoswiki.library;

import android.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flyingkite.library.util.ListUtil;
import com.flyingkite.library.widget.Library;
import com.flyingkite.library.widget.RVAdapter;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.StoneFix;
import com.flyingkite.mytoswiki.data.tos.TosCard;
import com.flyingkite.mytoswiki.tos.Runestones;
import com.flyingkite.mytoswiki.util.TosPageUtil;
import com.flyingkite.mytoswiki.util.ViewUtil;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class FixStoneAdapter extends RVAdapter<StoneFix, FixStoneAdapter.StoneFixVH, FixStoneAdapter.ItemListener> implements TosPageUtil {

    public interface ItemListener extends RVAdapter.ItemListener<StoneFix, StoneFixVH> {
        default void onClickShare(StoneFix item, StoneFixVH vh, View v, int position) {}
    }

    @NonNull
    @Override
    public StoneFixVH onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        return new StoneFixVH(inflateView(parent, R.layout.view_fix_stone));
    }

    @Override
    public void onBindViewHolder(StoneFixVH vh, int position) {
        super.onBindViewHolder(vh, position);
        StoneFix s = itemOf(position);
        vh.share.setOnClickListener((v) -> {
            if (onItem != null) {
                onItem.onClickShare(s, vh, v, position);
            }
        });
        vh.title.setText(s.desktop.detail);
        vh.setData(s);
    }

    public class StoneFixVH extends RecyclerView.ViewHolder implements ViewUtil {
        private final View share;
        private final TextView title;
        private final ViewGroup stone;
        private final Library<CardTileAdapter> cards;

        public StoneFixVH(@NonNull View v) {
            super(v);
            share = v.findViewById(R.id.deskShare);
            title = v.findViewById(R.id.deskTitle);
            stone = v.findViewById(R.id.deskDesktop);
            cards = new Library<>(v.findViewById(R.id.deskIcons));
            CardTileAdapter a = new CardTileAdapter() {
                @Override
                public FragmentManager getFragmentManager() {
                    return FixStoneAdapter.this.getFragmentManager();
                }
            };
            cards.setViewAdapter(a);
        }

        public void setData(StoneFix data) {
            List<TosCard> list = getCardsByIdNorms(ListUtil.nonNull(data.cards.split(",")));
            cards.adapter.setDataList(list);
            RunestoneAdapter a = new RunestoneAdapter();
            a.setDataList(Runestones.toList(data.desktop.stones));
            fillItems(stone, a);
        }
    }
}
