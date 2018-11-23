package com.flyingkite.mytoswiki.library;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flyingkite.mytoswiki.App;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.tos.TosMath;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MonsterLvAdapter extends RecyclerView.Adapter<MonsterLvAdapter.MonsterLvVH> {
    public interface ItemListener {
        void onClick(String item, MonsterLvVH holder, int position);
    }

    private static final int NO_POS = RecyclerView.NO_POSITION;

    private static final int[] CHILD_IDS = {R.id.mld_row_level, R.id.mld_row_exp_dx, R.id.mld_row_exp_sum
            //, R.id.mld_row_stamina, R.id.mld_row_team_cost, R.id.mld_row_team_slot
    };
    private final String[] header = _headerZh;//TosSummonerLevel.headerZh;
    private static final int MAX_LV = 100;
    private static final int SPAN = 3;
    private long[][] table;// = ;//TosSummonerLevel.table;
    private static final String[] _headerZh = {"等級", "升到下級", "累計經驗"};//, "體力上限", "隊伍空間", "隊伍數"};
    private static final String[] _header = {"Level", "EXP to next", "Accumulated EXP"};//, "Stamina", "Team Cost Limit", "Team Slots"};
    private int curve10K;

    private ItemListener onClick;
    private int selected = NO_POS;
    private int[] bgs = new int[5];

    public MonsterLvAdapter() {
        Arrays.fill(bgs, R.color.transparent);
        bgs[2] = R.color.skyBlue4;
        bgs[0] = R.color.skyBlue;
        setExpCurve(50);
    }

    public void setExpCurve(int of10K) {
        curve10K = of10K;
        table = new long[MAX_LV][3];
        for (int i = 1; i < MAX_LV; i++) {
            long sumExp = TosMath.getExpSum(i, curve10K);
            table[i][0] = i;
            table[i-1][1] = sumExp - table[i - 1][2];
            table[i][2] = sumExp;
        }
        notifyDataSetChanged();
    }

    public void itemListener(ItemListener listener) {
        onClick = listener;
    }

    @Override
    public MonsterLvVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_monster_level, parent, false);
        return new MonsterLvVH(v);
    }

    @Override
    public void onBindViewHolder(MonsterLvVH vh, int position) {
        int bg = R.color.grey8;
        if (position <= 2) {
            vh.setStrings(header);
        } else {
            int row = position / SPAN;
            int col = position % SPAN;
            int lv = 33 * col + row;

            int n = table[lv].length;
            String[] s = new String[n];
            for (int i = 0; i < n; i++) {
                s[i] = NumberFormat.getInstance().format(table[lv][i]);
                //s[i] = "" + table[row][i];
            }
            // Erase the LV 99
            if (lv == 99) {
                s[1] = "------";
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
        return table.length + 2;
    }

    public static class MonsterLvVH extends RecyclerView.ViewHolder {
        private List<TextView> texts = new ArrayList<>();
        private View divider;

        public MonsterLvVH(View v) {
            super(v);

            for (int id : CHILD_IDS) {
                texts.add(v.findViewById(id));
            }
            divider = v.findViewById(R.id.mld_row_divider);
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
