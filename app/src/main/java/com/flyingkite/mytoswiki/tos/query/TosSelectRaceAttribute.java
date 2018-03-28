package com.flyingkite.mytoswiki.tos.query;

import com.flyingkite.mytoswiki.data.TosCard;

import java.util.List;

public class TosSelectRaceAttribute extends TosSelectAttribute {
    public TosSelectRaceAttribute(List<TosCard> source, TosCardCondition condition) {
        super(source, condition);
    }

    @Override
    public boolean onSelect(TosCard c) {
        List<String> attributes = select.getAttr();
        List<String> races = select.getRace();
        return attributes.contains(c.attribute) && races.contains(c.race);
    }
}
