package com.tencent.qcloud.tuikit.tuigroup.ui.page;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import com.tencent.imsdk.v2.V2TIMGroupInfo;
import com.tencent.imsdk.v2.V2TIMGroupInfoResult;
import com.tencent.imsdk.v2.V2TIMGroupMemberFullInfo;
import com.tencent.imsdk.v2.V2TIMGroupMemberInfoResult;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.tencent.qcloud.tuicore.TUIConfig;
import com.tencent.qcloud.tuicore.TUICore;
import com.tencent.qcloud.tuicore.component.activities.ImageSelectActivity;
import com.tencent.qcloud.tuicore.component.fragments.BaseFragment;
import com.tencent.qcloud.tuicore.component.interfaces.IUIKitCallback;
import com.tencent.qcloud.tuicore.util.ImageUtil;
import com.tencent.qcloud.tuicore.util.ScreenUtil;
import com.tencent.qcloud.tuicore.util.ToastUtil;
import com.tencent.qcloud.tuikit.tuigroup.R;
import com.tencent.qcloud.tuikit.tuigroup.TUIGroupService;
import com.tencent.qcloud.tuikit.tuigroup.TUIGroupConstants;
import com.tencent.qcloud.tuikit.tuigroup.bean.GroupInfo;
import com.tencent.qcloud.tuikit.tuigroup.net.Common;
import com.tencent.qcloud.tuikit.tuigroup.net.EncryptUtil;
import com.tencent.qcloud.tuikit.tuigroup.net.OkHttp3_0Utils;
import com.tencent.qcloud.tuikit.tuigroup.presenter.GroupInfoPresenter;
import com.tencent.qcloud.tuikit.tuigroup.ui.view.GroupInfoLayout;
import com.tencent.qcloud.tuikit.tuigroup.ui.interfaces.IGroupMemberListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class GroupInfoFragment extends BaseFragment {
    private static final int CHOOSE_AVATAR_REQUEST_CODE = 101;
    private static final String TAG = "GroupInfoFragment";

    private View baseView;
    private GroupInfoLayout groupInfoLayout;

    private String groupId;

    private GroupInfoPresenter groupInfoPresenter = null;
    private Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        baseView = inflater.inflate(R.layout.group_info_fragment, container, false);
        initView();
        return baseView;
    }

    private void initView() {
        Bundle bundle = getArguments();
        if (bundle == null) {
            ToastUtil.toastShortMessage("groupId is empty. bundle is null");
            return;
        }
        groupId = bundle.getString(TUIGroupConstants.Group.GROUP_ID);
        groupInfoLayout = baseView.findViewById(R.id.group_info_layout);
        // 新建 presenter 与 layout 互相绑定
        groupInfoPresenter = new GroupInfoPresenter(groupInfoLayout);
        groupInfoLayout.setGroupInfoPresenter(groupInfoPresenter);
        groupInfoLayout.setOnModifyGroupAvatarListener(new OnModifyGroupAvatarListener() {
            @Override
            public void onModifyGroupAvatar(String originAvatarUrl) {
                ArrayList<String> faceList = new ArrayList<>();
                for (int i = 0; i < TUIGroupConstants.GROUP_FACE_COUNT; i++) {
                    faceList.add(String.format(TUIGroupConstants.GROUP_FACE_URL, (i + 1) + ""));
                }

                Intent intent = new Intent(getContext(), ImageSelectActivity.class);
                intent.putExtra(ImageSelectActivity.TITLE, getResources().getString(R.string.group_choose_avatar));
                intent.putExtra(ImageSelectActivity.SPAN_COUNT, 4);
                intent.putExtra(ImageSelectActivity.PLACEHOLDER, R.drawable.core_default_user_icon_light);
                intent.putExtra(ImageSelectActivity.ITEM_WIDTH, ScreenUtil.dip2px(77));
                intent.putExtra(ImageSelectActivity.ITEM_HEIGHT, ScreenUtil.dip2px(77));
                intent.putExtra(ImageSelectActivity.DATA, faceList);
                intent.putExtra(ImageSelectActivity.SELECTED, originAvatarUrl);
                startActivityForResult(intent, CHOOSE_AVATAR_REQUEST_CODE);
            }
        });
        groupInfoLayout.loadGroupInfo(groupId);
        groupInfoLayout.setRouter(new IGroupMemberListener() {
            @Override
            public void forwardListMember(GroupInfo info) {
                Intent intent = new Intent(getContext(), GroupMemberActivity.class);
                intent.putExtra(TUIGroupConstants.Group.GROUP_INFO, info);
                startActivity(intent);
            }

            @Override
            public void forwardAddMember(GroupInfo info) {
                Bundle param = new Bundle();
                param.putString(TUIGroupConstants.Group.GROUP_ID, info.getId());
                param.putBoolean(TUIGroupConstants.Selection.SELECT_FRIENDS, true);
                param.putSerializable(TUIGroupConstants.Selection.ADD_FRIENDS, info);
                TUICore.startActivity(GroupInfoFragment.this, "StartGroupMemberSelectActivity", param, 1);
            }

            @Override
            public void forwardDeleteMember(GroupInfo info) {
                Bundle param = new Bundle();
                param.putString(TUIGroupConstants.Group.GROUP_ID, info.getId());
                param.putBoolean(TUIGroupConstants.Selection.SELECT_FOR_CALL, true);
              //  param.putSerializable(TUIGroupConstants.Selection.ADD_FRIENDS, info);
                TUICore.startActivity(GroupInfoFragment.this, "StartGroupMemberSelectActivity", param, 2);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 3) {
            List<String> friends = (List<String>) data.getSerializableExtra(TUIGroupConstants.Selection.LIST);
            if (requestCode == 1) {
                inviteGroupMembers(friends);
            } else if (requestCode == 2) {
                deleteGroupMembers(friends);
            }
        } else if (requestCode == CHOOSE_AVATAR_REQUEST_CODE && resultCode == ImageSelectActivity.RESULT_CODE_SUCCESS) {
            if (data != null) {
                String avatarUrl = data.getStringExtra(ImageSelectActivity.DATA);
                modifyGroupAvatar(avatarUrl);
            }
        }
    }

    private void modifyGroupAvatar(String avatarUrl) {
        groupInfoLayout.modifyGroupAvatar(avatarUrl);
    }

    private void deleteGroupMembers(List<String> friends) {
        if (friends != null && friends.size() > 0) {
            if (groupInfoPresenter != null) {
                //移除群聊
                sp =   getContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
                String token = sp.getString("token", "");

                token = EncryptUtil.getInstance(getContext()).decrypt(token);
                String url = Common.MOVE_GROUP;

                Map<String , String> headerParams = new LinkedHashMap<>();
                headerParams.put("token", token);
                JSONArray jsonArray = new JSONArray();
                for (String s : friends) {
                    jsonArray.put(Integer.valueOf(s));

                }
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("groupId", Integer.valueOf(groupId));
                    jsonObject.put("members", jsonArray);
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
                                if (code == 200){
                                    //移除本地图片重新生成
                                    ImageUtil.setGroupConversationAvatar("group_"+groupId,"");
                                    //删除文件
                                    File file = new File(TUIConfig.getImageBaseDir() + "group_"+groupId);
                                    if (file.exists() && file.isFile()) {
                                        boolean delete = file.delete();
                                        Log.i(TAG, "onResponse: "+delete);
                                    }

                                    groupInfoLayout.loadGroupInfo(groupId);


                                }  else if (code==403){
                                    Intent intent = new Intent();
                                    intent.setAction("cn.twle.android.sendbroadcast.Setup.Token");
                                    intent.setComponent(new ComponentName("com.pingmo.chengyan",
                                            "com.pingmo.chengyan.receiver.TokenFileBroadcastReceiver"));
                                    getContext().sendBroadcast(intent);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }


             /*   presenter.deleteGroupMembers(mGroupInfo.getId(), friends, new IUIKitCallback<List<String>>() {
                    @Override
                    public void onSuccess(List<String> data) {
                        presenter.loadGroupInfo(mGroupInfo.getId());
                    }

                    @Override
                    public void onError(String module, int errCode, String errMsg) {

                    }
                });*/
            }



           /* if (groupInfoPresenter != null) {
                groupInfoPresenter.deleteGroupMembers(groupId, friends, new IUIKitCallback<List<String>>() {
                    @Override
                    public void onSuccess(List<String> data) {
                        groupInfoPresenter.loadGroupInfo(groupId);
                    }

                    @Override
                    public void onError(String module, int errCode, String errMsg) {

                    }
                });
            }*/
        }
    }
    private static String SP_NAME = "huimiaomiao_share_date";
    private SharedPreferences sp;
    private String myUserId;
    private boolean isAdmin = false;
    private void inviteGroupMembers(List<String> friends) {

        //邀请入群

        if (friends != null && friends.size() > 0) {
            //添加组成员也就是拉人进群
            sp =  getContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
            myUserId = sp.getString("userId", "");
            myUserId = EncryptUtil.getInstance(getContext()).decrypt(myUserId);
            String token = sp.getString("token", "");

            token = EncryptUtil.getInstance(getContext()).decrypt(token);
            String url = Common.INVITE_GROUP;

            Map<String , String> headerParams = new LinkedHashMap<>();
            headerParams.put("token", token);
            JSONArray jsonArray = new JSONArray();
            for (String s : friends) {
                jsonArray.put(Integer.valueOf(s));

            }
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("groupId", Integer.valueOf(groupId));
                jsonObject.put("members", jsonArray);
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
                            if (code == 200){
                                //判断管理员审核是不是打开 1 开启  0 关闭
                             //   cheakManageIsOpen();
                                //移除本地图片重新生成
                                ImageUtil.setGroupConversationAvatar("group_"+groupId,"");
                                //删除文件
                                File file = new File(TUIConfig.getImageBaseDir() + "group_"+groupId);
                                if (file.exists() && file.isFile()) {
                                    file.delete();
                                }
                                groupInfoLayout.loadGroupInfo(groupId);
                                String msg = jsonObject1.optString("msg");
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.toastLongMessage(msg);
                                    }
                                });


                            }  else if (code==403){
                                Intent intent = new Intent();
                                intent.setAction("cn.twle.android.sendbroadcast.Setup.Token");
                                intent.setComponent(new ComponentName("com.pingmo.chengyan",
                                        "com.pingmo.chengyan.receiver.TokenFileBroadcastReceiver"));
                                getContext().sendBroadcast(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
/*
            groupInfoPresenter.inviteGroupMembers(groupId, friends, new IUIKitCallback<Object>() {
                @Override
                public void onSuccess(Object data) {
                    if (data instanceof String) {
                        ToastUtil.toastLongMessage(data.toString());
                    } else {
                        ToastUtil.toastLongMessage(TUIGroupService.getAppContext().getString(R.string.invite_suc));
                    }
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    ToastUtil.toastLongMessage(TUIGroupService.getAppContext().getString(R.string.invite_fail) + errCode + "=" + errMsg);
                }
            });*/
        }
    }
    //判断群管理员开关是不是打开了
    private void cheakManageIsOpen() {
        sp =   getContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        String token = sp.getString("token", "");

        token = EncryptUtil.getInstance( getContext()).decrypt(token);
        //获取群消息
        String url = Common.GETGROUPINFO;

        Map<String , String> headerParams = new LinkedHashMap<>();
        headerParams.put("token", token);
        Map<String , String> mapPatams = new LinkedHashMap<>();
        mapPatams.put("groupId",groupId);

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
                            int invitationStatus = optJSONObject.optInt("invitationStatus");
                            if (invitationStatus == 0){
                                //拉人需要授权
                                ToastUtil.toastLongMessage(TUIGroupService.getAppContext().getString(R.string.request_wait));
                            }else if (invitationStatus == 1){
                                //可以直接拉人
                                ToastUtil.toastLongMessage(TUIGroupService.getAppContext().getString(R.string.invite_suc));


                            }
                        }

                    }
                    if (code == 403){
                        Intent intent = new Intent();
                        intent.setAction("cn.twle.android.sendbroadcast.Setup.Token");
                        intent.setComponent(new ComponentName("com.pingmo.chengyan",
                                "com.pingmo.chengyan.receiver.TokenFileBroadcastReceiver"));
                       getContext(). sendBroadcast(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    public void changeGroupOwner(String newOwnerId) {
        //转账群主
        sp =  getContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);

        String token = sp.getString("token", "");

        token = EncryptUtil.getInstance(getContext()).decrypt(token);
        String url = Common.TRANSFER_GROUP_OWNER;

        Map<String , String> headerParams = new LinkedHashMap<>();
        headerParams.put("token", token);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("groupId", Integer.valueOf(groupId));
            jsonObject.put("userId", newOwnerId);
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
                        if (code == 200){
                            groupInfoLayout.loadGroupInfo(groupId);

                        }  else if (code==403){
                            Intent intent = new Intent();
                            intent.setAction("cn.twle.android.sendbroadcast.Setup.Token");
                            intent.setComponent(new ComponentName("com.pingmo.chengyan",
                                    "com.pingmo.chengyan.receiver.TokenFileBroadcastReceiver"));
                            getContext().sendBroadcast(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }


      /*  groupInfoPresenter.transferGroupOwner(groupId, newOwnerId, new IUIKitCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                groupInfoLayout.loadGroupInfo(groupId);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {

            }
        });*/
    }


    public interface OnModifyGroupAvatarListener {
        void onModifyGroupAvatar(String originAvatarUrl);
    }

}
