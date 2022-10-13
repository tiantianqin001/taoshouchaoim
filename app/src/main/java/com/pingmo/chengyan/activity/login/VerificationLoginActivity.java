package com.pingmo.chengyan.activity.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.hjq.toast.ToastUtils;
import com.pingmo.chengyan.MainActivity;
import com.pingmo.chengyan.MyAPP;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.manager.BaseActivity;
import com.pingmo.chengyan.bean.LoginBean;
import com.pingmo.chengyan.dialoge.LoadDialog;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class VerificationLoginActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "VerificationLoginActivity";
    //手机号
    @BindView(R.id.et_login_username)
    EditText et_login_username;

    @BindView(R.id.et_verification_code)
    EditText et_verification_code;

    @BindView(R.id.btn_captcha)
    TextView btn_captcha;


    @BindView(R.id.tv_login_title)
    TextView tv_login_title;

    @BindView(R.id.et_invitation_code)
    EditText et_invitation_code;

    @BindView(R.id.et_invitation_password)
    EditText et_invitation_password;
    @BindView(R.id.ll_login_del)
    RelativeLayout ll_login_del;
    @BindView(R.id.et_invitation_two_password)
    EditText et_invitation_two_password;
    //注册
    @BindView(R.id.rl_login_button)
    RelativeLayout rl_login_button;

    @BindView(R.id.ctv)
    ImageView ctv;
    private String phone;
    //去登录
    @BindView(R.id.tv_go_login)
    TextView tv_go_login;

    @BindView(R.id.rl_invitation_code)
    RelativeLayout rl_invitation_code;

    @BindView(R.id.rl_invitation_password)
    RelativeLayout rl_invitation_password;

    @BindView(R.id.rl_invitation_two_password)
    RelativeLayout rl_invitation_two_password;


    @BindView(R.id.rl_account_number)
    RelativeLayout rl_account_number;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case Common.Interface_err:
                    if (loadDialog!=null){
                        loadDialog.dismiss();
                        loadDialog = null;
                    }
                    String info = (String) msg.obj;
                    ToastUtils.show(info);
                    break;
                case Common.Interface_success:
                    //注册成功进入登录界面 todo 带过去用户名和密码
                    if (loadDialog!=null){
                        loadDialog.dismiss();
                        loadDialog = null;
                    }
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
            }

        }
    };
    private CountTimer countTimer;
    private LoadDialog loadDialog;
    private ExecutorService executorService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regist_layout);
        isShow = true;
        ButterKnife.bind(this);
        executorService = Executors.newSingleThreadExecutor();

        tv_login_title.setText("验证码登录");
        rl_invitation_code.setVisibility(View.GONE);
        rl_invitation_password.setVisibility(View.GONE);
        rl_invitation_two_password.setVisibility(View.GONE);
        rl_account_number.setVisibility(View.GONE);
        tv_go_login.setVisibility(View.GONE);

        //设置手机号
        String phone = SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("phone");
        if (!TextUtils.isEmpty(phone)){
            et_login_username.setText(phone);
            et_login_username.setSelection(phone.length());
        }
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
        switch (v.getId()){
            case R.id.ll_login_del:
                finish();
                break;
            //去登录
            case R.id.tv_go_login:
                Intent intent = new Intent(this,LoginActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_captcha:
                //获取验证码
                phone = et_login_username.getText().toString().trim();
                if (QZXTools.isEmpty(phone)){
                    ToastUtils.show("请输入手机号");
                    return;
                }
                if (!QZXTools.isMobile(phone)){
                    ToastUtils.show("你输入的手机号不正确");
                    return;
                }

                //获取验证码
                getVitationCode(phone);

                countTimer = new CountTimer(10000*6, 1000);
                countTimer.start();
                break;
            case R.id.rl_login_button:
                //注册
                //获取手机号
                phone = et_login_username.getText().toString().trim();
                if (QZXTools.isEmpty(phone)){
                    ToastUtils.show("请输入手机号");
                    return;
                }
                //验证码
                String verificationCode = et_verification_code.getText().toString().trim();
                if (QZXTools.isEmpty(verificationCode)){
                    ToastUtils.show("请输入验证码");
                    return;
                }
                regist(phone,verificationCode);


                break;
        }
    }

    /**
     * 开始注册
     * @param phone
     * @param verificationCode
     */

    private void regist(String phone, String verificationCode) {
        loadDialog = new LoadDialog(this);
        loadDialog.show();

        String deviceName = QZXTools.getSystemModel();
        String deviceId = QZXTools.getDeviceId(this);
        String url = Common.LOGIN;
        Map<String, String> mapParams = new LinkedHashMap<>();
        //0 密码登录   1 验证码登录
        mapParams.put("type", "1");
        mapParams.put("code",verificationCode);
        mapParams.put("password",SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("password"));
        mapParams.put("phone",phone);
        mapParams.put("device", "android");
        mapParams.put("deviceName", deviceName);
        mapParams.put("deviceId", deviceId);
        OkHttp3_0Utils.getInstance().asyncBodyOkHttp(url, mapParams, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Message message = Message.obtain();
                message.obj ="登录失败";
                message.what = Common.Interface_err;
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String content = response.body().string();
                QZXTools.logE("onResponse: " + content, null);

                try {
                    JSONObject jsonObject = new JSONObject(content);
                    int code = jsonObject.optInt("code");
                    String msg = jsonObject.optString("msg");
                    if (code == 9000){
                        Message message = Message.obtain();
                        message.obj =msg;
                        message.what = Common.Interface_err;
                        handler.sendMessage(message);
                    } else if (code == 200){
                        LoginBean loginBean = JSON.parseObject(content, LoginBean.class);
                        Message message = Message.obtain();
                        message.what = Common.Interface_success;
                        message.obj = loginBean;
                        handler.sendMessage(message);
                    }else {
                        Message message = Message.obtain();
                        message.obj =msg;
                        message.what = Common.Interface_err;
                        handler.sendMessage(message);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void getVitationCode(String phone) {
        String url = Common.SENDCODE;
        Map<String, String> mapParams = new LinkedHashMap<>();
        mapParams.put("phone",phone);
        //验证码类型:0-注册;1-登录;2-修改交易密码;3-忘记密码(登录页)
        mapParams.put("type","1");

        OkHttp3_0Utils.getInstance().asyncBodyOkHttp(url, mapParams, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                QZXTools.logE("onResponse: " + e.getMessage(), null);
                if (!isShow){
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

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(content);
                    String code = jsonObject.optString("code");
                    String msg = jsonObject.optString("msg");
                    if (code.equals("200")) {
//                        MainEnterpriseListBean mainEnterpriseListBean = JSON.parseObject(content,
//                                MainEnterpriseListBean.class);

                        if (!isShow){
                            return;
                        }
//                        Message message = Message.obtain();
//                        message.what = Common.Interface_success;
//                        message.obj = mainEnterpriseListBean;
//                        handler.sendMessage(message);
                    }else {
                        if (!isShow){
                            return;
                        }
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
        if (countTimer!=null){
            countTimer.cancel();
            countTimer = null;
        }

        if (executorService!=null){
            executorService.shutdown();
            executorService=null;
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
            Log.e("Tag", "倒计时=" + (millisUntilFinished/1000));
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

                Intent intent = new Intent(VerificationLoginActivity.this, MainActivity.class);
                startActivity(intent);

            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                DemoLog.i(TAG, "imLogin errorCode = " + errorCode + ", errorInfo = " + errorMessage);

            }
        });
    }


}
