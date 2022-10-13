package com.pingmo.chengyan.activity.shop.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.shop.activity.MallTypeActivity;
import com.pingmo.chengyan.activity.shop.bean.ShopTypeBean;
import com.pingmo.chengyan.activity.shop.common.Constant;
import com.pingmo.chengyan.net.Common;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TypeImageAdapter extends RecyclerView.Adapter {
   private  ImageView img_icon;
   private  TextView txt_icon;
   private Context context;
   private List<ShopTypeBean.DataDTO> data;

   public TypeImageAdapter(Context context, List<ShopTypeBean.DataDTO> data) {
      this.context = context;
      this.data = data;
   }

   @NonNull
   @Override
   public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      //将我们自定义的item布局R.layout.item_one转换为View
      View view = LayoutInflater.from(parent.getContext())
              .inflate(R.layout.item_grid_icon, parent, false);
      ViewHolder  viewHolder = new ViewHolder(view);
      return viewHolder;
   }

   @Override
   public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
      ShopTypeBean.DataDTO dataDTO = data.get(position);
      txt_icon.setText(dataDTO.label);
      Glide.with(context)
              .asBitmap()
              .load(Common.BaseUrl + dataDTO.icon)
              .into(img_icon);

      img_icon.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent intent = new Intent(context, MallTypeActivity.class);
             intent.putExtra(Constant.TYPE, dataDTO.value);
             context.startActivity(intent);
         }
      });
   }

   @Override
   public int getItemCount() {
      return data.size();
   }


   class ViewHolder extends RecyclerView.ViewHolder{

      public ViewHolder(@NonNull View itemView) {
         super(itemView);
         img_icon = itemView.findViewById(R.id.img_icon);
         txt_icon = itemView.findViewById(R.id.txt_icon);
      }
   }
}
