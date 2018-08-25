package com.flyingkite.mytoswiki.dialog;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.flyingkite.fabric.FabricAnswers;
import com.flyingkite.library.Say;
import com.flyingkite.library.util.MathUtil;
import com.flyingkite.library.widget.Library;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.library.IconAdapter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebDialog extends BaseTosDialog {
    private List<Integer> toolsIds = Arrays.asList(R.drawable.ic_home_black_48dp
            , R.drawable.ic_arrow_back_black_48dp
            , R.drawable.ic_arrow_forward_black_48dp
            , R.drawable.ic_refresh_black_48
            , R.drawable.ic_share_black_48dp
            , R.drawable.ic_clear_black_48dp
    );

    public static final String BUNDLE_LINK = "WebDialog.WebLink";
    private static final String tosWikiHome = "http://zh.tos.wikia.com/wiki/%E7%A5%9E%E9%AD%94%E4%B9%8B%E5%A1%94_Tower_of_Saviors_%E7%BB%B4%E5%9F%BA";
    private Library<IconAdapter> iconLibrary;
    private SwipeRefreshLayout swipe;
    private WebView web;
    private ProgressBar progress;
    private String link = tosWikiHome;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_wiki_web;
    }

    @Override
    protected void onFinishInflate(View view, Dialog dialog) {
        progress = findViewById(R.id.wdProgress);
        parseBundle(getArguments());
        initToolBar();
        initWeb();
        initSwipeRefresh();
        logImpression();
    }

    private void parseBundle(Bundle b) {
        boolean hasLink = b != null && b.containsKey(BUNDLE_LINK);
        if (!hasLink) return;

        link = b.getString(BUNDLE_LINK);
    }

    @Override
    public boolean onBackPressed() {
        if (web.canGoBack()) {
            web.goBack();
            return true;
        } else {
            return super.onBackPressed();
        }
    }

    private void initToolBar() {
        iconLibrary = new Library<>(findViewById(R.id.wdTools));
        IconAdapter adapter = new IconAdapter();
        adapter.setAutoScroll(true);
        adapter.setDataList(toolsIds);
        adapter.setItemListener(new IconAdapter.ItemListener() {
            @Override
            public void onClick(Integer s, IconAdapter.IconVH iconVH, int position) {
                switch (s) {
                    case R.drawable.ic_home_black_48dp:
                        web.loadUrl(tosWikiHome);
                        break;
                    case R.drawable.ic_arrow_back_black_48dp:
                        if (web.canGoBack()) {
                            web.goBack();
                        }
                        break;
                    case R.drawable.ic_arrow_forward_black_48dp:
                        if (web.canGoForward()) {
                            web.goForward();
                        }
                        break;
                    case R.drawable.ic_share_black_48dp:
                        shareString(web.getUrl());
                        logShare();
                        break;
                    case R.drawable.ic_clear_black_48dp:
                        dismissAllowingStateLoss();
                        break;
                    case R.drawable.ic_refresh_black_48:
                        web.reload();
                        break;
                }
            }
        });

        iconLibrary.setViewAdapter(adapter);
    }

    private void initWeb() {
        web = findViewById(R.id.wdWeb);
        WebSettings ws = web.getSettings();
        // Desktop mode web page
        ws.setUserAgentString("Mozilla/5.0");
        // Open web link inside
        ws.setJavaScriptCanOpenWindowsAutomatically(true);
        // Let web can zoom
        ws.setBuiltInZoomControls(true);
        // Let tiles in table show up
        ws.setJavaScriptEnabled(true);

        web.setWebViewClient(client);
        web.setWebChromeClient(chromeClient);
        web.loadUrl(link);
    }

    private void initSwipeRefresh() {
        swipe = findViewById(R.id.wdRefresh);
        swipe.setOnRefreshListener(refresher);
    }

    private SwipeRefreshLayout.OnRefreshListener refresher = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            web.reload();
            swipe.setRefreshing(false);
        }
    };

    private WebChromeClient chromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            progress.setProgress(newProgress);
        }
    };

    private WebViewClient client = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progress.setProgress(0);
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progress.setVisibility(View.GONE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Say.Log("page : %s", url);
            return super.shouldOverrideUrlLoading(view, url);
        }
    };

    //-- Event

    private String liteLink(String link) {
        String s = decodeURL(link);
        int x = s.lastIndexOf("/");
        if (MathUtil.isInRange(x, 0, s.length() - 1)) {
            return s.substring(x + 1);
        } else {
            return s;
        }
    }
    private void logImpression() {
        Map<String, String> m = new HashMap<>();
        m.put("link", liteLink(link));
        FabricAnswers.logWeb(m);
    }

    private void logShare() {
        Map<String, String> m = new HashMap<>();
        m.put("share", liteLink(web.getUrl()));
        FabricAnswers.logWeb(m);
    }
    //-- Event
}
