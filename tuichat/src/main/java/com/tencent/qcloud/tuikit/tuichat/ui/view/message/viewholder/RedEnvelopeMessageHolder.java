package com.tencent.qcloud.tuikit.tuichat.ui.view.message.viewholder;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.hjq.toast.ToastUtils;
import com.tencent.qcloud.tuikit.tuichat.R;
import com.tencent.qcloud.tuikit.tuichat.TUIChatConstants;
import com.tencent.qcloud.tuikit.tuichat.TUIChatService;
import com.tencent.qcloud.tuikit.tuichat.adapter.GroupAdapter;
import com.tencent.qcloud.tuikit.tuichat.bean.GetReadBean;
import com.tencent.qcloud.tuikit.tuichat.bean.GroupRedPacketDetailBean;
import com.tencent.qcloud.tuikit.tuichat.bean.ReceivedGroupBean;
import com.tencent.qcloud.tuikit.tuichat.bean.RedPacketDetailBean;
import com.tencent.qcloud.tuikit.tuichat.bean.message.LocationMessageBean;
import com.tencent.qcloud.tuikit.tuichat.bean.message.RedenvelopeBean;
import com.tencent.qcloud.tuikit.tuichat.bean.message.TUIMessageBean;
import com.tencent.qcloud.tuikit.tuichat.interfaces.CallBackStatus;
import com.tencent.qcloud.tuikit.tuichat.interfaces.CallBackUtils;
import com.tencent.qcloud.tuikit.tuichat.net.Common;
import com.tencent.qcloud.tuikit.tuichat.net.OkHttp3_0Utils;
import com.tencent.qcloud.tuikit.tuichat.ui.page.GeographicMapStaileActivity;
import com.tencent.qcloud.tuikit.tuichat.ui.page.GroupRedPacketDetailsActivity;
import com.tencent.qcloud.tuikit.tuichat.ui.page.RedPacketDetailsActivity;
import com.tencent.qcloud.tuikit.tuichat.ui.page.RedPacketDetailsMeActivity;
import com.tencent.qcloud.tuikit.tuichat.util.ChatMessageBuilder;
import com.tencent.qcloud.tuikit.tuichat.util.DeviceUtil;
import com.tencent.qcloud.tuikit.tuichat.util.EncryptUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.net.IDN;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.LinearLayoutManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RedEnvelopeMessageHolder extends MessageContentHolder implements CallBackStatus {
    private static final String TAG = "RedEnvelope";
    public String desc;
    public String greetings;
    private RelativeLayout rl_base;
    private ImageView iv_red_envelope_received;
    private String token;
    private String myUserId;
    private List<ReceivedGroupBean> receivedGroupBeanList = new ArrayList<>();

    private long redPacketId;

    private static Map<String,TUIMessageBean> maps = new HashMap<>();



    private Handler handler = new Handler();
    private TextView tv_bottom_node;

    public RedEnvelopeMessageHolder(View itemView) {
        super(itemView);

        sp = itemView.getContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        token = sp.getString("token", "");
        token = EncryptUtil.getInstance(itemView.getContext()).decrypt(token);
        myUserId = sp.getString("userId", "0");
        myUserId = EncryptUtil.getInstance(itemView.getContext()).decrypt(myUserId);
        redEnvelopes.clear();

        CallBackUtils.setmCallBack(this);
    }

    @Override
    public int getVariableLayout() {
        return R.layout.chat_red_layout;
    }

    @Override
    public void layoutVariableViews(TUIMessageBean msg, int position) {

        msgArea.setBackground(null);
        msgArea.setPadding(0, 0, 0, 0);

        rl_base = itemView.findViewById(R.id.rl_base);
        iv_red_envelope_received = itemView.findViewById(R.id.iv_red_envelope_received);
        tv_bottom_node = itemView.findViewById(R.id.tv_bottom_node);
        TextView tv_top_node = itemView.findViewById(R.id.tv_top_node);
        if (msg instanceof RedenvelopeBean) {
            //?????????????????????userid
            String money = ((RedenvelopeBean) msg).money;
            redPacketId = ((RedenvelopeBean) msg).redPacketId;
            String notes = ((RedenvelopeBean) msg).notes;
            if (!TextUtils.isEmpty(notes)){
                if (!notes.contains("????????????")){
                    tv_top_node.setText(notes);
                }

            }

            Log.i(TAG, "layoutVariableViews: "+notes);
            maps.put(redPacketId+"",msg);
            if (notes.contains("????????????")){
                tv_bottom_node.setVisibility(View.VISIBLE);
                String[] strings = notes.split(",");
                if (strings!=null && strings.length == 3 && msg.isGroup()){
                 String ninkName =   strings[1];
                    tv_bottom_node.setText("???"+ninkName+"???????????????");

                    //???????????????
                    String cloudCustomData = msg.getV2TIMMessage().getCloudCustomData();

                    if (TextUtils.isEmpty(cloudCustomData)){
                        //????????????????????????

                        if (msg.getV2TIMMessage().isSelf()) {
                            //????????????????????????;
                            rl_base.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.envelope_background_two_right));
                        }else {
                            //???????????????
                            rl_base.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.envelope_background_two_left));
                        }
                        iv_red_envelope_received.setImageDrawable(itemView.getContext().getResources().getDrawable(R.drawable.no_red_envelope));

                    }else {
                        //????????????????????????????????????????????????
                        try {
                            JSONObject jsonObject = new JSONObject(cloudCustomData);
                            int status = jsonObject.optInt("status");
                            //0 ????????? 1 ?????????  2 ?????????
                            if (status!=0){
                                if (msg.getV2TIMMessage().isSelf()) {
                                    //????????????????????????;
                                    rl_base.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.red_envelope_received_two_right));

                                }else {
                                    rl_base.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.red_envelope_received_two_left));
                                }
                                iv_red_envelope_received.setImageDrawable(itemView.getContext().getResources().getDrawable(R.drawable.red_envelope_received_red));
                            }else {
                                //????????????????????????
                                if (msg.getV2TIMMessage().isSelf()) {
                                    //????????????????????????;
                                    rl_base.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.envelope_background_two_right));
                                }else {
                                    //???????????????
                                    rl_base.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.envelope_background_two_left));
                                }
                                iv_red_envelope_received.setImageDrawable(itemView.getContext().getResources().getDrawable(R.drawable.no_red_envelope));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    //????????????????????????
                    String url = Common.GROUP_RED_ENVELOPE_NEWS;
                    Map<String, String> mapParams = new LinkedHashMap<>();
                    Map<String, String> headerParams = new LinkedHashMap<>();
                    headerParams.put("token", token);
                    mapParams.put("redPacketId", redPacketId + "");
                    OkHttp3_0Utils.getInstance().asyncGetHeaderOkHttp(url, mapParams, headerParams, new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Message message = Message.obtain();
                            message.what = Common.Interface_err;
                            message.obj = "????????????????????????";
                            Log.i(TAG, "onFailure: " + e.getMessage());
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            String content = response.body().string();
                            Log.i(TAG, "onResponse: " + content);
                            try {
                                JSONObject jsonObject = new JSONObject(content);
                                String code = jsonObject.optString("code");
                                if (code.equals("200")) {
                                    //??????????????????
                                    GroupRedPacketDetailBean groupRedPacketDetailBean = new Gson().fromJson(content, GroupRedPacketDetailBean.class);
                                    GroupRedPacketDetailBean.DataDTO data = groupRedPacketDetailBean.getData();
                                    if (data != null) {
                                        //   ??????:0-?????????;1-?????????;2-??????
                                        int status = data.getStatus();

                                        List<GroupRedPacketDetailBean.DataDTO.MembersDTO> members = data.getMembers();
                                        //????????????????????????
                                        rl_base.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (status == 0) {
                                                    //??????????????????????????????????????? ???????????????????????????  ???????????????????????????
                                                    String cloudCustomData = msg.getV2TIMMessage().getCloudCustomData();

                                                    try {
                                                        String userId ="";
                                                        if (!TextUtils.isEmpty(cloudCustomData)){
                                                            JSONObject jsonObject1 = new JSONObject(cloudCustomData);

                                                            userId = jsonObject1.optString("userId");
                                                        }
                                                        //?????????????????????????????????
                                                        if (members!=null && members.size()>0){
                                                            for (GroupRedPacketDetailBean.DataDTO.MembersDTO member : members) {
                                                                int id = member.getUserId();
                                                                if (myUserId.equals(String.valueOf(id))){
                                                                    grabRedEnvelop(redPacketId + "", groupRedPacketDetailBean);
                                                                    return;
                                                                }
                                                            }

                                                        }

                                                        if (TextUtils.isEmpty(cloudCustomData) || TextUtils.isEmpty(userId) || !myUserId.equals(userId)) {
                                                            //???????????????????????????
                                                            Intent intent = new Intent(itemView.getContext(), RedPacketDetailsMeActivity.class);
                                                            intent.putExtra("redPacketId", redPacketId + "");
                                                            intent.putExtra("faceUrl",data.getHeadImage());
                                                            intent.putExtra("nickName",data.getNickName());
                                                            intent.putExtra("money",data.getMoney());
                                                            intent.putExtra("isGroup", true);
                                                            if (notes.contains("????????????")){
                                                                String[] strings = notes.split(",");
                                                                if (strings!=null && strings.length == 3 && msg.isGroup()){
                                                                   String receiverId = strings[2];
                                                                   String exclusiveName = strings[1];
                                                                    intent.putExtra("receiverId", receiverId);
                                                                    intent.putExtra("exclusiveName", exclusiveName);
                                                                }
                                                            }
                                                            itemView.getContext().startActivity(intent);

                                                        } else {
                                                            grabRedEnvelop(redPacketId + "", groupRedPacketDetailBean);
                                                        }

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }


                                                } else if (status == 1) {
                                                    //?????????
                                                    receivedGroupBeanList.clear();
                                                    GroupRedPacketDetailBean.DataDTO data1 = groupRedPacketDetailBean.getData();
                                                    if (data1 != null) {
                                                        List<GroupRedPacketDetailBean.DataDTO.MembersDTO> members = data1.getMembers();
                                                        if (members != null && members.size() > 0) {

                                                            for (GroupRedPacketDetailBean.DataDTO.MembersDTO member : members) {
                                                                ReceivedGroupBean receivedGroupBean = new ReceivedGroupBean();
                                                                receivedGroupBean.setHeadImage(member.getHeadImage());
                                                                receivedGroupBean.setMoney(member.getMoney());
                                                                receivedGroupBean.setNickName(member.getNickName());
                                                                receivedGroupBean.setRobTime(member.getRobTime());
                                                                receivedGroupBeanList.add(receivedGroupBean);
                                                            }

                                                            for (GroupRedPacketDetailBean.DataDTO.MembersDTO member : members) {
                                                                int userId = member.getUserId();

                                                                //???????????????
                                                                String sendName = data1.getNickName();
                                                                //????????????
                                                                String lumpSum = data1.getMoney();
                                                                //??????
                                                                String headImage = member.getHeadImage();
                                                                //??????
                                                                String nickName = member.getNickName();
                                                                //????????????
                                                                String grabMoney = member.getMoney();
                                                                //??????
                                                                String robTime = member.getRobTime();
                                                                //???????????????
                                                                int numberAll = data1.getNumber();
                                                                //????????????????????????
                                                                int receivedNumber = members.size();

                                                                Intent groupIntent = new Intent(itemView.getContext(), GroupRedPacketDetailsActivity.class);
                                                                //?????????????????????
                                                                groupIntent.putExtra("isReceived", true);
                                                                groupIntent.putExtra("lumpSum", lumpSum);
                                                                groupIntent.putExtra("headImage", headImage);
                                                                groupIntent.putExtra("nickName", nickName);
                                                                groupIntent.putExtra("grabMoney", grabMoney);
                                                                groupIntent.putExtra("robTime", robTime);
                                                                groupIntent.putExtra("numberAll", numberAll);
                                                                groupIntent.putExtra("receivedNumber", receivedNumber);
                                                                groupIntent.putExtra("sendName", sendName);
                                                                groupIntent.putExtra("redPacketId", redPacketId);
                                                                groupIntent.putExtra("receivedGroupBeans", (Serializable) receivedGroupBeanList);

                                                                itemView.getContext().startActivity(groupIntent);

                                                            }
                                                        }
                                                    }
                                                } else if (status == 2) {
                                                    //??????
                                                    mHandler.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            ToastUtils.show("???????????????");
                                                        }
                                                    });
                                                }
                                            }
                                        });

                                    }
                                } else {
                                    if (code.equals("403")) {
                                        Intent intent = new Intent();
                                        intent.setAction("cn.twle.android.sendbroadcast.Setup.Token");
                                        intent.setComponent(new ComponentName("com.pingmo.chengyan",
                                                "com.pingmo.chengyan.receiver.TokenFileBroadcastReceiver"));
                                        itemView.getContext().sendBroadcast(intent);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                return;

            }




            //???????????????
            String cloudCustomData = msg.getV2TIMMessage().getCloudCustomData();

                if (TextUtils.isEmpty(cloudCustomData)){
                    //????????????????????????

                    if (msg.getV2TIMMessage().isSelf()) {
                        //????????????????????????;
                        rl_base.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.envelope_background_two_right));
                    }else {
                        //???????????????
                        rl_base.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.envelope_background_two_left));
                    }
                    iv_red_envelope_received.setImageDrawable(itemView.getContext().getResources().getDrawable(R.drawable.no_red_envelope));
                }else {
                    //????????????????????????????????????????????????
                    try {
                        JSONObject jsonObject = new JSONObject(cloudCustomData);
                        int status = jsonObject.optInt("status");
                        //0 ????????? 1 ?????????  2 ?????????
                        if (status!=0){
                            if (msg.getV2TIMMessage().isSelf()) {
                                //????????????????????????;
                                rl_base.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.red_envelope_received_two_right));

                            }else {
                                rl_base.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.red_envelope_received_two_left));
                            }
                            iv_red_envelope_received.setImageDrawable(itemView.getContext().getResources().getDrawable(R.drawable.red_envelope_received_red));
                            tv_bottom_node.setText("?????????");
                        }else {
                            //????????????????????????
                            if (msg.getV2TIMMessage().isSelf()) {
                                //????????????????????????;
                                rl_base.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.envelope_background_two_right));
                            }else {
                                //???????????????
                                rl_base.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.envelope_background_two_left));
                            }
                            iv_red_envelope_received.setImageDrawable(itemView.getContext().getResources().getDrawable(R.drawable.no_red_envelope));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }



            //????????????
            if (msg.isGroup()) {

                //????????????????????? ????????????  ??????:0-?????????;1-?????????;2-??????
                //????????????????????????
                String url = Common.GROUP_RED_ENVELOPE_NEWS;
                Map<String, String> mapParams = new LinkedHashMap<>();
                Map<String, String> headerParams = new LinkedHashMap<>();
                headerParams.put("token", token);
                mapParams.put("redPacketId", redPacketId + "");
                OkHttp3_0Utils.getInstance().asyncGetHeaderOkHttp(url, mapParams, headerParams, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Message message = Message.obtain();
                        message.what = Common.Interface_err;
                        message.obj = "????????????????????????";
                        Log.i(TAG, "onFailure: " + e.getMessage());
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String content = response.body().string();
                        Log.i(TAG, "onResponse: " + content);
                        try {
                            JSONObject jsonObject = new JSONObject(content);
                            String code = jsonObject.optString("code");
                            if (code.equals("200")) {
                                //??????????????????
                                GroupRedPacketDetailBean groupRedPacketDetailBean = new Gson().fromJson(content, GroupRedPacketDetailBean.class);
                                GroupRedPacketDetailBean.DataDTO data = groupRedPacketDetailBean.getData();
                                if (data != null) {
                                    //   ??????:0-?????????;1-?????????;2-??????
                                    int status = data.getStatus();

                                    List<GroupRedPacketDetailBean.DataDTO.MembersDTO> members = data.getMembers();
                                    //????????????????????????
                                    rl_base.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (status == 0) {
                                                //??????????????????????????????????????? ???????????????????????????  ???????????????????????????
                                                String cloudCustomData = msg.getV2TIMMessage().getCloudCustomData();

                                                try {
                                                    String userId ="";
                                                    if (!TextUtils.isEmpty(cloudCustomData)){
                                                        JSONObject jsonObject1 = new JSONObject(cloudCustomData);

                                                         userId = jsonObject1.optString("userId");
                                                    }
                                                    //?????????????????????????????????
                                                    if (members!=null && members.size()>0){
                                                        for (GroupRedPacketDetailBean.DataDTO.MembersDTO member : members) {
                                                            int id = member.getUserId();
                                                            if (myUserId.equals(String.valueOf(id))){
                                                                grabRedEnvelop(redPacketId + "", groupRedPacketDetailBean);
                                                              return;
                                                            }
                                                        }

                                                    }

                                                    if (TextUtils.isEmpty(cloudCustomData) || TextUtils.isEmpty(userId) || !myUserId.equals(userId)) {

                                                        //???????????????????????????
                                                        Intent intent = new Intent(itemView.getContext(), RedPacketDetailsMeActivity.class);
                                                        intent.putExtra("redPacketId", redPacketId + "");
                                                        intent.putExtra("faceUrl",data.getHeadImage());
                                                        intent.putExtra("nickName",data.getNickName());
                                                        intent.putExtra("money",data.getMoney());
                                                        intent.putExtra("isGroup", true);
                                                        itemView.getContext().startActivity(intent);

                                                    } else {
                                                        grabRedEnvelop(redPacketId + "", groupRedPacketDetailBean);
                                                    }

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }


                                            } else if (status == 1) {
                                                //?????????
                                                receivedGroupBeanList.clear();
                                                GroupRedPacketDetailBean.DataDTO data1 = groupRedPacketDetailBean.getData();
                                                if (data1 != null) {
                                                    List<GroupRedPacketDetailBean.DataDTO.MembersDTO> members = data1.getMembers();
                                                    if (members != null && members.size() > 0) {

                                                        for (GroupRedPacketDetailBean.DataDTO.MembersDTO member : members) {
                                                            ReceivedGroupBean receivedGroupBean = new ReceivedGroupBean();
                                                            receivedGroupBean.setHeadImage(member.getHeadImage());
                                                            receivedGroupBean.setMoney(member.getMoney());
                                                            receivedGroupBean.setNickName(member.getNickName());
                                                            receivedGroupBean.setRobTime(member.getRobTime());
                                                            receivedGroupBeanList.add(receivedGroupBean);
                                                        }

                                                        for (GroupRedPacketDetailBean.DataDTO.MembersDTO member : members) {
                                                            int userId = member.getUserId();

                                                            //???????????????
                                                            String sendName = data1.getNickName();
                                                            //????????????
                                                            String lumpSum = data1.getMoney();
                                                            //??????
                                                            String headImage = member.getHeadImage();
                                                            //??????
                                                            String nickName = member.getNickName();
                                                            //????????????
                                                            String grabMoney = member.getMoney();
                                                            //??????
                                                            String robTime = member.getRobTime();
                                                            //???????????????
                                                            int numberAll = data1.getNumber();
                                                            //????????????????????????
                                                            int receivedNumber = members.size();

                                                            Intent groupIntent = new Intent(itemView.getContext(), GroupRedPacketDetailsActivity.class);
                                                            //?????????????????????
                                                            groupIntent.putExtra("isReceived", true);
                                                            groupIntent.putExtra("lumpSum", lumpSum);
                                                            groupIntent.putExtra("headImage", headImage);
                                                            groupIntent.putExtra("nickName", nickName);
                                                            groupIntent.putExtra("grabMoney", grabMoney);
                                                            groupIntent.putExtra("robTime", robTime);
                                                            groupIntent.putExtra("numberAll", numberAll);
                                                            groupIntent.putExtra("receivedNumber", receivedNumber);
                                                            groupIntent.putExtra("sendName", sendName);
                                                            groupIntent.putExtra("redPacketId", redPacketId);
                                                            groupIntent.putExtra("receivedGroupBeans", (Serializable) receivedGroupBeanList);

                                                            itemView.getContext().startActivity(groupIntent);

                                                        }
                                                    }
                                                }
                                            } else if (status == 2) {
                                                //??????
                                                mHandler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        ToastUtils.show("???????????????");
                                                    }
                                                });
                                            }
                                        }
                                    });

                                }
                            } else {
                                if (code.equals("403")) {
                                    Intent intent = new Intent();
                                    intent.setAction("cn.twle.android.sendbroadcast.Setup.Token");
                                    intent.setComponent(new ComponentName("com.pingmo.chengyan",
                                            "com.pingmo.chengyan.receiver.TokenFileBroadcastReceiver"));
                                    itemView.getContext().sendBroadcast(intent);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            } else {
                //???????????????
                getReadStatus(msg, redPacketId);
            }
        }
    }

    private SharedPreferences sp;
    private static String SP_NAME = "huimiaomiao_share_date";

    //??????????????????
    private void getReadStatus(TUIMessageBean msg, long redPacketId) {
        //???????????????????????????
        String url = Common.SINGLE_RED_PACKET_INFORMATION;
        Map<String, String> mapParams = new LinkedHashMap<>();
        Map<String, String> headerParams = new LinkedHashMap<>();
        headerParams.put("token", token);
        mapParams.put("redPacketId", redPacketId + "");
        OkHttp3_0Utils.getInstance().asyncGetHeaderOkHttp(url, mapParams, headerParams, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Message message = Message.obtain();
                message.what = Common.Interface_err;
                message.obj = "????????????????????????";
                Log.i(TAG, "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String content = response.body().string();
                Log.i(TAG, "onResponse: " + content);
                try {
                    JSONObject jsonObject = new JSONObject(content);
                    String code = jsonObject.optString("code");
                    if (code.equals("200")) {
                        //??????????????????
                        RedPacketDetailBean redPacketDetailBean = new Gson().fromJson(content, RedPacketDetailBean.class);
                        RedPacketDetailBean.DataDTO data = redPacketDetailBean.getData();
                        if (data != null) {
                            String headImage = data.getHeadImage();
                            String money = data.getMoney();
                            //0 ????????? 1 ?????????  2 ?????????
                            int status = data.getStatus();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    //?????????????????????
                                    rl_base.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String nickName = msg.getV2TIMMessage().getNickName();
                                            String faceUrl = msg.getV2TIMMessage().getFaceUrl();
                                            if (msg.getV2TIMMessage().isSelf()) {
                                                //??????????????????
                                                Intent intent = new Intent(itemView.getContext(), RedPacketDetailsActivity.class);
                                                intent.putExtra("money", money);
                                                intent.putExtra("redPacketId", redPacketId + "");
                                                intent.putExtra("isSelf", true);
                                                intent.putExtra("faceUrl", faceUrl);
                                                intent.putExtra("nickName", nickName);
                                                itemView.getContext().startActivity(intent);
                                            } else {
                                                //?????????????????? ????????????dialogeActivity
                                                if (status == 0) {
                                                    Intent intent = new Intent(itemView.getContext(), RedPacketDetailsMeActivity.class);
                                                    intent.putExtra("money", money);
                                                    msg.getV2TIMMessage().getSender();
                                                    intent.putExtra("redPacketId", redPacketId + "");
                                                    intent.putExtra("isSelf", false);
                                                    intent.putExtra("faceUrl", faceUrl);
                                                    intent.putExtra("status", status);
                                                    intent.putExtra("nickName", nickName);
                                                    itemView.getContext().startActivity(intent);
                                                } else {
                                                    Intent intent = new Intent(itemView.getContext(), RedPacketDetailsActivity.class);
                                                    intent.putExtra("money", money);
                                                    intent.putExtra("redPacketId", redPacketId + "");
                                                    intent.putExtra("isSelf", false);
                                                    intent.putExtra("status", status);
                                                    intent.putExtra("faceUrl", faceUrl);
                                                    intent.putExtra("nickName", nickName);
                                                    itemView.getContext().startActivity(intent);
                                                }

                                            }
                                        }
                                    });

                                }
                            });
                        }
                    } else {
                        if (code.equals("403")) {
                            Intent intent = new Intent();
                            intent.setAction("cn.twle.android.sendbroadcast.Setup.Token");
                            intent.setComponent(new ComponentName("com.pingmo.chengyan",
                                    "com.pingmo.chengyan.receiver.TokenFileBroadcastReceiver"));
                            itemView.getContext().sendBroadcast(intent);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //???????????????????????? ?????????????????????????????????????????????

    /**
     * @param redPacketId              ?????????id
     * @param groupRedPacketDetailBean ?????????????????? ?????????????????????
     */
    private void grabRedEnvelop(String redPacketId, GroupRedPacketDetailBean groupRedPacketDetailBean) {

        //??????????????????
        String url = Common.GRAB_RED_ENVELOPE;
        Map<String, String> mapParams = new LinkedHashMap<>();
        Map<String, String> headerParams = new LinkedHashMap<>();
        headerParams.put("token", token);
        mapParams.put("redPacketId", redPacketId);
        OkHttp3_0Utils.getInstance().asyncBodyOkHttp(url, mapParams, headerParams, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Message message = Message.obtain();
                message.what = Common.Interface_err;
                message.obj = "????????????????????????";
                Log.i(TAG, "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String content = response.body().string();
                Log.i(TAG, "onResponse: " + content);
                try {
                    JSONObject jsonObject = new JSONObject(content);
                    String code = jsonObject.optString("code");
                    if (code.equals("200")) {
                        //???????????????????????????
                        Intent intent = new Intent(itemView.getContext(), RedPacketDetailsMeActivity.class);
                        intent.putExtra("redPacketId", redPacketId + "");
                        intent.putExtra("isGroup", true);
                        itemView.getContext().startActivity(intent);

                    } else {
                        String msg = jsonObject.optString("msg");
                        if (code.equals("9000") && msg.equals("?????????,???????????????")) {
                            //???????????????????????????,????????????
                            //?????????????????????id  ??? ?????????????????????????????????
                            receivedGroupBeanList.clear();
                            GroupRedPacketDetailBean.DataDTO data = groupRedPacketDetailBean.getData();
                            if (data != null) {
                                List<GroupRedPacketDetailBean.DataDTO.MembersDTO> members = data.getMembers();
                                if (members != null && members.size() > 0) {

                                    for (GroupRedPacketDetailBean.DataDTO.MembersDTO member : members) {
                                        ReceivedGroupBean receivedGroupBean = new ReceivedGroupBean();
                                        receivedGroupBean.setHeadImage(member.getHeadImage());
                                        receivedGroupBean.setMoney(member.getMoney());
                                        receivedGroupBean.setNickName(member.getNickName());
                                        receivedGroupBean.setRobTime(member.getRobTime());
                                        receivedGroupBeanList.add(receivedGroupBean);
                                    }

                                    for (GroupRedPacketDetailBean.DataDTO.MembersDTO member : members) {
                                        int userId = member.getUserId();
                                        if (String.valueOf(userId).equals(myUserId)) {
                                            //???????????????
                                            String sendName = data.getNickName();
                                            //????????????
                                            String lumpSum = data.getMoney();
                                            //??????
                                            String headImage = member.getHeadImage();
                                            //??????
                                            String nickName = member.getNickName();
                                            //????????????
                                            String grabMoney = member.getMoney();
                                            //??????
                                            String robTime = member.getRobTime();
                                            //???????????????
                                            int numberAll = data.getNumber();
                                            //????????????????????????
                                            int receivedNumber = members.size();

                                            Intent groupIntent = new Intent(itemView.getContext(), GroupRedPacketDetailsActivity.class);
                                            //?????????????????????
                                            groupIntent.putExtra("isReceived", true);
                                            groupIntent.putExtra("lumpSum", lumpSum);
                                            groupIntent.putExtra("headImage", headImage);
                                            groupIntent.putExtra("nickName", nickName);
                                            groupIntent.putExtra("grabMoney", grabMoney);
                                            groupIntent.putExtra("robTime", robTime);
                                            groupIntent.putExtra("numberAll", numberAll);
                                            groupIntent.putExtra("receivedNumber", receivedNumber);
                                            groupIntent.putExtra("sendName", sendName);
                                            groupIntent.putExtra("redPacketId", redPacketId);
                                            groupIntent.putExtra("receivedGroupBeans", (Serializable) receivedGroupBeanList);
                                            itemView.getContext().startActivity(groupIntent);
                                        }
                                    }
                                }
                            }
                        }
                        if (code.equals("9000")){
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                  // ToastUtils.show(msg);
                                }
                            });
                        }
                        if (code.equals("403")) {
                            Intent intent = new Intent();
                            intent.setAction("cn.twle.android.sendbroadcast.Setup.Token");
                            intent.setComponent(new ComponentName("com.pingmo.chengyan",
                                    "com.pingmo.chengyan.receiver.TokenFileBroadcastReceiver"));
                            itemView.getContext().sendBroadcast(intent);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //???????????????????????????
    private static List<String> redEnvelopes = new ArrayList<>();
    @Override
    public void getStatus(String redPacketId) {
        //????????????
     TUIMessageBean   currentMsg =maps.get(redPacketId);
        Log.i(TAG, "getStatus: "+redPacketId);
        if (currentMsg == null)return;
        boolean group = currentMsg.isGroup();

        if (group){
            String url = Common.GROUP_RED_ENVELOPE_NEWS;
            Map<String, String> mapParams = new LinkedHashMap<>();
            Map<String, String> headerParams = new LinkedHashMap<>();
            headerParams.put("token", token);
            mapParams.put("redPacketId", redPacketId + "");
            OkHttp3_0Utils.getInstance().asyncGetHeaderOkHttp(url, mapParams, headerParams, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Message message = Message.obtain();
                    message.what = Common.Interface_err;
                    message.obj = "????????????????????????";
                    Log.i(TAG, "onFailure: " + e.getMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String content = response.body().string();
                    Log.i(TAG, "onResponse: " + content);
                    try {
                        JSONObject jsonObject = new JSONObject(content);
                        String code = jsonObject.optString("code");
                        if (code.equals("200")) {
                            //??????????????????
                            RedPacketDetailBean redPacketDetailBean = new Gson().fromJson(content, RedPacketDetailBean.class);
                            RedPacketDetailBean.DataDTO data = redPacketDetailBean.getData();

                            JSONObject customData = new JSONObject();
                            if (data != null) {

                                List<RedPacketDetailBean.DataDTO.MembersDTO> members = data.getMembers();
                                if (members!=null && members.size()>0){
                                    for (RedPacketDetailBean.DataDTO.MembersDTO member : members) {
                                        int userId = member.getUserId();
                                        if (myUserId.equals(String.valueOf(userId)) ){
                                            //????????????????????????????????????
                                            customData.put("userId",myUserId);
//
                                            break;
                                        }
                                    }
                                }
                                //0 ????????? 1 ?????????  2 ?????????
                                int status = data.getStatus();
                                customData.put("status",status);
                                currentMsg.getV2TIMMessage().setCloudCustomData(customData.toString());
                                presenter.modifyMessage(currentMsg);


                            }
                        } else {
                            if (code.equals("403")) {
                                Intent intent = new Intent();
                                intent.setAction("cn.twle.android.sendbroadcast.Setup.Token");
                                intent.setComponent(new ComponentName("com.pingmo.chengyan",
                                        "com.pingmo.chengyan.receiver.TokenFileBroadcastReceiver"));
                                itemView.getContext().sendBroadcast(intent);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });



        }else {
            //????????????????????????
            String url = Common.SINGLE_RED_PACKET_INFORMATION;
            Map<String, String> mapParams = new LinkedHashMap<>();
            Map<String, String> headerParams = new LinkedHashMap<>();
            headerParams.put("token", token);
            mapParams.put("redPacketId", redPacketId + "");
            OkHttp3_0Utils.getInstance().asyncGetHeaderOkHttp(url, mapParams, headerParams, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Message message = Message.obtain();
                    message.what = Common.Interface_err;
                    message.obj = "????????????????????????";
                    Log.i(TAG, "onFailure: " + e.getMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String content = response.body().string();
                    Log.i(TAG, "onResponse: " + content);
                    try {
                        JSONObject jsonObject = new JSONObject(content);
                        String code = jsonObject.optString("code");
                        if (code.equals("200")) {
                            //??????????????????
                            RedPacketDetailBean redPacketDetailBean = new Gson().fromJson(content, RedPacketDetailBean.class);
                            RedPacketDetailBean.DataDTO data = redPacketDetailBean.getData();

                            if (data != null) {
                                JSONObject customData1 = new JSONObject();
                                List<RedPacketDetailBean.DataDTO.MembersDTO> members = data.getMembers();
                                if (members!=null && members.size()>0){
                                    for (RedPacketDetailBean.DataDTO.MembersDTO member : members) {
                                        int userId = member.getUserId();
                                        if (myUserId.equals(String.valueOf(userId)) ){
                                            //????????????????????????????????????
                                            customData1.put("userId",myUserId);
                                            break;
                                        }
                                    }
                                }
                                //0 ????????? 1 ?????????  2 ?????????
                                int status = data.getStatus();
                                customData1.put("status",status);

                                currentMsg.getV2TIMMessage().setCloudCustomData(customData1.toString());
                                presenter.modifyMessage(currentMsg);

                            }
                        } else {
                            if (code.equals("403")) {
                                Intent intent = new Intent();
                                intent.setAction("cn.twle.android.sendbroadcast.Setup.Token");
                                intent.setComponent(new ComponentName("com.pingmo.chengyan",
                                        "com.pingmo.chengyan.receiver.TokenFileBroadcastReceiver"));
                                itemView.getContext().sendBroadcast(intent);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }


        Log.i(TAG, "getStatus: " );
    }
}
