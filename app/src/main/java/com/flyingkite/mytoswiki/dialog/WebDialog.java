package com.flyingkite.mytoswiki.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.flyingkite.fabric.FabricAnswers;
import com.flyingkite.library.Say;
import com.flyingkite.library.widget.Library;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.library.IconAdapter;
import com.flyingkite.mytoswiki.util.ShareUtil;
import com.flyingkite.mytoswiki.util.UrlUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class WebDialog extends BaseTosDialog implements ShareUtil {
    public static final String TAG = "WebDialog";

    public static final String BUNDLE_LINK = "WebDialog.WebLink";
    public static final String BUNDLE_PIN = "WebDialog.pin";
    public static final String tosWikiHome = "https://tos.fandom.com/zh/wiki/%E7%A5%9E%E9%AD%94%E4%B9%8B%E5%A1%94_%E7%B9%81%E4%B8%AD%E7%B6%AD%E5%9F%BA";
    //private static final String tosWikiHome = "http://zh.tos.wikia.com/wiki/%E7%A5%9E%E9%AD%94%E4%B9%8B%E5%A1%94_Tower_of_Saviors_%E7%BB%B4%E5%9F%BA";

    private Library<IconAdapter> iconLibrary;
    private SwipeRefreshLayout swipe;
    private WebView web;
    private ProgressBar progress;
    private String link = tosWikiHome;
    private boolean pinned = false;
    private OnWebAction onWeb;

    public interface OnWebAction {
        default void onBrowse(String link) {}
        default void onPin(String link, int position) {}
    }

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
        if (pinned) {
        } else {
            logImpression();
        }
    }

    private void parseBundle(Bundle b) {
        if (b == null) return;

        link = b.getString(BUNDLE_LINK, link);
        pinned = b.getBoolean(BUNDLE_PIN, pinned);
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

    private List<Integer> getToolsIds() {
        List<Integer> ids = new ArrayList<>();
        ids.add(R.drawable.ic_home_white_48dp);
        ids.add(R.drawable.ic_arrow_back_white_48dp);
        ids.add(R.drawable.ic_arrow_forward_white_48dp);
        ids.add(R.drawable.ic_refresh_white_48);
        ids.add(R.drawable.ic_share_white_48dp);
        if (pinned) {
        } else {
            ids.add(R.drawable.ic_clear_white_48dp);
            ids.add(R.drawable.ic_filter_1_white);
            ids.add(R.drawable.ic_filter_2_white);
            ids.add(R.drawable.ic_filter_3_white);
        }
        return ids;
    }

    private void initToolBar() {
        iconLibrary = new Library<>(findViewById(R.id.wdTools));
        IconAdapter adapter = new IconAdapter();
        adapter.setAutoScroll(true);
        adapter.setDataList(getToolsIds());
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
                    case R.drawable.ic_filter_1_black:
                        if (onWeb != null) {
                            onWeb.onPin(web.getUrl(), 1);
                        }
                        break;
                    case R.drawable.ic_filter_2_black:
                        if (onWeb != null) {
                            onWeb.onPin(web.getUrl(), 2);
                        }
                        break;
                    case R.drawable.ic_filter_3_black:
                        if (onWeb != null) {
                            onWeb.onPin(web.getUrl(), 3);
                        }
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

    public void loadUrl(String newLink) {
        if (web != null) {
            web.loadUrl(newLink);
        }
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnWebAction) {
            onWeb = (OnWebAction) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onWeb = null;
    }

    //-- Event

    private String liteLink(String link) {
        return UrlUtil.liteLink(link);
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
