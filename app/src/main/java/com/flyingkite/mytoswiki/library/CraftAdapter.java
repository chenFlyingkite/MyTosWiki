package com.flyingkite.mytoswiki.library;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyingkite.library.Say;
import com.flyingkite.library.widget.RVAdapter;
import com.flyingkite.mytoswiki.GlideApp;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.tos.BaseCraft;

public class CraftAdapter extends RVAdapter<BaseCraft, CraftAdapter.CraftVH, CraftAdapter.ItemListener> {

    public interface ItemListener extends RVAdapter.ItemListener<BaseCraft, CraftVH> {

    }
//    private boolean showFilter;
//    private List<Integer> filterIndices = new ArrayList<>();
//    private OnFilterCard onFilter;

//    public static final int NT_ID_NORM = 0;
//    public static final int NT_NAME = 1;
//
//    @Retention(RetentionPolicy.SOURCE)
//    @IntRange(from = NT_ID_NORM, to = NT_NAME)
//    public @interface NameType{}


//    private int nameType = NT_ID_NORM;

//    private TosCardSelection selection;
//    private List<Integer> selectedIndices = new ArrayList<>();
//    private List<String> selectedMessage = new ArrayList<>();
//    private AsyncTask<Void, Void, Void> selectTask;
//
//    public interface OnFilterCard {
//        void onFiltered(int selected, int total);
//    }
//
//    public interface OnClickCraft {
//        void onClick(int position, BaseCraft craft);
//    }

//    @SuppressLint("StaticFieldLeak")
//    public void setSelection(TosCardSelection s) {
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
//                selection = s == null ? new TosCardSelection.All(cards) : s;
//                if (isCancelled()) return null;
//                _indices = selection.query();
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
//    }

//    public void setCards(List<TosCard> c) {
//        cards = c;
//        setSelection(null);
//    }
//
//    public void setNameType(@NameType int type) {
//        nameType = type;
//    }
//
//    public String name(TosCard c) {
//        switch (nameType) {
//            default:
//            case NT_ID_NORM: return c.idNorm;
//            case NT_NAME:    return c.name;
//        }
//    }
//
//    public void updateSelection() {
//        setSelection(selection);
//    }
//
//    public void setOnClickCard(OnClickCard listener) {
//        onClick = listener;
//    }
//
//    public void setOnFilter(OnFilterCard listener) {
//        onFilter = listener;
//    }
//
//    private void notifyFiltered() {
//        if (onFilter != null) {
//            onFilter.onFiltered(selectedIndices.size(), cards.size());
//        }
//    }

    @NonNull
    @Override
    public CraftVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CraftVH(inflateView(parent, R.layout.view_square_image));
    }

    @Override
    public void onBindViewHolder(@NonNull CraftVH holder, int position) {
        super.onBindViewHolder(holder, position);
        BaseCraft c = itemOf(position);
        String msg = null;
//        if (selectedMessage != null && position < selectedMessage.size()) {
//            msg = selectedMessage.get(position);
//        }
        holder.setCard(c, c.name, msg);
    }

    @Override
    protected void onClickItem(BaseCraft c, CraftVH holder) {
        Say.Log("click %s, %s", c.idNorm, c.name);
    }
//
//    @Override
//    public int getItemCount() {
//        return selectedIndices.size();
//    }

    public static class CraftVH extends RecyclerView.ViewHolder {
        private ImageView thumb;
        private TextView text;
        private TextView message;

        public CraftVH(View v) {
            super(v);
            thumb = v.findViewById(R.id.squareImg);
            text = v.findViewById(R.id.squareText);
            message = v.findViewById(R.id.squareMessage);
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
            GlideApp.with(v.getContext()).load(url).centerCrop().placeholder(R.drawable.unknown_craft).into(v);
        }
    }
}

