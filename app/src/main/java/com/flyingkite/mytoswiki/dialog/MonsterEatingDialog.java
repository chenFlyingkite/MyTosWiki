package com.flyingkite.mytoswiki.dialog;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;

import com.flyingkite.fabric.FabricAnswers;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.charts.MPChartUtil;
import com.flyingkite.mytoswiki.data.tos.TosCard;
import com.flyingkite.mytoswiki.tos.TosMath;
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

import flyingkite.library.androidx.TicTac2;

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
        super.onViewCreated(view, savedInstanceState);
        FabricAnswers.logMonsterEat(null);
        parseBundle(getArguments());
        initChart();
    }

    private void parseBundle(Bundle b) {
        boolean hasCard = b != null && b.containsKey(BUNDLE_CARD);
        if (!hasCard) return;


        card = b.getParcelable(BUNDLE_CARD);
    }

    private void initChart() {
        mathChart = findViewById(R.id.medMathChart);

        long[][] table = initChartData(card);
        List<ILineDataSet> sets = asLineDataSet(headers, table, 1);
        LineData data = getLineData(Arrays.asList(1, 2, 3), sets);

        // Evaluate pretty zoom
        int N = card.LvMax;
        int xmin = Math.max(card.maxMUPerLevel - 2, 1);
        int xmax = Math.min(card.maxTUAllLevel + 1, N);
        MPChartUtil.ScaleXY z = MPChartUtil.getPrettyZoom(table, xmin, xmax, N);

        //mathChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mathChart.setData(data);
        mathChart.getAxisLeft().setEnabled(false);
        mathChart.zoom(z.sx, z.sy, z.x, z.y, YAxis.AxisDependency.RIGHT);
        mathChart.invalidate();
        setDesc("");

        findViewById(R.id.medChartReset).setOnClickListener((v) -> {
            // Reset back to pretty zoom
            float sx = 1F * z.sx / mathChart.getScaleX();
            float sy = 1F * z.sy / mathChart.getScaleY();
            mathChart.zoom(sx, sy, z.x, z.y, YAxis.AxisDependency.RIGHT);
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
        TicTac2.v clock = new TicTac2.v();
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
}
