package com.flyingkite.mytoswiki.library;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.flyingkite.library.Say;
import com.flyingkite.library.ThreadUtil;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.TosCard;
import com.flyingkite.mytoswiki.data.TosCardRMDKVIR;
import com.flyingkite.mytoswiki.tos.TosCardFilter;
import com.flyingkite.mytoswiki.tos.query.TosCardSelection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CardAdapter extends RecyclerView.Adapter<CardVH> {
    @Deprecated
    private List<TosCardRMDKVIR> cardsRMD = new ArrayList<>();
    private List<TosCard> cards = new ArrayList<>();
    private OnClickCard onClick;
    private boolean showFilter;
    private List<Integer> filterIndices = new ArrayList<>();
    private OnFilterCard onFilter;

    private TosCardSelection selection;
    private List<Integer> selectedIndices = new ArrayList<>();
    private List<String> selectedMessage = new ArrayList<>();
    private AsyncTask<Void, Void, Void> selectTask;

    public interface OnFilterCard {
        void onFiltered(int selected, int total);
    }

    public interface OnClickCard {
        void onClick(int position, TosCard card);
    }

    @SuppressLint("StaticFieldLeak")
    public void setSelection(TosCardSelection s) {
        if (selectTask != null) {
            selectTask.cancel(true);
        }
        selectTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                selection = s == null ? new TosCardSelection.All(cards) : s;
                selectedIndices = selection.query();
                selectedMessage = selection.getMessages(selectedIndices);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                notifyDataSetChanged();
                notifyFiltered();
            }
        };
        selectTask.executeOnExecutor(ThreadUtil.cachedThreadPool);
    }

    @Deprecated
    public void setCardsRMD(TosCardRMDKVIR[] c) {
        cardsRMD = asList(c);
    }

    public void setCards(TosCard[] c) {
        cards = asList(c);
        setSelection(null);
    }

    private <T> List<T> asList(T[] c) {
        return c == null ? new ArrayList<>() : Arrays.asList(c);
    }

    public void setOnClickCard(OnClickCard listener) {
        onClick = listener;
    }

    public void setOnFilter(OnFilterCard listener) {
        onFilter = listener;
    }

    private void notifyFiltered() {
        if (onFilter != null) {
            onFilter.onFiltered(selectedIndices.size(), cards.size());
        }
    }

    @Override
    public CardVH onCreateViewHolder(ViewGroup parent, int viewType) {
        //Say.Log("create type #%s", viewType);
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_square_image, parent, false);
        return new CardVH(v);
    }

    @Override
    public void onBindViewHolder(CardVH holder, int position) {
        TosCard c;
        c = cards.get(selectedIndices.get(position));
//        if (showFilter) {
//            c = cards.get(filterIndices.get(position));
//        } else {
//            c = cards.get(position);
//        }
        //Say.Log("bind #%d -> %s ,name = %s", position, c.id, c.name);
        String msg = null;
        if (selectedMessage != null && position < selectedMessage.size()) {
            msg = selectedMessage.get(position);
        }
        holder.setCard(c, msg);
        holder.itemView.setOnClickListener(w -> {
            Say.Log("click %s, %s", c.name, c.id);
            if (onClick != null) {
                onClick.onClick(position, c);
            }
        });
    }

    @Override
    public int getItemCount() {
        return selectedIndices.size();
//        //-- No need
//        if (showFilter) {
//            return filterIndices.size();
//        } else {
//            return cards.size();
//        }
    }

    public void showSelection(Map<String, String> map) {
        showFilter = map != null && map.size() > 0;

        filterIndices.clear();
        if (showFilter) {
            filterIndices = TosCardFilter.me.filter(cards, map);
            notifyDataSetChanged();
            if (onFilter != null) {
                onFilter.onFiltered(filterIndices.size(), cards.size());
            }
        }
    }
}

class CardVH extends RecyclerView.ViewHolder {
    private TosCardRMDKVIR cardRMDKVIR;
    private TosCard card;
    private ImageView thumb;
    private TextView text;
    private TextView message;

    public CardVH(View v) {
        super(v);
        thumb = v.findViewById(R.id.squareImg);
        text = v.findViewById(R.id.squareText);
        message = v.findViewById(R.id.squareMessage);
    }

    public void setCard(TosCardRMDKVIR c) {
        cardRMDKVIR = c;
        text.setText(c.display_num);
    }

    public void setCard(TosCard c, String msg) {
        boolean hasMsg = msg != null;
        card = c;
        text.setText(c.id);
        message.setText(msg);
        loadImage(thumb, c.icon);
        setVisible(text, !hasMsg);
        setVisible(message, hasMsg);
    }

    private void setVisible(View v, boolean visible) {
        if (v != null) {
            v.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    private void loadImage(ImageView v, String url) {
        Glide.with(v.getContext()).load(url)
                .apply(RequestOptions.centerCropTransform()
                        .placeholder(R.drawable.unknown_card))
                .into(v);
    }
}
