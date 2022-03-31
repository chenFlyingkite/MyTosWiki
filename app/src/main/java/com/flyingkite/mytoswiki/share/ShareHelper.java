package com.flyingkite.mytoswiki.share;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;

import com.flyingkite.library.TicTac2;
import com.flyingkite.library.log.Loggable;
import com.flyingkite.library.util.FileUtil;
import com.flyingkite.library.util.IOUtil;
import com.flyingkite.library.util.ThreadUtil;
import com.flyingkite.mytoswiki.App;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.util.ShareUtil;
import com.flyingkite.util.WaitingDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

/**
 * <a href="https://developer.android.com/training/sharing/send">
 *     https://developer.android.com/training/sharing/send
 *     </a>
 *
 * https://developer.android.com/training/data-storage
 */
public class ShareHelper {
    private static final Loggable z = new Loggable() {};

    // i18n text string for share title
    public static String shareTo(Context context) {
        return context.getString(R.string.share_to);
    }

    public static void sendString(@NonNull Context context, String msg) {
        sendString(context, msg, shareTo(context));
    }

    public static void sendString(@NonNull Context context, String msg, CharSequence title) {
        Intent it = new Intent(Intent.ACTION_SEND);
        it.putExtra(Intent.EXTRA_TEXT, msg);
        it.setType("text/plain");
        try {
            context.startActivity(Intent.createChooser(it, title));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void viewLink(@NonNull Context context, String link) {
        Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        try {
            context.startActivity(it);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void sendUriIntent(@NonNull Context context, Uri uri, String type) {
        sendUriIntent(context, shareTo(context), uri, type);
    }

    public static void sendUriIntent(@NonNull Context context, String title, Uri uri, String type) {
        Intent it = new Intent(Intent.ACTION_SEND);
        it.putExtra(Intent.EXTRA_STREAM, uri);
        int flg = Intent.FLAG_GRANT_READ_URI_PERMISSION;
        //flg |= Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
        it.addFlags(flg);
        //it.setClipData(ClipData.newRawUri("", uri));
        //it.setData(uri);
        it.setDataAndType(uri, type);
        //z.logE("sendUriIntent %s %s", type, uri);
        try {
            context.startActivity(Intent.createChooser(it, title));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void shareImage(@NonNull Activity context, View view, String filename) {
        shareImage(context, view, filename, view.getWidth(), view.getHeight());
    }

    public static void shareImage(@NonNull Activity context, View view, String filename, int width, int height) {
        SaveViewToBitmapTask task = new SaveViewToBitmapTask(context, view, filename) {
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                sharePng(context, filename);
            }
        };
        task.ofSize(width, height);
        task.executeOnExecutor(ThreadUtil.cachedThreadPool);
    }

    /**
     * 1. Scan png path as uri by file provider and send it as "image/png" intent
     * 2.
     */
    private static void sharePng(Context context, String pngPath) {
        File file = new File(pngPath);
        Uri uri = FileProvider.getUriForFile(context, getAuthority(context), file);
//        file = /storage/emulated/0/Android/data/com.flyingkite.mytoswiki.debug/cache/20220331_100723829.png
//        uri = content://com.flyingkite.mytoswiki.debug.fileprovider/cache_data/20220331_100723829.png
        z.logE("file = %s\nuri = %s", file, uri);
        sendUriIntent(context, uri, "image/png");
    }

    private static void scanFile(Context context, String filename) {
        MediaScannerConnection.scanFile(context, new String[]{filename}, null, (path, uri) -> {
            z.logE("Scanned %s\n  as -> %s", path, uri);
        });
    }

    // Fields for #extFilesFile()
    // Maybe public for other class
    private static final String data = "data";
    public static String getAuthority(Context c) {
        String a = c.getPackageName() + ".fileprovider";
        //z.logE("auth = %s", a);
        return a;
    }

    public static String cacheName(String name) {
        File folder = App.me.getExternalCacheDir();
        // FIXME : In 4.3, getExternalCacheDir() returns null
        File it = null;
        if (folder == null) {
            it = extFilesFile(name);
        } else {
            it = new File(folder, name);
        }
        return it.getAbsolutePath();
    }

    public static File extFilesFile(String filename) {
        File folder = App.me.getExternalFilesDir(data);
        return new File(folder, filename);
    }

    public static void makeSendImage(View w, ShareUtil util) {
        w.setOnClickListener((v) -> {
            util.shareImageTime(w);
            z.logE("ok");
            //showToast("ok : " + name);
        });
    }

//    @Deprecated
//    public static void shareBitmap(@NonNull Activity activity, String url) {
//        GlideApp.with(activity).asBitmap().load(url)
//        .listener(new RequestListener<Bitmap>() {
//            @Override
//            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
//                LogE("onLoadFailed, first = %s, model = %s, e = %s", Say.ox(isFirstResource), model, e);
//                return false;
//            }
//
//            @Override
//            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
//                LogV("onResourceReady, first = %s, bmp = %s", Say.ox(isFirstResource), resource);
//                return false;
//            }
//        }).into(new SimpleTarget<Bitmap>() {
//            @Override
//            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                LogV("onResourceReady, bmp = %s", resource);
//
//                Intent it = new Intent(Intent.ACTION_SEND);
//                it.putExtra(Intent.EXTRA_STREAM, resource);
//                it.setType("image/png");
//                try {
//                    activity.startActivity(it);
//                } catch (ActivityNotFoundException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }

    public static class SaveViewToBitmapTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<Activity> activity;
        private final WeakReference<View> view;
        private final String savedName;
        private int width;
        private int height;
        private WaitingDialog w;
        private final TicTac2 tt = new TicTac2();

        public SaveViewToBitmapTask(Activity act, View v, String filename) {
            activity = new WeakReference<>(act);
            view = new WeakReference<>(v);
            savedName = filename;
            ofSize(v.getWidth(), v.getHeight());
        }

        public SaveViewToBitmapTask ofSize(int w, int h) {
            width = w;
            height = h;
            return this;
        }

        private <T> T getW(WeakReference<T> obj) {
            if (obj != null) {
                return obj.get();
            } else {
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            if (getW(activity) == null || getW(view) == null) return;

            w = new WaitingDialog.Builder(getW(activity), true)
                    .message(getW(activity).getString(R.string.producing))
                    .onCancel((dialog) -> {
                        cancel(true);
                    }).buildAndShow();
            tt.tic();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            View vw = getW(view);
            if (vw == null || savedName == null) {
                z.log("Cannot save bitmap : %s, %s", view, savedName);
                return null;
            }

            // 1. [<10ms] Create new bitmap
            Bitmap bitmap = Bitmap.createBitmap(vw.getWidth(), vw.getHeight(), Bitmap.Config.ARGB_8888);
            if (isCancelled()) return null;

            // 2. [100ms] Let view draws to the bitmap
            Canvas c = new Canvas(bitmap);
            vw.draw(c);
            if (isCancelled()) return null;

            // 3. [<10ms] Create output file
            File f = new File(savedName);
            File fp = f.getParentFile();
            if (fp != null) {
                fp.mkdirs();
            }
            FileUtil.ensureDelete(f);
            if (isCancelled()) return null;

            // 4. Scale bitmap
            if (vw.getWidth() != width || vw.getHeight() != height) {
                Bitmap bmp = Bitmap.createScaledBitmap(bitmap, width, height, false);
                bitmap.recycle();
                bitmap = bmp;
            }

            // 5. [~1kms] Writing bitmap to file
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                IOUtil.closeIt(fos);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            tt.tac("Image saved %s", savedName);
            activity.clear();
            view.clear();
            if (w != null) {
                w.dismiss();
            }
        }
    }
}
