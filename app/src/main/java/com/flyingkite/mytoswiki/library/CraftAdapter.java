package com.flyingkite.mytoswiki.library;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.tos.BaseCraft;
import com.flyingkite.mytoswiki.tos.query.AllCards;
import com.flyingkite.mytoswiki.util.GlideUtil;
import com.flyingkite.util.select.SelectedData;
import com.flyingkite.util.select.Selector;

import java.util.ArrayList;
import java.util.List;

import flyingkite.library.android.util.ThreadUtil;
import flyingkite.library.androidx.recyclerview.RVAdapter;
import flyingkite.library.androidx.recyclerview.RVSelectAdapter;

public class CraftAdapter extends RVSelectAdapter<BaseCraft, CraftAdapter.CraftVH, CraftAdapter.ItemListener> {

    public interface ItemListener extends RVAdapter.ItemListener<BaseCraft, CraftVH> {
        default void onFiltered(int selected, int total) {}
    }

    private int nameType = Misc.NT_ID_NORM;

    private Selector<BaseCraft> selection; // It is the selection on crafts, but they have same item
    private List<SelectedData> selectedResult = new ArrayList<>();
    private Runnable selectTask;

    @Override
    public boolean hasSelection() {
        return selectTask != null;
    }

    public void setSelection(Selector<BaseCraft> s) {
        if (selection != null) {
            selection.setCancelled(true);
        }
        selectTask = getSearchTask(s);
        ThreadUtil.cachedThreadPool.submit(selectTask);
    }

    private Runnable getSearchTask(Selector<BaseCraft> s) {
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

    public String name(BaseCraft c) {
        int it = nameType;
        if (it == Misc.NT_NAME) {
            return c.name;
        } else {
            return c.idNorm;
        }
    }

    public void updateSelection() {
        setSelection(selection);
    }

    private void notifyFiltered() {
        if (onItem != null) {
            onItem.onFiltered(selectedIndices.size(), dataList.size());
        }
    }

    @NonNull
    @Override
    public CraftVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CraftVH(inflateView(parent, R.layout.view_tos_item_craft));
    }

    @Override
    public void onBindViewHolder(@NonNull CraftVH holder, int position) {
        super.onBindViewHolder(holder, position);
        BaseCraft c = itemOf(position);
        String msg = null;
        if (selectedResult != null && position < selectedResult.size()) {
            msg = selectedResult.get(position).message;
        }
        holder.setCard(c, name(c), msg);
    }

    @Override
    protected void onDidClickItem(BaseCraft c, CraftVH holder) {
    }

    public static class CraftVH extends RecyclerView.ViewHolder implements GlideUtil {
        private final ImageView thumb;
        private final TextView text;
        private final TextView message;

        public CraftVH(View v) {
            super(v);
            text = v.findViewById(R.id.tiText);
            thumb = v.findViewById(R.id.tiImg);
            message = v.findViewById(R.id.tiMessage);
        }

        public void setCard(BaseCraft c, String name, String msg) {
            boolean hasMsg = msg != null;
            text.setText(name);
            message.setText(msg);
            loadCraftToImageView(thumb, c.icon.iconLink);
            setVisible(text, !hasMsg);
            setVisible(message, hasMsg);
        }

        private void setVisible(View v, boolean visible) {
            if (v != null) {
                v.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
        }
    }
}

