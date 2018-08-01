package com.flyingkite.mytoswiki.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.flyingkite.library.widget.Library;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.library.MonsterLvAdapter;
import com.flyingkite.mytoswiki.library.TextAdapter;

import java.util.ArrayList;
import java.util.List;

public class MonsterLevelDialog extends BaseTosDialog {
    private Library<MonsterLvAdapter> tableLibrary;
    public static final String BUNDLE_CURVE = "MonsterLevelDialog.Curve";
    private boolean hasBundle;
    private int curve = 50;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_monster_level;
    }

    @Override
    protected void onFinishInflate(View view, Dialog dialog) {
        parseBundle(getArguments());
        initTable();
        initScrollTools(R.id.mldGoTop, R.id.mldGoBottom, tableLibrary.recyclerView);
        initShortcuts();
        if (hasBundle) {
            setHeader(curve + "萬");
        }
    }


    private void parseBundle(Bundle b) {
        boolean hasCurve = b != null && b.containsKey(BUNDLE_CURVE);
        if (hasCurve) {
            hasBundle = true;
            curve = b.getInt(BUNDLE_CURVE);
        }
    }

    private void initShortcuts() {
        RecyclerView shortcuts = findViewById(R.id.mldShortcuts);

        TextAdapter ba = new TextAdapter();
        List<String> s = new ArrayList<>();
        int max = 1000;
        for (int i = 50; i <= max; i += 50) {
            s.add(i + "萬");
        }
        ba.setDataList(s);
        ba.setAutoScroll(true);
        ba.setItemListener(new TextAdapter.ItemListener() {
            @Override
            public void onClick(String item, TextAdapter.TextVH holder, int position) {
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
        tableLibrary = new Library<>(findViewById(R.id.mld_recycler), 3);
        tableLibrary.recyclerView.setItemAnimator(null);
        MonsterLvAdapter a = new MonsterLvAdapter();
        a.setExpCurve(curve);
        tableLibrary.setViewAdapter(a);
    }
}
