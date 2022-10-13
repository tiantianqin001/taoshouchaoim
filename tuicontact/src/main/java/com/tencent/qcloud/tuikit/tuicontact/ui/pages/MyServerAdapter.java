package com.tencent.qcloud.tuikit.tuicontact.ui.pages;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.tencent.imsdk.v2.V2TIMFriendInfo;
import com.tencent.imsdk.v2.V2TIMFriendOperationResult;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMUserFullInfo;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.tencent.qcloud.tuicore.component.LineControllerView;
import com.tencent.qcloud.tuicore.component.gatherimage.ShadeImageView;
import com.tencent.qcloud.tuicore.util.ErrorMessageConverter;
import com.tencent.qcloud.tuikit.tuicontact.R;
import com.tencent.qcloud.tuikit.tuicontact.TUIContactConstants;
import com.tencent.qcloud.tuikit.tuicontact.TUIContactService;
import com.tencent.qcloud.tuikit.tuicontact.bean.ContactItemBean;
import com.tencent.qcloud.tuikit.tuicontact.util.ContactUtils;
import com.tencent.qcloud.tuikit.tuicontact.util.TUIContactLog;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class MyServerAdapter  extends RecyclerView.Adapter {
    private Switch blackList;
    private  TextView tv_official;
    private Context context;
    private List<V2TIMFriendInfo> datas;
    private int pos;
    private  ShadeImageView ivAvatar;
    private  TextView tvCity;
    private  RelativeLayout rl_my_server;
    public static final String TAG = "MyServerAdapter";
    private  ImageView iv_back;
    private List<String> idList = new ArrayList<>();

    public MyServerAdapter(Context context, List<V2TIMFriendInfo> datas, int pos) {
        this.context = context;
        this.datas = datas;
        this.pos = pos;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_server, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        if (pos == 1){
            iv_back.setVisibility(View.GONE);
            //1是黑名单不显示官方
            tv_official.setVisibility(View.GONE);
            blackList.setVisibility(View.VISIBLE);

        }else {
            iv_back.setVisibility(View.VISIBLE);
            blackList.setVisibility(View.GONE);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.setIsRecyclable(false);
        V2TIMFriendInfo v2TIMFriendInfo = datas.get(position);
        if (v2TIMFriendInfo!=null){
            V2TIMUserFullInfo userProfile = v2TIMFriendInfo.getUserProfile();
            if (userProfile!=null){
                Glide.with(context)
                 .asBitmap()
                  .load(userProfile.getFaceUrl())
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                  .error(R.drawable.index_image)
                  .into(ivAvatar);

                tvCity.setText(userProfile.getNickName());
                blackList.setChecked(true);
                idList.clear();

                //黑名单的点击事件
                blackList.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Log.i(TAG, "onCheckedChanged: "+isChecked);
                        idList.add(v2TIMFriendInfo.getUserID());
                        if (isChecked){

                            //添加到黑名单
                            V2TIMManager.getFriendshipManager().addToBlackList(idList, new V2TIMValueCallback<List<V2TIMFriendOperationResult>>() {
                                @Override
                                public void onError(int code, String desc) {
                                    TUIContactLog.e(TAG, "deleteBlackList err code = " + code + ", desc = " + ErrorMessageConverter.convertIMError(code, desc));

                                }

                                @Override
                                public void onSuccess(List<V2TIMFriendOperationResult> v2TIMFriendOperationResults) {
                                    TUIContactLog.i(TAG, "deleteBlackList success");


                                }
                            });

                        }else {
                            //加入黑名单和移除黑名单
                            V2TIMManager.getFriendshipManager().deleteFromBlackList(idList, new V2TIMValueCallback<List<V2TIMFriendOperationResult>>() {
                                @Override
                                public void onError(int code, String desc) {
                                    TUIContactLog.e(TAG, "deleteBlackList err code = " + code + ", desc = " + ErrorMessageConverter.convertIMError(code, desc));

                                }

                                @Override
                                public void onSuccess(List<V2TIMFriendOperationResult> v2TIMFriendOperationResults) {
                                    TUIContactLog.i(TAG, "deleteBlackList success");
                                    datas.remove(position);
                                    notifyDataSetChanged();

                                }
                            });

                        }
                    }
                });

            }
           if (pos == 100){
               rl_my_server.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
//                       ContactItemBean contact = new ContactItemBean();
//                       contact.setFriend(true);
//                       contact.setGroup(false);
//                       contact.setId(userProfile.getUserID());
//                       contact.setNickName(userProfile.getNickName());
//                       contact.setAvatarUrl(userProfile.getFaceUrl());
//                       contact.setBlackList(false) ;
//                       Intent intent = new Intent(TUIContactService.getAppContext(), FriendProfileActivity.class);
//                       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                       intent.putExtra(TUIContactConstants.ProfileType.CONTENT, contact);
//                       TUIContactService.getAppContext().startActivity(intent);


                       ContactUtils.startChatActivity(userProfile.getUserID(), ContactItemBean.TYPE_C2C,userProfile.getNickName(), "");

                   }
               });
           }

        }

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }


    private class ViewHolder  extends RecyclerView.ViewHolder{



        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            rl_my_server = itemView.findViewById(R.id.rl_my_server);
            tvCity = itemView.findViewById(R.id.tvCity);
            iv_back = itemView.findViewById(R.id.iv_back);
            tv_official = itemView.findViewById(R.id.tv_official);
            blackList = itemView.findViewById(R.id.blackList);
        }
    }
}
