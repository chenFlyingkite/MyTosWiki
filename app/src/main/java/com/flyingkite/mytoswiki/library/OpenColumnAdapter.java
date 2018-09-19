package com.flyingkite.mytoswiki.library;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.flyingkite.library.widget.RVAdapter;
import com.flyingkite.mytoswiki.R;

public class OpenColumnAdapter extends RVAdapter<Character, OpenColumnAdapter.OpenVH, OpenColumnAdapter.ItemListener> {

    public interface ItemListener extends RVAdapter.ItemListener<Character, OpenVH> {

    }

    @NonNull
    @Override
    public OpenVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OpenVH(inflateView(parent, R.layout.view_open_hour));
    }

    @Override
    public void onBindViewHolder(OpenVH vh, int position) {
        boolean open = itemOf(position) != '0';
        vh.tile.setSelected(open);
    }

    public class OpenVH extends RecyclerView.ViewHolder {
        private ImageView tile;

        public OpenVH(View v) {
            super(v);
            tile = v.findViewById(R.id.openTile);
        }
    }
}
