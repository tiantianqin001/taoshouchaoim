package com.tencent.qcloud.tuikit.tuigroup.ui.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencent.imsdk.v2.V2TIMGroupInfo;
import com.tencent.imsdk.v2.V2TIMGroupInfoResult;
import com.tencent.imsdk.v2.V2TIMGroupMemberFullInfo;
import com.tencent.imsdk.v2.V2TIMGroupMemberInfoResult;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.tencent.qcloud.tuicore.TUIConstants;
import com.tencent.qcloud.tuicore.TUICore;
import com.tencent.qcloud.tuicore.component.imageEngine.impl.GlideEngine;
import com.tencent.qcloud.tuicore.util.BackgroundTasks;
import com.tencent.qcloud.tuicore.util.ToastUtil;
import com.tencent.qcloud.tuikit.tuigroup.R;
import com.tencent.qcloud.tuikit.tuigroup.TUIGroupService;
import com.tencent.qcloud.tuikit.tuigroup.bean.GroupInfo;
import com.tencent.qcloud.tuikit.tuigroup.bean.GroupMemberInfo;
import com.tencent.qcloud.tuikit.tuigroup.net.Common;
import com.tencent.qcloud.tuikit.tuigroup.net.EncryptUtil;
import com.tencent.qcloud.tuikit.tuigroup.net.OkHttp3_0Utils;
import com.tencent.qcloud.tuikit.tuigroup.ui.interfaces.IGroupMemberListener;
import com.tencent.qcloud.tuikit.tuigroup.presenter.GroupInfoPresenter;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.ContentValues.TAG;


public class GroupInfoAdapter extends BaseAdapter {

    private static final int ADD_TYPE = -100;
    private static final int DEL_TYPE = -101;
    private static final int OWNER_PRIVATE_MAX_LIMIT = 8;  //讨论组,owner可以添加成员和删除成员，
    private static final int OWNER_PUBLIC_MAX_LIMIT = 9;   //公开群,owner不可以添加成员，但是可以删除成员
    private static final int OWNER_CHATROOM_MAX_LIMIT = 9; //聊天室,owner不可以添加成员，但是可以删除成员
    private static final int OWNER_COMMUNITY_MAX_LIMIT = 8; //社群,owner可以添加成员和删除成员，
    private static final int NORMAL_PRIVATE_MAX_LIMIT = 9; //讨论组,普通人可以添加成员
    private static final int NORMAL_PUBLIC_MAX_LIMIT = 10;  //公开群,普通人没有权限添加成员和删除成员
    private static final int NORMAL_CHATROOM_MAX_LIMIT = 10; //聊天室,普通人没有权限添加成员和删除成员
    private static final int NORMAL_COMMUNITY_MAX_LIMIT = 9; //社群,普通人可以添加成员

    private List<GroupMemberInfo> mGroupMembers = new ArrayList<>();
    private IGroupMemberListener mTailListener;
    private GroupInfo mGroupInfo;
    private List<V2TIMGroupMemberFullInfo> infoList;

    private GroupInfoPresenter presenter;
    private Context context;
    private String myUserId;

    public GroupInfoAdapter(Context context) {
        this.context = context;
        sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        myUserId = sp.getString("userId", "");

        myUserId = EncryptUtil.getInstance(context).decrypt(myUserId);
    }
    public void setPresenter(GroupInfoPresenter presenter) {
        this.presenter = presenter;
    }

    public void setManagerCallBack(IGroupMemberListener listener) {
        mTailListener = listener;
    }

    @Override
    public int getCount() {
        return mGroupMembers.size();
    }

