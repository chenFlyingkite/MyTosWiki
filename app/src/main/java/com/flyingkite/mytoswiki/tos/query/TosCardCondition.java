package com.flyingkite.mytoswiki.tos.query;

import com.flyingkite.library.ListUtil;

import java.util.ArrayList;
import java.util.List;

public class TosCardCondition {
    private List<String> attr = new ArrayList<>();
    private List<String> race = new ArrayList<>();
    private List<String> star = new ArrayList<>();

    public TosCardCondition attr(List<String> a) {
        attr = ListUtil.nonNull(a);
        return this;
    }

    public TosCardCondition race(List<String> r) {
        race = ListUtil.nonNull(r);
        return this;
    }

    public TosCardCondition star(List<String> s) {
        star = ListUtil.nonNull(s);
        return this;
    }

    public List<String> getAttr() {
        return attr;
    }

    public List<String> getRace() {
        return race;
    }

    public List<String> getStar() {
        return star;
    }
}