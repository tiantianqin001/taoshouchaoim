package com.pingmo.chengyan.activity.shop.utils;

import android.content.Context;
import android.widget.ImageView;

import com.youth.banner.loader.ImageLoader;

/**
 * 用于banner加载图片
 */
public class GlideBannerStringImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        if (path == null) {
            return;
        }
        GlideUtils.getLoad(path.toString(), imageView)
                .into(imageView);
    }


}