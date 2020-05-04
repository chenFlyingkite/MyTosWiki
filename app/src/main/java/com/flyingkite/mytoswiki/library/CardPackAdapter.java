package com.flyingkite.mytoswiki.library;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyingkite.library.util.ThreadUtil;
import com.flyingkite.library.widget.RVSelectAdapter;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.pack.PackCard;
import com.flyingkite.mytoswiki.data.tos.TosCard;
import com.flyingkite.mytoswiki.tos.TosWiki;
import com.flyingkite.mytoswiki.tos.query.AllCards;
import com.flyingkite.mytoswiki.tos.query.TosSelection;
import com.flyingkite.mytoswiki.util.GlideUtil;
import com.flyingkite.mytoswiki.util.TosCardUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

@Deprecated
public class CardPackAdapter extends RVSelectAdapter<PackCard, CardPackAdapter.PCardVH, CardPackAdapter.ItemListener> {

    public interface ItemListener extends RVSelectAdapter.ItemListener<PackCard, PCardVH> {
        default void onFiltered(int selected, int total) {}
    }

    private int nameType = Misc.NT_ID_NORM;

    private TosSelection<PackCard> selection;
    private List<String> selectedMessage = new ArrayList<>();
    private AsyncTask<Void, Void, Void> selectTask;

    @Override
    public boolean hasSelection() {
        return selectTask != null;
    }

    @SuppressLint("StaticFieldLeak")
    public void setSelection(TosSelection<PackCard> s) {
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
                if (isCancelled()) return null;
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

    public String name(TosCard c) {
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
    public PCardVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PCardVH(inflateView(parent, R.layout.view_tos_item_card_pack));
    }

    @Override
    public void onBindViewHolder(@NonNull PCardVH h, int position) {
        super.onBindViewHolder(h, position);
        PackCard c = itemOf(position);
        String msg = null;
        if (selectedMessage != null && position < selectedMessage.size()) {
            msg = selectedMessage.get(position);
        }
        TosCard d = TosWiki.getCardByIdNorm(TosCardUtil.idNorm("" + c.id));
        h.setCard(c, name(d), msg);
    }

    @Override
    protected void onDidClickItem(PackCard c, PCardVH holder) {
        //Say.Log("click %s, %s", c.idNorm, c.name);
    }

    public static class PCardVH extends RecyclerView.ViewHolder implements GlideUtil {
        private ImageView thumb;
        private TextView text;
        private TextView message;

        public PCardVH(View v) {
            super(v);
            thumb = v.findViewById(R.id.tiImg);
            text = v.findViewById(R.id.tiText);
            message = v.findViewById(R.id.tiMessage);
        }

        public void setCard(PackCard c, String name, String msg) {
            boolean hasMsg = msg != null;
            text.setText(name);
            message.setText(msg);
            //TosCard d = TosWiki.getCardByIdNorm(c.idNorm);
            TosCard d = TosWiki.getCardByIdNorm(TosCardUtil.idNorm("" + c.id));
            loadCardToImageView(thumb, d);
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