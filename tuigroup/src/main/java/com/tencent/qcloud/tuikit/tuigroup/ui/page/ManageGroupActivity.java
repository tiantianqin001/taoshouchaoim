package com.tencent.qcloud.tuikit.tuigroup.ui.page;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.tencent.qcloud.tuicore.component.CustomLinearLayoutManager;
import com.tencent.qcloud.tuicore.component.LineControllerView;
import com.tencent.qcloud.tuicore.component.TitleBarLayout;
import com.tencent.qcloud.tuicore.component.activities.BaseLightActivity;
import com.tencent.qcloud.tuicore.component.gatherimage.ShadeImageView;
import com.tencent.qcloud.tuicore.component.imageEngine.impl.GlideEngine;
import com.tencent.qcloud.tuicore.component.interfaces.ITitleBarLayout;
import com.tencent.qcloud.tuicore.component.interfaces.IUIKitCallback;
import com.tencent.qcloud.tuicore.util.ScreenUtil;
import com.tencent.qcloud.tuicore.util.ToastUtil;
import com.tencent.qcloud.tuikit.tuigroup.R;
import com.tencent.qcloud.tuikit.tuigroup.TUIGroupConstants;
import com.tencent.qcloud.tuikit.tuigroup.bean.GroupInfo;
import com.tencent.qcloud.tuikit.tuigroup.bean.GroupMemberInfo;
import com.tencent.qcloud.tuikit.tuigroup.bean.MuteBean;
import com.tencent.qcloud.tuikit.tuigroup.net.Common;
import com.tencent.qcloud.tuikit.tuigroup.net.EncryptUtil;
import com.tencent.qcloud.tuikit.tuigroup.net.OkHttp3_0Utils;
import com.tencent.qcloud.tuikit.tuigroup.presenter.GroupManagerPresenter;
import com.tencent.qcloud.tuikit.tuigroup.ui.view.GroupInfoLayout;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ManageGroupActivity extends BaseLightActivity {

    private static final String TAG = "ManageGroupActivity";
    private TitleBarLayout titleBarLayout;
    private LineControllerView setManagerView;
    private LineControllerView muteAllView;
    private View addMuteMemberView;
    private RecyclerView mutedList;
    private MutedMemberAdapter mutedMemberAdapter;

    private GroupManagerPresenter presenter;
    private GroupInfo groupInfo;
    private LineControllerView mJoinTypeView;
    private Handler handler = new Handler();

    private TextView tv_protect_bar;
    private LineControllerView join_protect_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_group_manager);
        presenter = new GroupManagerPresenter();
        titleBarLayout = findViewById(R.id.group_manage_title_bar);


        RelativeLayout rootView = titleBarLayout.getRootView();
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) rootView.getLayoutParams();
        params.setMargins(0, 0, 0, 0);
        rootView.setLayoutParams(params);

        setManagerView = findViewById(R.id.group_manage_set_manager);
        muteAllView = findViewById(R.id.group_manage_mute_all);
        addMuteMemberView = findViewById(R.id.group_manage_add_mute_member);
        mutedList = findViewById(R.id.group_manage_muted_member_list);


        //群保护模式
        join_protect_bar = findViewById(R.id.join_protect_bar);
        // 群邀请请确认
        mJoinTypeView = findViewById(R.id.join_type_bar);

        mutedList.setLayoutManager(new CustomLinearLayoutManager(this));
        mutedMemberAdapter = new MutedMemberAdapter();
        mutedList.setAdapter(mutedMemberAdapter);

        groupInfo = (GroupInfo) getIntent().getSerializableExtra(TUIGroupConstants.Group.GROUP_INFO);
       int invitationStatus = getIntent().getIntExtra("invitationStatus",0);
       int groupMemberProtect = getIntent().getIntExtra("groupMemberProtect",0);




        //判断是不是群主
        if (groupInfo.isOwner()) {

            mJoinTypeView.setVisibility(VISIBLE);
            join_protect_bar.setVisibility(VISIBLE);
        } else {
            if (groupInfo.isCanManagerGroup()) {
                mJoinTypeView.setVisibility(VISIBLE);
                join_protect_bar.setVisibility(VISIBLE);
            } else {
                join_protect_bar.setVisibility(GONE);
                mJoinTypeView.setVisibility(GONE);
            }
        }
        if (invitationStatus == 1){
            mJoinTypeView.setChecked(true);
        }else {
            mJoinTypeView.setChecked(false);
        }
        if (groupMemberProtect==1){
            join_protect_bar.setChecked(true);
        }else {
            join_protect_bar.setChecked(false);
        }


        mJoinTypeView.setCheckListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //在设置允许拉别人进群的时候，
                //设置加群方式
                //设置加入群模式 1 管理员授权  2 是容许加入 0 不允许加入
                //修改群信息的加群方式
                sp =  getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
                String token = sp.getString("token", "");

                token = EncryptUtil.getInstance(ManageGroupActivity.this).decrypt(token);
                String url = Common.UPDATA_GROUP;

                Map<String , String> headerParams = new LinkedHashMap<>();
                headerParams.put("token", token);

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("groupId", Integer.valueOf(groupInfo.getId()));
                    if (isChecked){
                        jsonObject.put("invitationStatus", 1);
                    }else {
                        jsonObject.put("invitationStatus", 0);
                    }
                    OkHttp3_0Utils.getInstance().asyncBodyOkHttp(url, jsonObject, headerParams, new Callback() {
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
                                if (code == 403){
                                    Intent intent = new Intent();
                                    intent.setAction("cn.twle.android.sendbroadcast.Setup.Token");
                                    intent.setComponent(new ComponentName("com.pingmo.chengyan",
                                            "com.pingmo.chengyan.receiver.TokenFileBroadcastReceiver"));
                                    sendBroadcast(intent);
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        //群成员保护模式的点击
        join_protect_bar.setCheckListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sp = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
                String token = sp.getString("token", "");

                token = EncryptUtil.getInstance(ManageGroupActivity.this).decrypt(token);
                String url = Common.UPDATA_GROUP;

                Map<String , String> headerParams = new LinkedHashMap<>();
                headerParams.put("token", token);

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("groupId", Integer.valueOf(groupInfo.getId()));
                    if (isChecked){
                        jsonObject.put("groupMemberProtect", 1);
                    }else {
                        jsonObject.put("groupMemberProtect", 0);
                    }
                    OkHttp3_0Utils.getInstance().asyncBodyOkHttp(url, jsonObject, headerParams, new Callback() {
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
                                if (code == 403){
                                    Intent intent = new Intent();
                                    intent.setAction("cn.twle.android.sendbroadcast.Setup.Token");
                                    intent.setComponent(new ComponentName("com.pingmo.chengyan",
                                            "com.pingmo.chengyan.receiver.TokenFileBroadcastReceiver"));
                                    sendBroadcast(intent);
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                join_protect_bar.setChecked(isChecked);
            }
        });

        muteAllView.setChecked(groupInfo.isAllMuted());
        if (groupInfo.isAllMuted()) {
            addMuteMemberView.setVisibility(GONE);
            mutedList.setVisibility(GONE);
        }

        titleBarLayout.setTitle(getString(R.string.group_manager), ITitleBarLayout.Position.MIDDLE);
        setClickListener();
        loadMutedMember();

        titleBarLayout.setBackgroundColor(getResources().getColor(R.color.white));
        titleBarLayout.getLeftIcon().setBackground(getResources().getDrawable(R.drawable.back));
    }

    private void setClickListener() {
        titleBarLayout.getLeftIcon().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        muteAllView.setCheckListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                presenter.muteAll(groupInfo.getId(), isChecked, new IUIKitCallback<Void>() {
                    @Override
                    public void onSuccess(Void data) {
                        if (isChecked) {
                            addMuteMemberView.setVisibility(GONE);
                            mutedList.setVisibility(GONE);

                            estoppelAll(false);
                        } else {
                            addMuteMemberView.setVisibility(VISIBLE);
                            mutedList.setVisibility(VISIBLE);
                            estoppelAll(true);
                        }
                    }

                    @Override
                    public void onError(String module, int errCode, String errMsg) {
                        ToastUtil.toastShortMessage(errCode + ", " + errMsg);
                    }
                });
            }
        });

        setManagerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.equals(groupInfo.getGroupType(), GroupInfo.GROUP_TYPE_AVCHATROOM)
                        || TextUtils.equals(groupInfo.getGroupType(), GroupInfo.GROUP_TYPE_WORK)) {
                    ToastUtil.toastShortMessage(getString(R.string.group_not_support_set_manager));
                    return;
                }
                Intent intent = new Intent(ManageGroupActivity.this, SetGroupManagerActivity.class);
                intent.putExtra(TUIGroupConstants.Group.GROUP_INFO, groupInfo);
                startActivity(intent);
            }
        });

        addMuteMemberView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.equals(groupInfo.getGroupType(), GroupInfo.GROUP_TYPE_AVCHATROOM)
                        || TextUtils.equals(groupInfo.getGroupType(), GroupInfo.GROUP_TYPE_WORK)) {
                    ToastUtil.toastShortMessage(getString(R.string.group_not_support_mute_member));
                    return;
                }

                Intent intent = new Intent(ManageGroupActivity.this, GroupMemberActivity.class);
                intent.putExtra(TUIGroupConstants.Selection.IS_SELECT_MODE, true);
                intent.putExtra(TUIGroupConstants.Selection.FILTER, GroupInfo.GROUP_MEMBER_FILTER_COMMON);
                intent.putExtra(TUIGroupConstants.Group.GROUP_INFO, groupInfo);
                startActivityForResult(intent, 1);
            }
        });

        mutedMemberAdapter.setOnItemLongClickListener(new SetGroupManagerActivity.OnItemLongClickListener() {
            @Override
            public void onClick(View view, GroupMemberInfo memberInfo, int position) {
                Drawable drawable = view.getBackground();
                if (drawable != null) {
                    drawable.setColorFilter(0xd9d9d9, PorterDuff.Mode.SRC_IN);
                }
                View itemPop = LayoutInflater.from(ManageGroupActivity.this).inflate(R.layout.group_manager_pop_menu, null);
                PopupWindow popupWindow = new PopupWindow(itemPop, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                popupWindow.setBackgroundDrawable(new ColorDrawable());
                popupWindow.setOutsideTouchable(true);
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        if (drawable != null) {
                            drawable.clearColorFilter();
                        }
                    }
                });
                TextView popText = itemPop.findViewById(R.id.pop_text);
                popText.setText(R.string.group_cancel_mute_label);
                popText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        presenter.cancelMuteGroupMember(groupInfo.getId(), memberInfo.getAccount(), new IUIKitCallback<Void>() {
                            @Override
                            public void onSuccess(Void data) {
                                mutedMemberAdapter.onItemRemoved(memberInfo);
                            }

                            @Override
                            public void onError(String module, int errCode, String errMsg) {
                                ToastUtil.toastShortMessage(errCode + ", " + errMsg);
                            }
                        });
                    }
                });
                int x = view.getWidth() / 2;
                int y = -view.getHeight() / 3;
                int popHeight = ScreenUtil.dip2px(45) * 3;
                if (y + popHeight + view.getY() + view.getHeight() > mutedList.getBottom()) {
                    y = y - popHeight;
                }
                popupWindow.showAsDropDown(view, x, y, Gravity.TOP | Gravity.START);
            }
        });

    }

    private static String SP_NAME = "huimiaomiao_share_date";
    private SharedPreferences sp;

    private void estoppelAll(boolean muteStatus) {

        sp = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        String token = sp.getString("token", "");

        token = EncryptUtil.getInstance(this).decrypt(token);
        String url = Common.ALL_SPEAK;

        Map<String, String> headerParams = new LinkedHashMap<>();
        headerParams.put("token", token);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("groupId", groupInfo.getId());
            jsonObject.put("muteStatus", muteStatus);
            OkHttp3_0Utils.getInstance().asyncBodyOkHttp(url, jsonObject, headerParams, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String content = response.body().string();
                    Log.i(TAG, "onResponse: " + content);
                    try {
                        JSONObject jsonObject1 = new JSONObject(content);
                        int code = jsonObject1.optInt("code");
                        if (code == 200) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    MuteBean muteBean = new MuteBean();
                                    if (!muteStatus) {
                                        ToastUtil.toastLongMessage("全员禁言中");
                                        EventBus.getDefault().post("0");
                                    } else {
                                        EventBus.getDefault().post("1");
                                        ToastUtil.toastLongMessage("取消全员禁言");
                                    }

                                }
                            });
                        }
                        if (code == 403) {
                            Intent intent = new Intent();
                            intent.setAction("cn.twle.android.sendbroadcast.Setup.Token");
                            intent.setComponent(new ComponentName("com.pingmo.chengyan",
                                    "com.pingmo.chengyan.receiver.TokenFileBroadcastReceiver"));
                            sendBroadcast(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadMutedMember() {
        presenter.loadMutedMembers(groupInfo.getId(), new IUIKitCallback<List<GroupMemberInfo>>() {
            @Override
            public void onSuccess(List<GroupMemberInfo> data) {
                mutedMemberAdapter.setGroupMemberInfoList(data);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                ToastUtil.toastShortMessage(errCode + ", " + errMsg);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null) {
            List<String> selectedList = data.getStringArrayListExtra(TUIGroupConstants.Selection.LIST);
            if (selectedList != null && !selectedList.isEmpty()) {
                for (String userId : selectedList) {
                    presenter.muteGroupMember(groupInfo.getId(), userId, new IUIKitCallback<Void>() {
                        @Override
                        public void onSuccess(Void data) {
                            loadMutedMember();
                        }

                        @Override
                        public void onError(String module, int errCode, String errMsg) {
                            ToastUtil.toastShortMessage(errCode + ", " + errMsg);
                        }
                    });
                }
            }
        }
    }

    class MutedMemberAdapter extends RecyclerView.Adapter<MutedMemberAdapter.MutedMemberViewHolder> {

        private List<GroupMemberInfo> groupMemberInfoList;

        private SetGroupManagerActivity.OnItemLongClickListener onItemLongClickListener;

        public void setOnItemLongClickListener(SetGroupManagerActivity.OnItemLongClickListener onItemLongClickListener) {
            this.onItemLongClickListener = onItemLongClickListener;
        }

        public void onItemRemoved(GroupMemberInfo memberInfo) {
            int index = groupMemberInfoList.indexOf(memberInfo);
            groupMemberInfoList.remove(memberInfo);
            notifyItemRemoved(index);
        }

        public void setGroupMemberInfoList(List<GroupMemberInfo> groupMemberInfoList) {
            this.groupMemberInfoList = groupMemberInfoList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public MutedMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_manager_item, parent, false);
            return new MutedMemberViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MutedMemberViewHolder holder, @SuppressLint("RecyclerView") int position) {
            GroupMemberInfo groupMemberInfo = groupMemberInfoList.get(position);
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onItemLongClickListener != null) {
                        onItemLongClickListener.onClick(v, groupMemberInfo, position);
                    }
                    return false;
                }
            });
            String displayName = getDisplayName(groupMemberInfo);
            holder.managerName.setText(displayName);
            GlideEngine.loadUserIcon(holder.faceIcon, groupMemberInfo.getIconUrl());
        }

        @Override
        public int getItemCount() {
            if (groupMemberInfoList == null || groupMemberInfoList.isEmpty()) {
                return 0;
            }
            return groupMemberInfoList.size();
        }

        class MutedMemberViewHolder extends RecyclerView.ViewHolder {
            ShadeImageView faceIcon;
            TextView managerName;

            public MutedMemberViewHolder(@NonNull View itemView) {
                super(itemView);
                faceIcon = itemView.findViewById(R.id.group_manager_face);
                managerName = itemView.findViewById(R.id.group_manage_name);
            }
        }
    }

    private String getDisplayName(GroupMemberInfo groupMemberInfo) {
        String displayName = groupMemberInfo.getNameCard();
        if (TextUtils.isEmpty(displayName)) {
            displayName = groupMemberInfo.getNickName();
        }
        if (TextUtils.isEmpty(displayName)) {
            displayName = groupMemberInfo.getAccount();
        }
        return displayName;
    }

    interface OnItemLongClickListener {
        void onClick(View view, GroupMemberInfo groupMemberInfo);
    }

}