package com.flyingkite.mytoswiki.util;

public interface BackPage {

    /**
     * @return true if this event was consumed.
     */
    default boolean onBackPressed() {
        return false;
    }
}
