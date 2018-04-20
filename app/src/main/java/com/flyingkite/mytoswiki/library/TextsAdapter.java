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

public class TextsAdapter extends RecyclerView.Adapter<TextsAdapter.TextsVH> implements ListUtil {
    public interface ItemListener {
        void onClick(String data, TextsVH vh, int position);
        void onDelete(String data, TextsVH vh, int position);
    }

    private List<String> data = new ArrayList<>();
    private ItemListener onClick;

    public TextsAdapter setData(List<String> strings) {
        data = nonNull(strings);
        return this;
    }

    public TextsAdapter setListener(ItemListener listener) {
        onClick = listener;
        return this;
    }

    @Override
    public TextsVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_text, parent, false);
        return new TextsVH(v);
    }

    @Override
    public void onBindViewHolder(TextsVH vh, int position) {
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

    public static class TextsVH extends RecyclerView.ViewHolder {
        private TextView text;
        private View delete;
        public TextsVH(View v) {
            super(v);
            text = v.findViewById(R.id.textMsg);
            delete = v.findViewById(R.id.textDelete);
        }
    }
}
