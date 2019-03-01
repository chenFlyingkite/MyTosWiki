package com.flyingkite.mytoswiki.data;

public class StoneDesktop {
    public String detail = "";
    public String stones = "";

    public StoneDesktop(String stone, String details) {
        detail = details;
        stones = stone;
    }

    @Override
    public String toString() {
        return stones + " " + detail;
    }
}
