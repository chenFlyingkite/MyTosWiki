package com.flyingkite.mytoswiki.library;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyingkite.library.widget.RVAdapter;
import com.flyingkite.library.widget.RVSelectAdapter;
import com.flyingkite.mytoswiki.App;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.pack.PackInfoCard;
import com.flyingkite.mytoswiki.data.tos.TosCard;
import com.flyingkite.mytoswiki.tos.TosWiki;
import com.flyingkite.mytoswiki.util.GlideUtil;
import com.flyingkite.util.select.Selector;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CardPackInfoAdapter extends RVSelectAdapter<PackInfoCard, CardPackInfoAdapter.PCardVH, CardPackInfoAdapter.ItemListener> {

    public interface ItemListener extends RVSelectAdapter.ItemListener<PackInfoCard, CardPackInfoAdapter.PCardVH> {
        default void onFiltered(int selected, int total) {}
        default void onFilteredAll(int selected, int total) {}
    }

    private int nameType = Misc.NT_ID_NORM;

    private Selector<PackInfoCard> selection;
    private List<String> selectedMessage = new ArrayList<>();
    private AsyncTask<Void, Void, Void> selectTask;
    // Including same cards
    private int cardsCount;

    @Override
    public boolean hasSelection() {
        return selectTask != null;
    }

    @Override
    public RVAdapter<PackInfoCard, PCardVH, ItemListener> setDataList(List<PackInfoCard> list) {
        super.setDataList(list);
        cardsCount = 0;
        for (int i = 0; i < list.size(); i++) {
            PackInfoCard p = list.get(i);
            cardsCount += p.packs.size();
        }
        return this;
    }

    @SuppressLint("StaticFieldLeak")
    public void setSelection(Selector<PackInfoCard> s) {
        // fixme
//        if (selectTask != null) {
//            selectTask.cancel(true);
//        }
//        selectTask = new AsyncTask<Void, Void, Void>() {
//            // Very fast to clicking on selection icons will have Inconsistency
//            // java.lang.IndexOutOfBoundsException: Inconsistency detected. Invalid view holder adapter positionViewHolder{d9296d2 position=15 id=-1, oldPos=-1, pLpos:-1 no parent}
//            // So we save result when in background, and then set data set on UI thread
//            private List<Integer> _indices = new ArrayList<>();
//            private List<String> _msg = new ArrayList<>();
//            @Override
//            protected Void doInBackground(Void... voids) {
//                selection = s == null ? new AllCards<>(dataList) : s;
//                if (isCancelled()) return null;
//                _indices = selection.query();
//                if (isCancelled()) return null;
//                _msg = selection.getMessages(_indices);
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                selectedIndices = _indices;
//                selectedMessage = _msg;
//                notifyDataSetChanged();
//                notifyFiltered();
//            }
//        };
//        selectTask.executeOnExecutor(ThreadUtil.cachedThreadPool);
    }

    public void setNameType(@NameType int type) {
        nameType = type;
    }

    public String name(TosCard c) {
        switch (nameType) {
            default:
            case Misc.NT_ID_NORM: return c.idNorm;
            case Misc.NT_NAME:    return c.name;
            case Misc.NT_NONE:    return "";
        }
    }

    public void updateSelection() {
        setSelection(selection);
    }

    private void notifyFiltered() {
        if (onItem != null) {
            onItem.onFiltered(selectedIndices.size(), dataList.size());

            int n = 0;
            for (int i = 0; i < selectedIndices.size(); i++) {
                int si = selectedIndices.get(i);
                PackInfoCard p = super_itemOf(si);
                n += p.packs.size();
            }
            onItem.onFilteredAll(n, cardsCount);
        }
    }

    @NonNull
    @Override
    public CardPackInfoAdapter.PCardVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CardPackInfoAdapter.PCardVH(inflateView(parent, R.layout.view_tos_item_card_pack_info));
    }

    @Override
    public void onBindViewHolder(@NonNull CardPackInfoAdapter.PCardVH h, int position) {
        super.onBindViewHolder(h, position);
        PackInfoCard c = itemOf(position);
        String msg = null;
        if (selectedMessage != null && position < selectedMessage.size()) {
            msg = selectedMessage.get(position);
        }
        TosCard d = TosWiki.getCardByIdNorm(c.idNorm);
        h.setCard(c, name(d), msg);
    }

    @Override
    protected void onDidClickItem(PackInfoCard c, CardPackInfoAdapter.PCardVH holder) {
        //Say.Log("click %s, %s", c.idNorm, c.name);
    }

    public static class PCardVH extends RecyclerView.ViewHolder implements GlideUtil {
        private final ImageView thumb;
        private final TextView text;
        private final TextView message;
        private final TextView cards;

        public PCardVH(View v) {
            super(v);
            thumb = v.findViewById(R.id.tiImg);
            text = v.findViewById(R.id.tiText);
            message = v.findViewById(R.id.tiMessage);
            cards = v.findViewById(R.id.tiCount);
        }

        public void setCard(PackInfoCard c, String name, String msg) {
            boolean hasMsg = msg != null;
            int n = c.packs.size();

            text.setText(name);
            message.setText(msg);
            cards.setText(App.me.getString(R.string.cards_n_card, n));
            thumb.setImageAlpha(n == 0 ? 153 : 255);

            TosCard d = TosWiki.getCardByIdNorm(c.idNorm);
            loadCardToImageView(thumb, d);
            if (TextUtils.isEmpty(name)) {
                setVisible(text, false);
                setVisible(message, false);
                setVisible(cards, false);
            } else {
                setVisible(text, !hasMsg);
                setVisible(message, hasMsg);
                setVisible(cards, true);
            }
        }

        private void setVisible(View v, boolean visible) {
            if (v != null) {
                v.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
        }
    }
}
