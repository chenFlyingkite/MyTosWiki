package com.flyingkite.mytoswiki.library;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyingkite.library.widget.RVAdapter;
import com.flyingkite.mytoswiki.App;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.stage.StageOpenTime;
import com.flyingkite.mytoswiki.data.tos.TosCard;
import com.flyingkite.mytoswiki.tos.TosWiki;
import com.flyingkite.mytoswiki.util.GlideUtil;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DailyStageAdapter extends RVAdapter<StageOpenTime, DailyStageAdapter.StageVH, DailyStageAdapter.ItemListener> {

    public interface ItemListener extends RVAdapter.ItemListener<StageOpenTime, DailyStageAdapter.StageVH> {

    }

    private int today = 0;

    public void setToday(int day) {
        today = day;
    }

    @NonNull
    @Override
    public DailyStageAdapter.StageVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DailyStageAdapter.StageVH(inflateView(parent, holderLayoutId()));
    }

    @LayoutRes
    public int holderLayoutId() {
        return R.layout.view_stage_row_2;
    }

    @Override
    public void onBindViewHolder(DailyStageAdapter.StageVH vh, int position) {
        super.onBindViewHolder(vh, position);
        vh.setStage(itemOf(position));
    }

    public class StageVH extends RecyclerView.ViewHolder implements GlideUtil {
        private final ImageView icon;
        private final TextView name;
        private final ViewGroup detail;

        public StageVH(View v) {
            super(v);
            icon = v.findViewById(R.id.stageIcon);
            name = v.findViewById(R.id.stageName);
            detail = v.findViewById(R.id.stageDetail);
        }

        public void setStage(StageOpenTime s) {
            TosCard c = TosWiki.getCardByIdNorm(s.stage.icon);
            if (c == null) {
                icon.setImageResource(R.drawable.gift);
            } else {
                loadCardToImageView(icon, c);
            }
            name.setText(s.stage.name);
            detail.removeAllViews();
            char[] cs = s.open.toCharArray();
            for (int i = 0; i < cs.length; i++) {
                View v = LayoutInflater.from(detail.getContext()).inflate(R.layout.view_open_hour, detail, false);
                ImageView img = v.findViewById(R.id.openTile);
                img.setSelected(cs[i] != '0');
                int color = Color.TRANSPARENT;
                if (i % 2 == 0) { // even number
                    color = App.res().getColor(R.color.colorPrimaryDark);
                }
                if (i == today) {
                    color = App.res().getColor(R.color.grey6);
                }
                v.setBackgroundColor(color);
                detail.addView(v);
            }
        }
    }
}

