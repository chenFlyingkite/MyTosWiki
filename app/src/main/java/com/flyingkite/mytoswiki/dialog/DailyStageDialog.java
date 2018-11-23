package com.flyingkite.mytoswiki.dialog;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyingkite.fabric.FabricAnswers;
import com.flyingkite.mytoswiki.App;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.stage.OpenTime;
import com.flyingkite.mytoswiki.data.stage.Stage;
import com.flyingkite.mytoswiki.library.OpenColumnAdapter;
import com.flyingkite.mytoswiki.library.StageAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DailyStageDialog extends BaseTosDialog {

    private List<Stage> daily = new ArrayList<>();
    private List<OpenTime> openHours = new ArrayList<>();
    private int today = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_daily_stage;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logImpression();
        initDailyStage();
        initOpenHour();

        fillStages();
    }

    private void fillStages() {
        StageAdapter a = new StageAdapter() {
            @Override
            public int holderLayoutId() {
                return R.layout.view_stage_row_2;
            }
        };
        a.setDataList(daily);
        a.setItemListener((stage, stageVH, position) -> {
            viewLinkAsWebDialog(stage.link);
        });
        fillItemsLinearly(findViewById(R.id.dds_stages), a);
        fillOpenTime();
    }

    private void fillOpenTime() {
        ViewGroup table = findViewById(R.id.dds_days);
        table.removeAllViews();
        for (int i = 0; i < openHours.size(); i++) {
            OpenTime time = openHours.get(i);

            // Fill in views
            View column = LayoutInflater.from(table.getContext()).inflate(R.layout.view_open_column, table, false);
            TextView title = column.findViewById(R.id.openTitle);
            title.setText(time.time);

            LinearLayout open = column.findViewById(R.id.openList);
            OpenColumnAdapter a = new OpenColumnAdapter();
            List<Character> hour = toCharList(time.open.toCharArray());
            a.setDataList(hour);
            fillItemsLinearly(open, a);
            int color = Color.TRANSPARENT;
            if (i % 2 == 0) { // even number
                 color = App.getColorF(R.color.colorPrimaryDark);
            }
            if (i == today) {
                color = App.getColorF(R.color.grey6);
            }
            column.setBackgroundColor(color);
            table.addView(column);
        }
    }

    private List<Character> toCharList(char[] cs) {
        if (cs == null) return null;

        List<Character> li = new ArrayList<>();
        for (int i = 0; i < cs.length; i++) {
            li.add(cs[i]);
        }
        return li;
    }

    private void initDailyStage() {
        final String[] links = {
                // 魔劍降臨之日
                "http://zh.tos.wikia.com/wiki/%E9%AD%94%E5%8A%8D%E9%99%8D%E8%87%A8%E4%B9%8B%E6%97%A5",
                // 狩獵珍獸之日
                "http://zh.tos.wikia.com/wiki/%E7%8B%A9%E7%8D%B5%E7%8F%8D%E7%8D%B8%E4%B9%8B%E6%97%A5",
                // 尋找水晶之日
                "http://zh.tos.wikia.com/wiki/%E5%B0%8B%E6%89%BE%E6%B0%B4%E6%99%B6%E4%B9%8B%E6%97%A5",
                // 魔物巢穴之日
                "http://zh.tos.wikia.com/wiki/%E9%AD%94%E7%89%A9%E5%B7%A2%E7%A9%B4%E4%B9%8B%E6%97%A5",
                // 元素魂魄之日
                "http://zh.tos.wikia.com/wiki/%E5%85%83%E7%B4%A0%E9%AD%82%E9%AD%84%E4%B9%8B%E6%97%A5",
                // 龍刻之日
                "http://zh.tos.wikia.com/wiki/%E9%BE%8D%E5%88%BB%E4%B9%8B%E6%97%A5",
                // 黃金之日
                "http://zh.tos.wikia.com/wiki/%E9%BB%83%E9%87%91%E4%B9%8B%E6%97%A5",
                // 靈魂之日
                "http://zh.tos.wikia.com/wiki/%E9%9D%88%E9%AD%82%E4%B9%8B%E6%97%A5",
                // 貪婪之日
                "http://zh.tos.wikia.com/wiki/%E8%B2%AA%E5%A9%AA%E4%B9%8B%E6%97%A5",
                // 捕捉靈魂石 ‧ 水
                "http://zh.tos.wikia.com/wiki/%E6%8D%95%E6%8D%89%E9%9D%88%E9%AD%82%E7%9F%B3_%E2%80%A7_%E6%B0%B4",
                // 捕捉靈魂石 ‧ 火
                "http://zh.tos.wikia.com/wiki/%E6%8D%95%E6%8D%89%E9%9D%88%E9%AD%82%E7%9F%B3_%E2%80%A7_%E7%81%AB",
                // 捕捉靈魂石 ‧ 木
                "http://zh.tos.wikia.com/wiki/%E6%8D%95%E6%8D%89%E9%9D%88%E9%AD%82%E7%9F%B3_%E2%80%A7_%E6%9C%A8",
                // 捕捉靈魂石 ‧ 光
                "http://zh.tos.wikia.com/wiki/%E6%8D%95%E6%8D%89%E9%9D%88%E9%AD%82%E7%9F%B3_%E2%80%A7_%E5%85%89",
                // 捕捉靈魂石 ‧ 暗
                "http://zh.tos.wikia.com/wiki/%E6%8D%95%E6%8D%89%E9%9D%88%E9%AD%82%E7%9F%B3_%E2%80%A7_%E6%9A%97",
        };
        final String[] icons = {"0266", "0269", "0263", "0387", "0256",
                // xx之日
                "0605", "0669", "0576", "0665",
                // 靈魂石
                "0426", "0427", "0428", "0398", "0291"
        };
        final String[] names = {"魔劍降臨之日", "狩獵珍獸之日", "尋找水晶之日", "魔物巢穴之日", "元素魂魄之日",
                "龍刻之日", "黃金之日", "靈魂之日", "貪婪之日",
                "捕捉靈魂石 ‧ 水", "捕捉靈魂石 ‧ 火", "捕捉靈魂石 ‧ 木", "捕捉靈魂石 ‧ 光", "捕捉靈魂石 ‧ 暗",
        };

        daily.clear();
        for (int i = 0; i < icons.length; i++) {
            Stage s = new Stage();
            s.link = links[i];
            s.icon = icons[i];
            s.name = names[i];
            daily.add(s);
        }
    }

    private void initOpenHour() {
        final String[] time = {"日", "一", "二", "三", "四", "五", "六"};
        final String[] hour = {
                // 0~9 = xx之日, 10~14 = 捕捉靈魂石
                "00000111000011",
                "11110000010000",
                "10001000001000",
                "01000110000100",
                "00100001000010",
                "00001100000001",
                "00010001111100",
        };
        for (int i = 0; i < time.length; i++) {
            OpenTime t = new OpenTime();
            t.time = time[i];
            t.open = hour[i];
            openHours.add(t);
        }
        Date d = new Date();
        today = d.getDay() % 7;
    }

    //-- Events
    private void logImpression() {
        Map<String, String> m = new HashMap<>();
        m.put("impression", "1");
        FabricAnswers.logDailyStage(m);
    }

    private void logShare(String type) {
        Map<String, String> m = new HashMap<>();
        m.put("share", type);
        FabricAnswers.logDailyStage(m);
    }
    //-- Events
}
