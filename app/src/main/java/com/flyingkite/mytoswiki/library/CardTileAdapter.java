package com.flyingkite.mytoswiki.library;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.tos.TosCard;
import com.flyingkite.mytoswiki.util.TosPageUtil;

public abstract class CardTileAdapter extends CardLiteAdapter implements TosPageUtil {
    public interface ItemListener extends CardLiteAdapter.ItemListener {

    }

    @NonNull
    @Override
    public TileVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TileVH(inflateView(parent, R.layout.view_simple_icon));
    }

    public class TileVH extends CardLVH {

        public TileVH(View v) {
            super(v);
        }

        @Override
        public void setCard(TosCard c) {
            setSimpleCard(img, c);
        }
    }
}
