package com.flyingkite.mytoswiki.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.flyingkite.library.Say;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.library.IconAdapter;
import com.flyingkite.mytoswiki.library.Library;

import java.util.Arrays;
import java.util.List;

public class WebDialog extends BaseTosDialog {
    public WebDialog(@NonNull Context context) {
        super(context);
    }

    private List<Integer> toolsIds = Arrays.asList(R.drawable.ic_home_black_48dp
            , R.drawable.ic_arrow_back_black_48dp
            , R.drawable.ic_arrow_forward_black_48dp
    );

    private static final String tosWikiHome = "http://zh.tos.wikia.com/wiki/%E7%A5%9E%E9%AD%94%E4%B9%8B%E5%A1%94_Tower_of_Saviors_%E7%BB%B4%E5%9F%BA";
    private Library<IconAdapter> iconLibrary;
    private WebView web;
    private ProgressBar progress;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_wiki_web;
    }

    @Override
    protected void onFinishInflate(View view, Dialog dialog) {
        web = findViewById(R.id.wdWeb);
        progress = findViewById(R.id.wdProgress);
        initToolBar();
        initWeb();
    }

    @Override
    public void onBackPressed() {
        if (web.canGoBack()) {
            web.goBack();
        } else {
            super.onBackPressed();
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
                switch (position) {
                    case 0:
                        web.loadUrl(tosWikiHome);
                        break;
                    case 1:
                        if (web.canGoBack()) {
                            web.goBack();
                        }
                        break;
                    case 2:
                        if (web.canGoForward()) {
                            web.goForward();
                        }
                        break;
                }
            }
        });

        iconLibrary.setViewAdapter(adapter);
    }

    private void initWeb() {
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
        web.loadUrl(tosWikiHome);
    }

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
}
