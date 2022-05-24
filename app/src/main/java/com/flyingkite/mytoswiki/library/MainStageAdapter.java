package com.flyingkite.mytoswiki.library;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.stage.MainStage;
import com.flyingkite.mytoswiki.util.PageUtil;
import com.flyingkite.mytoswiki.util.ViewUtil;

import flyingkite.library.androidx.recyclerview.RVAdapter;

public abstract class MainStageAdapter extends RVAdapter<MainStage, MainStageAdapter.MStageVH, MainStageAdapter.ItemListener> implements PageUtil {

    public interface ItemListener extends RVAdapter.ItemListener<MainStage, MStageVH> {

    }

    @NonNull
    @Override
    public MStageVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MStageVH(inflateView(parent, R.layout.view_stage_group_row));
    }

    @Override
    public void onBindViewHolder(MStageVH vh, int position) {
        super.onBindViewHolder(vh, position);
        vh.setMainStage(itemOf(position));
    }

    public class MStageVH extends RecyclerView.ViewHolder implements ViewUtil {
        private final TextView title;
        private final LinearLayout items;

        public MStageVH(View v) {
            super(v);
            title = v.findViewById(R.id.stageGroupTitle);
            items = v.findViewById(R.id.stageGroupItems);
        }

        public void setMainStage(MainStage s) {
            setTextOrHide(title, s.title);

            StageGroupAdapter a = new StageGroupAdapter() {
                @Override
                public Activity getActivity() {
                    return MainStageAdapter.this.getActivity();
                }
            };
            a.setDataList(s.substages);
            fillItems(items, a);
        }
    }
}
