package com.flyingkite.mytoswiki.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.flyingkite.library.TicTac;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.library.selectable.SelectableAdapter;

import java.util.ArrayList;
import java.util.List;

public class SkillEatingDialog extends BaseTosDialog {
    public SkillEatingDialog(DialogOwner own) {
        super(own);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_skill_eat;
    }

    private RecyclerView recycler;
    private SelectableAdapter adapter;

    @Override
    protected void onFinishInflate(View view, AlertDialog dialog) {
        TicTac.tic();
        initSpinners(view);
        TicTac.tac("spin");

        TicTac.tic();
        recycler = view.findViewById(R.id.skillTable);
        int row = 4;
        recycler.setLayoutManager(new GridLayoutManager(getActivity(), row));
        adapter = new SelectableAdapter();
        adapter.setRow(row);

        List<String> data = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            data.add("   " + i);
        }
        adapter.setDataList(data);
        recycler.setAdapter(adapter);
        TicTac.tac("adapter");
    }

    private void initSpinners(View v) {
        ArrayAdapter<String> levels = new ArrayAdapter<>(getActivity()
                , android.R.layout.simple_spinner_item
                //, R.layout.view_text
        );
        for (int i = 1; i <= 15; i++) {
            levels.add("   " + i + "   ");
        }

        Spinner from = v.findViewById(R.id.skillFrom);
        from.setAdapter(levels);
        Spinner to = v.findViewById(R.id.skillTo);
        to.setAdapter(levels);


        ArrayAdapter<String> progress = new ArrayAdapter<>(getActivity()
                , android.R.layout.simple_spinner_item
                //, R.layout.view_text
        );
        for (int i = 0; i < 100; i++) {
            progress.add("   " + i + "   ");
        }
        Spinner pgs = v.findViewById(R.id.skillPercent);
        pgs.setAdapter(progress);
    }

    private Activity getActivity() {
        return owner.getActivity();
    }
}
