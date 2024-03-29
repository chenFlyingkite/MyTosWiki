package com.flyingkite.firebase;

import androidx.annotation.NonNull;
import androidx.annotation.XmlRes;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import flyingkite.library.android.log.Loggable;
import flyingkite.library.androidx.TicTac2;

public class RemoteConfig {
    public static void init(@XmlRes int xmlDefault) {
        FirebaseRemoteConfig config = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings s = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0).build();
        config.setDefaultsAsync(xmlDefault);
        config.setConfigSettingsAsync(s);

        final int cacheExpireTime = 0;

        TicTac2.v t = new TicTac2.v();
        //printAll(); // Print them if need to peek old values
        t.tic();
        config.fetch(cacheExpireTime)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        t.tac("Remote config fetch");
                        z.logI("RemoteConfig fetch complete, success = %s", task.isSuccessful());
                        if (task.isSuccessful()) {
                            config.activate();
                        }
                        printAll();
                    }
                });
    }

    public interface RCKey {
        String getKey();
    }

    public static boolean getBoolean(RCKey key) {
        return FirebaseRemoteConfig.getInstance().getBoolean(key.getKey());
    }

    public static double getDouble(RCKey key) {
        return FirebaseRemoteConfig.getInstance().getDouble(key.getKey());
    }

    public static long getLong(RCKey key) {
        return FirebaseRemoteConfig.getInstance().getLong(key.getKey());
    }

    @NonNull
    public static String getString(RCKey key) {
        return FirebaseRemoteConfig.getInstance().getString(key.getKey());
    }

    private static void printAll() {
        z.logI("--- Config Values --- Begin");
        for (RemoteConfigKey k : RemoteConfigKey.values()) {
            if (k != null) {
                z.logI(k.getKey() + "\n-> " + getString(k));
            }
        }
        z.logI("--- Config Values --- End");
    }

    private static final Loggable z = new Loggable() {};
}
