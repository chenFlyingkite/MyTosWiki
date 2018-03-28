package com.flyingkite.mytoswiki.tos.query;

import java.util.ArrayList;
import java.util.List;

public interface Common {

    default <T> List<T> nonEmpty(List<T> list) {
        return list == null ? new ArrayList<>() : list;
    }
}
