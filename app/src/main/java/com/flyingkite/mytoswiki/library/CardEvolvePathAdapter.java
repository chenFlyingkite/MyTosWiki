package com.flyingkite.mytoswiki.library;

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

public class CardEvolvePathAdapter extends RVSelectAdapter<List<String>, CardEvolvePathAdapter.EvolvePathVH, CardEvolvePathAdapter.ItemListener> {

    public interface ItemListener extends RVAdapter.ItemListener<List<String>, EvolvePathVH> {
        default void onFiltered(int selected, int total) {}
        default void onPathItem(int at, TosCard c, CardVH vh) {}
        default void onPath(List<String> path, EvolvePathVH vh) {}
        default void onFilteredIndex(List<Integer> indices) {}

        default void onClickEach(int at, String item, EvolvePathVH vh, int position) {
        }
    }

    //private int nameType = Misc.NT_ID_NORM;

    private Selector<List<String>> selection;
    private List<SelectedData> selectedResult = new ArrayList<>();
    // current selection runnable
    private Runnable selectTask;

    @Override
    public boolean hasSelection() {
        return selectTask != null;
    }

    public void setSelection(Selector<List<String>> s) {
        if (selection != null) {
            selection.setCancelled(true);
        }
        selectTask = getSearchTask(s);
        ThreadUtil.cachedThreadPool.submit(selectTask);
    }

    private Runnable getSearchTask(Selector<List<String>> s) {
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

    private void notifyFiltered() {
        if (onItem != null) {
            onItem.onFiltered(selectedIndices.size(), dataList.size());
            onItem.onFilteredIndex(selectedIndices);

//            int n = 0;
//            for (int i = 0; i < selectedIndices.size(); i++) {
//                int si = selectedIndices.get(i);
//                List<String> p = super_itemOf(si);
//                n += p.packs.size();
//            }
//            onItem.onFilteredAll(n, cardsCount);
        }
    }

//    public void setNameType(@NameType int type) {
//        nameType = type;
//    }
//
//    public String name(TosCard c) {
//        int it = nameType;
//        if (it == Misc.NT_NONE) {
//            return "";
//        } else if (it == Misc.NT_NAME) {
//            return c.name;
//        } else {
//            return c.idNorm;
//        }
//    }

    @NonNull
    @Override
    public EvolvePathVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EvolvePathVH(inflateView(parent, R.layout.view_tos_item_evolve_path));
    }

    @Override
    public void onBindViewHolder(EvolvePathVH vh, int position) {
        super.onBindViewHolder(vh, position);
        //String msg = selectedResult.get(position).message;
        //vh.message.setText(msg);
        List<String> path = itemOf(position);
        vh.setPath(path);
    }

    public class EvolvePathVH extends RecyclerView.ViewHolder implements GlideUtil, ViewUtil2 {
        public List<CardVH> cards = new ArrayList<>();
        public TextView message;
        private List<String> paths;
        public EvolvePathVH(@NonNull View v) {
            super(v);
            message = v.findViewById(R.id.evolveMessage);
            cards.add(new CardVH(v.findViewById(R.id.evolveCard1)));
            cards.add(new CardVH(v.findViewById(R.id.evolveCard2)));
            cards.add(new CardVH(v.findViewById(R.id.evolveCard3)));
            cards.add(new CardVH(v.findViewById(R.id.evolveCard4)));
            cards.add(new CardVH(v.findViewById(R.id.evolveCard5)));
            cards.add(new CardVH(v.findViewById(R.id.evolveCard6)));
            for (int i = 0; i < cards.size(); i++) {
                CardVH vh = cards.get(i);
                final int at = i;
                vh.parent.setOnClickListener((w) -> {
                    if (onItem != null) {
                        onItem.onClickEach(at, paths.get(at), EvolvePathVH.this, getAdapterPosition());
                    }
                });
            }
        }

        private void setPath(List<String> path) {
            paths = path;
            int n = path.size();
            for (int i = 0; i < cards.size(); i++) {
                CardVH vh = cards.get(i);
                TosCard c = null;
                boolean hasCard = i < n;
                if (hasCard) {
                    c = TosWiki.getCardByIdNorm(path.get(i));
                }

                // arrow
                vh.arrow.setVisibility(View.GONE);
                if (i > 0 && hasCard) {
                    vh.arrow.setVisibility(View.VISIBLE);
                }

                setViewVisibility(vh.parent, hasCard);
                loadCardToImageView(vh.thumb, c);
                if (onItem != null) {
                    onItem.onPathItem(i, c, vh);
                }
            }
            if (onItem != null) {
                onItem.onPath(path, this);
            }
            //--
//            StringBuilder msg = new StringBuilder();
//            StringBuilder slv = new StringBuilder("SLvMax = ");
//            StringBuilder sb = new StringBuilder();
//            for (int i = 0; i < cards.size(); i++) {
//                CardVH vh = cards.get(i);
//                TosCard c = null;
//                PackInfoCard k = null;
//                int z = 0;
//                boolean hasCard = i < n;
//                if (hasCard) {
//                    c = TosWiki.getCardByIdNorm(path.get(i));
//                    k = myPack.get(c.idNorm);
//                    if (k != null) {
//                        z = k.packs.size();
//                    }
//                }
//
//                vh.arrow.setVisibility(View.GONE);
//                if (i > 0 && hasCard) {
//                    vh.arrow.setVisibility(View.VISIBLE);
//                }
//                setViewVisibility(vh.parent, hasCard);
//                vh.thumb.setImageAlpha(z == 0 ? 0x99 : 0xFF);
//                loadCardToImageView(vh.thumb, c);
//                vh.text.setText(itemMsg(c, k));
//
//                if (c != null) {
//                    int slvMax = c.skillCDMin1 - c.skillCDMax1 + 1;
//                    if (i > 0) {
//                        slv.append(", ");
//                    }
//                    slv.append(slvMax);
//                    String m = getMessage(c, k);
//
//                    if (i == 0 && n > 1 && m.contains("Eat")) {
//                        Log.e("Evolve", "Eat " + c + "\n, " + m);
//                    }
//                    String mi = App.me.getString(R.string.cards_evolution_info, c.idNorm, m);
//                    if (i > 0) {
//                        msg.append("\n");
//                    }
//                    msg.append(mi);
//                }
//            }
//            sb.append(slv).append("\n").append(msg);
//            message.setText(sb);
        }

        private String itemMsg(TosCard c, PackInfoCard k) {
            int z = k == null ? 0 : k.packs.size();
            return App.me.getString(R.string.cards_n_card, z);
        }
    }

    public static class CardVH {
        public View parent;
        public TextView text;
        public ImageView thumb;
        public ImageView arrow;

        CardVH(View v) {
            parent = v;
            text = v.findViewById(R.id.evolveText);
            thumb = v.findViewById(R.id.evolveCard);
            arrow = v.findViewById(R.id.evolveArrow);
        }
    }
}
