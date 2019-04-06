package com.flyingkite.mytoswiki.library;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.flyingkite.library.widget.RVAdapter;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.tos.BaseCraft;
import com.flyingkite.mytoswiki.util.GlideUtil;

public class CraftLiteAdapter extends RVAdapter<BaseCraft, CraftLiteAdapter.BCraftVH, CraftLiteAdapter.ItemListener> {

    public interface ItemListener extends RVAdapter.ItemListener<BaseCraft, BCraftVH> {

    }

    @NonNull
    @Override
    public BCraftVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BCraftVH(inflateView(parent, R.layout.view_simple_icon));
    }

    @Override
    public void onBindViewHolder(BCraftVH vh, int position) {
        super.onBindViewHolder(vh, position);
        vh.setCraft(itemOf(position));
    }

    public static class BCraftVH extends RecyclerView.ViewHolder implements GlideUtil {
        protected ImageView img;

        public BCraftVH(View v) {
            super(v);
            img = v.findViewById(R.id.itemIcon);
        }

        public void setCraft(BaseCraft c) {
            loadCraftToImageView(img, c.icon.iconLink);
        }
    }
}
