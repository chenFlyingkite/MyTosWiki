package com.flyingkite.firebase;

public enum RemoteConfigKey implements RemoteConfig.RCKey {
    HELLO("Hello"),
    DIALOG_BULLETIN_MESSAGE("dialog_bulletin_message"),
    DIALOG_STAGE_MEMO_USE_HTML("dialog_stage_memo_use_html"),
    DIALOG_STAGE_MEMO_CONTENT("dialog_stage_memo_content"),
    DIALOG_TOS_EVENT_MEMO_USE_HTML("dialog_tos_event_memo_use_html"),
    DIALOG_TOS_EVENT_MEMO_CONTENT("dialog_tos_event_memo_content"),
    DIALOG_TOS_EVENT_WEB("dialog_tos_event_web"),
    DIALOG_TOS_EVENT_BANNER("dialog_tos_event_banner"),
    DIALOG_FARM_POOL_CONTENT("dialog_farm_pool_content"),
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
