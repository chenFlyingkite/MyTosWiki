package com.flyingkite.mytoswiki.charts;

import android.graphics.Color;

import com.flyingkite.mytoswiki.tos.TosSummonerLevel;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import flyingkite.library.androidx.TicTac2;
import flyingkite.library.java.util.ListUtil;

public class TosSummonerChart {
    public static final String[] header = TosSummonerLevel.headerZh;
    private static final long[][] table = TosSummonerLevel.table;
    private static final List<ILineDataSet> allDataSet = new ArrayList<>();
    private static final TicTac2 clock = new TicTac2();

    public static int getRowCount() {
        return table.length;
    }

    public static int getColumnCount() {
        return header.length;
    }

    public static LineData getLineData(String... titles) {
        int n = titles.length;
        List<Integer> index = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int k = ListUtil.indexOf(header, titles[i]);
            if (k >= 0) {
                index.add(k);
            }
        }
        return getLineData(index);
    }

    public static LineData getLineData(List<Integer> index) {
        if (index == null || index.size() == 0) return null;

        List<ILineDataSet> set = new ArrayList<>();
        for (Integer i : index) {
            set.add(allDataSet.get(i));
        }

        return new LineData(set);
    }

    static {
        int m = table.length;
        int n = table[0].length;
        final int xKey = 0;
        int[] colors = {Color.BLACK, Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA};
        clock.tic();
        for (int j = 0; j < n; j++) {
            // Add each row points
            List<Entry> entries = new ArrayList<>();
            for (int i = 0; i < m; i++) {
                long x = table[i][xKey];
                long y = table[i][j];
                Entry ei = new Entry(x, y, x + ", " + y);
                entries.add(ei);
            }
            // As row data set
            LineDataSet set = new LineDataSet(entries, header[j]);
            set.setCircleColor(colors[j]);
            set.setCircleRadius(2);
            //set.setDrawCircles(false);
            set.setColor(colors[j]); // line color
            set.setAxisDependency(YAxis.AxisDependency.LEFT);

            allDataSet.add(set);
        }
        clock.tac("Data OK");
    }
}
