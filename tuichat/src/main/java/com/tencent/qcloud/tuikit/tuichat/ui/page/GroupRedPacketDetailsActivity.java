package com.tencent.qcloud.tuikit.tuichat.ui.page;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.toast.ToastUtils;
import com.tencent.qcloud.tuikit.tuichat.R;
import com.tencent.qcloud.tuikit.tuichat.adapter.GroupAdapter;
import com.tencent.qcloud.tuikit.tuichat.adapter.GroupTwoAdapter;
import com.tencent.qcloud.tuikit.tuichat.bean.GroupRedPacketDetailBean;
import com.tencent.qcloud.tuikit.tuichat.bean.ReceivedGroupBean;
import com.tencent.qcloud.tuikit.tuichat.interfaces.CallBackUtils;
import com.tencent.qcloud.tuikit.tuichat.net.Common;
import com.tencent.qcloud.tuikit.tuichat.net.OkHttp3_0Utils;
import com.tencent.qcloud.tuikit.tuichat.util.EncryptUtil;

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

public class GroupRedPacketDetailsActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "RedPacket";
    private String redPacketId;
    private boolean isSelf;
    private boolean isGroup;
    private RecyclerView rv_group_get_red;

    private TextView tv_small_change;
    private boolean isReceived;
    private TextView tv_red_pack_name;


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            //领取群红包的列表
            rv_group_get_red = findViewById(R.id.rv_group_get_red);
            List<GroupRedPacketDetailBean.DataDTO.MembersDTO> members = (List<GroupRedPacketDetailBean.DataDTO.MembersDTO>) msg.obj;
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(GroupRedPacketDetailsActivity.this);
            rv_group_get_red.setLayoutManager(linearLayoutManager);
            GroupTwoAdapter adapter = new GroupTwoAdapter(GroupRedPacketDetailsActivity.this,members);

            rv_group_get_red.setAdapter(adapter);
        }
    };
    private TextView tv_recipient_red;
    private String headImage;
    private ImageView iv_avatar;
    private String myUserId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置沉静状态栏
        ImmersionBar.with(this)
                .transparentBar()
                .init();
        setContentView(R.layout.group_red_pack_detail_layout);
        //红包的描述
        tv_recipient_red = (TextView) findViewById(R.id.tv_recipient_red);
        tv_red_pack_name = (TextView) findViewById(R.id.tv_red_pack_name);
        iv_avatar = (ImageView) findViewById(R.id.iv_avatar);


        String money = getIntent().getStringExtra("money");
        redPacketId = getIntent().getStringExtra("redPacketId");
        headImage = getIntent().getStringExtra("headImage");
        isSelf = getIntent().getBooleanExtra("isSelf", false);
        //判断红包是不是已经领取了
        isReceived = getIntent().getBooleanExtra("isReceived", false);
        tv_small_change = findViewById(R.id.tv_small_change);

        rv_group_get_red = findViewById(R.id.rv_group_get_red);

        findViewById(R.id.ll_base_back).setOnClickListener(this);

        sp = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);

        myUserId = sp.getString("userId", "0");
        myUserId = EncryptUtil.getInstance(this).decrypt(myUserId);


            //如果领取了就直接显示
        if (isReceived){
            // 总的金额
            String lumpSum = getIntent().getStringExtra("lumpSum");
            //发红包的人
            String sendName = getIntent().getStringExtra("sendName");
            tv_red_pack_name.setText(sendName+"发出的红包");
            //头像
            String headImage = getIntent().getStringExtra("headImage");
            if (!TextUtils.isEmpty(headImage)){
                Glide.with(this)
                        .load(headImage)
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .error(R.drawable.image_index)
                        .into(iv_avatar);
            }
            //昵称
            String nickName = getIntent().getStringExtra("nickName");

            //抢的金额
            String grabMoney = getIntent().getStringExtra("grabMoney");
            tv_small_change.setText(grabMoney);
            //时间
            String robTime = getIntent().getStringExtra("robTime");
            //总红包个数
            int numberAll = getIntent().getIntExtra("numberAll", 0);
            //已经领红包的个数
            int receivedNumber = getIntent().getIntExtra("receivedNumber", 0);
            //红包的描述
            tv_recipient_red.setText("已领取"+receivedNumber+"/"+numberAll+"个, 共"+lumpSum+"元");


            List<ReceivedGroupBean> receivedGroupBeanList  = ( List<ReceivedGroupBean>) getIntent().getSerializableExtra("receivedGroupBeans");
            Log.i(TAG, "onCreate: "+ receivedGroupBeanList);

            //领取群红包的列表

            @SuppressLint({"NewApi", "LocalSuppress"}) GroupAdapter adapter =
                    new GroupAdapter(this,receivedGroupBeanList);
            rv_group_get_red.setLayoutManager(new LinearLayoutManager(this));
            rv_group_get_red.setAdapter(adapter);
        }else {
            grabRedEnvelop();
        }





    }
    //是群发得就抢红包

    private List<ReceivedGroupBean> receivedGroups = new ArrayList();
    private void grabRedEnvelop() {
        sp = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);

        String token = sp.getString("token", "");
        token = EncryptUtil.getInstance(this).decrypt(token);


        String url = Common.GRAB_RED_ENVELOPE;
        Map<String, String> mapParams = new LinkedHashMap<>();
        Map<String , String> headerParams = new LinkedHashMap<>();
        headerParams.put("token", token);
        mapParams.put("redPacketId", redPacketId);



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


                        //发送红包消息
                        GroupRedPacketDetailBean groupRedPacketDetailBean = new Gson().fromJson(content, GroupRedPacketDetailBean.class);
                        GroupRedPacketDetailBean.DataDTO data = groupRedPacketDetailBean.getData();
                        if (data!=null){
                            int userId = data.getUserId();
                            receivedGroups.clear();
                            List<GroupRedPacketDetailBean.DataDTO.MembersDTO> members = data.getMembers();
                            if (members!=null && members.size()>0){
                                for (GroupRedPacketDetailBean.DataDTO.MembersDTO member : members) {

                                    //当前我自己抢了群红包
                                    ReceivedGroupBean receivedGroupBean = new ReceivedGroupBean();
                                    receivedGroupBean.setHeadImage(member.getHeadImage());
                                    receivedGroupBean.setMoney(member.getMoney());
                                    receivedGroupBean.setNickName(member.getNickName());
                                    receivedGroupBean.setRobTime(member.getRobTime());
                                    receivedGroups.add(receivedGroupBean);

                                    if (String.valueOf(member.getUserId()).equals(myUserId) ){
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                tv_red_pack_name.setText(member.getNickName()+"发出的红包");
                                                Glide.with(GroupRedPacketDetailsActivity.this)
                                                        .load(member.getHeadImage())
                                                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                                        .error(R.drawable.image_index)
                                                        .into(iv_avatar);

                                                  tv_small_change.setText(member.getMoney());

                                                //红包的描述
                                                tv_recipient_red.setText("已领取"+members.size()+"/"+data.getNumber()+"个, "+member.getMoney()+"/"+data.getMoney()+"元");
                                            }
                                        });
                                    }

                                }
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.i(TAG, "onCreate: "+ receivedGroups);
                                        //领取群红包的列表
                                        @SuppressLint({"NewApi", "LocalSuppress"}) GroupAdapter adapter =
                                                new GroupAdapter(GroupRedPacketDetailsActivity.this,receivedGroups);
                                        rv_group_get_red.setLayoutManager(new LinearLayoutManager(GroupRedPacketDetailsActivity.this));
                                        rv_group_get_red.setAdapter(adapter);



                                    }
                                });
                            }

                        }


                    }else {

                            if (code.equals("403")){
                                Intent intent = new Intent();
                                intent.setAction("cn.twle.android.sendbroadcast.Setup.Token");
                                intent.setComponent(new ComponentName("com.pingmo.chengyan",
                                        "com.pingmo.chengyan.receiver.TokenFileBroadcastReceiver"));
                               sendBroadcast(intent);
                            }

//                                    String msg = jsonObject.optString("msg");
//                                    ToastUtils.show(msg);
                        }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private SharedPreferences sp;
    private static String SP_NAME = "huimiaomiao_share_date";


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_base_back) {
            //说明群红包已经被抢嘞
          //  CallBackUtils.getStatus();
            finish();
        }
    }
}
