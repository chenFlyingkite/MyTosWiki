package com.flyingkite.mytoswiki.library;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.util.ListUtil;

import java.util.ArrayList;
import java.util.List;

public class IconAdapter extends RecyclerView.Adapter<IconAdapter.IconVH> implements ListUtil {
    public interface ItemListener {
        void onClick(String name, IconVH vh, int position);
    }

    private List<String> icons = new ArrayList<>();
    private int layout = R.layout.view_simple_icon;
    private ItemListener onClick;

    public void setIcons(List<String> list) {
        icons = nonNull(list);
    }

    private void setLayout(@LayoutRes int id) {
        layout = id;
    }

    public void setItemListener(ItemListener listener) {
        onClick = listener;
    }

    @Override
    public IconVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new IconVH(v);
    }

    @Override
    public void onBindViewHolder(IconVH vh, int position) {
        Context c = vh.itemView.getContext();
        String cid = icons.get(position);
        Glide.with(c).load(cid).apply(RequestOptions.centerCropTransform()
                .placeholder(R.drawable.unknown_card))
                .into(vh.icon);
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClick != null) {
                    onClick.onClick(cid, vh, vh.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return icons.size();
    }

    public static class IconVH extends RecyclerView.ViewHolder {
        private ImageView icon;

        public IconVH(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.itemIcon);
        }
    }
}
