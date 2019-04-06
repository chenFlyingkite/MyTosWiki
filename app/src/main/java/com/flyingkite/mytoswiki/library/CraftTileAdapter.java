package com.flyingkite.mytoswiki.library;

import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.tos.BaseCraft;
import com.flyingkite.mytoswiki.util.TosPageUtil;

public abstract class CraftTileAdapter extends CraftLiteAdapter implements TosPageUtil {
    public interface ItemListener extends CraftLiteAdapter.ItemListener {

    }

    @NonNull
    @Override
    public TileVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TileVH(inflateView(parent, R.layout.view_simple_icon));
    }

    public class TileVH extends BCraftVH {

        public TileVH(View v) {
            super(v);
        }

        @Override
        public void setCraft(BaseCraft c) {
            setSimpleCraft(img, c);
        }
    }
}
