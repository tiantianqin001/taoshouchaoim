package com.pingmo.chengyan.activity.persion;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.hjq.toast.ToastUtils;
import com.pingmo.chengyan.MyAPP;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.manager.BaseActivity;
import com.pingmo.chengyan.bean.IdentificatonBean;
import com.pingmo.chengyan.net.Common;
import com.pingmo.chengyan.utils.OkHttp3_0Utils;
import com.pingmo.chengyan.utils.QZXTools;
import com.pingmo.chengyan.utils.SharedPreferenceUtil;
import com.tencent.qcloud.tuikit.tuichat.ui.interfaces.IPhoneCode;
import com.tencent.qcloud.tuikit.tuichat.ui.view.PhoneCode;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class VerifyChangeActivity extends BaseActivity implements View.OnClickListener {
    public static final int INIT_SUCCESS = 0x0001;
    public static final int INIT_FAILED = 0x0002;
    public static final int AUTH_SUCCESS = 0x0003;
    public static final int AUTH_FAILED = 0x0004;

    public static final String TAG = "VerifyIdentidyActivity";
    private PhoneCode pc_code;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_change_layout);
        addActivity(this);
        initView();
    }


    @Override
    protected void handleActMessage(Message msg) {
        super.handleActMessage(msg);
        switch (msg.what) {
            case INIT_SUCCESS:
                IdentificatonBean bean = (IdentificatonBean) msg.obj;
                ToastUtils.show("修改密码成功");
                finish();
                break;
            case INIT_FAILED:
                String infoFailed = (String) msg.obj;
                ToastUtils.show(infoFailed);
                break;
            case AUTH_SUCCESS:
                String authSuccess = (String) msg.obj;
                ToastUtils.show(authSuccess);
                finish();
                break;
            case AUTH_FAILED:
                String authFailed = (String) msg.obj;
                ToastUtils.show(authFailed);
                break;
        }
    }

    private String verificationCodeShow;

    private void initView() {
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setVisibility(View.GONE);

        pc_code = (PhoneCode) findViewById(com.tencent.qcloud.tuikit.tuichat.R.id.pc_code);
        TextView tv_next = findViewById(R.id.tv_next);
        LinearLayout ll_base_back = findViewById(R.id.ll_base_back);
        ll_base_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //下一步的点击事件
        tv_next.setOnClickListener(this);


        pc_code.setOnVCodeCompleteListener(new IPhoneCode.OnVCodeInputListener() {


            @Override
            public void vCodeComplete(String verificationCode) {
                verificationCodeShow = verificationCode;
                int i = Log.i(TAG, "vCodeComplete: " + verificationCode);
                tv_next.setBackground(getResources()
                        .getDrawable(R.drawable.save_finish_next_bg));
                tv_next.setTextColor(getResources().getColor(R.color.white));
            }

            @Override
            public void vCodeIncomplete(String verificationCode) {

            }
        });


    }

    private void nextInfo() {
        if (TextUtils.isEmpty(verificationCodeShow)) {
            ToastUtils.show("密码不能为空");
            return;
        }

        Map<String, String> mapParams = new LinkedHashMap<>();
        mapParams.put("code", "888888");
        mapParams.put("newPassword", verificationCodeShow);
        Map<String, String> headParams = new LinkedHashMap<>();
        headParams.put("token", SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("token"));
        OkHttp3_0Utils.getInstance().asyncBodyOkHttp(Common.PAYMENT_PASSWORD, mapParams, headParams, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                dismissWaitingDlg();
                Message message = Message.obtain();
                message.what = INIT_FAILED;
                message.obj = "获取本修改密码失败，请稍后再试";
                mHandler.sendMessage(message);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String content = response.body().string();
                QZXTools.logE("onResponse: " + content, null);
                JSONObject jsonObject = null;
                dismissWaitingDlg();
                try {
                    jsonObject = new JSONObject(content);
                    int code = jsonObject.optInt("code");
                    String msg = jsonObject.optString("msg");
                    if (code == 200) {
                        IdentificatonBean bean = JSON.parseObject(content, IdentificatonBean.class);
                        Message message = Message.obtain();
                        message.what = INIT_SUCCESS;
                        message.obj = bean;
                        mHandler.sendMessage(message);
                    }else if (code == 403){
                        Intent intent = new Intent();
                        intent.setAction("cn.twle.android.sendbroadcast.Setup.Token");
                        intent.setComponent(new ComponentName("com.pingmo.chengyan",
                                "com.pingmo.chengyan.receiver.TokenFileBroadcastReceiver"));
                        sendBroadcast(intent);
                    } else {
                        Message message = Message.obtain();
                        message.what = INIT_FAILED;
                        message.obj = msg;
                        mHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    Message message = Message.obtain();
                    message.what = INIT_FAILED;
                    message.obj = "获取实名认证信息失败，请稍后再试";
                    mHandler.sendMessage(message);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_next:
                Log.e(TAG, "onClick:wwwwww ", null);
                nextInfo();
                break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActivity(this);
    }
}
