package com.flyingkite.mytoswiki.library;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.flyingkite.library.widget.RVAdapter;
import com.flyingkite.mytoswiki.R;

public class IconAdapter extends RVAdapter<Integer, IconAdapter.IconVH, IconAdapter.ItemListener> {
    public interface ItemListener extends RVAdapter.ItemListener<Integer, IconVH> {
        //void onClick(String name, IconVH vh, int position);
    }

    @Override
    public IconVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new IconVH(inflateView(parent, R.layout.view_simple_icon));
    }

    @Override
    public void onBindViewHolder(IconVH vh, int position) {
        super.onBindViewHolder(vh, position);
        Context c = vh.itemView.getContext();
        int cid = itemOf(position);
        vh.icon.setImageResource(cid);
        // This is string part
        //String cid = itemOf(position);
//        Glide.with(c).load(cid)
//                .apply(RequestOptions.centerCropTransform().placeholder(R.drawable.unknown_card))
//                .into(vh.icon);
    }

    public static class IconVH extends RecyclerView.ViewHolder {
        private ImageView icon;

        public IconVH(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.itemIcon);
        }
    }
}
