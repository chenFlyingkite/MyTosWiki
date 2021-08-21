package com.flyingkite.mytoswiki.data.seal.v1;

import com.flyingkite.mytoswiki.data.seal.BaseSeal;

import androidx.annotation.StringRes;

public class SealItem {
    public BaseSeal seal;
    @StringRes
    public int strId;

    public SealItem(@StringRes int id, BaseSeal s) {
        strId = id;
        seal = s;
    }

    @Override
    public String toString() {
        return seal.name();
    }
}
