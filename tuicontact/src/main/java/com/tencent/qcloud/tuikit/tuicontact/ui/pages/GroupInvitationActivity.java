package com.tencent.qcloud.tuikit.tuicontact.ui.pages;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.tencent.imsdk.v2.V2TIMGroupApplication;
import com.tencent.imsdk.v2.V2TIMGroupApplicationResult;
import com.tencent.imsdk.v2.V2TIMGroupInfoResult;
import com.tencent.imsdk.v2.V2TIMGroupMemberFullInfo;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.tencent.qcloud.tuicore.component.TitleBarLayout;
import com.tencent.qcloud.tuicore.component.activities.BaseLightActivity;
import com.tencent.qcloud.tuicore.component.interfaces.ITitleBarLayout;
import com.tencent.qcloud.tuicore.component.interfaces.IUIKitCallback;
import com.tencent.qcloud.tuicore.util.ErrorMessageConverter;
import com.tencent.qcloud.tuikit.tuicontact.R;
import com.tencent.qcloud.tuikit.tuicontact.TUIContactConstants;
import com.tencent.qcloud.tuikit.tuicontact.TUIContactService;
import com.tencent.qcloud.tuikit.tuicontact.bean.ContactItemBean;
import com.tencent.qcloud.tuikit.tuicontact.bean.FriendApplicationBean;

