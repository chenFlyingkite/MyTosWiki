package com.flyingkite.mytoswiki.tos.query;

import android.support.annotation.NonNull;

import com.flyingkite.mytoswiki.data.TosCard;

import java.util.ArrayList;
import java.util.List;

public class TosSelectRaceAttribute extends TosSelectAttribute {
    protected  List<String> races;
    public TosSelectRaceAttribute(List<TosCard> source, List<String> attr, List<String> race) {
        super(source, attr);
        races = nonEmpty(race);
    }

    @NonNull
    @Override
    public List<Integer> select() {
        List<Integer> index = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            TosCard c = data.get(i);
            if (attributes.contains(c.attribute) && races.contains(c.race)) {
                index.add(i);
            }
        }
        return index;
    }
}