    @Override
    public GroupMemberInfo getItem(int i) {
        return mGroupMembers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, final ViewGroup viewGroup) {
        MyViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.group_member_item_layout, viewGroup, false);
            holder = new MyViewHolder();
            holder.memberIcon = view.findViewById(R.id.group_member_icon);
            holder.memberName = view.findViewById(R.id.group_member_name);
            holder.groupLogo = view.findViewById(R.id.iv_group_logo);
            view.setTag(holder);
        } else {
            holder = (MyViewHolder) view.getTag();
        }
        final GroupMemberInfo info = getItem(i);
        GlideEngine.loadImage(holder.memberIcon, info.getIconUrl());
        // 显示优先级 群名片->昵称->账号
        if (!TextUtils.isEmpty(info.getNameCard())) {
            holder.memberName.setText(info.getNameCard());
        } else {
            if (!TextUtils.isEmpty(info.getNickName())) {
                holder.memberName.setText(info.getNickName());
            } else {
                if (!TextUtils.isEmpty(info.getAccount())) {
                    holder.memberName.setText(info.getAccount());
                } else {
                    holder.memberName.setText("");
                }
            }
        }
        //设置哪个用户是群主
        holder.groupLogo.setVisibility(View.VISIBLE);
        String owner = mGroupInfo.getOwner();
        if (owner.equals(info.getAccount())){

            holder.groupLogo.setBackground(viewGroup.getContext().getResources()
                    .getDrawable(R.drawable.group_admin));
        }else {
            //获取群管理员
            holder.groupLogo.setBackground(null);
            if (infoList!=null && infoList.size()>0){
                for (V2TIMGroupMemberFullInfo fullInfo : infoList) {
                    Log.i("", "onSuccess: "+fullInfo);
                    String userID = fullInfo.getUserID();
                    if (userID.equals(info.getAccount())){
                        //当前设置了群管理员
                        holder.groupLogo.setBackground(viewGroup.getContext()
                                .getResources().getDrawable(R.drawable.group_manage));
                        break;
                    }else {
                        holder.groupLogo.setBackground(null);
                    }
                }
            }


        }
        //加号只有群主和管理员

        view.setOnClickListener(null);
        holder.memberIcon.setBackground(null);
        holder.memberIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);


        if (info.getMemberType() == ADD_TYPE) {
            holder.memberIcon.setImageResource(R.drawable.add_group_member);
            holder.memberIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            holder.memberIcon.setBackgroundResource(R.drawable.bottom_action_border);
            holder.groupLogo.setVisibility(View.GONE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTailListener != null) {
                        mTailListener.forwardAddMember(mGroupInfo);
                    }
                }
            });
        } else if (info.getMemberType() == DEL_TYPE) {
            holder.memberIcon.setImageResource(R.drawable.del_group_member);
            holder.memberIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            holder.memberIcon.setBackgroundResource(R.drawable.bottom_action_border);
            holder.groupLogo.setVisibility(View.GONE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTailListener != null) {
                        mTailListener.forwardDeleteMember(mGroupInfo);
                    }
                }
            });
        }else {
            //头像的点击事件
            View finalView = view;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("", "onClick: "+mGroupInfo);


                    //获取群保护状态
                    sp = finalView.getContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
                    String token = sp.getString("token", "");

                    token = EncryptUtil.getInstance(finalView.getContext()).decrypt(token);
                    //获取群消息
                    String url = Common.GETGROUPINFO;

                    Map<String , String> headerParams = new LinkedHashMap<>();
                    headerParams.put("token", token);
                    Map<String , String> mapPatams = new LinkedHashMap<>();
                    mapPatams.put("groupId",mGroupInfo.getId());

                    OkHttp3_0Utils.getInstance().asyncGetHeaderOkHttp(url, mapPatams, headerParams, new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {

                        }
                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            String content = response.body().string();
                            Log.i(TAG, "onResponse: "+content);
                            try {
                                JSONObject jsonObject1 = new JSONObject(content);
                                int code = jsonObject1.optInt("code");
                                if (code == 200){
                                    JSONObject optJSONObject = jsonObject1.optJSONObject("data");
                                    if (optJSONObject!=null){
                                        int groupMemberProtect = optJSONObject.optInt("groupMemberProtect");
                                        if (groupMemberProtect == 1){
                                            BackgroundTasks.getInstance().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    List<String> groupList = new ArrayList<>();
                                                    groupList.add(mGroupInfo.getId());
                                                    V2TIMManager.getGroupManager().getGroupsInfo(groupList, new V2TIMValueCallback<List<V2TIMGroupInfoResult>>() {
                                                        @Override
                                                        public void onSuccess(List<V2TIMGroupInfoResult> v2TIMGroupInfoResults) {
                                                            Log.i(TAG, "onSuccess: "+v2TIMGroupInfoResults);
                                                            if (v2TIMGroupInfoResults!=null){
                                                                V2TIMGroupInfoResult v2TIMGroupInfoResult = v2TIMGroupInfoResults.get(0);
                                                                if (v2TIMGroupInfoResult!=null){
                                                                    V2TIMGroupInfo groupInfo = v2TIMGroupInfoResult.getGroupInfo();
                                                                    if (groupInfo!=null){
                                                                        String owner = groupInfo.getOwner();
                                                                        Log.i(TAG, "onSuccess: "+owner);

                                                                        if (owner.equals(myUserId)){
                                                                            //我是群主
                                                                            Bundle bundle = new Bundle();
                                                                            bundle.putString(TUIConstants.TUIChat.CHAT_ID, info.getAccount());
                                                                            bundle.putBoolean("isGroup",true);
                                                                            bundle.putBoolean("isAdmin",true);
                                                                            bundle.putInt("groupMemberProtect",groupMemberProtect);
                                                                            TUICore.startActivity("FriendProfileActivity", bundle);
                                                                            return;

                                                                        }else {
                                                                            //获取群管理员
                                                                            int filter = V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_FILTER_ADMIN; //管理员
                                                                            V2TIMManager.getGroupManager().getGroupMemberList(mGroupInfo.getId(), filter, 0,
                                                                                    new V2TIMValueCallback<V2TIMGroupMemberInfoResult>(){
                                                                                        @Override
                                                                                        public void onSuccess(V2TIMGroupMemberInfoResult v2TIMGroupMemberInfoResult) {
                                                                                            List<V2TIMGroupMemberFullInfo> infoList = v2TIMGroupMemberInfoResult.getMemberInfoList();
                                                                                            for (V2TIMGroupMemberFullInfo fullInfo : infoList) {
                                                                                                Log.i(TAG, "onSuccess: "+fullInfo);
                                                                                                String userID = fullInfo.getUserID();
                                                                                                if (userID.equals(myUserId)){
                                                                                                    //当前设置了群管理员
                                                                                                    Bundle bundle = new Bundle();
                                                                                                    bundle.putString(TUIConstants.TUIChat.CHAT_ID, info.getAccount());
                                                                                                    bundle.putBoolean("isGroup",true);
                                                                                                    bundle.putBoolean("isAdmin",true);
                                                                                                    bundle.putInt("groupMemberProtect",groupMemberProtect);
                                                                                                    TUICore.startActivity("FriendProfileActivity", bundle);

                                                                                                    return;
                                                                                                }
                                                                                            }

                                                                                            ToastUtil.toastLongMessage("本群已开启了群保护模式");



                                                                                        }

                                                                                        @Override
                                                                                        public void onError(int i, String s) {

                                                                                        }
                                                                                    });
                                                                        }
                                                                    }
                                                                }
                                                            }

                                                        }

                                                        @Override
                                                        public void onError(int i, String s) {

                                                        }
                                                    });



                                                }
                                            });

                                        }else {
                                            Bundle bundle = new Bundle();
                                            bundle.putString(TUIConstants.TUIChat.CHAT_ID, info.getAccount());
                                            bundle.putBoolean("isGroup",true);
                                            bundle.putBoolean("isAdmin",false);
                                            bundle.putInt("groupMemberProtect",groupMemberProtect);
                                            TUICore.startActivity("FriendProfileActivity", bundle);
                                        }
                                    }

                                }
                                if (code == 403){
                                    Intent intent = new Intent();
                                    intent.setAction("cn.twle.android.sendbroadcast.Setup.Token");
                                    intent.setComponent(new ComponentName("com.pingmo.chengyan",
                                            "com.pingmo.chengyan.receiver.TokenFileBroadcastReceiver"));
                                    finalView.getContext(). sendBroadcast(intent);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }

            });
        }
        return view;
    }
    private static String SP_NAME = "huimiaomiao_share_date";
    private SharedPreferences sp;
    public void setDataSource(GroupInfo info) {
        mGroupInfo = info;

        mGroupMembers.clear();
        List<GroupMemberInfo> members = info.getMemberDetails();
        if (members != null) {
            int shootMemberCount = 0;
            if (TextUtils.equals(info.getGroupType(), TUIConstants.GroupType.TYPE_PRIVATE)
                    || TextUtils.equals(info.getGroupType(), TUIConstants.GroupType.TYPE_WORK)) {
                if (info.isOwner()) {
                    shootMemberCount = members.size() > OWNER_PRIVATE_MAX_LIMIT ? OWNER_PRIVATE_MAX_LIMIT : members.size();
                } else {
                    shootMemberCount = members.size() > NORMAL_PRIVATE_MAX_LIMIT ? NORMAL_PRIVATE_MAX_LIMIT : members.size();
                }
            } else if (TextUtils.equals(info.getGroupType(), TUIConstants.GroupType.TYPE_PUBLIC)) {
                if (info.isOwner()) {
                    shootMemberCount = members.size() > OWNER_PUBLIC_MAX_LIMIT ? OWNER_PUBLIC_MAX_LIMIT : members.size();
                } else {
                    shootMemberCount = members.size() > NORMAL_PUBLIC_MAX_LIMIT ? NORMAL_PUBLIC_MAX_LIMIT : members.size();
                }
            } else if (TextUtils.equals(info.getGroupType(), TUIConstants.GroupType.TYPE_CHAT_ROOM)
                    || TextUtils.equals(info.getGroupType(), TUIConstants.GroupType.TYPE_MEETING)) {
                if (info.isOwner()) {
                    shootMemberCount = members.size() > OWNER_CHATROOM_MAX_LIMIT ? OWNER_CHATROOM_MAX_LIMIT : members.size();
                } else {
                    shootMemberCount = members.size() > NORMAL_CHATROOM_MAX_LIMIT ? NORMAL_CHATROOM_MAX_LIMIT : members.size();
                }
            } else if (TextUtils.equals(info.getGroupType(), TUIConstants.GroupType.TYPE_COMMUNITY)) {
                if (info.isOwner()) {
                    shootMemberCount = members.size() > OWNER_COMMUNITY_MAX_LIMIT ? OWNER_COMMUNITY_MAX_LIMIT : members.size();
                } else {
                    shootMemberCount = members.size() > NORMAL_COMMUNITY_MAX_LIMIT ? NORMAL_COMMUNITY_MAX_LIMIT : members.size();
                }
            }

            for (int i = 0; i < shootMemberCount; i++) {
                mGroupMembers.add(members.get(i));
            }
            if (TextUtils.equals(info.getGroupType(), TUIConstants.GroupType.TYPE_PRIVATE)
                    || TextUtils.equals(info.getGroupType(), TUIConstants.GroupType.TYPE_WORK)
                    || TextUtils.equals(info.getGroupType(), TUIConstants.GroupType.TYPE_COMMUNITY)
                    || TextUtils.equals(info.getGroupType(), TUIConstants.GroupType.TYPE_PUBLIC)) {
                // 公开群/聊天室 只有APP管理员可以邀请他人入群
                GroupMemberInfo add = new GroupMemberInfo();
                add.setMemberType(ADD_TYPE);
                mGroupMembers.add(add);
            }

            GroupMemberInfo self = null;
            for (int i = 0; i < mGroupMembers.size(); i++) {
                GroupMemberInfo memberInfo = mGroupMembers.get(i);
                if (isSelf(memberInfo.getAccount())) {
                    self = memberInfo;
                    break;
                }
            }

            if (info.isOwner() || (self != null && isAdmin(self.getMemberType()))) {
                GroupMemberInfo del = new GroupMemberInfo();
                del.setMemberType(DEL_TYPE);
                mGroupMembers.add(del);
            }

            int filter = V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_FILTER_ADMIN;
            V2TIMManager.getGroupManager().getGroupMemberList(mGroupInfo.getId(), filter, 0,  new V2TIMValueCallback<V2TIMGroupMemberInfoResult>(){
                @Override
                public void onSuccess(V2TIMGroupMemberInfoResult v2TIMGroupMemberInfoResult) {
                    infoList = v2TIMGroupMemberInfoResult.getMemberInfoList();

                    BackgroundTasks.getInstance().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notifyDataSetChanged();
                        }
                    });



                }
                @Override
                public void onError(int i, String s) {

                }
            });



        }

    }

    public boolean isAdmin(int memberType) {
        if (presenter != null) {
            return presenter.isAdmin(memberType);
        }
        return false;
    }

    public boolean isSelf(String userId) {
        if (presenter != null) {
            return presenter.isSelf(userId);
        }
        return false;
    }



    private class MyViewHolder {
        private ImageView memberIcon;
        private TextView memberName;
        private ImageView groupLogo;
    }


}
