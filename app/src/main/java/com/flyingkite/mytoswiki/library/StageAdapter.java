package com.flyingkite.mytoswiki.library;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyingkite.library.widget.RVAdapter;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.stage.Stage;
import com.flyingkite.mytoswiki.data.tos.TosCard;
import com.flyingkite.mytoswiki.tos.TosWiki;
import com.flyingkite.mytoswiki.util.GlideUtil;

public class StageAdapter extends RVAdapter<Stage, StageAdapter.StageVH, StageAdapter.ItemListener> {

    public interface ItemListener extends RVAdapter.ItemListener<Stage, StageVH> {

    }

    @NonNull
    @Override
    public StageVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StageVH(inflateView(parent, holderLayoutId()));
    }

    @LayoutRes
    public int holderLayoutId() {
        return R.layout.view_stage_row;
    }

    @Override
    public void onBindViewHolder(StageVH vh, int position) {
        super.onBindViewHolder(vh, position);
        vh.setStage(itemOf(position));
    }

    public class StageVH extends RecyclerView.ViewHolder implements GlideUtil {
        private final ImageView icon;
        private final TextView name;

        public StageVH(View v) {
            super(v);
            icon = v.findViewById(R.id.stageIcon);
            name = v.findViewById(R.id.stageName);
        }

        public void setStage(Stage s) {
            TosCard c = TosWiki.getCardByIdNorm(s.icon);
            loadCardToImageView(icon, c);
            name.setText(s.name);
        }
    }
}
