package com.flyingkite.crashlytics;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.flyingkite.library.StringUtils;
import com.flyingkite.mytoswiki.BuildConfig;
import com.flyingkite.util.FileUtil;
import com.flyingkite.util.TicTacv;

import java.io.File;
import java.util.List;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;

public class CrashReport {
    private static final String TAG = "CrashReport";
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private CrashReport() {}

    public static void init(Context context) {
        TicTacv t = new TicTacv();
        t.tic();
        Fabric.with(new Fabric.Builder(context)
                .kits(new Crashlytics())
                .debuggable(DEBUG)
                .build());

        logDeviceInfo(context);
        t.tac("%s init OK", TAG);
    }

    /**
     * Log message to crash console.
     */
    public static void logMessage(String msg) {
        if (DEBUG) {
            log(msg);
        } else {
            Crashlytics.log(msg);
        }
    }

    /**
     * Log non-fatal exception to crash console.
     */
    public static void logException(Throwable e) {
        if (DEBUG) {
            e.printStackTrace();
        } else {
            Crashlytics.logException(e);
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

        logMemory(context);
        logGLES(context);
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

    private static void logMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memInfo);

        setValue("Max_Hw_Mem", StringUtils.formatByte(memInfo.totalMem));
        setValue("Max_Vm_Mem", StringUtils.formatByte(Runtime.getRuntime().maxMemory()));
    }

    private static void logGLES(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        setValue("GL_ES_Ver", Integer.toString(am.getDeviceConfigurationInfo().reqGlEsVersion, 16));
    }

    public static void setValue(String key, Object value) {
        if (DEBUG) {
            log("%s -> %s", key, value);
            return;
        }

        if (value instanceof Boolean) {
            Crashlytics.setBool(key, (Boolean) value);
        } else if (value instanceof Integer) {
            Crashlytics.setInt(key, (Integer) value);
        } else if (value instanceof Long) {
            Crashlytics.setLong(key, (Long) value);
        } else if (value instanceof Float) {
            Crashlytics.setFloat(key, (Float) value);
        } else if (value instanceof Double) {
            Crashlytics.setDouble(key, (Double) value);
        } else {
            Crashlytics.setString(key, (String) value);
        }
    }

    private static void log(String fmt, Object... params) {
        Log.i(TAG, "" + String.format(Locale.US, fmt, params));
    }
}
