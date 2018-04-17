package com.flyingkite.mytoswiki.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

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
    }

    private Activity getActivity() {
        return owner.getActivity();
    }
}
