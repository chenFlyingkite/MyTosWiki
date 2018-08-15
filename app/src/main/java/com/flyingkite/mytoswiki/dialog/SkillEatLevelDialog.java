package com.flyingkite.mytoswiki.dialog;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.library.NumberLineAdapter;

import java.util.ArrayList;
import java.util.List;

public class SkillEatLevelDialog extends BaseTosDialog {
    @Override
    protected int getLayoutId() {
        return R.layout.dialog_skill_eat_level;
    }

    private LinearLayout levelLinear;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //initLevels();
        dismissWhenClick(R.id.selConcept);
    }

    private void initLevels() {
        //levelLinear = findViewById(R.id.selLevels);

        NumberLineAdapter a = new NumberLineAdapter();
        TypedArray num30 = getResources().obtainTypedArray(R.array.iconsNumber);
        List<Integer> num = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            int id = num30.getResourceId(i, 0);
            num.add(id);
        }
        num30.recycle();
        a.setDataList(num);
        //fillItemsLinearly(levelLinear, a);
    }
}
