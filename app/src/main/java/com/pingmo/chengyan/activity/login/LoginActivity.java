package com.pingmo.chengyan.activity.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.hjq.toast.ToastUtils;
import com.pingmo.chengyan.MainActivity;
import com.pingmo.chengyan.MyAPP;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.manager.BaseActivity;
import com.pingmo.chengyan.bean.LoginBean;
import com.pingmo.chengyan.net.Common;
import com.pingmo.chengyan.utils.Constants;
import com.pingmo.chengyan.utils.DemoLog;
import com.pingmo.chengyan.utils.OkHttp3_0Utils;
import com.pingmo.chengyan.utils.QZXTools;
import com.pingmo.chengyan.utils.SharedPreferenceUtil;
import com.tencent.imsdk.v2.V2TIMFriendAddApplication;
import com.tencent.imsdk.v2.V2TIMFriendOperationResult;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.tencent.qcloud.tuicore.TUILogin;
import com.tencent.qcloud.tuicore.interfaces.TUICallback;
import com.tencent.qcloud.tuicore.util.ErrorMessageConverter;
import com.tencent.qcloud.tuikit.tuicontact.util.TUIContactLog;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    @BindView(R.id.rl_login_button)
    RelativeLayout rl_login_button;

    @BindView(R.id.et_login_username)
    EditText et_login_username;

    @BindView(R.id.et_login_password)
    EditText et_login_password;

    @BindView(R.id.ll_login_register)
    LinearLayout ll_login_register;

    @BindView(R.id.ll_forget_password)
    LinearLayout ll_forget_password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        initData();
        addActivity(this);

    }
    private void initData() {
        rl_login_button.setOnClickListener(this);
        ll_login_register.setOnClickListener(this);
        ll_forget_password.setOnClickListener(this);

        String phone = SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("phone");

        if (!QZXTools.isEmpty(phone) ) {
            et_login_username.setText(phone);
            et_login_username.setSelection(phone.length());
        }
    }

    @Override
    protected void handleActMessage(Message msg) {
        super.handleActMessage(msg);
        switch (msg.what) {
            case Common.Interface_err:
                String info = (String) msg.obj;
                ToastUtils.show(info);
                dismissWaitingDlg();
                break;
            case Common.Interface_success:
                dismissWaitingDlg();
                LoginBean loginBean = (LoginBean) msg.obj;
                LoginBean.DataDTO loginBeanData = loginBean.getData();
                String token = loginBeanData.getToken();
                int userId = loginBeanData.getUserId();
                String userSig = loginBeanData.getUserSig();
                String phone = loginBeanData.getPhone();
                String headImage = loginBeanData.getHeadImage();
                String nickName = loginBeanData.getNickName();
                int phoneVerify = loginBeanData.getPhoneVerify();

                SharedPreferenceUtil.getInstance(MyAPP.getInstance()).setString("token", token);
                SharedPreferenceUtil.getInstance(MyAPP.getInstance()).setString("userId", userId + "");
                SharedPreferenceUtil.getInstance(MyAPP.getInstance()).setString("userSig", userSig);
                SharedPreferenceUtil.getInstance(MyAPP.getInstance()).setString("phone", phone);
                SharedPreferenceUtil.getInstance(MyAPP.getInstance()).setString("headImage", headImage);
                SharedPreferenceUtil.getInstance(MyAPP.getInstance()).setString("nickName", nickName);
                SharedPreferenceUtil.getInstance(MyAPP.getInstance()).setInt("phoneVerify",phoneVerify);
                SharedPreferenceUtil.getInstance(MyAPP.getInstance()).setString("user_info", JSON.toJSONString(loginBeanData));
                initTengSdkLogin();
                break;
            case Common.Interface_success_law:
                //开启验证码登录
                Intent intent = new Intent(this,VerificationLoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_login_register:
                //注册的点击
                QZXTools.logD("注册");
                Intent intent = new Intent(this, RegistActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_login_button:
                //登录
                String phone = et_login_username.getText().toString().trim();
                if (QZXTools.isEmpty(phone)) {
                    ToastUtils.show("请输入手机号");
                    return;
                }

                String password = et_login_password.getText().toString().trim();
                if (QZXTools.isEmpty(password)) {
                    ToastUtils.show("请输入密码");
                    return;
                }
                login(phone, password);
                break;
            //忘记密码
            case R.id.ll_forget_password:
                Intent intent1 = new Intent(this, ForgotPasswordActivity.class);
                startActivity(intent1);
                break;
        }

    }

    private void login(String phone, String password) {
        showWaitingDlg();
        String deviceName = QZXTools.getSystemModel();
        String deviceId = QZXTools.getDeviceId(this);
        String url = Common.LOGIN;
        Map<String, String> mapParams = new LinkedHashMap<>();
        mapParams.put("type", "0");
        mapParams.put("password", password);
        mapParams.put("phone", phone);
        mapParams.put("device", "android");
        mapParams.put("deviceName", deviceName);
        mapParams.put("deviceId", deviceId);
        OkHttp3_0Utils.getInstance().asyncBodyOkHttp(url, mapParams, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Message message = Message.obtain();
                message.what = Common.Interface_err;
                message.obj = "当前服务登录失败";
                mHandler.sendMessage(message);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String content = response.body().string();
                QZXTools.logE("onResponse: " + content, null);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(content);
                    int code = jsonObject.optInt("code");
                    String msg = jsonObject.optString("msg");
                    if (code == 200) {
                        LoginBean loginBean = JSON.parseObject(content, LoginBean.class);
                        Message message = Message.obtain();
                        message.what = Common.Interface_success;
                        message.obj = loginBean;
                        mHandler.sendMessage(message);

                        SharedPreferenceUtil.getInstance(MyAPP.getInstance()).setString("phone", phone);
                        SharedPreferenceUtil.getInstance(MyAPP.getInstance()).setString("password", password);
                    }else if (code == 201){
                        //使用验证码登录
                        SharedPreferenceUtil.getInstance(MyAPP.getInstance()).setString("phone", phone);
                        SharedPreferenceUtil.getInstance(MyAPP.getInstance()).setString("password", password);

                        Message message = Message.obtain();
                        message.what = Common.Interface_success_law;
                        message.obj = msg;
                        mHandler.sendMessage(message);

                    } else {
                        Message message = Message.obtain();
                        message.what = Common.Interface_err;
                        message.obj = msg;
                        mHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    Message message = Message.obtain();
                    message.what = Common.Interface_err;
                    message.obj = "当前服务登录失败";
                    mHandler.sendMessage(message);
                }


            }
        });
    }

    public static void onLoginOut(Activity activity) {
        SharedPreferenceUtil.getInstance(MyAPP.getInstance()).setString("token", "");
        SharedPreferenceUtil.getInstance(MyAPP.getInstance()).setString("userId", "");
        SharedPreferenceUtil.getInstance(MyAPP.getInstance()).setString("userSig", "");
       // SharedPreferenceUtil.getInstance(MyAPP.getInstance()).setString("phone", "");
        SharedPreferenceUtil.getInstance(MyAPP.getInstance()).setString("password", "");
        SharedPreferenceUtil.getInstance(MyAPP.getInstance()).setString("user_info", "");
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
       finishAll();


    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActivity(this);


    }



    private void initTengSdkLogin() {
        String userId = SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("userId");
        String userSig = SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("userSig");
//        String headImage = SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("headImage");
//        String nickName = SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("nickName");
        //开始初始化 imsdk
        TUILogin.login(MyAPP.getInstance(), Constants.SDKAPPID, userId + "", userSig, new TUICallback() {
            @Override
            public void onSuccess() {
                QZXTools.logE("成功", null);
//                Intent intent = new Intent(MainActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
                //加我验证方式
                TUILogin.updateProfile(1);

                //添加好友
                V2TIMFriendAddApplication v2TIMFriendAddApplication = new V2TIMFriendAddApplication("13532325");

                v2TIMFriendAddApplication.setAddSource("android");

                V2TIMManager.getFriendshipManager().addFriend(v2TIMFriendAddApplication, new V2TIMValueCallback<V2TIMFriendOperationResult>() {
                    @Override
                    public void onError(int code, String desc) {
                        TUIContactLog.e(TAG, "addFriend err code = " + code + ", desc = " + ErrorMessageConverter.convertIMError(code, desc));
                        // ContactUtils.callbackOnError(callback, TAG, code, desc);
                    }

                    @Override
                    public void onSuccess(V2TIMFriendOperationResult v2TIMFriendOperationResult) {
                        TUIContactLog.i(TAG, "addFriend success");
                        //  ContactUtils.callbackOnSuccess(callback, new Pair<>(v2TIMFriendOperationResult.getResultCode(), v2TIMFriendOperationResult.getResultInfo()));
                    }
                });

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                DemoLog.i(TAG, "imLogin errorCode = " + errorCode + ", errorInfo = " + errorMessage);

            }
        });
    }
}
