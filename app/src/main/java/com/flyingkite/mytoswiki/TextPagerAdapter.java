package com.flyingkite.mytoswiki;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.flyingkite.mytoswiki.data.tos.TosCard;
import com.flyingkite.mytoswiki.util.GlideUtil;
import com.flyingkite.util.PGAdapter;

import flyingkite.library.android.log.Loggable;

public class TextPagerAdapter extends PGAdapter<TosCard> implements GlideUtil, Loggable {

    @Override
    public int pageLayoutId(ViewGroup parent, int position) {
        return R.layout.view_tos_item_card;
    }

    @Override
    public void onCreateView(View v, int position) {
        TosCard c = itemOf(position);
        ImageView img = v.findViewById(R.id.tiImg);
        loadCardToImageView(img, c);
        TextView t = v.findViewById(R.id.tiText);
        t.setText(c.toString());
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        logE("+init #%s", position);
        return super.instantiateItem(container, position);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        logE("~ bye #%s", position);
        super.destroyItem(container, position, object);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        //if (true) return null;
        TosCard c = itemOf(position);
        return "#" + c.idNorm + " " + c.name;
    }
}
