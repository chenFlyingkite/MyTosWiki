package com.flyingkite.mytoswiki.dialog;

import android.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flyingkite.library.Say;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.library.SummonLvAdapter;

public class SummonerLevelDialog extends BaseTosDialog {
    public SummonerLevelDialog(DialogOwner own) {
        super(own);
    }

    private RecyclerView tableRV;
    private SummonLvAdapter tableAT;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_summoner_level;
    }

    @Override
    protected void onFinishInflate(View view, AlertDialog dialog) {
        initTable();
        initScrollTools(R.id.sldGoTop, R.id.sldGoBottom, tableRV);
        initShortcuts();
    }

    private void initShortcuts() {
        ViewGroup ll = findViewById(R.id.sldShortcuts);

        View child = LayoutInflater.from(ll.getContext()).inflate(R.layout.view_round_button, ll, false);

        findViewById(R.id.sld50).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Say.Log("clk");
            }
        });
    }

    private void initTable() {
        tableRV = findViewById(R.id.sld_recycler);
        tableAT = new SummonLvAdapter();
        tableRV.setLayoutManager(tableAT.getLayoutManager(tableRV.getContext()));
        tableRV.setAdapter(tableAT);
    }
}
