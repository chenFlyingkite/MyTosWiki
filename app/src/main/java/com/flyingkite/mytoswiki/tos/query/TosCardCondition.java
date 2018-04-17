package com.flyingkite.mytoswiki.tos.query;

import com.flyingkite.util.ListUtil;

import java.util.ArrayList;
import java.util.List;

public class TosCardCondition implements ListUtil {
    private List<String> attr = new ArrayList<>();
    private List<String> race = new ArrayList<>();
    private List<String> star = new ArrayList<>();

    public TosCardCondition attr(List<String> a) {
        attr = nonEmpty(a);
        return this;
    }

    public TosCardCondition race(List<String> r) {
        race = nonEmpty(r);
        return this;
    }

    public TosCardCondition star(List<String> s) {
        star = nonEmpty(s);
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