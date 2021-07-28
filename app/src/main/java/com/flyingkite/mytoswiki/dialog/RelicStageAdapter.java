package com.flyingkite.mytoswiki.dialog;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyingkite.library.widget.RVAdapter;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.stage.RelicStage;
import com.flyingkite.mytoswiki.data.tos.TosCard;
import com.flyingkite.mytoswiki.tos.TosWiki;
import com.flyingkite.mytoswiki.util.GlideUtil;
import com.flyingkite.mytoswiki.util.PageUtil;

public abstract class RelicStageAdapter extends RVAdapter<RelicStage, RelicStageAdapter.RelicVH, RelicStageAdapter.ItemListener> implements PageUtil {
    public interface ItemListener extends RVAdapter.ItemListener<RelicStage, RelicVH> {

    }

    @NonNull
    @Override
    public RelicVH onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        return new RelicVH(inflateView(parent, R.layout.view_relic_stage_row));
    }

    @Override
    public void onBindViewHolder(RelicVH vh, int position) {
        super.onBindViewHolder(vh, position);
        RelicStage s = itemOf(position);
        vh.setStage(s);
    }

    public class RelicVH extends RecyclerView.ViewHolder implements GlideUtil {
        private final ImageView icon;
        private final TextView name;
        private final TextView price;

        public RelicVH(View v) {
            super(v);
            icon = v.findViewById(R.id.stageIcon);
            name = v.findViewById(R.id.stageName);
            price = v.findViewById(R.id.stagePrice);
        }

        @SuppressLint("SetTextI18n")
        public void setStage(RelicStage s) {
            TosCard c = TosWiki.getCardByIdNorm(s.icon);
            loadCardToImageView(icon, c);
            name.setText(s.name);
            price.setText(s.coin / 10000 + "萬");
        }
    }
}
