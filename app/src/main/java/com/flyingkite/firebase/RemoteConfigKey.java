package com.flyingkite.firebase;

public enum RemoteConfigKey implements RemoteConfig.RCKey {
    HELLO("Hello"),
    DIALOG_BULLETIN_MESSAGE("dialog_bulletin_message"),
    DIALOG_STAGE_MEMO_USE_HTML("dialog_stage_memo_use_html"),
    DIALOG_STAGE_MEMO_CONTENT("dialog_stage_memo_content"),
    ;

    private final String key;
    RemoteConfigKey(String name) {
        key = name;
    }

    @Override
    public String getKey() {
        return key;
    }
}
