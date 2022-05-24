package com.flyingkite.mytoswiki.library;

import com.flyingkite.mytoswiki.R;

import flyingkite.library.androidx.recyclerview.TextRVAdapter;

public class TextAdapter extends TextRVAdapter {

    @Override
    protected int holderLayout() {
        return R.layout.view_round_button;
    }

    @Override
    protected int itemId() {
        return R.id.textRound;
    }

    public interface ItemListener extends TextRVAdapter.ItemListener {
        //void onDelete(String data, ButtonVH vh, int position);
    }

//    @Override
//    public ButtonVH onCreateViewHolder(ViewGroup parent, int viewType) {
//        initCenterScroller(parent);
//        return new ButtonVH(inflateView(parent, R.layout.view_round_button));
//    }
//
//    @Override
//    public void onBindViewHolder(ButtonVH vh, int position) {
//        super.onBindViewHolder(vh, position);
//        String msg = itemOf(position);
//        vh.text.setText(msg);
//    }
//
//    public static class ButtonVH extends RecyclerView.ViewHolder {
//        private TextView text;
//
//        public ButtonVH(View v) {
//            super(v);
//            text = v.findViewById(R.id.textRound);
//        }
//    }
}