package com.pingmo.chengyan.activity.persion;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gyf.immersionbar.ImmersionBar;
import com.hjq.toast.ToastUtils;
import com.pingmo.chengyan.MyAPP;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.manager.BaseActivity;
import com.pingmo.chengyan.net.Common;
import com.pingmo.chengyan.utils.CommonUtil;
import com.pingmo.chengyan.utils.OkHttp3_0Utils;
import com.pingmo.chengyan.utils.QZXTools;
import com.pingmo.chengyan.utils.SharedPreferenceUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ModifyActivity extends BaseActivity implements View.OnClickListener {

    public static final int YZM_SUCCESS = 0x0001;
    public static final int YZM_FAILED = 0x0002;
    public static final int MODIFY_SUCCESS = 0x0003;
    public static final int MODIFY_FAILED = 0x0004;

    private EditText mPhone;
    private EditText mSms;
    private TextView mYzm;
    private EditText mPwd;
    private EditText mPwd2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_layout);
        initView();
        addActivity(this);
    }

    private void initView() {
        getTitleBar().setTitle("修改登录密码");
        getTitleBar().enableBack();
        ImmersionBar.setTitleBar(this, getTitleBar().getTitle());
        mPhone = findViewById(R.id.phone);
        mSms = findViewById(R.id.sms);
        mYzm = findViewById(R.id.yzm);
        mPwd = findViewById(R.id.pwd);
        mPwd2 = findViewById(R.id.pwd2);
        mYzm.setOnClickListener(this);
        findViewById(R.id.submit).setOnClickListener(this);
    }

    @Override
    protected void handleActMessage(Message msg) {
        super.handleActMessage(msg);
        switch (msg.what) {
            case YZM_SUCCESS:
                dismissWaitingDlg();
                String yzmInfoSuccess = (String) msg.obj;
                ToastUtils.show(yzmInfoSuccess);
                break;
            case YZM_FAILED:
                dismissWaitingDlg();
                String yzmInfoErr = (String) msg.obj;
                ToastUtils.show(yzmInfoErr);
                break;
            case MODIFY_SUCCESS:
                dismissWaitingDlg();
                String infoSuccess = (String) msg.obj;
                ToastUtils.show(infoSuccess);
                finish();
                break;
            case MODIFY_FAILED:
                dismissWaitingDlg();
                String infoErr = (String) msg.obj;
                ToastUtils.show(infoErr);
                break;
        }
    }

    private void clickyzm() {
        if (CommonUtil.isFastDoubleClick()) {
            return;
        }
        String phone = mPhone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.show("请输入手机号");
            return;
        } else if (!CommonUtil.phonenumberCheck(phone)) {
            ToastUtils.show("请输入正确格式的手机号");
            return;
        }
        showWaitingDlg();
        countDownTimer.start();
        String url = Common.SENDCODE;
        Map<String, String> mapParams = new LinkedHashMap<>();
        mapParams.put("phone", phone);
        mapParams.put("type", "3");

        OkHttp3_0Utils.getInstance().asyncBodyOkHttp(url, mapParams, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                QZXTools.logE("onResponse: " + e.getMessage(), null);
                if (isFinishing()) {
                    return;
                }
                Message message = Message.obtain();
                message.what = YZM_FAILED;
                message.obj = "获取验证码失败，请稍后再试";
                mHandler.sendMessage(message);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String content = response.body().string();
                QZXTools.logE("onResponse: " + content, null);
                if (isFinishing()) {
                    return;
                }
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(content);
                    String code = jsonObject.optString("code");
                    String msg = jsonObject.optString("msg");
                    if (code.equals("200")) {
                        Message message = Message.obtain();
                        message.what = YZM_SUCCESS;
                        message.obj = msg;
                        mHandler.sendMessage(message);
                    } else {
                        Message message = Message.obtain();
                        message.what = YZM_FAILED;
                        message.obj = msg;
                        mHandler.sendMessage(message);
                    }
                } catch (JSONException e) {
                    Message message = Message.obtain();
                    message.what = YZM_FAILED;
                    message.obj = "获取验证码失败，请稍后再试";
                    mHandler.sendMessage(message);
                }
            }
        });
    }

    private void clickSubmit() {
        if (CommonUtil.isFastDoubleClick()) {
            return;
        }
        String phone = mPhone.getText().toString();
        String yzm = mSms.getText().toString();
        String pwd = mPwd.getText().toString();
        String pwd2 = mPwd2.getText().toString();
//        if (TextUtils.isEmpty(phone)) {
//            ToastUtils.show("请输入手机号");
//            return;
//        } else if (!CommonUtil.phonenumberCheck(phone)) {
//            ToastUtils.show("请输入正确格式的手机号");
//            return;
//        } else if (TextUtils.isEmpty(yzm)) {
//            ToastUtils.show("请输入验证码");
//            return;
//        } else

        if (TextUtils.isEmpty(pwd)) {
            ToastUtils.show("请输入旧密码");
            return;
        } else if (TextUtils.isEmpty(pwd2)) {
            ToastUtils.show("请输入新密码");
            return;
        }
        showWaitingDlg();
        String url = Common.UPDATEPWD;
        Map<String, String> mapParams = new LinkedHashMap<>();
        mapParams.put("oldPassword", pwd);
        mapParams.put("newPassword", pwd2);
        Map<String, String> headParams = new LinkedHashMap<>();
        headParams.put("token", SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("token"));
        OkHttp3_0Utils.getInstance().asyncBodyOkHttp(url, mapParams, headParams, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                QZXTools.logE("onResponse: " + e.getMessage(), null);
                if (isFinishing()) {
                    return;
                }
                Message message = Message.obtain();
                message.what = MODIFY_FAILED;
                message.obj = "修改密码失败，请稍后再试";
                mHandler.sendMessage(message);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String content = response.body().string();
                QZXTools.logE("onResponse: " + content, null);
                if (isFinishing()) {
                    return;
                }
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(content);
                    String code = jsonObject.optString("code");
                    String msg = jsonObject.optString("msg");
                    if (code.equals("200")) {
                        Message message = Message.obtain();
                        message.what = MODIFY_SUCCESS;
                        message.obj = msg;
                        mHandler.sendMessage(message);
                    } else {
                        Message message = Message.obtain();
                        message.what = MODIFY_FAILED;
                        message.obj = msg;
                        mHandler.sendMessage(message);
                    }
                } catch (JSONException e) {
                    Message message = Message.obtain();
                    message.what = MODIFY_FAILED;
                    message.obj = "修改密码失败，请稍后再试";
                    mHandler.sendMessage(message);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.yzm:
                clickyzm();
                break;
            case R.id.submit:
                clickSubmit();
                break;
        }
    }

    private static final int COUNT = 60;
    private static final int TIME = 1000;

    CountDownTimer countDownTimer = new CountDownTimer(COUNT * TIME, TIME) {
        @Override
        public void onTick(long millisUntilFinished) {
            mYzm.setEnabled(false);
            mYzm.setText(millisUntilFinished / TIME + "s后重新获取");
        }

        @Override
        public void onFinish() {
            mYzm.setText("获取验证码");
            mYzm.setEnabled(true);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActivity(this);
    }

}
