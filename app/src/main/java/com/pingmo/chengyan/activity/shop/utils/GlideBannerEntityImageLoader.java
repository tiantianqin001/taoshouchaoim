package com.pingmo.chengyan.activity.shop.utils;

import android.content.Context;
import android.widget.ImageView;


import com.pingmo.chengyan.activity.shop.model.entity.BannerEntity;
import com.youth.banner.loader.ImageLoader;

/**
 * 用于banner加载图片
 */
public class GlideBannerEntityImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        BannerEntity bannerEntity = (BannerEntity) path;
        GlideUtils.getLoad(bannerEntity.imgSrc, imageView)
//                .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))//通过此方式不会闪烁
                .into(imageView);
    }


}