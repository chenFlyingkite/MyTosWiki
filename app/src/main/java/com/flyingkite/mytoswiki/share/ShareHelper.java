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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.flyingkite.library.FilesHelper;
import com.flyingkite.library.IOUtil;
import com.flyingkite.library.Say;
import com.flyingkite.library.ThreadUtil;
import com.flyingkite.library.TicTac2;
import com.flyingkite.mytoswiki.App;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.util.WaitingDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;

/**
 * <a href="https://developer.android.com/training/sharing/send">
 *     https://developer.android.com/training/sharing/send
 *     </a>
 */
public class ShareHelper {
    private ShareHelper() {}

    public static void shareString(@NonNull Context context, String msg) {
        shareString(context, msg, context.getString(R.string.share_to));
    }

    public static void shareString(@NonNull Context context, String msg, String chooser) {
        Intent it = new Intent(Intent.ACTION_SEND);
        it.putExtra(Intent.EXTRA_TEXT, msg);
        it.setType("text/plain");
        try {
            context.startActivity(Intent.createChooser(it, chooser));
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
        Intent it = new Intent(Intent.ACTION_SEND);
        it.putExtra(Intent.EXTRA_STREAM, uri);
        it.setType(type);
        try {
            context.startActivity(it);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void shareImage(@NonNull Activity context, View view, String filename) {
        SaveViewToBitmapTask task = new SaveViewToBitmapTask(context, view, filename){
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                MediaScannerConnection.scanFile(context, new String[]{filename}, null,
                        (path, uri) -> {
                            LogV("Scanned %s\n  as -> %s", path, uri);
                            sendUriIntent(context, uri, "image/png");
                        });
            }
        };
        task.executeOnExecutor(ThreadUtil.cachedThreadPool);
    }

    public static String cacheName(String name) {
        File folder = App.me.getExternalCacheDir();
        return folder.getAbsolutePath() + File.separator + name;
    }

    @Deprecated
    public static void shareBitmap(@NonNull Activity activity, String url) {
        Glide.with(activity).asBitmap().load(url)
        .listener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                LogE("onLoadFailed, first = %s, model = %s, e = %s", Say.ox(isFirstResource), model, e);
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                LogV("onResourceReady, first = %s, bmp = %s", Say.ox(isFirstResource), resource);
                return false;
            }
        }).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                LogV("onResourceReady, bmp = %s", resource);

                Intent it = new Intent(Intent.ACTION_SEND);
                it.putExtra(Intent.EXTRA_STREAM, resource);
                it.setType("image/png");
                try {
                    activity.startActivity(it);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static class SaveViewToBitmapTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<Activity> activity;
        private WeakReference<View> view;
        private String savedName;
        private int width;
        private int height;
        private WaitingDialog w;
        private TicTac2 tt = new TicTac2();

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
                    .onCancel((dialog) -> {
                        cancel(true);
                    }).buildAndShow();
            tt.tic();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (getW(view) == null || savedName == null) {
                LogV("Cannot save bitmap : %s, %s", view, savedName);
                return null;
            }

            // 1. [<10ms] Create new bitmap
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            if (isCancelled()) return null;

            // 2. [100ms] Let view draws to the bitmap
            Canvas c = new Canvas(bitmap);
            getW(view).draw(c);
            if (isCancelled()) return null;

            // 3. [<10ms] Create output file
            File f = new File(savedName);
            File fp = f.getParentFile();
            if (fp != null) {
                fp.mkdirs();
            }
            FilesHelper.ensureDelete(f);
            if (isCancelled()) return null;

            // 4. [~1kms] Writing bitmap to file
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
            tt.tac("Save done");
            if (w != null) {
                w.dismiss();
            }
        }
    }

    private static void LogV(String msg, Object... param) {
        LogV(String.format(msg, param));
    }

    private static void LogV(String msg) {
        Log.v("ShareHelper", msg);
    }

    private static void LogE(String msg, Object... param) {
        LogE(String.format(msg, param));
    }

    private static void LogE(String msg) {
        Log.e("ShareHelper", msg);
    }

}
