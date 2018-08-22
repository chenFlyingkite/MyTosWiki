package com.flyingkite.mytoswiki.dialog;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyingkite.library.widget.RVAdapter;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.seal.BaseSeal;
import com.flyingkite.mytoswiki.data.seal.SealSample;
import com.flyingkite.mytoswiki.tos.TosWiki;
import com.flyingkite.mytoswiki.util.TosPageUtil;

import flyingkite.math.Math2;

public abstract class SealDataRowAdapter extends RVAdapter<BaseSeal, SealDataRowAdapter.SealRowVH, SealDataRowAdapter.ItemListener> implements TosPageUtil {

    public interface ItemListener extends RVAdapter.ItemListener<BaseSeal, SealRowVH> {

    }

    private BaseSeal seal;
    private boolean raised;

    public void setSeal(BaseSeal info) {
        seal = info;
    }

    public void setRaised(boolean up) {
        raised = up;
    }

    @NonNull
    @Override
    public SealRowVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SealRowVH(inflateView(parent, R.layout.view_seal_row));
    }

    @Override
    public void onBindViewHolder(SealRowVH vh, int position) {
        super.onBindViewHolder(vh, position);
        //noinspection UnnecessaryLocalVariable
        final int i = position;
        SealSample ss = raised ? seal.raisedSample : seal.normalSample;
        int n = Math2.sum(ss.observe);
        vh.title.setText("#" + (position + 1)); // ↦
        vh.expect.setText(_fmt("%.1f\n%.2f %%", ss.pdf[i] * n, ss.pdf[i] * 100));
        vh.observe.setText(_fmt("%s\n%.2f %%", ss.observe[i], ss.observePdf[i] * 100));
        setSimpleCard(vh.actor, TosWiki.getCardByIdNorm(seal.sealCards.get(position)));
    }

    @Override
    public int getItemCount() {
        return seal == null ? 0 : seal.sealCards.size();
    }

    public class SealRowVH extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView expect;
        private ImageView actor;
        private TextView observe;

        public SealRowVH(View v) {
            super(v);
            title = v.findViewById(R.id.sealNumber);
            expect = v.findViewById(R.id.sealExpect);
            actor = v.findViewById(R.id.sealActor);
            observe = v.findViewById(R.id.sealObserve);
        }
    }
}
