package com.pingmo.chengyan.activity.persion;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.alibaba.fastjson.JSON;
import com.hjq.toast.ToastUtils;
import com.pingmo.chengyan.MyAPP;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.manager.BaseActivity;
import com.pingmo.chengyan.activity.shop.activity.TitleWhiteBaseActivity;
import com.pingmo.chengyan.activity.shop.view.ClearWriteEditText;
import com.pingmo.chengyan.bean.IdentificatonBean;
import com.pingmo.chengyan.net.Common;
import com.pingmo.chengyan.utils.OkHttp3_0Utils;
import com.pingmo.chengyan.utils.QZXTools;
import com.pingmo.chengyan.utils.SharedPreferenceUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * 修改支付密码
 */
public class ResetPayPasswordActivity extends BaseActivity implements View.OnClickListener {
    private ClearWriteEditText mCweCode, mCweTransactionPassword, mCweConfirmTransactionPassword;
    private TextView mTvSendCode, mTvPhone;
    private Button mBtnOk;
    private String mPhoneNumber;

    private boolean isSet = false;

    public static final int INIT_SUCCESS = 0x0001;
    public static final int INIT_FAILED = 0x0002;
    public static final int AUTH_SUCCESS = 0x0003;
    public static final int AUTH_FAILED = 0x0004;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Common.Interface_err:

                    String info = (String) msg.obj;
                    ToastUtils.show(info);
                    break;
                case Common.Interface_success:


                    break;
            }

        }

    };
    private CountTimer countTimer;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pay_password);
        isSet = getIntent().getBooleanExtra("isSet", false);
        initView();
    }


    protected void initView() {
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(isSet ? "设置支付密码" : "修改支付密码");
        LinearLayout ll_base_back = findViewById(R.id.ll_base_back);
        ll_base_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mPhoneNumber = SharedPreferenceUtil.getInstance(this).getString("phone");
        mCweCode = findViewById(R.id.cwe_code);
        mCweTransactionPassword = findViewById(R.id.cwe_transaction_password);
        mCweConfirmTransactionPassword = findViewById(R.id.cwe_confirm_transaction_password);
        mTvSendCode = findViewById(R.id.tv_send_code);
        mTvPhone = findViewById(R.id.tv_phone);
        mBtnOk = findViewById(R.id.btn_ok);
        mBtnOk.setOnClickListener(this);
        mTvSendCode.setOnClickListener(this);
        if (!TextUtils.isEmpty(mPhoneNumber) && mPhoneNumber.length() > 10) {
            String mobile = mPhoneNumber.trim().substring(0, 3) + "****" + mPhoneNumber.trim().substring(7);
            mTvPhone.setText(String.format("请输入手机号%s收到的短信验证码", mobile));
        } else {
            mTvPhone.setText("测试账号不支持短信，请注册正式账号！");
        }
    }


    /**
     * 请求发送验证码
     *
     * @param phoneNumber 手机号
     */
    private void sendCode(String phoneNumber) {
        String url = Common.SENDCODE;
        Map<String, String> mapParams = new LinkedHashMap<>();
        mapParams.put("phone", phoneNumber);
        mapParams.put("type", "2");
        Map<String, String> headerParams = new LinkedHashMap<>();
        String token = SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("token");
        headerParams.put("token", token);
        OkHttp3_0Utils.getInstance().asyncBodyOkHttp(url, mapParams, headerParams, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                QZXTools.logE("onResponse: " + e.getMessage(), null);

                Message message = Message.obtain();
                message.what = Common.Interface_net_err;
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String content = response.body().string();
                QZXTools.logE("onResponse: " + content, null);

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(content);
                    String code = jsonObject.optString("code");
                    String msg = jsonObject.optString("msg");
                    if (code.equals("200")) {
//                        MainEnterpriseListBean mainEnterpriseListBean = JSON.parseObject(content,
//                                MainEnterpriseListBean.class);


//                        Message message = Message.obtain();
//                        message.what = Common.Interface_success;
//                        message.obj = mainEnterpriseListBean;
//                        handler.sendMessage(message);
                    } else if (code.equals("403")) {

//                        Message message = Message.obtain();
//                        message.what = Common.Interface_err;
//                        message.obj = msg;
//                        handler.sendMessage(message);

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_send_code:
                sendCode(mPhoneNumber);
                countTimer = new CountTimer(10000 * 6, 1000);
                countTimer.start();
                break;
            case R.id.btn_ok:
                String pass = mCweTransactionPassword.getText().toString();
                String pass2 = mCweConfirmTransactionPassword.getText().toString();
                String code = mCweCode.getText().toString();
                if (TextUtils.isEmpty(pass) || TextUtils.isEmpty(pass2)) {
                    ToastUtils.show("支付密码不能为空");
                    return;
                }
                if (!pass.equals(pass2)) {
                    ToastUtils.show("密码不一致");
                    return;
                }
                if (TextUtils.isEmpty(code)) {
                    ToastUtils.show("验证码不能为空");
                    return;
                }
                nextInfo(code, pass2);
                break;
        }
    }

    private void nextInfo(String code, String password) {


        Map<String, String> mapParams = new LinkedHashMap<>();
        mapParams.put("code", code);
        mapParams.put("newPassword", password);
        Map<String, String> headParams = new LinkedHashMap<>();
        headParams.put("token", SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("token"));
        OkHttp3_0Utils.getInstance().asyncBodyOkHttp(Common.PAYMENT_PASSWORD, mapParams, headParams, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                dismissWaitingDlg();
                Message message = Message.obtain();
                message.what = INIT_FAILED;
                message.obj = "修改支付密码失败";
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
                    } else if (code == 403) {
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
                    message.obj = "修改支付密码失败";
                    mHandler.sendMessage(message);
                }
            }
        });
    }


    class CountTimer extends CountDownTimer {
        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public CountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            Log.e("Tag", "倒计时=" + (millisUntilFinished / 1000));
            mTvSendCode.setText(millisUntilFinished / 1000 + "s后重新发送");
            //设置倒计时中的按钮外观
            mTvSendCode.setClickable(false);//倒计时过程中将按钮设置为不可点击
        }

        @Override
        public void onFinish() {
            mTvSendCode.setText("重新发送");
            mTvSendCode.setClickable(true);
        }
    }
}
