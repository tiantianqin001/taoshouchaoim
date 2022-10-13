package com.pingmo.chengyan.activity.shop.adapter;

import android.widget.ImageView;




import com.chad.library.adapter.base.BaseQuickAdapter;


import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.shop.common.Constant;
import com.pingmo.chengyan.activity.shop.model.entity.ShopEntity;
import com.pingmo.chengyan.activity.shop.utils.GlideUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WeChatMallAdapter extends BaseQuickAdapter<ShopEntity, BaseViewHolder> implements LoadMoreModule {
    public WeChatMallAdapter(int layoutResId, @Nullable List<ShopEntity> data) {
        super(layoutResId, data);

    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, ShopEntity shopEntity) {
        if (shopEntity == null) {
            return;
        }
        holder.setText(R.id.tv_title, shopEntity.title);
     //holder.setText(R.id.tv_money, Constant.money + shopEntity.price + "+" + shopEntity.integral.toBigInteger() + "积分");
     holder.setText(R.id.tv_money, Constant.money + shopEntity.price );
        ImageView ivCoverImg = (ImageView) holder.getView(R.id.iv_coverImg);
        GlideUtils.getLoad(shopEntity.coverImg, ivCoverImg).into(ivCoverImg);
    }
}
