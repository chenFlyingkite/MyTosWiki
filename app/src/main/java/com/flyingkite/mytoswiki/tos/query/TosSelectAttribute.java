package com.flyingkite.mytoswiki.tos.query;

import android.support.annotation.NonNull;

import com.flyingkite.library.util.ListUtil;
import com.flyingkite.mytoswiki.data.TosCard;

import java.util.List;

public class TosSelectAttribute implements TosCardSelection {
    protected List<TosCard> data;
    protected TosCardCondition select;

    public TosSelectAttribute(List<TosCard> source, TosCardCondition condition) {
        data = ListUtil.nonNull(source);
        select = condition;
    }

    @NonNull
    @Override
    public List<TosCard> from() {
        return data;
    }

    @Override
    public boolean onSelect(TosCard c) {
        return select.getAttr().contains(c.attribute);
    }
}
