package com.flyingkite.mytoswiki.util;

import com.flyingkite.mytoswiki.data.tos.BaseCraft;
import com.flyingkite.mytoswiki.data.tos.TosCard;
import com.flyingkite.mytoswiki.tos.TosWiki;

import java.util.ArrayList;
import java.util.List;

public interface TosWikiUtil {

    default List<TosCard> getCardsByIdNorms(List<String> idNorms) {
        List<TosCard> ans = new ArrayList<>();
        for (int i = 0; i < idNorms.size(); i++) {
            String id = idNorms.get(i).trim();
            TosCard c = TosWiki.getCardByIdNorm(id);
            if (c != null) {
                ans.add(c);
            }
        }
        return ans;
    }

    default List<BaseCraft> getCraftsByIdNorms(List<String> idNorms) {
        List<BaseCraft> ans = new ArrayList<>();
        for (int i = 0; i < idNorms.size(); i++) {
            String idNorm = idNorms.get(i).trim();
            BaseCraft c = TosWiki.getCraftByIdNorm(idNorm);
            if (c != null) {
                ans.add(c);
            }
        }
        return ans;
    }
}
