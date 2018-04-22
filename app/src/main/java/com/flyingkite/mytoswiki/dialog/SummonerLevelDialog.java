package com.flyingkite.mytoswiki.dialog;

import android.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;

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
    }

    private void initTable() {
        tableRV = findViewById(R.id.sld_recycler);
        tableAT = new SummonLvAdapter();
        tableRV.setLayoutManager(tableAT.getLayoutManager(tableRV.getContext()));
        tableRV.setAdapter(tableAT);
    }
}
