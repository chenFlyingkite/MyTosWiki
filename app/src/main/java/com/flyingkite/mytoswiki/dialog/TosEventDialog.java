package com.flyingkite.mytoswiki.dialog;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.flyingkite.fabric.FabricAnswers;
import com.flyingkite.firebase.RemoteConfig;
import com.flyingkite.firebase.RemoteConfigKey;
import com.flyingkite.mytoswiki.R;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;

public class TosEventDialog extends BaseTosDialog {
    @Override
    protected int getLayoutId() {
        return R.layout.dialog_tos_event;
    }

    @Override
    protected boolean useFloating() {
        return true;
    }

    private TextView eventMemo;
    private String eventPage = "https://towerofsaviors.com/2018/08/18/%E3%80%90%E6%9C%AB%E4%B8%96%E6%A2%B5%E9%9F%B3%EF%BC%9A%E8%B6%85%E8%84%AB%E8%84%88%E8%BC%AA%E4%B9%8B%E7%90%86%E3%80%91%E6%85%B6%E7%A5%9D%E6%B4%BB%E5%8B%95%E8%A9%B3%E6%83%85/";
    private final String eventImage = "https://tos157310821.files.wordpress.com/2018/08/138_website_851x315_zh_logo.jpg";

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logImpression();
        dismissWhenClick(R.id.ted_header, R.id.tedEventImage);
        eventMemo = findViewById(R.id.ted_message);
        // Set share
        View stageShare = findViewById(R.id.ted_share);
        stageShare.setOnClickListener((v) -> {
            shareString(eventMemo.getText().toString());
            logShare("text");
        });
        // Set web, hide since it will have ERR_SPDY_PROTOCAL_ERROR
        View web = findViewById(R.id.ted_web);
        web.setOnClickListener((v) -> {
            String tosMain = "https://towerofsaviors.com";
            viewLinkAsWebDialog(tosMain);
        });
        eventPage = RemoteConfig.getString(RemoteConfigKey.DIALOG_TOS_EVENT_WEB);
        String image = RemoteConfig.getString(RemoteConfigKey.DIALOG_TOS_EVENT_BANNER);
        loadLinkToImageView(findViewById(R.id.tedEventImage), image, getActivity());

        // Set text
        boolean html = RemoteConfig.getBoolean(RemoteConfigKey.DIALOG_TOS_EVENT_MEMO_USE_HTML);
        String s = RemoteConfig.getString(RemoteConfigKey.DIALOG_TOS_EVENT_MEMO_CONTENT);
        eventMemo.setText(html ? Html.fromHtml(s) : escapeNewLine(s));
    }

    //-- Events
    private void logShare(String type) {
        Map<String, String> m = new HashMap<>();
        m.put("share", type);
        FabricAnswers.logTosEventMemo(m);
    }

    private void logImpression() {
        Map<String, String> m = new HashMap<>();
        m.put("impression", "1");
        FabricAnswers.logTosEventMemo(m);
    }
    //-- Events

}
