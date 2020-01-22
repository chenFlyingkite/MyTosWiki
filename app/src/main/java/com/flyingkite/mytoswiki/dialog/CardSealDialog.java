package com.flyingkite.mytoswiki.dialog;

import android.app.Dialog;
import android.app.FragmentManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.flyingkite.fabric.FabricAnswers;
import com.flyingkite.library.Say;
import com.flyingkite.library.widget.Library;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.seal.BaseSeal;
import com.flyingkite.mytoswiki.data.seal.FairyTail;
import com.flyingkite.mytoswiki.data.seal.GiantLight;
import com.flyingkite.mytoswiki.data.seal.GiftedScientists;
import com.flyingkite.mytoswiki.data.seal.HinduGods;
import com.flyingkite.mytoswiki.data.seal.KonoSubarashi;
import com.flyingkite.mytoswiki.data.seal.MasterCathieves;
import com.flyingkite.mytoswiki.data.seal.Minerelves;
import com.flyingkite.mytoswiki.data.seal.OldenTrio;
import com.flyingkite.mytoswiki.data.seal.PhantomTroupe;
import com.flyingkite.mytoswiki.data.seal.PrimalDeities;
import com.flyingkite.mytoswiki.data.seal.SaintSeiya;
import com.flyingkite.mytoswiki.data.seal.SealItem;
import com.flyingkite.mytoswiki.data.seal.SealSample;
import com.flyingkite.mytoswiki.data.seal.SengokuSamurai;
import com.flyingkite.mytoswiki.data.seal.UnearthlyCharm;
import com.flyingkite.mytoswiki.data.seal.VirtualSingers;
import com.flyingkite.mytoswiki.data.tos.TosCard;
import com.flyingkite.mytoswiki.library.SealCardAdapter;
import com.flyingkite.mytoswiki.library.SealSeriesAdapter;
import com.flyingkite.mytoswiki.tos.TosWiki;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.IdRes;
import androidx.recyclerview.widget.RecyclerView;
import flyingkite.math.ChiSquarePearson;
import flyingkite.math.ChiSquareTable;
import flyingkite.math.Math2;

