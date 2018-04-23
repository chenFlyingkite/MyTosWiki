package com.flyingkite.mytoswiki.library;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flyingkite.mytoswiki.R;
import com.flyingkite.util.ListUtil;

import java.util.ArrayList;
import java.util.List;

public class ButtonsAdapter extends RecyclerView.Adapter<ButtonsAdapter.ButtonVH> implements ListUtil {
    public interface ItemListener {
        void onClick(String data, ButtonVH vh, int position);
        void onDelete(String data, ButtonVH vh, int position);
    }

    private List<String> data = new ArrayList<>();
    private ItemListener onClick;

    public ButtonsAdapter setData(List<String> strings) {
        data = nonNull(strings);
        return this;
    }

    public ButtonsAdapter setListener(ItemListener listener) {
        onClick = listener;
        return this;
    }

    @Override
    public ButtonVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_text, parent, false);
        return new ButtonVH(v);
    }

    @Override
    public void onBindViewHolder(ButtonVH vh, int position) {
        String msg = data.get(position);
        vh.text.setText(msg);
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClick != null) {
                    onClick.onClick(msg, vh, vh.getAdapterPosition());
                }
            }
        });
        vh.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClick != null) {
                    onClick.onDelete(msg, vh, vh.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ButtonVH extends RecyclerView.ViewHolder {
        private TextView text;
        private View delete;
        public ButtonVH(View v) {
            super(v);
            text = v.findViewById(R.id.textMsg);
            delete = v.findViewById(R.id.textDelete);
        }
    }
}

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
