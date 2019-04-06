package com.flyingkite.mytoswiki.dialog;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

import com.flyingkite.fabric.FabricAnswers;
import com.flyingkite.library.widget.Library;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.library.RunestoneAdapter;
import com.flyingkite.mytoswiki.tos.Runestones;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RunestoneEditorDialog extends BaseTosDialog {
    @Override
    protected int getLayoutId() {
        return R.layout.dialog_runestone_editor;
    }

    private Library<RunestoneAdapter> stoneFrom;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initDesktops();
        findViewById(R.id.redRandom).setOnClickListener((v) -> {
            initDesktops();
        });

//        dismissWhenClick(R.id.abd_header);
//        bulletin = findViewById(R.id.abd_message);
//        String s = RemoteConfig.getString(RemoteConfigKey.DIALOG_BULLETIN_MESSAGE);
//        bulletin.setText(Html.fromHtml(s));
        logImpression();
    }

    private void initDesktops() {
        stoneFrom = new Library<>(findViewById(R.id.redDesktopFrom), 6);
        RunestoneAdapter a = new RunestoneAdapter();
        String stones = Runestones.random(30);
        char[] chs = stones.toCharArray();
        List<Character> stone = new ArrayList<>();
        for (char c : chs) {
            stone.add(c);
        }
        a.setDataList(stone);
        stoneFrom.setViewAdapter(a);
        logE("stones = %s", stones);
    }

    //-- Events
    private void logShare(String type) {
        Map<String, String> m = new HashMap<>();
        m.put("share", type);
        FabricAnswers.logRunestone(m);
    }

    private void logImpression() {
        Map<String, String> m = new HashMap<>();
        m.put("impression", "1");
        FabricAnswers.logRunestone(m);
    }
    //-- Events
}

