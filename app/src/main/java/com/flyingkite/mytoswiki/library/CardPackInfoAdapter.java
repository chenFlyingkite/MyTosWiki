package com.flyingkite.mytoswiki.library;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyingkite.library.util.ThreadUtil;
import com.flyingkite.library.widget.RVAdapter;
import com.flyingkite.library.widget.RVSelectAdapter;
import com.flyingkite.mytoswiki.App;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.pack.PackInfoCard;
import com.flyingkite.mytoswiki.data.tos.TosCard;
import com.flyingkite.mytoswiki.tos.TosWiki;
import com.flyingkite.mytoswiki.tos.query.AllCards;
import com.flyingkite.mytoswiki.util.GlideUtil;
import com.flyingkite.mytoswiki.util.ViewUtil2;
import com.flyingkite.util.select.SelectedData;
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
    private List<SelectedData> selectedResult = new ArrayList<>();
    // current selection runnable
    private Runnable selectTask;
    // Including same cards
    private int cardsCount;

    @Override
    public boolean hasSelection() {
        return selectTask != null;
    }

    @Override
    public RVAdapter<PackInfoCard, PCardVH, ItemListener> setDataList(List<PackInfoCard> list) {
        super.setDataList(list);
        int sum = 0;
        for (int i = 0; i < list.size(); i++) {
            PackInfoCard p = list.get(i);
            sum += p.packs.size();
        }
        cardsCount = sum;
        return this;
    }

    public void setSelection(Selector<PackInfoCard> s) {
        if (selection != null) {
            selection.setCancelled(true);
        }
        selectTask = getSearchTask(s);
        ThreadUtil.cachedThreadPool.submit(selectTask);
    }

    private Runnable getSearchTask(Selector<PackInfoCard> s) {
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
        if (it == Misc.NT_NONE) {
            return "";
        } else if (it == Misc.NT_NAME) {
            return c.name;
        } else {
            return c.idNorm;
        }
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
        if (selectedResult != null && position < selectedResult.size()) {
            msg = selectedResult.get(position).message;
        }
        TosCard d = TosWiki.getCardByIdNorm(c.idNorm);
        h.setCard(c, name(d), msg);
    }

    @Override
    protected void onDidClickItem(PackInfoCard c, CardPackInfoAdapter.PCardVH holder) {
    }

    public static class PCardVH extends RecyclerView.ViewHolder implements GlideUtil, ViewUtil2 {
        private final ImageView thumb;
        private final TextView text;
        private final TextView cards;
        private final TextView message;

        public PCardVH(View v) {
            super(v);
            text = v.findViewById(R.id.tiText);
            thumb = v.findViewById(R.id.tiImg);
            cards = v.findViewById(R.id.tiCount);
            message = v.findViewById(R.id.tiMessage);
        }

        public void setCard(PackInfoCard c, String name, String msg) {
            boolean hasMsg = msg != null;
            int n = c.packs.size();

            text.setText(name);
            message.setText(msg);
            cards.setText(App.me.getString(R.string.cards_n_card, n));
            thumb.setImageAlpha(n == 0 ? 0x99 : 0xFF);

            TosCard d = TosWiki.getCardByIdNorm(c.idNorm);
            loadCardToImageView(thumb, d);
            if (TextUtils.isEmpty(name)) {
                setViewVisibility(text, false);
                setViewVisibility(message, false);
                setViewVisibility(cards, false);
            } else {
                setViewVisibility(text, !hasMsg);
                setViewVisibility(message, hasMsg);
                setViewVisibility(cards, true);
            }
        }
    }
}
