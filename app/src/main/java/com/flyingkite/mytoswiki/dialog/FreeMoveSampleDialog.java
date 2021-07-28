package com.flyingkite.mytoswiki.dialog;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flyingkite.fabric.FabricAnswers;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.StoneDesktop;
import com.flyingkite.mytoswiki.library.StoneDesktopAdapter;
import com.flyingkite.mytoswiki.share.ShareHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FreeMoveSampleDialog extends BaseTosDialog {
    @Override
    protected int getLayoutId() {
        return R.layout.dialog_free_move_sample;
    }

    private final List<StoneDesktop> stones = new ArrayList<>();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();
        initActions();
        initDesktops();
        logImpression();
    }

    private void initData() {
        stones.clear();
        Collections.addAll(stones,
              //                         1         2         3
              //                123456789012345678901234567890
              new StoneDesktop("WFEWEFWFEWEFWFEWEFLLLDDDDDDLLL", "(0)   10 Combo\n浪速虎鯨 阿羅哈 (#1406)")
            , new StoneDesktop("LLLHHHwwwLWLhhhLWLwwwLWLhhhhhL", "(2, 1)    9 Combo")
            , new StoneDesktop("HHHwfwWFEwfwWFEwfwWFEdddWFEHHH", "(1, 1, 1) 9 Combo")
            , new StoneDesktop("ELdwwwELdhhhELdwwwELdeeeELdwww", "(2, 2, 2)     8 Combo")
            , new StoneDesktop("HlDDDEHlFEFEHlFEFEHlFEFEHlFDDD", "(2, 2, 1, 1)  8 Combo")
            , new StoneDesktop("weeeeFwFwFwFwFwFwFwFwFwFwLLLLF", "(2, 2, 1, 1)  8 Combo")
            , new StoneDesktop("HHHLLLdddddEWWWWFEDDDDFEHHHHFE", "(2, 1, 1, 1, 1)   8 Combo")
            , new StoneDesktop("HHHLDWWFELDWWFELDWWFELDWWFEHHH", "(1, 1, 1, 1, 1, 1)  8 Combo\n魔女之罪 傑拉爾 (#1963)")
            , new StoneDesktop("wEHHHHwEwfwEwEwfwEwEwfwEwEwfwE", "(2, 2, 1, 1, 1, 1, 1) 7 Combo")
            , new StoneDesktop("dwfelddwfelddwfelddwfelddwfeld", "(2, 2, 2, 2, 2, 2) 6 Combo\n因果破壞 阿撒托斯 (#1645)")
        );
    }

    private void initActions() {
        TextView detail = findViewById(R.id.fsdDetail);
        detail.setOnClickListener((v) -> {
            ShareHelper.shareString(getActivity(), detail.getText().toString());
            logShare("detail");
        });

        findViewById(R.id.fsdSave).setOnClickListener((v) -> {
            shareImage(findViewById(R.id.fsdContent));
            logShare("all");
        });
    }

    private void initDesktops() {
        ViewGroup desktops = findViewById(R.id.fsdDesktopAll);
        StoneDesktopAdapter a = new StoneDesktopAdapter();
        a.setDataList(stones);
        a.setItemListener(new StoneDesktopAdapter.ItemListener() {
            @Override
            public void onClick(StoneDesktop item, StoneDesktopAdapter.StoneDesktopVH holder, int position) {

            }

            @Override
            public void onClickShare(StoneDesktop item, StoneDesktopAdapter.StoneDesktopVH holder, View v, int position) {
                shareImage(holder.itemView);
                logShare(item.detail.substring(1, item.detail.indexOf(')')).replaceAll("[, ]", ""));
            }
        });
        fillItems(desktops, a);
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