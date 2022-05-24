package com.flyingkite.mytoswiki.library.selectable;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.flyingkite.mytoswiki.R;

import flyingkite.library.androidx.recyclerview.RVAdapter;

public class SelectableAdapter extends RVAdapter<String, SelectableAdapter.SelectableVH, SelectableAdapter.ItemListener> {
    public interface ItemListener extends RVAdapter.ItemListener<String, SelectableVH> {
        //void onClick(String item, SelectableVH holder, int position);
    }

    private static final int NO_POS = RecyclerView.NO_POSITION;
    private int selected = NO_POS;
    private int rows = 3;

    public void setRow(int row) {
        rows = Math.max(1, row);
    }

    @Override
    public SelectableVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SelectableVH(inflateView(parent, R.layout.view_text2));
    }

    @Override
    public void onBindViewHolder(SelectableVH vh, int position) {
        super.onBindViewHolder(vh, position);
        final String s = dataList.get(position);
        vh.text.setText(s);

        boolean sel = false;
        if (selected >= 0) {
            int selRow = selected % rows;
            int posRow = position % rows;
            int selCol = selected / rows;
            int posCol = position / rows;
            boolean sameRow = posCol == selCol;
            boolean sameCol = selRow == posRow;
            sel = sameRow || sameCol;
        }
        vh.itemView.setSelected(sel);
    }

    @Override
    protected void onDidClickItem(String item, SelectableVH vh) {
        int pos = vh.getAdapterPosition();
        if (selected == pos) {
            selected = NO_POS;
        } else {
            selected = pos;
        }
        notifyDataSetChanged();
    }

    public static class SelectableVH extends RecyclerView.ViewHolder {
        private final TextView text;
        public SelectableVH(View v) {
            super(v);
            text = v.findViewById(R.id.simpleText);
        }
    }
}
