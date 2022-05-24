package com.flyingkite.mytoswiki.tos.query;

import java.util.ArrayList;
import java.util.List;

import flyingkite.library.java.util.ListUtil;

public class TosCondition {
    private List<String> attr = new ArrayList<>();
    private List<String> race = new ArrayList<>();
    private List<String> star = new ArrayList<>();
    // For craft
    private List<String> mode = new ArrayList<>();

    public TosCondition attr(List<String> a) {
        attr = ListUtil.nonNull(a);
        return this;
    }

    public TosCondition race(List<String> r) {
        race = ListUtil.nonNull(r);
        return this;
    }

    public TosCondition star(List<String> s) {
        star = ListUtil.nonNull(s);
        return this;
    }

    public TosCondition mode(List<String> m) {
        mode = ListUtil.nonNull(m);
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

    public List<String> getMode() {
        return mode;
    }
}