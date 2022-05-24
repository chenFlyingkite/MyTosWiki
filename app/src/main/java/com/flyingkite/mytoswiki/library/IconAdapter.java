package com.flyingkite.mytoswiki.library;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;

import com.flyingkite.mytoswiki.R;

import flyingkite.library.androidx.recyclerview.IconRVAdapter;

public class IconAdapter extends IconRVAdapter {
    protected @LayoutRes int holderLayout() {
        return R.layout.view_simple_icon;
    }

    protected @IdRes int itemId() {
        return R.id.itemIcon;
    }

    public interface ItemListener extends IconRVAdapter.ItemListener {
        //void onClick(String name, IconVH vh, int position);
        //void onHello();
    }

//    public class IconVH extends RecyclerView.ViewHolder {
//        private ImageView icon;
//
//        public IconVH(View itemView) {
//            super(itemView);
//            icon = itemView.findViewById(itemId());
//        }
//    }
}
