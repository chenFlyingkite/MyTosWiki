package com.flyingkite.mytoswiki.dialog;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flyingkite.fabric.FabricAnswers;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.StoneDesktop;
import com.flyingkite.mytoswiki.data.StoneFix;
import com.flyingkite.mytoswiki.library.FixStoneAdapter;
import com.flyingkite.mytoswiki.share.ShareHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;

public class FixedStoneDialog extends BaseTosDialog {
    @Override
    protected int getLayoutId() {
        return R.layout.dialog_fix_stone;
    }

    private List<StoneFix> stones = new ArrayList<>();

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
        // stone = (row)5, row = [wfeldhWFELDH]6, list left to right, up to down
        Collections.addAll(stones,
              //                           1         2         3
              //                  123456789012345678901234567890
            new StoneFix("2158",
                new StoneDesktop("hdwefhhdweflhdweflhdwefldwefll", "心+五屬各5個"))
            , new StoneFix("1428,1429,1960,1969",
                new StoneDesktop("EEhhEEhhEEhhEEhhEEhhEEhhEEhhEE", "(14心,16木強)\n木強 -> 水強 = 格雷 (#1960) + 茱比亞 (#1969)"))
            , new StoneFix("1169,2044,1175,2050,1666",
                new StoneDesktop("FFFhhFhhFhhFFFFFFFFhhFhhFhhFFF", "(12心,18X)\n心 -> 心強 = 潛解革新英雄系列 洛可可 (#2044), 優格圖 (#2050)"))
            , new StoneFix("1721,1983",
                new StoneDesktop("WDEWDEWDEWDEWDEWDEWDEWDEWDEWDE", "(10X, 10Y, 10Z), X = 水, Y = 暗, Z = 木\n秦始皇 = 自選 X, Y, Z\n艾普塞朗 = (15心,15火) = 1, 3, 5 行火強 + 2, 4, 6 行心強(5火,5心,5火,5心,5火,5心)"))
            , new StoneFix("2108",
                new StoneDesktop("HLHHHLLHLLLHHHLLHHHLHHLHLHHLHH", "(18心強,12光強)"))
            , new StoneFix("2023",
                new StoneDesktop("LLLLLLLddddLLddddLLddddLLLLLLL", "(18光,12暗)"))
            , new StoneFix("0740,1438",
                new StoneDesktop("LHLLHLHLHHLHHLLLLHLHLLHLLLHHLL", "(12心強,18光強) 合體技能"))
            , new StoneFix("1097",
                new StoneDesktop("ddhhdddhddhdhhddhhdhddhdddhhdd", "(12心,18暗)"))
            , new StoneFix("0877,0878",
                new StoneDesktop("eHewHwHfHHfHHffffHeHffHweeHHww", "(5水, 5木, 8火, 12心)\n心 -> 心強 = 牛郎織女合體技能"))
            , new StoneFix("2161",
                new StoneDesktop("--dd---dlld-dlhhldlh--hlh----h", "6暗(1st) 12暗(2nd:光 -> 暗) 18暗(3rd: 光心 -> 暗)"))
            , new StoneFix("2154",
                new StoneDesktop("------------WFELDHWFELDHWFELDH", "心+五屬強化各3個"))
            , new StoneFix("2152",//
                new StoneDesktop("---FFF---EEE---LLL---DDD---WWW", "五屬強化各3個"))
            , new StoneFix("2069",
                new StoneDesktop("WWW---FFF---EEE---LLL---DDD---", "五屬強化各3個"))
            , new StoneFix("2151",
                new StoneDesktop("D----WD----WD----WD----WWWWDDD", "(7暗強神,7水強神)"))
            , new StoneFix("2169,1094",
                new StoneDesktop("--dd----dd--dddddd--dd----dd--", "14X\n X = 暗神 -> 超人捷德 (#2169)\n X = 水神 -> 超人捷德 & 超人ZERO (#2150)\n X = 光 -> 亞瑟 (#2169)"))
            , new StoneFix("2107",
                new StoneDesktop("-L---LL-LLL---LL---L--L-L--L--", "12光強"))
            , new StoneFix("2102",
                new StoneDesktop("ff--wwf----w------w----fww--ff", "(6水,6火)"))
            , new StoneFix("1585",
                new StoneDesktop("w----ww----ww----w-w--w---ww--", "10水"))
            , new StoneFix("1224,1391",
                new StoneDesktop("f--f---f--f---f--ff--f---f--f-", "10火"))
            , new StoneFix("2117",
                new StoneDesktop("--DD---D--D-D----D-D--D---DD--", "10暗強"))
            , new StoneFix("1279",
                new StoneDesktop("-ff--------ff----ff--------ff-", "8火"))
            , new StoneFix("2101",
                new StoneDesktop("---ww-w-----w----w-----w-ww---", "8水"))
            , new StoneFix("1854",
                new StoneDesktop("---lll-----l-----l-----l-----l", "7光"))
        );
    }

    private void initActions() {
        TextView detail = findViewById(R.id.fxdDetail);
        detail.setOnClickListener((v) -> {
            ShareHelper.shareString(getActivity(), detail.getText().toString());
            logShare("detail");
        });

        findViewById(R.id.fxdSave).setOnClickListener((v) -> {
            shareImage(findViewById(R.id.fxdContent));
            logShare("all");
        });
    }

    private void initDesktops() {
        ViewGroup desktops = findViewById(R.id.fxdDesktopAll);
        FixStoneAdapter a = new FixStoneAdapter() {
            @Override
            public FragmentManager getFragmentManager() {
                return getActivity().getFragmentManager();
            }
        };
        a.setDataList(stones);
        a.setItemListener(new FixStoneAdapter.ItemListener() {
            @Override
            public void onClick(StoneFix item, FixStoneAdapter.StoneFixVH holder, int position) {

            }

            @Override
            public void onClickShare(StoneFix item, FixStoneAdapter.StoneFixVH holder, View v, int position) {
                shareImage(holder.itemView);
                logShare(item.desktop.detail);
            }
        });
        fillItems(desktops, a);
    }

    //-- Events
    private void logShare(String type) {
        Map<String, String> m = new HashMap<>();
        m.put("share", type);
        FabricAnswers.logFixRunestone(m);
    }

    private void logImpression() {
        Map<String, String> m = new HashMap<>();
        m.put("impression", "1");
        FabricAnswers.logFixRunestone(m);
    }
    //-- Events
}