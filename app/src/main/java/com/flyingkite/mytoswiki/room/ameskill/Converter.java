package com.flyingkite.mytoswiki.room.ameskill;

import android.arch.persistence.room.TypeConverter;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.List;

public class Converter {
    @TypeConverter
    public static List<String> split(String data) {
        return Arrays.asList(data.split(","));
    }

    @TypeConverter
    public static String join(List<String> data) {
        return TextUtils.join(",", data);
    }

}
