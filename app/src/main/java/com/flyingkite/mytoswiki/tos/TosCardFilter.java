package com.flyingkite.mytoswiki.tos;

import android.support.annotation.NonNull;

import com.flyingkite.mytoswiki.data.TosCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TosCardFilter {
    public static final TosCardFilter me = new TosCardFilter();
    private TosCardFilter() {}

    public List<Integer> filter(@NonNull List<TosCard> cards, @NonNull Map<String, String> map) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < cards.size(); i++) {
            TosCard c = cards.get(i);
            if (matchesIn(c, map)) {
                result.add(i);
            }
        }
        return result;
    }

    private boolean matchesIn(TosCard card, Map<String, String> map) {
        for (String key : map.keySet()) {
            String value = map.get(key);

            switch (key) {
                case TosAttribute.KEY:
                    if (!value.contains(card.attribute)) {
                        return false;
                    }
                    break;
            }
        }
        return true;
    }
}