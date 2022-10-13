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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import com.tencent.imsdk.v2.V2TIMGroupInfo;
import com.tencent.imsdk.v2.V2TIMGroupInfoResult;
import com.tencent.imsdk.v2.V2TIMGroupMemberFullInfo;
import com.tencent.imsdk.v2.V2TIMGroupMemberInfoResult;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.tencent.qcloud.tuicore.TUIConstants;
import com.tencent.qcloud.tuicore.TUICore;
import com.tencent.qcloud.tuicore.component.imageEngine.impl.GlideEngine;
import com.tencent.qcloud.tuicore.component.interfaces.IUIKitCallback;
import com.tencent.qcloud.tuicore.util.BackgroundTasks;
import com.tencent.qcloud.tuicore.util.PopWindowUtil;
import com.tencent.qcloud.tuicore.util.ScreenUtil;
import com.tencent.qcloud.tuicore.util.ToastUtil;
import com.tencent.qcloud.tuikit.tuigroup.R;
import com.tencent.qcloud.tuikit.tuigroup.TUIGroupService;
import com.tencent.qcloud.tuikit.tuigroup.bean.GroupInfo;
import com.tencent.qcloud.tuikit.tuigroup.bean.GroupMemberInfo;
import com.tencent.qcloud.tuikit.tuigroup.net.Common;
import com.tencent.qcloud.tuikit.tuigroup.net.EncryptUtil;
import com.tencent.qcloud.tuikit.tuigroup.net.OkHttp3_0Utils;
import com.tencent.qcloud.tuikit.tuigroup.ui.interfaces.IGroupMemberChangedCallback;
import com.tencent.qcloud.tuikit.tuigroup.presenter.GroupInfoPresenter;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class GroupMemberAdapter extends RecyclerView.Adapter<GroupMemberAdapter.GroupMemberViewHodler> {

    private IGroupMemberChangedCallback mCallback;
    private GroupInfo mGroupInfo;
    private List<GroupMemberInfo> mGroupMembers = new ArrayList<>();

    private GroupInfoPresenter presenter;
    private boolean isSelectMode;

    private static String SP_NAME = "huimiaomiao_share_date";
    private SharedPreferences sp;

    private ArrayList<String> selectedMember = new ArrayList<>();
    private String myUserId;

    public GroupMemberAdapter(Context context) {

        sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        myUserId = sp.getString("userId", "");

        myUserId = EncryptUtil.getInstance(context).decrypt(myUserId);
    }

    public void setSelectMode(boolean selectMode) {
        isSelectMode = selectMode;
    }

    public ArrayList<String> getSelectedMember() {
        return selectedMember;
    }

    public void setPresenter(GroupInfoPresenter presenter) {
        this.presenter = presenter;
    }

    public void setMemberChangedCallback(IGroupMemberChangedCallback callback) {
        this.mCallback = callback;
    }

    @NonNull
    @Override
    public GroupMemberViewHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_member_list_item, parent, false);
        return new GroupMemberViewHodler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupMemberViewHodler holder, int position) {
        final GroupMemberInfo info = mGroupMembers.get(position);
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

        if (isSelectMode) {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.checkBox.setChecked(!holder.checkBox.isChecked());
                    if (holder.checkBox.isChecked()) {
                        selectedMember.add(info.getAccount());
                    } else {
                        selectedMember.remove(info.getAccount());
                    }
                }
            });

        } else {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!mGroupInfo.isOwner()) {
                        return false;
                    }
                    TextView delete = new TextView(holder.itemView.getContext());
                    delete.setText(R.string.group_remove_member);
                    int padding = ScreenUtil.getPxByDp(10);
                    delete.setPadding(padding, padding, padding, padding);
                    delete.setBackgroundResource(R.drawable.text_border);
                    int location[] = new int[2];
                    v.getLocationInWindow(location);
                    final PopupWindow window = PopWindowUtil.popupWindow(delete, holder.itemView, location[0], location[1] + v.getMeasuredHeight() / 3);
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            List<GroupMemberInfo> dels = new ArrayList<>();
                            dels.add(info);
                            if (presenter == null) {
                                return;
                            }
                            presenter.removeGroupMembers(mGroupInfo, dels, new IUIKitCallback() {
                                @Override
                                public void onSuccess(Object data) {
                                    mGroupMembers.remove(info);
                                    notifyDataSetChanged();
                                    if (mCallback != null) {
                                        mCallback.onMemberRemoved(info);
                                    }
                                }

                                @Override
                                public void onError(String module, int errCode, String errMsg) {
                                    ToastUtil.toastLongMessage(TUIGroupService.getAppContext().getString(R.string.remove_fail_tip) + ":errCode=" + errCode);
                                }
                            });
                            window.dismiss();
                        }
                    });
                    return false;
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //判断是不是开启群成员保护

                    //获取群保护状态
                    sp =  holder.itemView.getContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
                    String token = sp.getString("token", "");

                    token = EncryptUtil.getInstance(  holder.itemView.getContext()).decrypt(token);
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
                                            bundle.putBoolean("isAdmin",false);
                                            TUICore.startActivity("FriendProfileActivity", bundle);
                                        }





                                    }

                                }
                                if (code == 403){
                                    Intent intent = new Intent();
                                    intent.setAction("cn.twle.android.sendbroadcast.Setup.Token");
                                    intent.setComponent(new ComponentName("com.pingmo.chengyan",
                                            "com.pingmo.chengyan.receiver.TokenFileBroadcastReceiver"));
                                    holder.itemView.getContext(). sendBroadcast(intent);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });




                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mGroupMembers == null) {
            return 0;
        }
        return mGroupMembers.size();
    }

    public void setDataSource(GroupInfo groupInfo) {
        if (groupInfo != null) {
            mGroupInfo = groupInfo;
            this.mGroupMembers = groupInfo.getMemberDetails();
            BackgroundTasks.getInstance().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }
    }

    protected class GroupMemberViewHodler extends RecyclerView.ViewHolder {
        private ImageView memberIcon;
        private TextView memberName;
        private CheckBox checkBox;

        public GroupMemberViewHodler(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.group_member_check_box);
            memberIcon = itemView.findViewById(R.id.group_member_icon);
            memberName = itemView.findViewById(R.id.group_member_name);
        }
    }
}
