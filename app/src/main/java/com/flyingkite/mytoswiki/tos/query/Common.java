package com.flyingkite.mytoswiki.tos.query;

import java.util.ArrayList;
import java.util.List;

public interface Common {

    default <T> List<T> nonEmpty(List<T> list) {
        return list == null ? new ArrayList<>() : list;
    }

    default <T> int indexOf(T[] list, T item) {
        if (list != null) {
            for (int i = 0; i < list.length; i++) {
                T li = list[i];
                if (item == null) {
                    if (li == null) {
                        return i;
                    }
                } else {
                    if (item.equals(li)) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }
}
