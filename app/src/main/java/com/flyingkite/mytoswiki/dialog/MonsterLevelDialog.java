package com.flyingkite.mytoswiki.dialog;

import android.app.Dialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.library.ButtonsAdapter;
import com.flyingkite.mytoswiki.library.Library;
import com.flyingkite.mytoswiki.library.MonsterLvAdapter;

import java.util.ArrayList;
import java.util.List;

public class MonsterLevelDialog extends BaseTosDialog {
    private Library<MonsterLvAdapter> tableLibrary;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_monster_level;
    }

    @Override
    protected void onFinishInflate(View view, Dialog dialog) {
        initTable();
        initScrollTools(R.id.mldGoTop, R.id.mldGoBottom, tableLibrary.recyclerView);
        initShortcuts();
    }

    private void initShortcuts() {
        RecyclerView shortcuts = findViewById(R.id.mldShortcuts);

        ButtonsAdapter ba = new ButtonsAdapter();
        List<String> s = new ArrayList<>();
        int max = 1000;
        for (int i = 50; i <= max; i += 50) {
            s.add(i + "萬");
        }
        ba.setDataList(s);
        ba.setAutoScroll(true);
        ba.setItemListener(new ButtonsAdapter.ItemListener() {
            @Override
            public void onClick(String item, ButtonsAdapter.ButtonVH holder, int position) {
                setHeader(item);

                // Change table
                int pos = (position + 1) * 50;
                tableLibrary.adapter.setExpCurve(pos);
            }
        });

        shortcuts.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        shortcuts.setAdapter(ba);
    }

    private void setHeader(String h) {
        TextView t = findViewById(R.id.mldHeader);
        t.setText(h);
    }

    private void initTable() {
        tableLibrary = new Library<>(findViewById(R.id.mld_recycler), new GridLayoutManager(getActivity(), 3));
        tableLibrary.recyclerView.setItemAnimator(null);
        tableLibrary.setViewAdapter(new MonsterLvAdapter());
    }
}
