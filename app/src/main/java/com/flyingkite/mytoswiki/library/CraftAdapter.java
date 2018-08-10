package com.flyingkite.mytoswiki.library;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyingkite.library.util.ThreadUtil;
import com.flyingkite.library.widget.RVAdapter;
import com.flyingkite.library.widget.RVSelectAdapter;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.tos.BaseCraft;
import com.flyingkite.mytoswiki.tos.query.AllCards;
import com.flyingkite.mytoswiki.tos.query.TosSelection;
import com.flyingkite.mytoswiki.util.GlideUtil;

import java.util.ArrayList;
import java.util.List;

public class CraftAdapter extends RVSelectAdapter<BaseCraft, CraftAdapter.CraftVH, CraftAdapter.ItemListener> {

    public interface ItemListener extends RVAdapter.ItemListener<BaseCraft, CraftVH> {
        default void onFiltered(int selected, int total) {}
    }

    private int nameType = Misc.NT_ID_NORM;

    private TosSelection<BaseCraft> selection; // It is the selection on crafts, but they have same item
    private List<String> selectedMessage = new ArrayList<>();
    private AsyncTask<Void, Void, Void> selectTask;

    @Override
    public boolean hasSelection() {
        return selectTask != null;
    }

    @SuppressLint("StaticFieldLeak")
    public void setSelection(TosSelection<BaseCraft> s) {
        if (selectTask != null) {
            selectTask.cancel(true);
        }
        selectTask = new AsyncTask<Void, Void, Void>() {
            // Very fast to clicking on selection icons will have Inconsistency
            // java.lang.IndexOutOfBoundsException: Inconsistency detected. Invalid view holder adapter positionViewHolder{d9296d2 position=15 id=-1, oldPos=-1, pLpos:-1 no parent}
            // So we save result when in background, and then set data set on UI thread
            private List<Integer> _indices = new ArrayList<>();
            private List<String> _msg = new ArrayList<>();
            @Override
            protected Void doInBackground(Void... voids) {
                selection = s == null ? new AllCards<>(dataList) : s;
                if (isCancelled()) return null;
                _indices = selection.query();
                _msg = selection.getMessages(_indices);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                selectedIndices = _indices;
                selectedMessage = _msg;
                notifyDataSetChanged();
                notifyFiltered();
            }
        };
        selectTask.executeOnExecutor(ThreadUtil.cachedThreadPool);
    }

    public void setNameType(@NameType int type) {
        nameType = type;
    }

    public String name(BaseCraft c) {
        switch (nameType) {
            default:
            case Misc.NT_ID_NORM: return c.idNorm;
            case Misc.NT_NAME:    return c.name;
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
        if (selectedMessage != null && position < selectedMessage.size()) {
            msg = selectedMessage.get(position);
        }
        holder.setCard(c, name(c), msg);
    }

    @Override
    protected void onClickItem(BaseCraft c, CraftVH holder) {
        //Say.Log("click %s, %s", c.idNorm, c.name);
    }

    public static class CraftVH extends RecyclerView.ViewHolder implements GlideUtil {
        private ImageView thumb;
        private TextView text;
        private TextView message;

        public CraftVH(View v) {
            super(v);
            thumb = v.findViewById(R.id.tiImg);
            text = v.findViewById(R.id.tiText);
            message = v.findViewById(R.id.tiMessage);
        }

        public void setCard(BaseCraft c, String name, String msg) {
            boolean hasMsg = msg != null;
            text.setText(name);
            message.setText(msg);
            loadImage(thumb, c.icon.iconLink);
            setVisible(text, !hasMsg);
            setVisible(message, hasMsg);
        }

        private void setVisible(View v, boolean visible) {
            if (v != null) {
                v.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
        }

        private void loadImage(ImageView v, String url) {
            loadCraftToImageView(v, url);
        }
    }
}

