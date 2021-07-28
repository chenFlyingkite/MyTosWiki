package com.flyingkite.mytoswiki.library;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyingkite.library.widget.RVAdapter;
import com.flyingkite.mytoswiki.App;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.stage.Stage;
import com.flyingkite.mytoswiki.data.stage.StageGroup;
import com.flyingkite.mytoswiki.util.PageUtil;
import com.flyingkite.mytoswiki.util.ViewUtil;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class StageGroupAdapter extends RVAdapter<StageGroup, StageGroupAdapter.StageGVH, StageGroupAdapter.ItemLisntener> implements PageUtil {
    public interface ItemLisntener extends RVAdapter.ItemListener<StageGroup, StageGVH> {

    }

    @NonNull
    @Override
    public StageGVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StageGVH(inflateView(parent, R.layout.view_stage_group_row));
    }

    @Override
    public void onBindViewHolder(StageGVH vh, int position) {
        super.onBindViewHolder(vh, position);
        vh.setStageGroup(itemOf(position));
    }

    public class StageGVH extends RecyclerView.ViewHolder implements ViewUtil {
        private final TextView title;
        private final LinearLayout items;
        private final View line;

        public StageGVH(View v) {
            super(v);
            title = v.findViewById(R.id.stageGroupTitle);
            items = v.findViewById(R.id.stageGroupItems);
            line = v.findViewById(R.id.stageLine);
        }

        public void setStageGroup(StageGroup s) {
            int green = App.res().getColor(R.color.green);
            setTextOrHide(title, s.group);
            title.setGravity(Gravity.LEFT);
            title.setTextColor(green);
            //title.setTextSize(TypedValue.COMPLEX_UNIT_PX, App.getDimensionPixelSizeF(R.dimen.t23dp));

            line.setBackgroundColor(green);

            StageAdapter a = new StageAdapter();
            a.setDataList(s.stages);
            a.setItemListener(new StageAdapter.ItemListener() {
                @Override
                public void onClick(Stage stage, StageAdapter.StageVH holder, int position) {
                    viewLinkAsWebDialog(stage.link);
                }
            });
            fillItems(items, a);
        }
    }
}
