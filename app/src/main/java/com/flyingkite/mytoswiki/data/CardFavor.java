package com.flyingkite.mytoswiki.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CardFavor {
    @SerializedName("favors")
    public List<String> favors = new ArrayList<>();

    public void add(String idNorm) {
        boolean exist = exist(idNorm);
        if (!exist) {
            favors.add(idNorm);
        }
    }

    public void remove(String idNorm) {
        boolean exist = exist(idNorm);
        if (exist) {
            favors.remove(idNorm);
        }
    }

    public void addOrRemove(boolean add, String idNorm) {
        if (add) {
            add(idNorm);
        } else {
            remove(idNorm);
        }
    }

    public boolean exist(String idNorm) {
        if (favors == null) {
            return false;
        } else {
            return favors.contains(idNorm);
        }
    }

    public CardFavor copy() {
        CardFavor c = new CardFavor();
        c.favors.addAll(favors);
        return c;
    }
}
