package com.flyingkite.mytoswiki.dialog;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.TosCard;
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

        findViewById(R.id.medChartReset).setOnClickListener((v) -> {
            mathChart.fitScreen();
        });

        long[][] table = initChartData(card);
        List<ILineDataSet> sets = asLineDataSet(headers, table);
        LineData data = getLineData(Arrays.asList(1, 2, 3), sets);

        //mathChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mathChart.getAxisLeft().setEnabled(false);
        mathChart.setData(data);
        mathChart.invalidate();
        setDesc("");
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

        final int n = c.LvMax;
        long[][] result = new long[n][4];
        int lv;
        for (int i = 0; i < n; i++) {
            lv = i + 1;
            result[i][0] = lv;
            result[i][1] = getExpSum(lv, c);
            result[i][2] = getExpSacrifice(lv, c);
            result[i][3] = result[i][2] - result[i][1];
        }
        return result;
    }

    private long getExpSum(int lv, TosCard c) {
        int curve10K = c.expCurve;
        double p = (lv - 1) / 98.0;
        double exp = curve10K * 10_000F * p * p;
        long sumExp = (long) Math.ceil(exp);
        return sumExp;
    }

    private long getExpSacrifice(int lv, TosCard c) {
        return c.minExpSacrifice + lv * c.perLvExpSacrifice;
    }

    private List<ILineDataSet> asLineDataSet(String[] head, long[][] table) {
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
            for (int i = 0; i < m; i++) {
                long x = table[i][xKey];
                long y = table[i][j];
                Entry ei = new Entry(x, y, x + ", " + y);
                entries.add(ei);
            }
            // As row data set
            LineDataSet set = new LineDataSet(entries, head[j]);
            set.setCircleColor(colors[j]);
            set.setCircleRadius(2);
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
