package com.tencent.qcloud.tuikit.tuichat.ui.page;

import android.app.Activity;
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
import com.tencent.qcloud.tuikit.tuichat.net.Common;
import com.tencent.qcloud.tuikit.tuichat.net.OkHttp3_0Utils;
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

public class RedPacketDetailsMeActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "RedPacketDetails";
    private TextView tv_open_red;
    private String money;
    private String redPacketId;
    private boolean isSelf;
    private boolean isGroup;
    private String faceUrl;
    private String nickName;
    private int status;
    private String myUserId;
    private TextView tv_exclusive;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置沉静状态栏
        ImmersionBar.with(this)
                .transparentBar()
                .init();
        setContentView(R.layout.red_pack_detail_me_layout);
        tv_open_red = (TextView) findViewById(R.id.tv_open_red);
        tv_exclusive = (TextView) findViewById(R.id.tv_exclusive);
        initListener();
        faceUrl = getIntent().getStringExtra("faceUrl");
        nickName = getIntent().getStringExtra("nickName");
        money = getIntent().getStringExtra("money");
        redPacketId = getIntent().getStringExtra("redPacketId");
        isGroup = getIntent().getBooleanExtra("isGroup", false);
        status = getIntent().getIntExtra("status", 0);
        //判断是不是专属红包
        String receiverId = getIntent().getStringExtra("receiverId");
        if (!TextUtils.isEmpty(receiverId)){
            if (receiverId.equals(myUserId)){
                tv_open_red.setVisibility(View.VISIBLE);
            }else {
                String exclusiveName = getIntent().getStringExtra("exclusiveName");
                tv_exclusive.setText("仅"+exclusiveName+"可领取");
                tv_open_red.setVisibility(View.GONE);
            }

        }

        TextView tv_name_red_envelop = findViewById(R.id.tv_name_red_envelop);

        ImageView iv_red_avtor = findViewById(R.id.iv_red_avtor);
        if (!TextUtils.isEmpty(nickName)){
            tv_name_red_envelop.setText(nickName);
        }

        if (!TextUtils.isEmpty(faceUrl)){
            if (!faceUrl.contains("http")){
                faceUrl = Common.BaseUrl + faceUrl;

            }
            Glide.with(this)
                    .asBitmap()
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .load(faceUrl)
                    .error(R.drawable.image_index)
                    .into(iv_red_avtor);
        }


    }
    private SharedPreferences sp;
    private static String SP_NAME = "huimiaomiao_share_date";
    private void initListener() {
        sp = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);

        myUserId = sp.getString("userId", "0");
        myUserId = EncryptUtil.getInstance(this).decrypt(myUserId);


        tv_open_red.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_open_red){
            //这个是发给我的红包
            if (!isGroup){
                Intent intent = new Intent(this, RedPacketDetailsActivity.class);
                intent.putExtra("isRecipient",true);
                intent.putExtra("money",money);
                intent.putExtra("redPacketId",redPacketId);
                intent.putExtra("faceUrl",faceUrl);
                intent.putExtra("nickName",nickName);
                intent.putExtra("isSelf",isSelf);
                intent.putExtra("isGroup",isGroup);
                intent.putExtra("status",status);
                startActivity(intent);
                finish();
            }else {
                //发给群的就是抢红包
                Intent groupIntent = new Intent(this,GroupRedPacketDetailsActivity.class);
                groupIntent.putExtra("isReceived",false);
                groupIntent.putExtra("redPacketId",redPacketId);
                groupIntent.putExtra("money",money);
                groupIntent.putExtra("headImage",faceUrl);
                groupIntent.putExtra("nickName",nickName);
                groupIntent.putExtra("isSelf",isSelf);
                groupIntent.putExtra("isGroup",isGroup);
                groupIntent.putExtra("status",status);
                startActivity(groupIntent);
                finish();
            }
        }

    }


}