public class CardSealDialog extends BaseTosDialog {

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_card_seal;
    }

    public static final String BUNDLE_SEAL = "BUNDLE_SEAL";
    private static final List<SealItem> sealSeries = new ArrayList<>();

    static {
        sealSeries.clear();
        sealSeries.add(new SealItem(R.string.card_series_olden_trio, new OldenTrio()));
        sealSeries.add(new SealItem(R.string.card_series_phantom_troupe, new PhantomTroupe()));
        sealSeries.add(new SealItem(R.string.card_series_minerelves, new Minerelves()));
        sealSeries.add(new SealItem(R.string.card_series_virtual_singers,   new VirtualSingers()));
        sealSeries.add(new SealItem(R.string.card_series_giant_light,       new GiantLight()));
        sealSeries.add(new SealItem(R.string.card_series_primal_deities,    new PrimalDeities()));
        sealSeries.add(new SealItem(R.string.card_series_saint_seiya,       new SaintSeiya()));
        sealSeries.add(new SealItem(R.string.card_series_unearthly_charm,   new UnearthlyCharm()));
        sealSeries.add(new SealItem(R.string.card_series_gifted_scientists, new GiftedScientists()));
        sealSeries.add(new SealItem(R.string.card_series_fairy_tail,        new FairyTail()));
        sealSeries.add(new SealItem(R.string.card_series_master_cathieves,  new MasterCathieves()));
        sealSeries.add(new SealItem(R.string.card_series_kono_subarashi,    new KonoSubarashi()));
        sealSeries.add(new SealItem(R.string.card_series_hindu_gods,        new HinduGods()));
        sealSeries.add(new SealItem(R.string.card_series_sengoku_samurai,   new SengokuSamurai()));
    }

    private BaseSeal seals;

    // Views
    private Library<SealCardAdapter> cardPoolLibrary;
    private Library<SealSeriesAdapter> seriesLibrary;
    private View share;
    private View save;
    private View resetPool;
    private View resetTable;
    private CheckBox peekCard;
    private CheckBox raised;
    private LinearLayout sealTable;
    private TextView sealDrawn;
    private BarChart chart;
    private View autoDraw;
    private Spinner autoDrawN;
    private TextView pearsonChi;
    private TextView pearsonH0;

    @Override
    protected void onFinishInflate(View view, Dialog dialog) {
        super.onFinishInflate(view, dialog);
        logImpression();

        parseBundle(getArguments());
        dismissWhenClick(R.id.csdTitle, R.id.csdHeader, R.id.toswiki, R.id.empty);
        initViews();
        initSeries();
        initPools();
        initTable();
        initChart();
        setupPearsonChi();
    }

    private void parseBundle(Bundle b) {
        if (b == null) {
            seals = sealSeries.get(0).seal;
        } else {
            seals = b.getParcelable(BUNDLE_SEAL);
        }
    }

    @Override
    public boolean onBackPressed() {
        boolean ask = true;
        if (ask) {
            new CommonDialog().message(getString(R.string.leave)).listener(new CommonDialog.Action() {
                @Override
                public void onConfirm() {
                    dismissAllowingStateLoss();
                }
            }).show(getActivity());
            return true;
        } else {
            return super.onBackPressed();
        }
    }

    private void initViews() {
        save = findViewById(R.id.csdSave);
        share = findViewById(R.id.csdShare);
        resetPool = findViewById(R.id.csdPoolReset);
        sealDrawn = findViewById(R.id.csdDrawN);
        sealTable = findViewById(R.id.csdCardTable);
        resetTable = findViewById(R.id.csdTableReset);
        raised = findViewById(R.id.csdRaisedProbability);
        peekCard = findViewById(R.id.csdPeekCard);
        autoDraw = findViewById(R.id.csdAutoDraw);
        chart = findViewById(R.id.csdChart);
        pearsonChi = findViewById(R.id.csdPearsonChi);
        pearsonH0 = findViewById(R.id.csdPearsonH0);
    }

    private void initPools() {
        RecyclerView pool = findViewById(R.id.csdCardPool);

        // Card pool library
        cardPoolLibrary = new Library<>(pool, 5);
        SealCardAdapter a = new SealCardAdapter();
        List<TosCard> cards = drawNCards(false, 100);
        a.setDataList(cards);
        a.setItemListener(new SealCardAdapter.ItemListener() {
            @Override
            public void onClick(TosCard card, SealCardAdapter.SCardVH vh, int position) {
                if (card == null) return;

                if (vh.isDrawn()) {

                } else {
                    SealSample ss = getWorkingSample();
                    int cid = seals.sealCards.indexOf(card.idNorm);
                    ss.observe[cid]++;
                    ss.evalObservePdf();
                    setupTable();
                }
            }
        });
        cardPoolLibrary.setViewAdapter(a);

        // Init icons
        save.setOnClickListener((v) -> {
            logShare("save");
            shareImage(findViewById(R.id.csdContents));
        });

        // Actions on pool
        resetPool.setOnClickListener((v) -> {
            resetPool();
        });
        raised.setOnClickListener((v) -> {
            resetPool();
            logAction("Raise:" + Say.ox(raised.isChecked()));
        });
        peekCard.setOnClickListener((v) -> {
            boolean chk = peekCard.isChecked();
            a.setPeekCard(chk);
            a.notifyDataSetChanged();
            logAction("Peek:" + Say.ox(chk));
        });
        initScrollTools(R.id.csdPoolGoTop, R.id.csdPoolGoBottom, pool);

        // Auto draw
        TextView en = findViewById(R.id.csdAutoDrawEN);
        autoDrawN = makeSpin(R.id.csdAutoDrawN, 1, 1000);

        autoDraw.setOnClickListener((v) -> {
            String src = "10";
            src = en.getText().toString();
            //src = autoDrawN.getSelectedItem().toString();
            int auto;
            try {
                auto = Integer.parseInt(src);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return;
            }

            SealSample ss = getWorkingSample();
            ss.drawSample(auto);
            ss.evalObservePdf();
            setupTable();

        });
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            // Clear all the selections
            if (seriesLibrary != null) {
                seriesLibrary.adapter.setSelectIndex(0);
            }
            raised.setChecked(false);
            peekCard.setChecked(false);
        }
    }

    private void initSeries() {
        seriesLibrary = new Library<>(findViewById(R.id.csdCardSeries2), true);
        SealSeriesAdapter a = new SealSeriesAdapter();
        a.setDataList(sealSeries);
        a.setItemListener(new SealSeriesAdapter.ItemListener() {
            @Override
            public void onClick(SealItem item, SealSeriesAdapter.SealVH holder, int position) {
                BaseSeal p = item.seal;
                seals = p;
                logAction("Series:" + p.name());
                resetPool();
                setupTable();
            }
        });
        seriesLibrary.setViewAdapter(a);
    }

    private Spinner makeSpin(@IdRes int spinnerID, int from, int to) {
        int downId = android.R.layout.simple_spinner_dropdown_item;
        int layoutId = R.layout.view_spinner_item;

        // Set up adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), layoutId);
        adapter.setDropDownViewResource(downId);
        for (int i = from; i <= to; i++) {
            adapter.add("" + i);
        }

        // Set up spinner
        Spinner spinner = findViewById(spinnerID);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(adapterSelect);

        return spinner;
    }

    private AdapterView.OnItemSelectedListener adapterSelect = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void initTable() {
        resetTable.setOnClickListener((v) -> {
            logAction("Reset:Table");
            seals.normalSample.clearSample();
            seals.raisedSample.clearSample();
            setupTable();
        });

        initTableHeader();
        setupTable();
    }

    private void setupTable() {
        // Setup library
        SealDataRowAdapter a = new SealDataRowAdapter() {
            @Override
            public FragmentManager getFragmentManager() {
                return CardSealDialog.this.getFragmentManager();
            }
        };
        a.setItemListener(new SealDataRowAdapter.ItemListener() {
            @Override
            public void onItemStep(int position, int step) {
                SealSample ss = getWorkingSample();
                int cid = position;
                if (ss.observe[cid] + step < 0) return; // omit if negative

                ss.observe[cid] += step;
                ss.evalObservePdf();
                setupTable();
            }

            @Override
            public void onClick(BaseSeal baseSeal, SealDataRowAdapter.SealRowVH sealRowVH, int i) {

            }
        });
        a.setSeal(seals);
        a.setRaised(raised.isChecked());
        sealTable.removeAllViews();
        fillItems(sealTable, a);

        // Update drawn N
        int n = Math2.sum(getWorkingSample().observe);
        sealDrawn.setText(getString(R.string.card_n_draw, n, 5 * n));

        // Update chart & pearson
        initChart();
        setupPearsonChi();
    }

    private void initTableHeader() {
        ViewGroup vg = findViewById(R.id.csdTableHeader);
        TextView t;
        t = vg.findViewById(R.id.sealNumber);
        t.setText(getString(R.string.cards_normId));
        t = vg.findViewById(R.id.sealExpect);
        t.setText(getString(R.string.card_expect));
        t = vg.findViewById(R.id.sealObserve);
        t.setText(getString(R.string.card_observe));
        ImageView i = vg.findViewById(R.id.sealActor);
        i.setImageResource(0);
        i = vg.findViewById(R.id.sealPlus);
        i.setImageResource(0);
        i = vg.findViewById(R.id.sealMinus);
        i.setImageResource(0);
    }

    private void initChart() {
        findViewById(R.id.csdChartResetZoom).setOnClickListener((v) -> {
            chart.fitScreen();
        });
        setupChart();
    }

    private void setupChart() {
        BarData d = getBarData();
        d.setBarWidth(0.3f);

        XAxis x = chart.getXAxis();
        x.setTextColor(Color.WHITE);
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setAxisMinimum(-1);
        x.setAxisMaximum(seals.sealCards.size() + 1);
        chart.setData(d);
        setDesc("");
        chart.setDrawGridBackground(false);
        chart.getAxisLeft().setEnabled(false);
        chart.getAxisLeft().setAxisMinimum(0);
        chart.getAxisRight().setTextColor(Color.WHITE);
        chart.groupBars(0, 0.3f, 0.05f);
        chart.getLegend().setTextColor(Color.WHITE);
        chart.getDescription().setTextColor(Color.CYAN);
        chart.setOnChartValueSelectedListener(onChartValue);

        chart.invalidate();
    }

    private void setupPearsonChi() {
        SealSample ss = getWorkingSample();
        int n = Math2.sum(ss.observe);
        boolean pearson = false;
        if (n > 0) {
            pearson = ChiSquarePearson.acceptH0(ss, ChiSquareTable._100);
        }
        pearsonH0.setText(pearson ? R.string.accept : R.string.reject);
        updateLuck(pearson);

        String s = _fmt("pearson accept = %s, ss = %s", pearson, ss);
        pearsonChi.setText(s);
        double chi = ChiSquarePearson.getChiSquareValue(ss);
        Double[] alphas = ChiSquareTable.getChiTableRow(ss.size() - 1);
        StringBuilder s2 = new StringBuilder();
        for (int i = ChiSquareTable._100; i <= ChiSquareTable._005; i++) {
            s2.append(_fmt(" %7s,", alphas[i]));
        }
        s2.deleteCharAt(s2.length() - 1);
        s = _fmt("χ2 = %3.5f\nα   :    0.100,   0.050,   0.025,   0.010,   0.005\nχ2α : %s", chi, s2);
        pearsonChi.setText(s);
        logI("pearson accept = %s\nsample = %s", pearson, ss);
        logI("%s", s);
    }

    private void updateLuck(boolean accept) {
        findViewById(R.id.csdCardLuckNormal).setSelected(accept);
        findViewById(R.id.csdCardLuckAbnormal).setSelected(!accept);
    }

    private void setDesc(String s) {
        chart.getDescription().setText(s);
    }

    private OnChartValueSelectedListener onChartValue = new OnChartValueSelectedListener() {
        @Override
        public void onValueSelected(Entry e, Highlight h) {
            setDesc(e.getData().toString());
        }

        @Override
        public void onNothingSelected() {
            setDesc("");
        }
    };


    private List<BarEntry> getData(double[] a) {
        List<BarEntry> be = new ArrayList<>();
        for (int i = 0; i < a.length; i++) {
            double v = a[i];
            String s = _fmt("%s, %.3f", i + 1, v);
            be.add(new BarEntry(i + 1, (float) v, s));
        }
        return be;
    }

    private BarData getBarData(){
        SealSample ss = getWorkingSample();
        BarDataSet setA = new BarDataSet(getData(ss.pdf), getString(R.string.card_expect_value));
        setA.setValueTextColor(Color.WHITE);
        setA.setColor(Color.RED);
        BarDataSet setB = new BarDataSet(getData(ss.observePdf), getString(R.string.card_observe_value));
        setB.setValueTextColor(Color.WHITE);
        setB.setColor(Color.BLUE);

        // add the datasets
        List<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(setA);
        dataSets.add(setB);

        return new BarData(dataSets);
    }

    private void resetPool() {
        cardPoolLibrary.adapter.setDataList(drawNCards(100)).notifyDataSetChanged();
        setupTable();
    }

    private List<TosCard> drawNCards(int n) {
        return drawNCards(raised.isChecked(), n);
    }

    private List<TosCard> drawNCards(boolean raise, int n) {
        SealSample ss = getWorkingSample(raise);
        List<Integer> ids = ss.randomSample(n);
        List<TosCard> cards = new ArrayList<>();
        for (int id : ids) {
            cards.add(TosWiki.getCardByIdNorm(seals.sealCards.get(id)));
        }
        return cards;
    }

    private SealSample getWorkingSample() {
        return getWorkingSample(raised.isChecked());
    }

    private SealSample getWorkingSample(boolean raise) {
        return raise ? seals.raisedSample : seals.normalSample;
    }


    //-- Events
    private void logShare(String type) {
        Map<String, String> m = new HashMap<>();
        m.put("share", type);
        FabricAnswers.logCardSeal(m);
    }
    private void logAction(String type) {
        Map<String, String> m = new HashMap<>();
        m.put("action", type);
        FabricAnswers.logCardSeal(m);
    }

    private void logImpression() {
        Map<String, String> m = new HashMap<>();
        m.put("impression", "1");
        FabricAnswers.logCardSeal(m);
    }
    //-- Events

}
