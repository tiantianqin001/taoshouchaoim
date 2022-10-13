package com.tencent.qcloud.tuikit.tuichat.ui.page;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;

import com.hjq.toast.ToastUtils;
import com.tencent.imsdk.v2.V2TIMGroupInfo;
import com.tencent.imsdk.v2.V2TIMGroupInfoResult;
import com.tencent.imsdk.v2.V2TIMGroupMemberFullInfo;
import com.tencent.imsdk.v2.V2TIMGroupMemberInfoResult;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.tencent.qcloud.tuicore.TUIConstants;
import com.tencent.qcloud.tuicore.TUICore;
import com.tencent.qcloud.tuicore.component.interfaces.IUIKitCallback;
import com.tencent.qcloud.tuicore.util.BackgroundTasks;
import com.tencent.qcloud.tuicore.util.ToastUtil;
import com.tencent.qcloud.tuikit.tuichat.TUIChatConstants;
import com.tencent.qcloud.tuikit.tuichat.bean.CardBean;
import com.tencent.qcloud.tuikit.tuichat.bean.ChatInfo;
import com.tencent.qcloud.tuikit.tuichat.bean.GroupInfo;
import com.tencent.qcloud.tuikit.tuichat.bean.message.TUIMessageBean;
import com.tencent.qcloud.tuikit.tuichat.bean.message.TextMessageBean;
import com.tencent.qcloud.tuikit.tuichat.net.Common;
import com.tencent.qcloud.tuikit.tuichat.net.OkHttp3_0Utils;
import com.tencent.qcloud.tuikit.tuichat.presenter.GroupChatPresenter;
import com.tencent.qcloud.tuikit.tuichat.ui.interfaces.OnItemClickListener;
import com.tencent.qcloud.tuikit.tuichat.util.ChatMessageParser;
import com.tencent.qcloud.tuikit.tuichat.util.EncryptUtil;
import com.tencent.qcloud.tuikit.tuichat.util.TUIChatLog;

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

public class TUIGroupChatFragment extends TUIBaseChatFragment {
    private static final String TAG = TUIGroupChatFragment.class.getSimpleName();

    private GroupChatPresenter presenter;
    private GroupInfo groupInfo;
    private static String SP_NAME = "huimiaomiao_share_date";
    private SharedPreferences sp;

    private String token;
    private String myUserId;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        TUIChatLog.i(TAG, "oncreate view " + this);

        baseView = super.onCreateView(inflater, container, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle == null) {
            return baseView;
        }
        groupInfo = (GroupInfo) bundle.getSerializable(TUIChatConstants.CHAT_INFO);
        if (groupInfo == null) {
            return baseView;
        }

        sp = getContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        myUserId = sp.getString("userId", "");

        myUserId = EncryptUtil.getInstance(getContext()).decrypt(myUserId);

        token = sp.getString("token", "");
        token = EncryptUtil.getInstance(getContext()).decrypt(token);

