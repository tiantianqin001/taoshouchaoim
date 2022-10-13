package com.tencent.qcloud.tuikit.tuichat.ui.page;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.gyf.immersionbar.ImmersionBar;
import com.tencent.qcloud.tuikit.tuichat.R;
import com.tencent.qcloud.tuikit.tuichat.bean.LoginBean;
import com.tencent.qcloud.tuikit.tuichat.util.EncryptUtil;

public class AddRedEnvelopeActivity extends Activity {

    private SharedPreferences sp;
    private static String SP_NAME = "huimiaomiao_share_date";

    private String headImage;
    private String nickName;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置沉静状态栏
        ImmersionBar.with(this)
                .transparentBar()
                .init();
        setContentView(R.layout.add_red_envelope_layout);

        findViewById(R.id.ll_red_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.tv_user_cover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

       ImageView iv_avatar = findViewById(R.id.iv_avatar);
       TextView tv_name_red_envelop = (TextView) findViewById(R.id.tv_name_red_envelop);


        sp = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        String userInfo = sp.getString("user_info", "");
        userInfo = EncryptUtil.getInstance(this).decrypt(userInfo);

        if (!TextUtils.isEmpty(userInfo)) {
            LoginBean.DataDTO user = JSON.parseObject(userInfo, LoginBean.DataDTO.class);
            headImage = user.getHeadImage();
            nickName = user.getNickName();

            if (!TextUtils.isEmpty(headImage)){
                Glide.with(this)
                        .asBitmap()
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .error(R.drawable.image_index)
                        .load(headImage)
                        .into(iv_avatar);

            }
            if (!TextUtils.isEmpty(nickName)){
                tv_name_red_envelop.setText(nickName);
            }

        }
    }
}
