package com.tencent.qcloud.tuikit.tuichat.ui.page;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.toast.ToastUtils;
import com.tencent.qcloud.tuikit.tuichat.R;
import com.tencent.qcloud.tuikit.tuichat.bean.message.TUIMessageBean;
import com.tencent.qcloud.tuikit.tuichat.interfaces.CallBackUtils;
import com.tencent.qcloud.tuikit.tuichat.net.Common;
import com.tencent.qcloud.tuikit.tuichat.net.OkHttp3_0Utils;
import com.tencent.qcloud.tuikit.tuichat.util.ChatMessageBuilder;
import com.tencent.qcloud.tuikit.tuichat.util.EncryptUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RedPacketDetailsActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "RedPacket";
    private String redPacketId;
    private boolean isSelf;
    private boolean isGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置沉静状态栏
        ImmersionBar.with(this)
                .transparentBar()
                .init();
        setContentView(R.layout.red_pack_detail_layout);
      TextView tv_recipient_red=(TextView) findViewById(R.id.tv_recipient_red);

        String money = getIntent().getStringExtra("money");
        redPacketId = getIntent().getStringExtra("redPacketId");
        isSelf = getIntent().getBooleanExtra("isSelf", false);
        //如果是群 就是抢红包
        isGroup = getIntent().getBooleanExtra("isGroup", false);
        TextView tv_small_change =  findViewById(R.id.tv_small_change);
        tv_small_change.setText(money);

        findViewById(R.id.ll_base_back).setOnClickListener(this);
        //头像
       ImageView iv_avatar =  findViewById(R.id.iv_avatar);
       TextView tv_red_ninkname =  findViewById(R.id.tv_red_ninkname);

        boolean isRecipient = getIntent().getBooleanExtra("isRecipient", false);
//        if (isRecipient){
//            tv_recipient_red.setVisibility(View.GONE);
//        }

        //领取单人红包 接收别人给我发的红包
        if (!isSelf){
            //设置一下别人发给我的红包
            String faceUrl = getIntent().getStringExtra("faceUrl");
            String nickName = getIntent().getStringExtra("nickName");
            if (!TextUtils.isEmpty(faceUrl)){
                Glide.with(this)
                        .asBitmap()
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .error(R.drawable.image_index)
                        .load(faceUrl)
                        .into(iv_avatar);
            }

            if (!TextUtils.isEmpty(nickName)) {
                tv_red_ninkname.setText(nickName + "发出的红包");
            }
            int status = getIntent().getIntExtra("status", 0);
            //如果当前红包邻过了，就不能再领了
            if (status ==0){
                initData();
            }

        }else {
            //根据id 查看自己的用户信息
            String headUrl = getIntent().getStringExtra("faceUrl");
            String nickName = getIntent().getStringExtra("nickName");
                Glide.with(this)
                        .asBitmap()
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .load(headUrl)
                        .error(R.drawable.image_index)
                        .into(iv_avatar);


            if (!TextUtils.isEmpty(nickName)){
                tv_red_ninkname.setText(nickName+"发出的红包");
            }



            Log.i(TAG, "onCreate: "+headUrl);

        }
        if (isGroup){
            //是群发得就抢红包
            grabRedEnvelop();
        }
    }
    //是群发得就抢红包
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
    //单人红包
    private void initData() {
        sp = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        String token = sp.getString("token", "");
        token = EncryptUtil.getInstance(this).decrypt(token);


        String url = Common.RECEIVE_SINGLE_RED_ENVELOPE;
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



                    }else {
                        if (code.equals("403")){
                            Intent intent = new Intent();
                            intent.setAction("cn.twle.android.sendbroadcast.Setup.Token");
                            intent.setComponent(new ComponentName("ccom.pingmo.chengyan",
                                    "com.pingmo.chengyan.receiver.TokenFileBroadcastReceiver"));
                            sendBroadcast(intent);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_base_back) {
            finish();
        }
    }



}
