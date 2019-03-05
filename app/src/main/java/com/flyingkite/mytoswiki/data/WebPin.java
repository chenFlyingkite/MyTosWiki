package com.flyingkite.mytoswiki.data;

import com.flyingkite.mytoswiki.dialog.WebDialog;
import com.flyingkite.mytoswiki.util.UrlUtil;
import com.google.gson.annotations.SerializedName;

public class WebPin {

    @SerializedName("web1")
    public String web1 = WebDialog.tosWikiHome;

    @SerializedName("web2")
    public String web2 = WebDialog.tosWikiHome;

    @SerializedName("web3")
    public String web3 = WebDialog.tosWikiHome;

    @Override
    public String toString() {
        return UrlUtil.decodeURL(web1) + "\n" +
               UrlUtil.decodeURL(web2) + "\n" +
               UrlUtil.decodeURL(web3);
    }

    public String get(int position) {
        switch (position) {
            case 1: return web1;
            case 2: return web2;
            case 3: return web3;
            default: return WebDialog.tosWikiHome;
        }
    }

    public void set(int position, String link) {
        switch (position) {
            case 1: web1 = link; return;
            case 2: web2 = link; return;
            case 3: web3 = link; return;
        }
    }
}
