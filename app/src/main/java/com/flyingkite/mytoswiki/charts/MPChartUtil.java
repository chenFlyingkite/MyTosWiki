package com.flyingkite.mytoswiki.charts;

import java.util.ArrayList;
import java.util.List;

public final class MPChartUtil {
    private MPChartUtil() {}

    public static class ScaleXY {
        public float x;
        public float y;
        public float sx;
        public float sy;

        @Override
        public String toString() {
            return String.format(java.util.Locale.US, "p = (%s, %s), s = (%s, %s)", x, y, sx, sy);
        }
    }

    public static ScaleXY getPrettyZoom(long[][] table, int xmin, int xmax, int lvMax) {
        ScaleXY p = new ScaleXY();
        // Center of x
        final int N = lvMax;
        p.x = (xmax + xmin) * 0.5F;

        // Center of y
        List<Long> yIn = new ArrayList<>();
        yIn.addAll(values(table, xmin, 1, 4));
        yIn.addAll(values(table, xmax, 1, 4));
        long ymin = minOf(yIn);
        long ymax = maxOf(yIn);
        p.y = (ymin + ymax) * 0.5F;

        // Scale of x
        float sx;
        int q = xmax - xmin + 1;
        if (q == 0) {
            sx = 1;
        } else {
            sx = 1F * N / q;
        }
        p.sx = sx;

        // Scale of y
        List<Long> yAll = values(table, N, 1, 4);
        long yAllmin = minOf(yAll);
        long yAllmax = maxOf(yAll);
        float sy = 1F * (yAllmax - yAllmin) / (ymax - ymin);
        p.sy = sy;

        return p;
    }

    private static List<Long> values(long[][] table, int d0, int fromD1, int toD1) {
        List<Long> list = new ArrayList<>();
        for (int i = fromD1; i < toD1; i++) {
            list.add(table[d0][i]);
        }
        return list;
    }

    // TODO : Math
    private static long minOf(List<Long> param) {
        if (param == null || param.size() == 0) return 0;
        long min = param.get(0);
        for (int i = 1; i < param.size(); i++) {
            long v = param.get(i);
            if (v < min) {
                min = v;
            }
        }
        return min;
    }

    private static long maxOf(List<Long> param) {
        if (param == null || param.size() == 0) return 0;
        long max = param.get(0);
        for (int i = 1; i < param.size(); i++) {
            long v = param.get(i);
            if (v > max) {
                max = v;
            }
        }
        return max;
    }
}
