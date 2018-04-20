package com.flyingkite.util;

import java.util.ArrayList;
import java.util.List;

public interface ListUtil {

    default <T> List<T> nonNull(List<T> list) {
        return list == null ? new ArrayList<>() : list;
    }

    default <T> int indexOf(T[] list, T item) {
        if (list != null) {
            // Just as it done in ArrayList#indexOf(Object)
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
