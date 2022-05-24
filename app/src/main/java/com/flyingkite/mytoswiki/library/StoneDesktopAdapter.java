package com.flyingkite.mytoswiki.library;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.StoneDesktop;
import com.flyingkite.mytoswiki.tos.Runestones;
import com.flyingkite.mytoswiki.util.ViewUtil;

import flyingkite.library.androidx.recyclerview.RVAdapter;

public class StoneDesktopAdapter extends RVAdapter<StoneDesktop, StoneDesktopAdapter.StoneDesktopVH, StoneDesktopAdapter.ItemListener> {

    public interface ItemListener extends RVAdapter.ItemListener<StoneDesktop, StoneDesktopVH> {
        default void onClickShare(StoneDesktop item, StoneDesktopVH vh, View v, int position) {}
    }

    @NonNull
    @Override
    public StoneDesktopVH onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        return new StoneDesktopVH(inflateView(parent, R.layout.view_desktop));
    }

    @Override
    public void onBindViewHolder(StoneDesktopVH vh, int position) {
        super.onBindViewHolder(vh, position);
        StoneDesktop s = itemOf(position);
        vh.share.setOnClickListener((v) -> {
            if (onItem != null) {
                onItem.onClickShare(s, vh, v, position);
            }
        });
        vh.title.setText(s.detail);
        vh.setData(s.stones);
    }

    public static class StoneDesktopVH extends RecyclerView.ViewHolder implements ViewUtil {
        private final View share;
        private final TextView title;
        private final ViewGroup stone;

        public StoneDesktopVH(@NonNull View v) {
            super(v);
            share = v.findViewById(R.id.deskShare);
            title = v.findViewById(R.id.deskTitle);
            stone = v.findViewById(R.id.deskDesktop);
        }

        public void setData(String data) {
            RunestoneAdapter a = new RunestoneAdapter();
            a.setDataList(Runestones.toList(data));
            fillItems(stone, a);
        }
    }
}
