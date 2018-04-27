package com.flyingkite.mytoswiki.library;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flyingkite.library.widget.RVAdapter;
import com.flyingkite.mytoswiki.R;

public class ButtonsAdapter extends RVAdapter<String, ButtonsAdapter.ButtonVH, ButtonsAdapter.ItemListener> {
    public interface ItemListener extends RVAdapter.ItemListener<String, ButtonVH> {
        //void onDelete(String data, ButtonVH vh, int position);
    }

    @Override
    public ButtonVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ButtonVH(inflateView(parent, R.layout.view_round_button));
    }

    @Override
    public void onBindViewHolder(ButtonVH vh, int position) {
        super.onBindViewHolder(vh, position);
        String msg = itemOf(position);
        vh.text.setText(msg);
    }

    public static class ButtonVH extends RecyclerView.ViewHolder {
        private TextView text;

        public ButtonVH(View v) {
            super(v);
            text = v.findViewById(R.id.textRound);
        }
    }
}

/*
class AAdapter<T, U extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<U> {
    public interface ItemListener<X> {
        void c(X data);
    }
    // https://www.safaribooksonline.com/library/view/java-generics-and/0596527756/ch04s03.html
    ItemListener<T> a;

    List<T> data = new ArrayList<>();

    @Override
    public U onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    public View inflate(ViewGroup parent, int id) {
        return LayoutInflater.from(parent.getContext()).inflate(id, parent, false);
    }

    @Override
    public void onBindViewHolder(U holder, int position) {
        holder.itemView.getAlpha();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}

class AAA extends AAdapter<String, AAA.AVH> {

    @Override
    public AVH onCreateViewHolder(ViewGroup parent, int viewType) {
        data.get(0);
        return new AVH(inflate(parent, 0));
    }

    static class AVH extends RecyclerView.ViewHolder {

        public AVH(View itemView) {
            super(itemView);
        }
    }
}
*/
