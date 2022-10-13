package com.pingmo.chengyan.activity.shop.utils;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.pingmo.chengyan.activity.shop.common.SealTalkUrl;


import org.jetbrains.annotations.NotNull;


public class GlideUtils {

    @NotNull
    public static RequestBuilder<Drawable> getLoad(@NonNull String url, @NonNull ImageView imageView) {
        if (!TextUtils.isEmpty(url)) {
            if (!url.contains("http") && !url.contains("file:") && !url.contains("resource://com.pinoocle.catchdoll/")) {
                url = SealTalkUrl.DOMAIN + url;
            }
        }
        return Glide.with(imageView)
                .applyDefaultRequestOptions(
                        new RequestOptions()
                                .format(DecodeFormat.PREFER_ARGB_8888)
                .diskCacheStrategy(DiskCacheStrategy.ALL))
                .load(url);
    }
}
