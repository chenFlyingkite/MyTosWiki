package com.flyingkite.mytoswiki.tos.query;

import java.util.ArrayList;
import java.util.List;

public class TosCardCondition implements Common {
    private List<String> attr = new ArrayList<>();
    private List<String> race = new ArrayList<>();

    public TosCardCondition attr(List<String> a) {
        attr = nonEmpty(a);
        return this;
    }

    public TosCardCondition race(List<String> r) {
        race = nonEmpty(r);
        return this;
    }

    public List<String> getAttr() {
        return attr;
    }

    public List<String> getRace() {
        return race;
    }
}