package com.tencent.qcloud.tuikit.tuichat.adapter;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.tencent.qcloud.tuikit.tuichat.R;
import com.tencent.qcloud.tuikit.tuichat.bean.ReceivedGroupBean;

import java.util.ArrayList;
import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<ReceivedGroupBean> receivedGroupBeanList;
    private  TextView tv_group_time;
    private  TextView tv_group_name;
    private  TextView tv_group_money;
    private List<Float> mums = new ArrayList<>();
    private final Float maxValue;
    private  TextView tv_group_best_luck;
    private  ImageView iv_group_best_luck;
    private  ImageView tv_group_avatar;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public GroupAdapter(Context context, List<ReceivedGroupBean> receivedGroupBeanList){
        mums.clear();
        this.context = context;
        this.receivedGroupBeanList = receivedGroupBeanList;
        for (ReceivedGroupBean receivedGroupBean : receivedGroupBeanList) {
            String money = receivedGroupBean.getMoney();
            Float aDouble = Float.valueOf(money);
            mums.add(aDouble);
        }
        maxValue = mums.stream().reduce(Float::max).get();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_item_view, parent, false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ReceivedGroupBean receivedGroupBean = receivedGroupBeanList.get(position);
        tv_group_time.setText(receivedGroupBean.getRobTime());
        tv_group_name.setText(receivedGroupBean.getNickName());
        tv_group_money.setText(receivedGroupBean.getMoney()+"元");
        String headImage = receivedGroupBean.getHeadImage();
        if (!TextUtils.isEmpty(headImage)){
            Glide.with(context)
                    .load(headImage)
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .error(R.drawable.image_index)
                    .into(tv_group_avatar);
        }

        String money = receivedGroupBean.getMoney();
        Float aFloat = Float.valueOf(money);
        if (String.valueOf(maxValue).equals(String.valueOf(aFloat)) ){
            tv_group_best_luck.setVisibility(View.VISIBLE);
            iv_group_best_luck.setVisibility(View.VISIBLE);
        }else {
            tv_group_best_luck.setVisibility(View.GONE);
            iv_group_best_luck.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return receivedGroupBeanList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            tv_group_avatar = itemView.findViewById(R.id.tv_group_avatar);
            tv_group_time = itemView.findViewById(R.id.tv_group_time);
            tv_group_name = itemView.findViewById(R.id.tv_group_name);
            tv_group_money = itemView.findViewById(R.id.tv_group_money);
            tv_group_best_luck = itemView.findViewById(R.id.tv_group_best_luck);
            iv_group_best_luck = itemView.findViewById(R.id.iv_group_best_luck);

        }
    }
}
