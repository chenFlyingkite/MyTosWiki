package com.flyingkite.mytoswiki.dialog;

import android.app.Dialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.View;
import android.widget.TextView;

import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.library.ButtonsAdapter;
import com.flyingkite.mytoswiki.library.Library;
import com.flyingkite.mytoswiki.library.SummonLvAdapter;
import com.flyingkite.mytoswiki.tos.TosSummonerLevel;

import java.util.ArrayList;
import java.util.List;

public class SummonerLevelDialog extends BaseTosDialog {
    private Library<SummonLvAdapter> tableLibrary;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_summoner_level;
    }

    @Override
    protected void onFinishInflate(View view, Dialog dialog) {
        initTable();
        initScrollTools(R.id.sldGoTop, R.id.sldGoBottom, tableLibrary.recyclerView);
        initShortcuts();
    }

    private void initShortcuts() {
        RecyclerView shortcuts = findViewById(R.id.sldShortcuts);

        ButtonsAdapter ba = new ButtonsAdapter();
        List<String> s = new ArrayList<>();
        int max = TosSummonerLevel.table.length;
        for (int i = 0; i <= max; i += 50) {
            s.add("" + i);
        }
        ba.setDataList(s);
        ba.setAutoScroll(true);
        ba.setItemListener(new ButtonsAdapter.ItemListener() {
            @Override
            public void onClick(String item, ButtonsAdapter.ButtonVH holder, int position) {
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
}
