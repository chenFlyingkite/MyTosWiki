package com.flyingkite.mytoswiki.tos;

public enum TCard {
    // #0617, 小鳥
    Bird("https://vignette.wikia.nocookie.net/tos/images/a/aa/617i.png/revision/latest/scale-to-width-down/100?cb=20140717094037&path-prefix=zh"),
    ;

    public final String url;

    TCard(String link) {
        url = link;
    }
}
