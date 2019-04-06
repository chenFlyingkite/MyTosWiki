package com.flyingkite.mytoswiki.library;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flyingkite.mytoswiki.App;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.tos.TosSummonerLevel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SummonLvAdapter extends RecyclerView.Adapter<SummonLvAdapter.SummonLvVH> {
    public interface ItemListener {
        void onClick(String item, SummonLvVH holder, int position);
    }

    private static final int NO_POS = RecyclerView.NO_POSITION;

    private static final int[] CHILD_IDS = {R.id.sld_row_level, R.id.sld_row_exp_dx, R.id.sld_row_exp_sum
            , R.id.sld_row_stamina, R.id.sld_row_team_cost, R.id.sld_row_team_slot};
    private final String[] header = TosSummonerLevel.headerZh;
    private final long[][] table = TosSummonerLevel.table;
    private ItemListener onClick;
    private int selected = NO_POS;
    private int[] bgs = new int[5];

    public SummonLvAdapter() {
        Arrays.fill(bgs, R.color.transparent);
        bgs[1] = R.color.skyBlue4;
        bgs[4] = R.color.skyBlue;
    }

    public void itemListener(ItemListener listener) {
        onClick = listener;
    }

    @Override
    public SummonLvVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_summon_level, parent, false);
        return new SummonLvVH(v);
    }

    @Override
    public void onBindViewHolder(SummonLvVH vh, int position) {
        int bg = R.color.grey8;
        if (position == 0) {
            vh.setStrings(header);
        } else {
            int row = position - 1;
            int n = table[row].length;
            String[] s = new String[n];
            for (int i = 0; i < n; i++) {
                s[i] = NumberFormat.getInstance().format(table[row][i]);
                //s[i] = "" + table[row][i];
            }
            vh.setStrings(s);

            // Set background
            bg = bgs[row % 5];
        }
        vh.divider.setBackgroundColor(App.me.getResources().getColor(bg));
        vh.itemView.setSelected(selected == position);

        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newPos = vh.getAdapterPosition();
                int oldPos = selected;

                if (oldPos == newPos) {
                    selected = NO_POS;
                } else {
                    selected = newPos;
                }
                notifyItemChanged(oldPos);
                notifyItemChanged(newPos);
                if (onClick != null) {
                    onClick.onClick("", vh, vh.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return table.length + 1;
    }

    public static class SummonLvVH extends RecyclerView.ViewHolder {
        private List<TextView> texts = new ArrayList<>();
        private View divider;

        public SummonLvVH(View v) {
            super(v);

            for (int id : CHILD_IDS) {
                texts.add(v.findViewById(id));
            }
            divider = v.findViewById(R.id.sld_row_divider);
        }

        public void setStrings(String... strs) {
            for (int i = 0; i < strs.length; i++) {
                if (i < texts.size()) {
                    texts.get(i).setText(strs[i]);
                }
            }
        }

    }
}

