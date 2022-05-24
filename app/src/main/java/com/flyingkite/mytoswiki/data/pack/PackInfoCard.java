package com.flyingkite.mytoswiki.data.pack;

import androidx.annotation.NonNull;

import com.flyingkite.mytoswiki.data.tos.TosCard;

import java.util.ArrayList;
import java.util.List;

import flyingkite.library.java.log.Formattable;

public class PackInfoCard implements Formattable {
    public String idNorm = "";

    public String name = "";

    public final List<PackCard> packs = new ArrayList<>();

    public static PackInfoCard from(TosCard s) {
        PackInfoCard c = new PackInfoCard();
        if (s != null) {
            c.idNorm = s.idNorm;
            c.name = s.name;
        }
        return c;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < packs.size(); i++) {
            PackCard p = packs.get(i);
            if (i > 0) {
                b.append(", ");
            }
            b.append(p);
        }
        return _fmt("%s : %s => %s", idNorm, packs.size(), b.toString());
    }
}
