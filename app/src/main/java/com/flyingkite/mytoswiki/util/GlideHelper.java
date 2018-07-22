package com.flyingkite.mytoswiki.util;

import android.support.annotation.DrawableRes;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.flyingkite.mytoswiki.R;

public final class GlideHelper {
    public static final DrawableTransitionOptions fadeIn = new DrawableTransitionOptions().transition(R.anim.fadein);

    public static RequestOptions fitCenter_Placeholder(@DrawableRes int holder) {
        return new RequestOptions().fitCenter().placeholder(holder);
    }

    public static RequestOptions centerCrop_Placeholder(@DrawableRes int holder) {
        return new RequestOptions().centerCrop().placeholder(holder);
    }

    public static ObjectKey ofKey(Object key) {
        return new ObjectKey(String.valueOf(key));
    }
}
