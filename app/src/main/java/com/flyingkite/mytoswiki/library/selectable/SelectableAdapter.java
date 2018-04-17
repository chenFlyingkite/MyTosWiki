package com.flyingkite.mytoswiki.library.selectable;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flyingkite.mytoswiki.R;
import com.flyingkite.util.ListUtil;

import java.util.ArrayList;
import java.util.List;

public class SelectableAdapter extends RecyclerView.Adapter<SelectableAdapter.SelectableVH> implements ListUtil {
    public interface ItemListener {
        void onClick(String item, SelectableVH holder, int position);
    }

    private static final int NO_POS = RecyclerView.NO_POSITION;
    private List<String> dataList = new ArrayList<>();
    private ItemListener onClick;
    private int selected = NO_POS;
    private int rows = 3;

    public void setDataList(List<String> list) {
        dataList = nonEmpty(list);
    }

    public void itemListener(ItemListener listener) {
        onClick = listener;
    }

    public void setRow(int row) {
        rows = Math.max(1, row);
    }

    @Override
    public SelectableVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_text2, parent, false);
        return new SelectableVH(v);
    }

    @Override
    public void onBindViewHolder(SelectableVH vh, int position) {
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

        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = vh.getAdapterPosition();
                if (selected == pos) {
                    selected = NO_POS;
                } else {
                    selected = pos;
                }
                notifyDataSetChanged();
                if (onClick != null) {
                    onClick.onClick(s, vh, vh.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


    public static class SelectableVH extends RecyclerView.ViewHolder {
        private TextView text;
        public SelectableVH(View v) {
            super(v);
            text = v.findViewById(R.id.simpleText);
        }
    }
}
