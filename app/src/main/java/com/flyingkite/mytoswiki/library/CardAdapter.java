package com.flyingkite.mytoswiki.library;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyingkite.library.log.Loggable;
import com.flyingkite.library.util.ThreadUtil;
import com.flyingkite.library.widget.RVSelectAdapter;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.tos.TosCard;
import com.flyingkite.mytoswiki.tos.query.AllCards;
import com.flyingkite.mytoswiki.util.GlideUtil;
import com.flyingkite.mytoswiki.util.ViewUtil2;
import com.flyingkite.util.select.SelectedData;
import com.flyingkite.util.select.Selector;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CardAdapter extends RVSelectAdapter<TosCard, CardAdapter.CardVH, CardAdapter.ItemListener>
    implements Loggable
{

    public interface ItemListener extends RVSelectAdapter.ItemListener<TosCard, CardVH> {
        default void onFiltered(int selected, int total) {}
    }

    private int nameType = Misc.NT_ID_NORM;

    private Selector<TosCard> selection;
    private final boolean showPercentLine = true;
    private int[] colorPercent;
    private List<SelectedData> selectedResult = new ArrayList<>();
    // current selection runnable
    private Runnable selectTask;

    public CardAdapter() {
        prepareColorPercent();
    }

    private void prepareColorPercent() {
        //       black, brown, red, orange, yellow,
        //       green, blue, purple, grey, white
        String[] codes = {
                "#000000", "#B97A57", "#FF0000", "#FF8800", "#FFC90E",
                "#008800", "#0000FF", "#FF00FF", "#888888", "#BBBBBB"};
        int[] colors = new int[codes.length];
        for (int i = 0; i < codes.length; i++) {
            colors[i] = Color.parseColor(codes[i]);
        }
        colorPercent = colors;
    }

    @Override
    public boolean hasSelection() {
        return selectTask != null;
    }

    public void setSelection(Selector<TosCard> s) {
        if (selection != null) {
            selection.setCancelled(true);
        }
        selectTask = getSearchTask(s);
        ThreadUtil.cachedThreadPool.submit(selectTask);
    }

    private Runnable getSearchTask(Selector<TosCard> s) {
        return new Runnable() {
            @Override
            public void run() {
                selection = s == null ? new AllCards<>(dataList) : s;
                if (selectTask != this) return;
                // perform query and projection on index
                List<SelectedData> done = selection.query();
                List<Integer> index = SelectedData.getIndices(done);

                ThreadUtil.runOnUiThread(() -> {
                    if (selectTask != this) return;

                    selectedResult = done;
                    selectedIndices = index;
                    notifyDataSetChanged();
                    notifyFiltered();
                });
            }
        };
    }

    public void setNameType(@NameType int type) {
        nameType = type;
    }

    public String name(TosCard c) {
        int it = nameType;
        if (it == Misc.NT_NAME_ID_NORM) {
            return c.idNorm + " " + c.name;
        } else if (it == Misc.NT_NAME) {
            return c.name;
        } else {
            return c.idNorm;
        }
    }

    private void notifyFiltered() {
        if (onItem != null) {
            onItem.onFiltered(selectedResult.size(), dataList.size());
        }
    }

    @NonNull
    @Override
    public CardVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CardVH(inflateView(parent, R.layout.view_tos_item_card_white));
    }

    @Override
    public void onBindViewHolder(@NonNull CardVH h, int position) {
        super.onBindViewHolder(h, position);
        TosCard c = itemOf(position);
        //logE("bind #%4d = %s", position, c);
        String msg = null;

        if (selectedResult != null && position < selectedResult.size()) {
            msg = selectedResult.get(position).message;
        }
        h.setCard(c, name(c), msg);
        // Setup right line color
        if (showPercentLine && getItemCount() > 9) {
            setColorPercent(h.right, position, getItemCount());
        } else {
            h.right.setText("");
            h.right.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private void setColorPercent(TextView view, int position, int count) {
        int n = colorPercent.length;
        int thiz = n * (position + 0) / count;
        int next = n * (position + 1) / count;
        if (thiz != next) {
            view.setText(next + "");
            view.setBackgroundColor(colorPercent[thiz]);
        } else {
            view.setText("");
            view.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    protected void onDidClickItem(TosCard c, CardVH holder) {

    }

    public static class CardVH extends RecyclerView.ViewHolder implements GlideUtil, ViewUtil2 {
        private final ImageView thumb;
        private final TextView text;
        private final TextView message;
        private final TextView right;

        public CardVH(View v) {
            super(v);
            text = v.findViewById(R.id.tiText);
            thumb = v.findViewById(R.id.tiImg);
            right = v.findViewById(R.id.tiRightLine);
            message = v.findViewById(R.id.tiMessage);
        }

        public void setCard(TosCard c, String name, String msg) {
            boolean hasMsg = msg != null;
            text.setText(name);
            message.setText(msg);
            loadCardToImageView(thumb, c);
            setViewVisibility(text, !hasMsg);
            setViewVisibility(message, hasMsg);
        }
    }
}

