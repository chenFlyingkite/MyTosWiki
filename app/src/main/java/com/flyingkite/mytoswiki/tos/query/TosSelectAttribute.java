package com.flyingkite.mytoswiki.tos.query;

import android.support.annotation.NonNull;

import com.flyingkite.mytoswiki.data.TosCard;

import java.util.ArrayList;
import java.util.List;

public class TosSelectAttribute implements TosCardSelection {
    protected List<TosCard> data;
    protected List<String> attributes;

    public TosSelectAttribute(List<TosCard> source, List<String> attr) {
        data = nonEmpty(source);
        attributes = (attr);
    }

    @NonNull
    @Override
    public List<TosCard> from() {
        return data;
    }

    @NonNull
    @Override
    public List<Integer> select() {
        List<Integer> index = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            TosCard c = data.get(i);
            if (attributes.contains(c.attribute)) {
                index.add(i);
            }
        }
        return index;
    }
}
