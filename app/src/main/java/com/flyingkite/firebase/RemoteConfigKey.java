package com.flyingkite.firebase;

public enum RemoteConfigKey implements RemoteConfig.RCKey {
    HELLO("Hello")
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