import com.tencent.qcloud.tuikit.tuicontact.bean.GroupNewLoadBean;
import com.tencent.qcloud.tuikit.tuicontact.bean.GroupSelectBean;
import com.tencent.qcloud.tuikit.tuicontact.net.Common;
import com.tencent.qcloud.tuikit.tuicontact.presenter.NewFriendPresenter;
import com.tencent.qcloud.tuikit.tuicontact.ui.OkHttp3_0Utils;
import com.tencent.qcloud.tuikit.tuicontact.ui.interfaces.INewFriendActivity;
import com.tencent.qcloud.tuikit.tuicontact.ui.view.NewFriendListAdapter;
import com.tencent.qcloud.tuikit.tuicontact.util.EncryptUtil;
import com.tencent.qcloud.tuikit.tuicontact.util.TUIContactLog;
import com.tencent.qcloud.tuikit.tuigroup.util.TUIGroupLog;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class GroupInvitationActivity extends BaseLightActivity implements INewFriendActivity {
    private static final String TAG = GroupInvitationActivity.class.getSimpleName();
    private TitleBarLayout mTitleBar;
    private RecyclerView mNewFriendLv;
    private NewFriendListAdapter mAdapter;
    private TextView notFoundTip;
    private String token;
    private boolean canManagerGroup;
    private String userId;

    private List<GroupSelectBean> groupSelectBeanList = new ArrayList<>();

    private Handler handler = new Handler(){
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    //显示数据
                    if (groupSelectBeanList.size() == 0){
                        mNewFriendLv.setVisibility(View.GONE);
                        notFoundTip.setVisibility(View.VISIBLE);
                    }else {
                        mNewFriendLv.setVisibility(View.VISIBLE);
                        notFoundTip.setVisibility(View.GONE);
                        GroupInvitationAdapter groupInvitationAdapter = new GroupInvitationAdapter(GroupInvitationActivity.this,groupSelectBeanList,token);
                        mNewFriendLv.setLayoutManager(new LinearLayoutManager(GroupInvitationActivity.this));
                        mNewFriendLv.setAdapter(groupInvitationAdapter);
                    }


                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置沉静状态栏
        ImmersionBar.with(this)
                .transparentBar()
                .init();
        setContentView(R.layout.contact_group_new_friend_activity);
        init();
        sp = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        token = sp.getString("token", "");
        token = EncryptUtil.getInstance(GroupInvitationActivity.this).decrypt(token);
        userId = sp.getString("userId", "");
        userId = EncryptUtil.getInstance(GroupInvitationActivity.this).decrypt(userId);
        groupSelectBeanList.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initPendency();
    }
    private void init() {
        mTitleBar = findViewById(R.id.new_friend_titlebar);
        mTitleBar.setBackgroundColor(getResources().getColor(R.color.white));
        mTitleBar.getLeftIcon().setBackground(getResources().getDrawable(R.drawable.back));
        mTitleBar.setTitle("群通知", ITitleBarLayout.Position.MIDDLE);
        mTitleBar.getMiddleTitle().setTextColor(getResources().getColor(R.color.split_lint_friend));
        mTitleBar.getMiddleTitle().setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mTitleBar.setOnLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitleBar.getRightIcon().setVisibility(View.GONE);

        mNewFriendLv = findViewById(R.id.new_friend_list);
        notFoundTip = findViewById(R.id.not_found_tip);
    }
    private static String SP_NAME = "huimiaomiao_share_date";
    private SharedPreferences sp;
    int count =0;
    private void initPendency() {
        //获取数据 群邀请列表
        String url = Common.GROUP_INVITATION_LIST;
        Map<String, String> mapParams = new LinkedHashMap<>();

        Map<String, String> headerParams = new LinkedHashMap<>();
        headerParams.put("token", token);
        mapParams.put("pageNum","1");
        mapParams.put("pageSize","50");
        OkHttp3_0Utils.getInstance().asyncGetHeaderOkHttp(url, mapParams, headerParams,
                new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    }
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        String content = response.body().string();
                        Log.i(TAG, "onResponse: "+content);
                        try {
                            JSONObject jsonObject = new JSONObject(content);
                            int code = jsonObject.optInt("code");
                            if (code == 200){
                                Gson gson = new Gson();
                                GroupNewLoadBean groupNewLoadBean = gson.fromJson(content, GroupNewLoadBean.class);
                                GroupNewLoadBean.DataDTO data = groupNewLoadBean.getData();
                                if (data!=null){

                                    List<GroupNewLoadBean.DataDTO.RowsDTO> rows = data.getRows();
                                    if (rows!=null && rows.size()>0){
                                        for (GroupNewLoadBean.DataDTO.RowsDTO rowsDTO : rows) {
                                            String groupId = rowsDTO.getGroupId()+"";
                                            count++;
                                            List<String> groupList = new ArrayList<>();
                                            groupList.add(groupId);

                                            V2TIMManager.getGroupManager().getGroupsInfo(groupList, new V2TIMValueCallback<List<V2TIMGroupInfoResult>>() {
                                                @Override
                                                public void onError(int code, String desc) {
                                                    TUIGroupLog.e(TAG, "loadGroupPublicInfo failed, code: " + code + "|desc: " + ErrorMessageConverter.convertIMError(code, desc));
                                                    Message message = Message.obtain();
                                                    message.what = 1;
                                                    message.obj = groupSelectBeanList;
                                                    handler.sendMessage(message);
                                                }

                                                @Override
                                                public void onSuccess(List<V2TIMGroupInfoResult> v2TIMGroupInfoResults) {
                                                    if (v2TIMGroupInfoResults.size() > 0) {
                                                        V2TIMGroupInfoResult infoResult = v2TIMGroupInfoResults.get(0);
                                                        TUIGroupLog.i(TAG, infoResult.toString());
                                                        String owner = infoResult.getGroupInfo().getOwner();

                                                        int role = infoResult.getGroupInfo().getRole();
                                                        //是不是管理员
                                                        boolean  canManagerGroup = role == V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_ROLE_OWNER
                                                                || role == V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_ROLE_ADMIN
                                                                || infoResult.getGroupInfo().getGroupType() == V2TIMManager.GROUP_TYPE_WORK;

                                                        //owner群主的id  canManagerGroup 是不是管理员
                                                        Log.i(TAG, "onSuccess: "+canManagerGroup+"...."+owner+"....."+ userId);
                                                        int status = rowsDTO.getStatus();
                                                            if (userId.equals(owner) || canManagerGroup){
                                                                //说明是群组或群管理员
                                                                GroupSelectBean groupSelectBean = new GroupSelectBean();
                                                                groupSelectBean.setId(rowsDTO.getId());
                                                                groupSelectBean.setGroupId(rowsDTO.getGroupId()+"");
                                                                groupSelectBean.setStatus(status);
                                                                groupSelectBean.setGroupName(rowsDTO.getGroupName());
                                                                groupSelectBean.setUserNickName(rowsDTO.getUserNickName());
                                                                groupSelectBean.setOperatorName(rowsDTO.getOperatorName());
                                                                groupSelectBean.setInviterName(rowsDTO.getInviterName());
                                                                groupSelectBeanList.add(groupSelectBean);

                                                            }


                                                        //已经遍历完了
                                                        if (count == rows.size()){
                                                            Message message = Message.obtain();
                                                            message.what = 1;
                                                            message.obj = groupSelectBeanList;
                                                            handler.sendMessage(message);
                                                        }
                                                    }
                                                }
                                            });
                                        }

                                    }
                                }

                            } else if (code == 403){
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

    }

    @Override
    public void onDataSourceChanged(List<FriendApplicationBean> dataSource) {
        TUIContactLog.i(TAG, "getFriendApplicationList success");
//        if (dataSource == null || dataSource.isEmpty()) {
//            notFoundTip.setVisibility(View.VISIBLE);
//        } else {
//            notFoundTip.setVisibility(View.GONE);
//        }
//        mNewFriendLv.setVisibility(View.VISIBLE);
//        mAdapter = new NewFriendListAdapter(GroupInvitationActivity.this, R.layout.contact_new_friend_item, dataSource);
//        mAdapter.setPresenter(presenter);
//        mNewFriendLv.setAdapter(mAdapter);
//        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void finish() {
        super.finish();
    }

}
