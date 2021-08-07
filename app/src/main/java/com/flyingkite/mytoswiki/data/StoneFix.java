package com.flyingkite.mytoswiki.data;

public class StoneFix {
    public StoneDesktop desktop;
    public String cards;

    public StoneFix(String card, String stones, String detail) {
        cards = card;
        desktop =  new StoneDesktop(stones, detail);
    }

    @Override
    public String toString() {
        return cards + " " + desktop;
    }
}