        initView();
        return baseView;
    }
    @Override
    protected void initView() {
        super.initView();
        chatView.setPresenter(presenter);
        presenter.setGroupInfo(groupInfo);
        chatView.setChatInfo(groupInfo);
        chatView.getMessageLayout().setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onMessageLongClick(View view, int position, TUIMessageBean messageBean) {
                //因为adapter中第一条为加载条目，位置需减1
                chatView.getMessageLayout().showItemPopMenu(position - 1, messageBean, view);
            }
            @Override
            public void onUserIconClick(View view, int position, TUIMessageBean messageBean) {
                if (null == messageBean) {
                    return;
                }

                boolean group = messageBean.isGroup();
                if (group){
                    //获取群保护状态
                    sp =   getContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
                    String token = sp.getString("token", "");

                    token = EncryptUtil.getInstance( getContext()).decrypt(token);
                    //获取群消息
                    String url = Common.GETGROUPINFO;

                    Map<String , String> headerParams = new LinkedHashMap<>();
                    headerParams.put("token", token);
                    Map<String , String> mapPatams = new LinkedHashMap<>();
                    mapPatams.put("groupId",messageBean.getGroupId()+"");

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
                                                    groupList.add(presenter.getChatInfo().getId());
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
                                                                            ChatInfo info = new ChatInfo();
                                                                            info.setId(messageBean.getSender());


                                                                            Bundle bundle = new Bundle();
                                                                            bundle.putString(TUIConstants.TUIChat.CHAT_ID, info.getId());
                                                                            bundle.putBoolean("isGroup",group);
                                                                            bundle.putBoolean("isAdmin",true);
                                                                            bundle.putInt("groupMemberProtect",groupMemberProtect);
                                                                            TUICore.startActivity("FriendProfileActivity", bundle);
                                                                            return;

                                                                        }else {
                                                                            //获取群管理员
                                                                            int filter = V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_FILTER_ADMIN; //管理员
                                                                            V2TIMManager.getGroupManager().getGroupMemberList(presenter.getChatInfo().getId(), filter, 0,
                                                                                    new V2TIMValueCallback<V2TIMGroupMemberInfoResult>(){
                                                                                        @Override
                                                                                        public void onSuccess(V2TIMGroupMemberInfoResult v2TIMGroupMemberInfoResult) {
                                                                                            List<V2TIMGroupMemberFullInfo> infoList = v2TIMGroupMemberInfoResult.getMemberInfoList();
                                                                                            for (V2TIMGroupMemberFullInfo fullInfo : infoList) {
                                                                                                Log.i(TAG, "onSuccess: "+fullInfo);
                                                                                                String userID = fullInfo.getUserID();
                                                                                                if (userID.equals(myUserId)){
                                                                                                    //当前设置了群管理员
                                                                                                    ChatInfo info = new ChatInfo();
                                                                                                    info.setId(messageBean.getSender());


                                                                                                    Bundle bundle = new Bundle();
                                                                                                    bundle.putString(TUIConstants.TUIChat.CHAT_ID, info.getId());
                                                                                                    bundle.putBoolean("isGroup",group);
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
                                            ChatInfo info = new ChatInfo();
                                            info.setId(messageBean.getSender());


                                            Bundle bundle = new Bundle();
                                            bundle.putString(TUIConstants.TUIChat.CHAT_ID, info.getId());
                                            bundle.putBoolean("isGroup",group);
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
                                    getContext(). sendBroadcast(intent);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }else {
                    ChatInfo info = new ChatInfo();
                    info.setId(messageBean.getSender());


                    Bundle bundle = new Bundle();
                    bundle.putString(TUIConstants.TUIChat.CHAT_ID, info.getId());
                    bundle.putBoolean("isGroup",group);
                    TUICore.startActivity("FriendProfileActivity", bundle);
                }






                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(chatView,InputMethodManager.SHOW_FORCED);

                imm.hideSoftInputFromWindow(chatView.getWindowToken(), 0); //强制隐藏键盘

            }

            @Override
            public void onMessageDoubleClick(View view, int position, TUIMessageBean messageBean) {
                String result_id = messageBean.getV2TIMMessage().getSender();
                String result_name = messageBean.getV2TIMMessage().getNickName();
                //长安发送专属红包
                Intent intent = new Intent(getContext(), ExclusiveRedActivity.class);
                //接收人id
                intent.putExtra("recipientId",result_id);
                intent.putExtra("exclusiveRed",true);

                String finalToken = token;
                ExclusiveRedActivity.mCallBack = new IUIKitCallback() {
                    @Override
                    public void onSuccess(Object data) {
                        super.onSuccess(data);
                        Log.i(TAG, "onSuccess: "+data);
                        String userId = result_id;

                        CardBean cardBean = (CardBean) data;
                        String cardPassword = cardBean.cardPassword;
                        String money = cardBean.money;
                        String note = cardBean.note;
                        //发送群红包
                        String url = Common.FAQUN_RED_ENVELOPE;
                        Map<String, String> mapParams = new LinkedHashMap<>();
                        Map<String , String> headerParams = new LinkedHashMap<>();
                        headerParams.put("token", finalToken);
                        mapParams.put("money", money);
                        mapParams.put("notes", "定向红包"+","+result_name+","+userId);
                        mapParams.put("password", cardPassword);
                        mapParams.put("number", "1");
                        //   mapParams.put("toUserId", userId);
                        mapParams.put("groupId", messageBean.getGroupId());
                        OkHttp3_0Utils.getInstance().asyncBodyOkHttp(url, mapParams,headerParams, new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                Message message = Message.obtain();
                                message.what = Common.Interface_err;
                                message.obj = "当前服务请求失败";
                                Log.i(TAG, "onFailure: "+e.getMessage());
                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                String content = response.body().string();
                                Log.i(TAG, "onResponse: "+content);
                                try {
                                    JSONObject jsonObject = new JSONObject(content);
                                    String code = jsonObject.optString("code");
                                    if (code.equals("200")){
                                    }else if (code.equals("9000")){
                                        //请设置支付密码
                                        String msg = jsonObject.optString("msg");
                                        if (msg.equals("请设置支付密码")){
                                            Intent intent = new Intent();
                                            intent.setAction("cn.twle.android.sendbroadcast.Setup.payment.password");
                                            intent.setComponent(new ComponentName("com.pingmo.chengyan",
                                                    "com.pingmo.chengyan.receiver.PaymentBroadcastReceiver"));
                                            getContext().sendBroadcast(intent);
                                        }
                                        ToastUtils.show(msg);

                                    }else {
                                        if (code.equals("403")){
                                            Intent intent = new Intent();
                                            intent.setAction("cn.twle.android.sendbroadcast.Setup.Token");
                                            intent.setComponent(new ComponentName("com.pingmo.chengyan",
                                                    "com.pingmo.chengyan.receiver.TokenFileBroadcastReceiver"));
                                            getContext().sendBroadcast(intent);
                                        }

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        });




                    }

                    @Override
                    public void onError(int errCode, String errMsg, Object data) {
                        super.onError(errCode, errMsg, data);
                    }
                };


                getActivity().startActivityForResult(intent,12);
            }

            @Override
            public void onUserIconLongClick(View view, int position, TUIMessageBean messageBean) {
                String result_id = messageBean.getV2TIMMessage().getSender();
                String result_name = messageBean.getV2TIMMessage().getNickName();
                chatView.getInputLayout().addInputText(result_name, result_id);


            }

            @Override
            public void onReEditRevokeMessage(View view, int position, TUIMessageBean messageInfo) {
                if (messageInfo == null) {
                    return;
                }
                int messageType = messageInfo.getMsgType();
                if (messageType == V2TIMMessage.V2TIM_ELEM_TYPE_TEXT){
                    chatView.getInputLayout().appendText(messageInfo.getV2TIMMessage().getTextElem().getText());
                } else {
                    TUIChatLog.e(TAG, "error type: " + messageType);
                }
            }

            @Override
            public void onRecallClick(View view, int position, TUIMessageBean messageInfo) {

            }

            @Override
            public void onTextSelected(View view, int position, TUIMessageBean messageInfo) {
                chatView.getMessageLayout().setSelectedPosition(position);
                chatView.getMessageLayout().showItemPopMenu(position - 1, messageInfo, view);
            }
        });
    }

    public void setPresenter(GroupChatPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public GroupChatPresenter getPresenter() {
        return presenter;
    }

    @Override
    public ChatInfo getChatInfo() {
        return groupInfo;
    }
}
