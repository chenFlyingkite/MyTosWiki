package com.flyingkite.mytoswiki.dialog;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.flyingkite.fabric.FabricAnswers;
import com.flyingkite.mytoswiki.App;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.stage.StageOpenTime;
import com.flyingkite.mytoswiki.library.DailyStageAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;

public class DailyStageDialog extends BaseTosDialog {

    private final List<StageOpenTime> dailyStages = getStages();

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_daily_stage;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logImpression();

        fillStages();
        dismissWhenClick(R.id.dds_title, R.id.dds_desc);
    }

    private void fillStages() {
        int today = new Date().getDay() % 7;
        setHeader(today);
        DailyStageAdapter a = new DailyStageAdapter();
        a.setToday(today);
        a.setDataList(dailyStages);
        a.setItemListener(new DailyStageAdapter.ItemListener() {
            @Override
            public void onClick(StageOpenTime it, DailyStageAdapter.StageVH vh, int position) {
                viewLinkAsWebDialog(it.stage.link);
            }
        });
        fillItems(findViewById(R.id.dds_stages), a);
    }

    private void setHeader(int today) {
        // set header background
        int[] ids = {R.id.dds_day0, R.id.dds_day1, R.id.dds_day2, R.id.dds_day3,
                R.id.dds_day4, R.id.dds_day5, R.id.dds_day6};
        for (int i = 0; i < 7; i++) {
            int id = ids[i];
            int color = Color.TRANSPARENT;
            if (i % 2 == 0) { // even number
                color = App.res().getColor(R.color.colorPrimaryDark);
            }
            if (i == today) {
                color = App.res().getColor(R.color.grey6);
            }
            findViewById(id).setBackgroundColor(color);
        }
    }

    private List<StageOpenTime> getStages() {
        List<StageOpenTime> li = new ArrayList<>();
        li.add(new StageOpenTime(
                "魔劍降臨之日", "0266", "0110000",
                "https://tos.fandom.com/zh/wiki/%E9%AD%94%E5%8A%8D%E9%99%8D%E8%87%A8%E4%B9%8B%E6%97%A5"
        ));
        li.add(new StageOpenTime(
                "狩獵珍獸之日", "0269", "0101000",
                "https://tos.fandom.com/zh/wiki/%E7%8B%A9%E7%8D%B5%E7%8F%8D%E7%8D%B8%E4%B9%8B%E6%97%A5"
        ));
        li.add(new StageOpenTime(
                "尋找水晶之日", "0263", "0100100",
                "https://tos.fandom.com/zh/wiki/%E5%B0%8B%E6%89%BE%E6%B0%B4%E6%99%B6%E4%B9%8B%E6%97%A5"
        ));
        li.add(new StageOpenTime(
                "魔物巢穴之日", "0387", "0100001",
                "https://tos.fandom.com/zh/wiki/%E9%AD%94%E7%89%A9%E5%B7%A2%E7%A9%B4%E4%B9%8B%E6%97%A5"
        ));
        li.add(new StageOpenTime(
                "元素之日", "0256", "0010010",
                "https://tos.fandom.com/zh/wiki/%E5%85%83%E7%B4%A0%E4%B9%8B%E6%97%A5"
        ));
        li.add(new StageOpenTime(
                "龍刻之日", "0266", "1001010",
                "https://tos.fandom.com/zh/wiki/%E9%BE%8D%E5%88%BB%E4%B9%8B%E6%97%A5"
        ));
        li.add(new StageOpenTime(
                "黃金之日", "0669", "1111111",
                "https://tos.fandom.com/zh/wiki/%E9%BB%83%E9%87%91%E4%B9%8B%E6%97%A5"
        ));
        li.add(new StageOpenTime(
                "靈魂之日", "0578", "1010101",
                "https://tos.fandom.com/zh/wiki/%E9%9D%88%E9%AD%82%E4%B9%8B%E6%97%A5"
        ));
        li.add(new StageOpenTime(
                "貪婪之日", "0670", "1111111",
                "https://tos.fandom.com/zh/wiki/%E8%B2%AA%E5%A9%AA%E4%B9%8B%E6%97%A5"
        ));
        // gift.png
        li.add(new StageOpenTime(
                "週四資源關", "gift", "0000100",
                "https://tos.fandom.com/zh/wiki/%E9%80%B1%E5%9B%9B%E8%B3%87%E6%BA%90%E9%97%9C"
        ));
        li.add(new StageOpenTime(
                "週末福利關", "gift", "1000000",
                "https://tos.fandom.com/zh/wiki/%E9%80%B1%E6%9C%AB%E7%A6%8F%E5%88%A9%E9%97%9C"
        ));
        li.add(new StageOpenTime(
                "捕捉靈魂石 ‧ 水", "0426", "0100001",
                "https://tos.fandom.com/zh/wiki/%E6%8D%95%E6%8D%89%E9%9D%88%E9%AD%82%E7%9F%B3_%E2%80%A7_%E6%B0%B4"
        ));
        li.add(new StageOpenTime(
                "捕捉靈魂石 ‧ 火", "0427", "0010001",
                "https://tos.fandom.com/zh/wiki/%E6%8D%95%E6%8D%89%E9%9D%88%E9%AD%82%E7%9F%B3_%E2%80%A7_%E7%81%AB"
        ));
        li.add(new StageOpenTime(
                "捕捉靈魂石 ‧ 木", "0428", "0001001",
                "https://tos.fandom.com/zh/wiki/%E6%8D%95%E6%8D%89%E9%9D%88%E9%AD%82%E7%9F%B3_%E2%80%A7_%E6%9C%A8"
        ));
        li.add(new StageOpenTime(
                "捕捉靈魂石 ‧ 光", "0398", "1000100",
                "https://tos.fandom.com/zh/wiki/%E6%8D%95%E6%8D%89%E9%9D%88%E9%AD%82%E7%9F%B3_%E2%80%A7_%E5%85%89"
        ));
        li.add(new StageOpenTime(
                "捕捉靈魂石 ‧ 暗", "0291", "1000010",
                "https://tos.fandom.com/zh/wiki/%E6%8D%95%E6%8D%89%E9%9D%88%E9%AD%82%E7%9F%B3_%E2%80%A7_%E6%9A%97"
        ));
        return li;
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
