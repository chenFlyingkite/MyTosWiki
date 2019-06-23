package com.flyingkite.mytoswiki.data;

public class StoneFix {
    public StoneDesktop desktop = new StoneDesktop("", "");
    public String cards = "";

    public StoneFix(String card, StoneDesktop desk) {
        cards = card;
        desktop = desk;
    }

    @Override
    public String toString() {
        return cards + " " + desktop.toString();
    }
}
