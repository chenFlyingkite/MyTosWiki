package com.flyingkite.mytoswiki.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.flyingkite.fabric.FabricAnswers;
import com.flyingkite.library.widget.Library;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.charts.TosSummonerChart;
import com.flyingkite.mytoswiki.library.SummonLvAdapter;
import com.flyingkite.mytoswiki.library.TextAdapter;
import com.flyingkite.mytoswiki.tos.TosSummonerLevel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SummonerLevelDialog extends BaseTosDialog {
    private Library<SummonLvAdapter> tableLibrary;
    private ViewGroup chartOptions;
    private LineChart mathChart;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_summoner_level;
    }

    @Override
    protected void onFinishInflate(View view, Dialog dialog) {
        FabricAnswers.logSummonerLevel(null);
        initTable();
        initScrollTools(R.id.sldGoTop, R.id.sldGoBottom, tableLibrary.recyclerView);
        initShortcuts();
        initChart();
        initTools();
    }

    private void initTools() {
        View fx = findViewById(R.id.sldSave);
        fx.setOnClickListener((v) -> {
            boolean sel = !fx.isSelected();
            fx.setSelected(sel);
            int VIS = sel ? View.VISIBLE : View.GONE;
            int vis = sel ? View.GONE : View.VISIBLE;

            setVisibilities(VIS, R.id.sld_chart, R.id.sld_chartOptions);
            setVisibilities(vis, R.id.sld_table, R.id.sld_shortcut);
        });
    }

    private void initShortcuts() {
        RecyclerView shortcuts = findViewById(R.id.sldShortcuts);

        TextAdapter ba = new TextAdapter();
        List<String> s = new ArrayList<>();
        int max = TosSummonerLevel.table.length;
        for (int i = 0; i <= max; i += 50) {
            s.add("" + i);
        }
        ba.setDataList(s);
        ba.setAutoScroll(true);
        ba.setItemListener(new TextAdapter.ItemListener() {
            @Override
            public void onClick(String item, TextAdapter.TextVH holder, int position) {
                setHeader(item);

                // Scroll to item
                int pos = Integer.parseInt(item);
                LayoutManager lm = tableLibrary.recyclerView.getLayoutManager();

                if (lm instanceof LinearLayoutManager) {
                    LinearLayoutManager llm = (LinearLayoutManager) lm;
                    llm.scrollToPositionWithOffset(pos, 0);
                } else {
                    lm.scrollToPosition(pos);
                }
            }
        });

        shortcuts.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        shortcuts.setAdapter(ba);
    }

    private void setHeader(String h) {
        TextView t = findViewById(R.id.sldHeader);
        t.setText(h);
    }

    private void initTable() {
        tableLibrary = new Library<>(findViewById(R.id.sld_recycler), true);
        tableLibrary.recyclerView.setItemAnimator(null);
        tableLibrary.setViewAdapter(new SummonLvAdapter());
    }

    private void initChart() {
        mathChart = findViewById(R.id.sldMathChart);
        chartOptions = findViewById(R.id.sldChartOptions);

        findViewById(R.id.sldChartReset).setOnClickListener((v) -> {
            mathChart.fitScreen();
        });
        int n = TosSummonerChart.getColumnCount();
        final String[] header = TosSummonerChart.header;
        LineData data = TosSummonerChart.getLineData(Arrays.asList(4));

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

        ViewGroup vg = chartOptions;
        for (int j = 1; j < n; j++) {
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.view_checkbox, vg, false);
            CheckBox t = v.findViewById(R.id.check);
            t.setText(header[j]);
            t.setTextColor(Color.BLACK);
            t.setOnClickListener(this::clickChartItem);
            t.setChecked(j == 4);
            vg.addView(v);
        }
    }

    private void setDesc(String s) {
        mathChart.getDescription().setText(s);
    }

    private void clickChartItem(View v) {
        List<Integer> indices = new ArrayList<>();
        int n = chartOptions.getChildCount();
        for (int i = 0; i < n; i++) {
            View w = chartOptions.getChildAt(i);
            CheckBox ch = w.findViewById(R.id.check);
            if (ch.isChecked()) {
                indices.add(i + 1);
            }
        }

        LineData data = TosSummonerChart.getLineData(indices);

        mathChart.setData(data);
        mathChart.invalidate();
    }
}
