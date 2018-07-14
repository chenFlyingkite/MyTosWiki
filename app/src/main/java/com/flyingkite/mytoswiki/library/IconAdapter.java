package com.flyingkite.mytoswiki.library;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;

import com.flyingkite.library.widget.IconRVAdapter;
import com.flyingkite.mytoswiki.R;

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
