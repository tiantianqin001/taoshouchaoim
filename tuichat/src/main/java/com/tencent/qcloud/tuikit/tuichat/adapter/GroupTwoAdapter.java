package com.tencent.qcloud.tuikit.tuichat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.tencent.qcloud.tuikit.tuichat.R;
import com.tencent.qcloud.tuikit.tuichat.bean.GroupRedPacketDetailBean;
import com.tencent.qcloud.tuikit.tuichat.bean.ReceivedGroupBean;

import java.util.List;

public class GroupTwoAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<GroupRedPacketDetailBean.DataDTO.MembersDTO> receivedGroupBeanList;
    private  TextView tv_group_time;
    private  TextView tv_group_name;
    private  TextView tv_group_money;
    private  ImageView tv_group_avatar;

    public GroupTwoAdapter(Context context, List<GroupRedPacketDetailBean.DataDTO.MembersDTO> receivedGroupBeanList){

        this.context = context;
        this.receivedGroupBeanList = receivedGroupBeanList;
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
        GroupRedPacketDetailBean.DataDTO.MembersDTO receivedGroupBean = receivedGroupBeanList.get(position);
        tv_group_time.setText(receivedGroupBean.getRobTime());
        tv_group_name.setText(receivedGroupBean.getNickName());
        tv_group_money.setText(receivedGroupBean.getMoney());

        Glide.with(context)
                .load(receivedGroupBean.getHeadImage())
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .error(R.drawable.image_index)
                .into(tv_group_avatar);
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

        }
    }
}
