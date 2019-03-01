package com.flyingkite.mytoswiki.library;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.flyingkite.library.widget.RVAdapter;
import com.flyingkite.mytoswiki.App;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.tos.Runestones;

public class RunestoneAdapter extends RVAdapter<Character, RunestoneAdapter.RunestoneVH, RunestoneAdapter.ItemListener> {

    public interface ItemListener extends RVAdapter.ItemListener<Character, RunestoneVH> {

    }

    private int row = 6;
    private final int[] bgColors = {R.color.brown2, R.color.brown1};

    @NonNull
    @Override
    public RunestoneVH onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        return new RunestoneVH(inflateView(parent, R.layout.view_runestone));
    }

    public void setRow(int r) {
        row = r;
    }

    @Override
    public void onBindViewHolder(RunestoneVH vh, int position) {
        super.onBindViewHolder(vh, position);
        char c = itemOf(position);
        int r = Math.max(row, 2); // at least 2
        vh.setStone(c);
        int pos = position + position / r % 2;
        vh.stone.setBackgroundColor(App.getColorF(bgColors[pos % bgColors.length]));
    }

    public static class RunestoneVH extends RecyclerView.ViewHolder {
        private ImageView stone;

        public RunestoneVH(@NonNull View v) {
            super(v);
            stone = v.findViewById(R.id.stone);
        }

        public void setStone(char c) {
            int id = Runestones.imageId(c);
            stone.setImageResource(id);
        }
    }
}
