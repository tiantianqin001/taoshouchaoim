package com.pingmo.chengyan.activity.persion;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.aliyun.aliyunface.api.ZIMFacade;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.toast.ToastUtils;
import com.pingmo.chengyan.MyAPP;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.manager.BaseActivity;
import com.pingmo.chengyan.bean.IdentificatonBean;
import com.pingmo.chengyan.net.Common;
import com.pingmo.chengyan.utils.OkHttp3_0Utils;
import com.pingmo.chengyan.utils.QZXTools;
import com.pingmo.chengyan.utils.SharedPreferenceUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class VerifyIdentidyActivity extends BaseActivity implements View.OnClickListener {
    public static final int INIT_SUCCESS = 0x0001;
    public static final int INIT_FAILED = 0x0002;
    public static final int AUTH_SUCCESS = 0x0003;
    public static final int AUTH_FAILED = 0x0004;
    private EditText et_verify_code;
    private EditText et_verification_name;

    public static final String TAG = "VerifyIdentidyActivity";

    private TextView tv_next;
    private boolean canNext = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_identidy_layout);
        addActivity(this);
        initView();
    }


    @Override
    protected void handleActMessage(Message msg) {
        super.handleActMessage(msg);
        switch (msg.what) {
            case INIT_SUCCESS:
                IdentificatonBean bean = (IdentificatonBean) msg.obj;
                //showFace(bean.getData().getCertifyId());
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

    private void initView() {
        ImmersionBar.setTitleBar(this, findViewById(R.id.title));
        et_verify_code = findViewById(R.id.et_verify_code);
        et_verification_name = findViewById(R.id.et_verification_name);
        tv_next = findViewById(R.id.tv_next);
        findViewById(R.id.back).setOnClickListener(this);
        tv_next.setOnClickListener(this);
        et_verification_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInfo();
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });
        et_verify_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInfo();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void checkInfo() {
        String verification_name = et_verification_name.getText().toString().trim();
        if (!TextUtils.isEmpty(verification_name)) {
            String verification_code = et_verify_code.getText().toString().trim();
            if (verification_code.length() > 8) {
                canNext = true;
                tv_next.setBackground(getResources().getDrawable(R.drawable.save_finish_next_bg));
                tv_next.setTextColor(getResources().getColor(R.color.white));
                return;
            }
        }
        canNext = false;
        tv_next.setBackground(getResources().getDrawable(R.drawable.save_finish_bg));
        tv_next.setTextColor(getResources().getColor(R.color.btn_next_gray));
    }

    private void nextInfo() {
        if (!canNext) {
            return;
        }
        String verificationName = et_verification_name.getText().toString();
        if (TextUtils.isEmpty(verificationName)) {
            ToastUtils.show("姓名不能为空");
            return;
        }
        String verificationCode = et_verify_code.getText().toString();
        if (TextUtils.isEmpty(verificationCode)) {
            ToastUtils.show("身份证号不能为空");
            return;
        }

        Map<String, String> mapParams = new LinkedHashMap<>();
        mapParams.put("idCard", verificationCode);
        mapParams.put("metaInfo", ZIMFacade.getMetaInfos(this));
        mapParams.put("name", verificationName);
        Map<String, String> headParams = new LinkedHashMap<>();
        headParams.put("token", SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("token"));
        OkHttp3_0Utils.getInstance().asyncBodyOkHttp(Common.ALIYUNFACEINIT, mapParams, headParams, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                dismissWaitingDlg();
                Message message = Message.obtain();
                message.what = INIT_FAILED;
                message.obj = "获取本人信息失败，请稍后再试";
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
            case R.id.back:
                finish();
                break;
            case R.id.tv_next:
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
