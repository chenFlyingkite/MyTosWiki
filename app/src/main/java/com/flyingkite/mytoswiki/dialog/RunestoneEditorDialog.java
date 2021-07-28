package com.flyingkite.mytoswiki.dialog;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flyingkite.fabric.FabricAnswers;
import com.flyingkite.library.widget.Library;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.library.RunestoneAdapter;
import com.flyingkite.mytoswiki.tos.Runestones;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class RunestoneEditorDialog extends BaseTosDialog {
    @Override
    protected int getLayoutId() {
        return R.layout.dialog_runestone_editor;
    }

    @Override
    protected boolean useFloating() {
        return true;
    }

    private Library<RunestoneAdapter> stoneFrom;
    private Library<RunestoneAdapter> stoneTo;

    private ViewGroup normalPalette;
    private ViewGroup enchantPalette;
    private View selectStone;

    private TextView fromStone;
    private TextView toStone;
    private View sameStone;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initActions();
        initDesktops();
        initStonePalette();
        findViewById(R.id.redRandom).setOnClickListener((v) -> {
            initDesktops();
        });

//        dismissWhenClick(R.id.abd_header);
//        bulletin = findViewById(R.id.abd_message);
//        String s = RemoteConfig.getString(RemoteConfigKey.DIALOG_BULLETIN_MESSAGE);
//        bulletin.setText(Html.fromHtml(s));
        logImpression();
    }

    @Override
    public boolean onBackPressed() {
        boolean ask = true;
        if (ask) {
            new CommonDialog().message(getString(R.string.leave)).listener(new CommonDialog.Action() {
                @Override
                public void onConfirm() {
                    dismissAllowingStateLoss();
                }
            }).show(getActivity());
            return true;
        } else {
            return super.onBackPressed();
        }
    }

    private void initActions() {
        toStone = findViewById(R.id.redDesktopToStones);
        sameStone = findViewById(R.id.redStoneSameCount);
        fromStone = findViewById(R.id.redDesktopFromStones);
        normalPalette = findViewById(R.id.redStoneNormal);
        enchantPalette = findViewById(R.id.redStoneEnchant);
        findViewById(R.id.redShare).setOnClickListener((v) -> {
            shareImage(findViewById(R.id.redDesktop));
            logShare("palette");
        });
        findViewById(R.id.redCopyDesktopFrom).setOnClickListener((v) -> {
            String s = stoneFrom.adapter.getStones();
            stoneTo.adapter.setStones(s);
            onStoneToChanged();
        });
        findViewById(R.id.redCopyDesktopTo).setOnClickListener((v) -> {
            String s = stoneTo.adapter.getStones();
            stoneFrom.adapter.setStones(s);
            onStoneFromChanged();
        });
    }

    private void initStonePalette() {
        View.OnClickListener onClickStone = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectStone != null) {
                    selectStone.setSelected(false);
                }
                selectStone = v;
                if (selectStone != null) {
                    selectStone.setSelected(true);
                }
            }
        };

        ViewGroup[] vgs = {normalPalette, enchantPalette};
        for (int i = 0; i < vgs.length; i++) {
            ViewGroup vg = vgs[i];
            for (int j = 0; j < vg.getChildCount(); j++) {
                View v = vg.getChildAt(j);
                v.setOnClickListener(onClickStone);
            }
        }
    }

    private void initDesktops() {
        initDesktopsFrom();
        initDesktopsTo();
    }

    private char getStoneKey(View v) {
        String s = (String) v.getTag();
        return s.charAt(0);
    }

    private void initDesktopsFrom() {
        stoneFrom = new Library<>(findViewById(R.id.redDesktopFrom), 6);
        RunestoneAdapter a = new RunestoneAdapter();
        String stones = Runestones.random(30);
        char[] chs = stones.toCharArray();
        List<Character> stone = new ArrayList<>();
        for (char c : chs) {
            stone.add(c);
        }
        a.setDataList(stone);
        a.setItemListener(new RunestoneAdapter.ItemListener() {
            @Override
            public void onClick(Character item, RunestoneAdapter.RunestoneVH holder, int position) {
                if (selectStone == null) {
                    showToast(getString(R.string.enterStone));
                } else {
                    Character c = getStoneKey(selectStone);
                    a.setStone(position, c);
                    onStoneFromChanged();
                }
            }
        });
        stoneFrom.setViewAdapter(a);
        stoneFrom.recyclerView.setItemAnimator(null);
        onStoneFromChanged();
        logE("stones f = %s", stones);
    }

    private void initDesktopsTo() {
        stoneTo = new Library<>(findViewById(R.id.redDesktopTo), 6);
        RunestoneAdapter a = new RunestoneAdapter();
        String stones = Runestones.random(30);
        char[] chs = stones.toCharArray();
        List<Character> stone = new ArrayList<>();
        for (char c : chs) {
            stone.add(c);
        }
        a.setDataList(stone);
        a.setItemListener(new RunestoneAdapter.ItemListener() {
            @Override
            public void onClick(Character item, RunestoneAdapter.RunestoneVH holder, int position) {
                if (selectStone == null) {
                    showToast(getString(R.string.enterStone));
                } else {
                    Character c = getStoneKey(selectStone);
                    a.setStone(position, c);
                    onStoneToChanged();
                }
            }
        });
        stoneTo.setViewAdapter(a);
        stoneTo.recyclerView.setItemAnimator(null);
        onStoneToChanged();
        logE("stones t = %s", stones);
    }

    private void onStoneToChanged() {
        toStone.setText(countStones(stoneTo.adapter.getStoneChars()));
        onStonesChanged();
    }

    private void onStoneFromChanged() {
        fromStone.setText(countStones(stoneFrom.adapter.getStoneChars()));
        onStonesChanged();
    }

    private void onStonesChanged() {
        sameStone.setSelected(toStone.getText().equals(fromStone.getText()));
    }

    private final RecyclerView.AdapterDataObserver onStoneToChanged = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
        }
    };

    private String countStones(char[] chs) {
        // W, F, E, L, D, H
        int[] stone = new int[7];
        for (int i = 0; i < chs.length; i++) {
            char c = chs[i];
            int k = 0;
            switch (c) {
                case 'w': case 'W': k = 1; break;
                case 'f': case 'F': k = 2; break;
                case 'e': case 'E': k = 3; break;
                case 'l': case 'L': k = 4; break;
                case 'd': case 'D': k = 5; break;
                case 'h': case 'H': k = 6; break;
            }
            stone[k]++;
        }

        StringBuilder s = new StringBuilder();
        String[] attr = {"", "水", "火", "木", "光", "暗", "心"};
        for (int i = 1; i < attr.length; i++) {
            if (i > 1) {
                s.append(", ");
            }
            s.append(attr[i]).append(" = ").append(stone[i]);

        }
        return s.toString();
    }

    //-- Events
    private void logShare(String type) {
        Map<String, String> m = new HashMap<>();
        m.put("share", type);
        m.put("from", stoneFrom.adapter.getStones());
        m.put("to", stoneTo.adapter.getStones());
        FabricAnswers.logEditRunestone(m);
    }

    private void logImpression() {
        Map<String, String> m = new HashMap<>();
        m.put("impression", "1");
        FabricAnswers.logEditRunestone(m);
    }
    //-- Events
}

