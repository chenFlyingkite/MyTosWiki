package com.flyingkite.firebase;

import com.flyingkite.library.TicTac2;
import com.flyingkite.library.log.Loggable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.nio.charset.Charset;

import androidx.annotation.NonNull;
import androidx.annotation.XmlRes;

public class RemoteConfig {
    private static final String TAG = "RemoteConfig";

    public static void init(@XmlRes int xmlDefault) {
        FirebaseRemoteConfig config = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings s = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0).build();
        config.setConfigSettingsAsync(s);
        config.setDefaults(xmlDefault);

        final int cacheExpireTime = 0;

        TicTac2.v t = new TicTac2.v();
        //printAll(); // Print them if need to peek old values
        t.tic();
        config.fetch(cacheExpireTime)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        t.tac("Remote config fetch");
                        log("RemoteConfig fetch complete, success = %s", task.isSuccessful());
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
        //return FirebaseRemoteConfig.getInstance().getString(key.getKey());
        String s = FirebaseRemoteConfig.getInstance().getString(key.getKey());
        // https://stackoverflow.com/questions/57754661/firebase-remote-config-with-chinese-characters
        //log("s = %s", s);
        Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
        Charset UTF_8 = Charset.forName("UTF-8");
        String t = new String(s.getBytes(ISO_8859_1), UTF_8);
        //log("t = %s", t);
        return t;
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
