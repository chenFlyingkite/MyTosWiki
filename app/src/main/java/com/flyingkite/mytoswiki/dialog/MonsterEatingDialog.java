package com.flyingkite.mytoswiki.dialog;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.charts.MPChartUtil;
import com.flyingkite.mytoswiki.data.TosCard;
import com.flyingkite.mytoswiki.tos.TosMath;
import com.flyingkite.util.TicTacv;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MonsterEatingDialog extends BaseTosDialog {
    @Override
    protected int getLayoutId() {
        return R.layout.dialog_monster_eat;
    }

    public static final String TAG = "MonsterEatingDialog";
    public static final String BUNDLE_CARD = "MonsterEatingDialog.TosCard";
    private static final String[] headers = {"等級", "飼料經驗", "貢獻經驗", "經驗收益"};
    private LineChart mathChart;
    private TosCard card;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        parseBundle(getArguments());
        initScrollTools(R.id.medGoTop, R.id.medGoBottom, null); // TODO : NO NEED
        initChart();
        //adjust1();
    }

    private void parseBundle(Bundle b) {
        boolean hasCard = b != null && b.containsKey(BUNDLE_CARD);
        if (!hasCard) return;

        card = b.getParcelable(BUNDLE_CARD);
    }

    private void initChart() {
        mathChart = findViewById(R.id.medMathChart);
        //chartOptions = findViewById(R.id.sldChartOptions);

        long[][] table = initChartData(card);
        List<ILineDataSet> sets = asLineDataSet(headers, table, 1);
        LineData data = getLineData(Arrays.asList(1, 2, 3), sets);

        int N = card.LvMax;
        // Center of x
        int xmin = Math.max(card.maxMUPerLevel - 2, 1);
        int xmax = Math.min(card.maxTUAllLevel + 1, N);
        MPChartUtil.ScaleXY z = MPChartUtil.getPrettyZoom(table, xmin, xmax, N);
        /*
        int x = (xmax + xmin) / 2;
        float xf = (xmax + xmin) * 0.5F;

        // Center of y
        List<Long> yIn = Arrays.asList(table[xmin][1], table[xmin][2], table[xmin][3]
                , table[xmax][1], table[xmax][2], table[xmax][3]);
        long ymin = mins(yIn);
        long ymax = maxs(yIn);
        long y = (ymin + ymax) / 2;
        // 小魔女 0086

        // Scale of x
        float sx;
        int q = xmax - xmin + 1;
        if (q == 0) {
            sx = 1;
        } else {
            sx = 1F * N / q;
        }
        // Scale of y
        List<Long> yAll = Arrays.asList(table[N][1], table[N][2], table[N][3]);
        long yAllmin = mins(yAll);
        long yAllmax = maxs(yAll);
        float sy = 1F * (yAllmax - yAllmin) / (ymax - ymin);
        Say.Log("min = %s, max = %s, x = %s", xmin, xmax, x);
        Say.Log("y : min = %s, max = %s", ymin, ymax);
        Say.Log("O : min = %s, max = %s", yAllmin, yAllmax);
        Say.Log("sy : %s", sy);
        */
        //mathChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mathChart.setData(data);
        mathChart.getAxisLeft().setEnabled(false);
        mathChart.zoom(z.sx, z.sy, z.x, z.y, YAxis.AxisDependency.RIGHT);
        //mathChart.setVisibleYRange(table[xmax][3], Math.max(table[xmax][2], table[xmax][1]), YAxis.AxisDependency.LEFT);
        //mathChart.moveViewToX(center);
        mathChart.invalidate();
        setDesc("");

        findViewById(R.id.medChartReset).setOnClickListener((v) -> {
            mathChart.zoom(z.sx, z.sy, z.x, z.y, YAxis.AxisDependency.RIGHT);
        });
        mathChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                setDesc(e.getData().toString());
            }

            @Override
            public void onNothingSelected() {
                setDesc("");
            }
        });
        //mathChart.setMarker(new MarkerView(getActivity(), R.layout.view_text2));
        //mathChart.resetZoom();

//        ViewGroup vg = chartOptions;
//        for (int j = 1; j < n; j++) {
//            View v = LayoutInflater.from(getActivity()).inflate(R.layout.view_checkbox, vg, false);
//            CheckBox t = v.findViewById(R.id.check);
//            t.setText(header[j]);
//            t.setTextColor(Color.BLACK);
//            t.setOnClickListener(this::clickChartItem);
//            t.setChecked(j == 4);
//            vg.addView(v);
//        }
    }

    private void setDesc(String s) {
        mathChart.getDescription().setText(s);
    }

    private long[][] initChartData(TosCard c) {
        if (c == null) return null;

        final int n = c.LvMax + 1;
        long[][] result = new long[n][4];
        int lv;
        for (int i = 1; i < n; i++) {
            lv = i;
            result[i][0] = lv;
            result[i][1] = getExpSum(lv, c);
            result[i][2] = getExpSacrifice(lv, c);
            result[i][3] = result[i][2] - result[i][1];
        }
        return result;
    }

    private long getExpSum(int lv, TosCard c) {
        return TosMath.getExpSum(lv, c.expCurve);
    }

    private long getExpSacrifice(int lv, TosCard c) {
        return TosMath.getExpSacrifice(lv, c);
    }

    private List<ILineDataSet> asLineDataSet(String[] head, long[][] table, int start) {
        int m = table.length;
        int n = table[0].length;
        final int xKey = 0;
        int[] colors = {Color.BLACK, Color.RED, Color.GREEN, Color.BLUE};

        final List<ILineDataSet> allDataSet = new ArrayList<>();
        TicTacv clock = new TicTacv();
        clock.tic();
        for (int j = 0; j < n; j++) {
            // Add each row points
            List<Entry> entries = new ArrayList<>();
            for (int i = start; i < m; i++) {
                long x = table[i][xKey];
                long y = table[i][j];
                Entry ei = new Entry(x, y, x + ", " + y);
                entries.add(ei);
            }
            // As row data set
            LineDataSet set = new LineDataSet(entries, head[j]);
            set.setCircleColor(colors[j]);
            set.setCircleRadius(2F);
            //set.setDrawCircles(false);
            set.setColor(colors[j]); // line color
            set.setAxisDependency(YAxis.AxisDependency.LEFT);

            allDataSet.add(set);
        }
        return allDataSet;
    }

    private LineData getLineData(List<Integer> index, List<ILineDataSet> allDataSet) {
        if (index == null || index.size() == 0) return null;

        List<ILineDataSet> set = new ArrayList<>();
        for (Integer i : index) {
            set.add(allDataSet.get(i));
        }
        return new LineData(set);
    }
//
//    private void clickChartItem(View v) {
//        List<Integer> indices = new ArrayList<>();
//        int n = chartOptions.getChildCount();
//        for (int i = 0; i < n; i++) {
//            View w = chartOptions.getChildAt(i);
//            CheckBox ch = w.findViewById(R.id.check);
//            if (ch.isChecked()) {
//                indices.add(i + 1);
//            }
//        }
//
//        LineData data = TosSummonerChart.getLineData(indices);
//
//        mathChart.setData(data);
//        mathChart.invalidate();
//    }
}
