package com.flyingkite.mytoswiki.library;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flyingkite.library.widget.RVAdapter;
import com.flyingkite.mytoswiki.R;

public class TextsAdapter extends RVAdapter<String, TextsAdapter.TextsVH, TextsAdapter.ItemListener> {
    public interface ItemListener extends RVAdapter.ItemListener<String, TextsVH> {
        //void onClick(String data, TextsVH vh, int position);
        void onDelete(String data, TextsVH vh, int position);
    }

    @Override
    public TextsVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TextsVH(inflateView(parent, R.layout.view_text));
    }

    @Override
    public void onBindViewHolder(TextsVH vh, int position) {
        super.onBindViewHolder(vh, position);
        String msg = itemOf(position);
        vh.text.setText(msg);
        vh.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItem != null) {
                    onItem.onDelete(msg, vh, vh.getAdapterPosition());
                }
            }
        });
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
