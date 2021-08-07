package com.flyingkite.crashlytics;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.flyingkite.library.TicTac2;
import com.flyingkite.library.util.FileUtil;
import com.flyingkite.library.util.StringUtil;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class CrashReport {
    private static final String TAG = "CrashReport";
    private static boolean DEBUG = true;
    private CrashReport() {}

    public static void init(Context context, boolean debug) {
        DEBUG = debug;
        TicTac2.v t = new TicTac2.v();
        t.tic();

        // https://firebase.google.com/docs/crashlytics/upgrade-sdk?platform=android
        // Explicit initialization of Crashlytics is no longer required.
        // OPTIONAL: If crash reporting has been explicitly disabled previously, add:
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!debug);

        logDeviceInfo(context); // 50ms
        t.tac("%s init OK", TAG);
    }

    /**
     * Log message to crash console.
     */
    public static void logMessage(String msg) {
        if (DEBUG) {
            log(msg);
        } else {
            FirebaseCrashlytics.getInstance().log(msg);
        }
    }

    /**
     * Log non-fatal exception to crash console.
     */
    public static void logException(Throwable e) {
        if (DEBUG) {
            e.printStackTrace();
        } else {
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    private static void logDeviceInfo(Context context) {
        setValue("BRAND", Build.BRAND);
        setValue("MODEL", Build.MODEL);
        setValue("BOARD", Build.BOARD);
        setValue("DEVICE", Build.DEVICE);
        setValue("PRODUCT", Build.PRODUCT);
        setValue("HARDWARE", Build.HARDWARE);
        setValue("MANUFACTURER", Build.MANUFACTURER);

        setValue("CPU_HW", parseCpuHardwareInfo());

        logMemoryAndGLES(context);
    }

    private static String parseCpuHardwareInfo() {
        List<String> cpuInfo = FileUtil.readFromFile(new File("/proc/cpuinfo"));
        for (String line : cpuInfo) {
            if (line != null && line.toLowerCase().contains("hardware\t:")) {
                return line;
            }
        }
        return null;
    }

    private static void logMemoryAndGLES(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) return;

        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memInfo);

        setValue("Max_Hw_Mem", StringUtil.formatByte(memInfo.totalMem));
        setValue("Max_Vm_Mem", StringUtil.formatByte(Runtime.getRuntime().maxMemory()));

        // GLES
        setValue("GL_ES_Ver", Integer.toString(am.getDeviceConfigurationInfo().reqGlEsVersion, 16));
    }

    public static void setValue(String key, Object value) {
        if (DEBUG) {
            log("%s -> %s", key, value);
            return;
        }

        FirebaseCrashlytics fc = FirebaseCrashlytics.getInstance();
        if (value instanceof Boolean) {
            fc.setCustomKey(key, (Boolean) value);
        } else if (value instanceof Integer) {
            fc.setCustomKey(key, (Integer) value);
        } else if (value instanceof Long) {
            fc.setCustomKey(key, (Long) value);
        } else if (value instanceof Float) {
            fc.setCustomKey(key, (Float) value);
        } else if (value instanceof Double) {
            fc.setCustomKey(key, (Double) value);
        } else { // Other data type would be treated as String.
            fc.setCustomKey(key, (String) value);
        }
    }

    private static void log(String fmt, Object... params) {
        Log.i(TAG, "" + String.format(Locale.US, fmt, params));
    }
}
