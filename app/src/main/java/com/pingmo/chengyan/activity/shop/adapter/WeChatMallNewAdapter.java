package com.pingmo.chengyan.activity.shop.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hjq.toast.ToastUtils;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.shop.activity.ProductDetailsActivity;
import com.pingmo.chengyan.activity.shop.common.Constant;
import com.pingmo.chengyan.activity.shop.model.entity.ShopEntity;
import com.pingmo.chengyan.activity.shop.utils.GlideUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WeChatMallNewAdapter extends RecyclerView.Adapter {

    private Context content;
    private List<ShopEntity> mList;
    private  ImageView iv_coverImg;
    private  TextView tv_title;
    private  TextView tv_money;

    public WeChatMallNewAdapter(Context content, List<ShopEntity> mList) {

        this.content = content;
        this.mList = mList;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_we_chat_mall, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        ShopEntity shopEntity = mList.get(position);
        GlideUtils.getLoad(shopEntity.coverImg, iv_coverImg).into(iv_coverImg);
        tv_title.setText(shopEntity.title);
        tv_money.setText(Constant.money + shopEntity.price);

        iv_coverImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(content, ProductDetailsActivity.class);
            ShopEntity shopEntity = mList.get(position);
            if (shopEntity != null) {
               intent.putExtra(Constant.PUBLIC_PARAMETER, shopEntity.id);
                content.startActivity(intent);
            } else {
               ToastUtils.show("商品已下架");
           }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

 private class ViewHolder extends RecyclerView.ViewHolder{

      public ViewHolder(@NonNull @NotNull View itemView) {
          super(itemView);
          iv_coverImg = itemView.findViewById(R.id.iv_coverImg);
          tv_title = itemView.findViewById(R.id.tv_title);
          tv_money = itemView.findViewById(R.id.tv_money);

      }
  }
}
