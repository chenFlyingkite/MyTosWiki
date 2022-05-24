package com.flyingkite.mytoswiki.library;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flyingkite.mytoswiki.App;
import com.flyingkite.mytoswiki.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import flyingkite.library.androidx.recyclerview.RVAdapter;

public class StaminaAdapter extends RVAdapter<String, StaminaAdapter.StaminaVH, StaminaAdapter.ItemListener> {
    public interface ItemListener extends RVAdapter.ItemListener<String, StaminaVH> {

    }

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.US);

    // 1st item's time
    private long startTime = new Date().getTime();
    public void setStartTime(long time) {
        startTime = time;
    }

    // Row's time gap
    private long periodTime = 30*60*1000; // 30 min
    public void setPeriod(long period) {
        periodTime = period;
    }

    // Target value of stamina
    private int highlight = -1;
    // Index of target in adapter
    private int hlIndex = -1;

    public void setTarget(int target) {
        highlight = target;
        int newIndex = findTarget();
        notifyItemChanged(newIndex);
        notifyItemChanged(hlIndex);
        hlIndex = newIndex;
    }

    @NonNull
    @Override
    public StaminaVH onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        return new StaminaVH(inflateView(parent, R.layout.view_stamina_row));
    }

    @Override
    public void onBindViewHolder(StaminaVH vh, int position) {
        super.onBindViewHolder(vh, position);
        int p = position - 1;
        String s = itemOf(p);
        if (position == 0) {
            vh.stamina.setText(R.string.stamina);
            vh.date.setText(R.string.date);
            vh.time.setText("\uD83C\uDF1E \uD83C\uDF1B"); // Sun, Moon
        } else {
            Date d = new Date(startTime + periodTime * p);

            vh.stamina.setText(s);
            // Set text color
            int tColor = Color.TRANSPARENT;
            if (p == hlIndex) { // Target stamina
                tColor = App.res().getColor(R.color.green);
            } else if (position == 1) { // 1st one
                tColor = App.res().getColor(R.color.orange);
            } else if (d.getHours() < 1 && d.getMinutes() < 31) { // date line
                tColor = App.res().getColor(R.color.skyBlue);
            }
            vh.itemView.setBackgroundColor(tColor);

            vh.date.setText(format.format(d));
            vh.time.setText(isAtNight(d) ? "\uD83C\uDF1B \uD83C\uDF03" : "\uD83C\uDF1E \uD83C\uDF07");
        }
    }

    private int findTarget() {
        for (int i = 0; i < getItemCount(); i++) {
            int si = Integer.parseInt(itemOf(i));
            if (si >= highlight) {
                return i;
            }
        }
        return -1;
    }

    private boolean isAtNight(Date d) {
        int h = d.getHours();
        return h <= 5 || 18 <= h;
    }

    public static class StaminaVH extends RecyclerView.ViewHolder {
        private final TextView stamina;
        private final TextView date;
        private final TextView time;

        public StaminaVH(@NonNull View v) {
            super(v);
            date = v.findViewById(R.id.sdDateTime);
            time = v.findViewById(R.id.sdTime2);
            stamina = v.findViewById(R.id.sdStamina);
        }
    }
}
