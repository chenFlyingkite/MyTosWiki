package com.flyingkite.firebase;

import android.support.annotation.NonNull;
import android.support.annotation.XmlRes;

import com.flyingkite.library.TicTac2;
import com.flyingkite.library.log.Loggable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class RemoteConfig {
    private static final String TAG = "RemoteConfig";
    private static boolean DEBUG = true;

    public static void init(boolean debug, @XmlRes int xmlDefault) {
        DEBUG = debug;

        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings s = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(DEBUG).build();
        remoteConfig.setConfigSettings(s);
        remoteConfig.setDefaults(xmlDefault);

        final int cacheExpireTime = 0;

        TicTac2.v t = new TicTac2.v();
        //printAll(); // Print them if need to peek old values
        t.tic();
        remoteConfig.fetch(cacheExpireTime)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        t.tac("Remote config fetch");
                        log("RemoteConfig fetch complete, success = %s", task.isSuccessful());
                        if (task.isSuccessful()) {
                            remoteConfig.activateFetched();
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
        log("--- Config Values --- Begin");
        for (RemoteConfigKey k : RemoteConfigKey.values()) {
            if (k != null) {
                log(k.getKey() + "\n-> " + getString(k));
            }
        }
        log("--- Config Values --- End");
    }

    private static void log(String message) {
        z.logI(message);
    }

    private static void log(String fmt, Object... params) {
        z.logI(fmt, params);
    }

    private static Loggable z = new Loggable() {
        @Override
        public String LTag() {
            return TAG;
        }
    };
}
