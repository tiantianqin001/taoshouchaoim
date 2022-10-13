package com.pingmo.chengyan.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hjq.toast.ToastUtils;
import com.pingmo.chengyan.MyAPP;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.manager.BaseActivity;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class ForgotPasswordActivity extends BaseActivity implements View.OnClickListener {
    //手机号
    @BindView(R.id.et_login_username)
    EditText et_login_username;

    @BindView(R.id.et_verification_code)
    EditText et_verification_code;

    @BindView(R.id.btn_captcha)
    TextView btn_captcha;

    @BindView(R.id.et_invitation_code)
    EditText et_invitation_code;

    @BindView(R.id.et_invitation_password)
    EditText et_invitation_password;

    @BindView(R.id.et_invitation_two_password)
    EditText et_invitation_two_password;

    @BindView(R.id.rl_invitation_code)
    RelativeLayout rl_invitation_code;


    @BindView(R.id.ll_login_del)
    RelativeLayout ll_login_del;

    //注册
    @BindView(R.id.rl_login_button)
    RelativeLayout rl_login_button;
    private String phone;
    //去登录
    @BindView(R.id.tv_go_login)
    TextView tv_go_login;


    @BindView(R.id.tv_login_title)
    TextView tv_login_title;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case Common.Interface_err:

                    String info = (String) msg.obj;
                    ToastUtils.show(info);
                    break;
                case Common.Interface_success:
                        ToastUtils.show("修改密码成功");

                    Intent intent = new Intent(ForgotPasswordActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();

                    break;
            }

        }
    };
    private CountTimer countTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regist_layout);
        isShow = true;
        ButterKnife.bind(this);
        tv_go_login.setVisibility(View.GONE);
        rl_invitation_code.setVisibility(View.GONE);
        tv_login_title.setText("忘记密码");
        initData();
        initListener();
        addActivity(this);
    }

    private void initListener() {
        //获取验证码
        btn_captcha.setOnClickListener(this);
        rl_login_button.setOnClickListener(this);

        tv_go_login.setOnClickListener(this);
        ll_login_del.setOnClickListener(this);
    }

    private void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_login_del:
                finish();
                break;
            case R.id.btn_captcha:
                //获取验证码
                phone = et_login_username.getText().toString().trim();
                if (QZXTools.isEmpty(phone)) {
                    ToastUtils.show("请输入手机号");
                    return;
                }
                if (!QZXTools.isMobile(phone)) {
                    ToastUtils.show("你输入的手机号不正确");
                    return;
                }

                //获取验证码
                getVitationCode(phone);

                countTimer = new CountTimer(10000 * 6, 1000);
                countTimer.start();
                break;
            case R.id.rl_login_button:
                //注册
                //获取手机号
                phone = et_login_username.getText().toString().trim();
                if (QZXTools.isEmpty(phone)) {
                    ToastUtils.show("请输入手机号");
                    return;
                }
                //验证码
                String verificationCode = et_verification_code.getText().toString().trim();
                if (QZXTools.isEmpty(verificationCode)) {
                    ToastUtils.show("请输入验证码");
                    return;
                }
                //邀请码
                String invitation_code = et_invitation_code.getText().toString().trim();

                //第一个密码
                String invitation_password = et_invitation_password.getText().toString().trim();
                if (QZXTools.isEmpty(invitation_password)) {
                    ToastUtils.show("请输入密码");
                    return;
                }

                if (invitation_password.length() < 6) {
                    ToastUtils.show("密码太短");
                    return;
                }

                //第二次输入密码
                String invitation_two_password = et_invitation_two_password.getText().toString().trim();
                if (QZXTools.isEmpty(invitation_two_password)) {
                    ToastUtils.show("请再次输入密码");
                    return;
                }
                //判断两次密码一样
                if (!invitation_password.equals(invitation_two_password)) {
                    ToastUtils.show("两次密码不一样");
                    return;
                }
                //修改密码
                updatepassword(phone, verificationCode, invitation_password);

                break;
        }
    }

    /**
     * 开始注册
     *
     * @param phone
     * @param verificationCode
     * @param invitation_password
     */

    private void updatepassword(String phone, String verificationCode, String invitation_password) {
        String url = Common.FORGETPASSWORD;
        Map<String, String> mapParams = new LinkedHashMap<>();
        mapParams.put("code", verificationCode);
        // mapParams.put("code","888888");
        mapParams.put("password", invitation_password);
        mapParams.put("phone", phone);
        // mapParams.put("phone","13137687374");
        OkHttp3_0Utils.getInstance().asyncBodyOkHttp(url, mapParams, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (!isShow){
                    return;
                }
                Message message = Message.obtain();
                message.what = Common.Interface_err;
                message.obj = "当前服务不可用";
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String content = response.body().string();
                QZXTools.logE("onResponse: " + content, null);
                if (!isShow) {
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(content);
                    int code = jsonObject.optInt("code");
                    String msg = jsonObject.optString("msg");
                    if (code == 200){
                        Message message = Message.obtain();
                        message.what = Common.Interface_success;
                        message.obj = msg;
                        handler.sendMessage(message);

                    }else {
                        Message message = Message.obtain();
                        message.what = Common.Interface_err;
                        message.obj = msg;
                        handler.sendMessage(message);
                    }
                } catch (Exception e) {
                    Message message = Message.obtain();
                    message.what = Common.Interface_err;
                    message.obj = "当前服务不可用";
                    handler.sendMessage(message);
                }
            }
        });

    }

    private void getVitationCode(String phone) {


        String url = Common.SENDCODE;
        Map<String, String> mapParams = new LinkedHashMap<>();
        Map<String , String> headerParams = new LinkedHashMap<>();
        String token = SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("token");
        headerParams.put("token", token);
        mapParams.put("phone", phone);
        mapParams.put("type", "3");

        OkHttp3_0Utils.getInstance().asyncBodyOkHttp(url, mapParams,headerParams, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                QZXTools.logE("onResponse: " + e.getMessage(), null);
                if (!isShow) {
                    return;
                }

                Message message = Message.obtain();
                message.what = Common.Interface_net_err;
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String content = response.body().string();
                QZXTools.logE("onResponse: " + content, null);
                if (!isShow) {
                    return;
                }

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
                    } else {

                        Message message = Message.obtain();
                        message.what = Common.Interface_err;
                        message.obj = msg;
                        handler.sendMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isShow = false;
        if (countTimer != null) {
            countTimer.cancel();
            countTimer = null;
        }
        removeActivity(this);
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
            btn_captcha.setText(millisUntilFinished / 1000 + "s后重新发送");
            //设置倒计时中的按钮外观
            btn_captcha.setClickable(false);//倒计时过程中将按钮设置为不可点击


        }

        @Override
        public void onFinish() {
            btn_captcha.setText("重新发送");
            btn_captcha.setClickable(true);
        }
    }

}
