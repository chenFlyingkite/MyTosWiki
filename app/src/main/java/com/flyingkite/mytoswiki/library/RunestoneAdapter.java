package com.flyingkite.mytoswiki.library;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.flyingkite.library.widget.RVAdapter;
import com.flyingkite.mytoswiki.App;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.tos.Runestones;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import flyingkite.math.MathUtil;

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
        vh.stone.setBackgroundColor(App.res().getColor(bgColors[pos % bgColors.length]));
    }

    public void setStone(int index, Character c) {
        if (!MathUtil.isInRange(index, 0, getItemCount())) return;

        dataList.set(index, c);
        notifyItemChanged(index);
    }

    public char[] getStoneChars() {
        int n = getItemCount();
        char[] cs = new char[n];
        for (int i = 0; i < n; i++) {
            cs[i] = dataList.get(i);
        }
        return cs;
    }

    public String getStones() {
        StringBuilder s = new StringBuilder("");
        for (int i = 0; i < dataList.size(); i++) {
            s.append(dataList.get(i));
        }
        return s.toString();
    }

    public void setStones(String s) {
        char[] cs = s.toCharArray();
        dataList.clear();
        for (int i = 0; i < cs.length; i++) {
            dataList.add(cs[i]);
        }
        notifyDataSetChanged();
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
