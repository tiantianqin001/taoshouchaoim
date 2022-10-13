package com.pingmo.chengyan.activity.persion;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.aliyun.aliyunface.api.ZIMCallback;
import com.aliyun.aliyunface.api.ZIMFacade;
import com.aliyun.aliyunface.api.ZIMFacadeBuilder;
import com.aliyun.aliyunface.api.ZIMResponse;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.toast.ToastUtils;
import com.pingmo.chengyan.MyAPP;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.manager.BaseActivity;
import com.pingmo.chengyan.bean.IdentificatonBean;
import com.pingmo.chengyan.bean.LoginBean;
import com.pingmo.chengyan.eventbus.EventBus;
import com.pingmo.chengyan.net.Common;
import com.pingmo.chengyan.utils.OkHttp3_0Utils;
import com.pingmo.chengyan.utils.QZXTools;
import com.pingmo.chengyan.utils.SharedPreferenceUtil;
import com.tencent.qcloud.tuikit.tuicontact.model.UserRefresh;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class IdentificationActivity extends BaseActivity implements View.OnClickListener {

    public static final int INIT_SUCCESS = 0x0001;
    public static final int INIT_FAILED = 0x0002;
    public static final int AUTH_SUCCESS = 0x0003;
    public static final int AUTH_FAILED = 0x0004;

    private EditText name, id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ZIMFacade.install(this);
        setContentView(R.layout.activity_new_identification_layout);
        initView();
        addActivity(this);
    }

    private void initView() {
        getTitleBar().setTitle("实名认证");
        getTitleBar().enableBack();
        ImmersionBar.setTitleBar(this, getTitleBar().getTitle());
        name = findViewById(R.id.name);
        id = findViewById(R.id.id);
        findViewById(R.id.submit).setOnClickListener(this);
    }

    @Override
    protected void handleActMessage(Message msg) {
        super.handleActMessage(msg);
        switch (msg.what) {
            case INIT_SUCCESS:
                IdentificatonBean bean = (IdentificatonBean) msg.obj;
                showFace(bean.getData().getCertifyId());
                break;
            case INIT_FAILED:
                String infoFailed = (String) msg.obj;
                ToastUtils.show(infoFailed);
                break;
            case AUTH_SUCCESS:
                String authSuccess = (String) msg.obj;
                ToastUtils.show(authSuccess);
                String userInfo = SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("user_info");
                LoginBean.DataDTO user;
                if (!TextUtils.isEmpty(userInfo)) {
                    user = JSON.parseObject(userInfo, LoginBean.DataDTO.class);
                } else {
                    user = new LoginBean.DataDTO();
                }
                user.setRealStatus(2);
                SharedPreferenceUtil.getInstance(MyAPP.getInstance()).setString("user_info", JSON.toJSONString(user));
                EventBus.getDefault().post(new UserRefresh());
                finish();
                break;
            case AUTH_FAILED:
                String authFailed = (String) msg.obj;
                ToastUtils.show(authFailed);
                break;
        }
    }

    private void showFace(String certifyId) {
        QZXTools.logD("certifyId : " + certifyId);
        ZIMFacade zimFacade = ZIMFacadeBuilder.create(this);
        zimFacade.verify(certifyId, true, new ZIMCallback() {
            @Override
            public boolean response(ZIMResponse response) {
                if (null != response && 1000 == response.code) {
                    // 认证成功。
                    faceResult(certifyId);
                } else {
                    // 认证失败。
                    if (response != null) {
                        ToastUtils.show(response.msg);
                    } else {
                        ToastUtils.show("认证失败，请稍后重试");
                    }
                }
                return true;
            }
        });
    }

    private void faceResult(String certifyId) {
        showWaitingDlg();
        String nameStr = name.getText().toString().trim();
        String idStr = id.getText().toString().trim();
        Map<String, String> mapParams = new LinkedHashMap<>();
        mapParams.put("certifyId", certifyId);
        Map<String, String> headParams = new LinkedHashMap<>();
        headParams.put("token", SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("token"));
        OkHttp3_0Utils.getInstance().asyncBodyOkHttp(Common.ALIYUNFACERESULT, mapParams, headParams, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                dismissWaitingDlg();
                Message message = Message.obtain();
                message.what = AUTH_FAILED;
                message.obj = "实名认证失败，请稍后再试";
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
                        Message message = Message.obtain();
                        message.what = AUTH_SUCCESS;
                        message.obj = "实名认证成功";
                        mHandler.sendMessage(message);
                    } else {
                        Message message = Message.obtain();
                        message.what = AUTH_FAILED;
                        message.obj = msg;
                        mHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    Message message = Message.obtain();
                    message.what = AUTH_FAILED;
                    message.obj = "实名认证失败，请稍后再试";
                    mHandler.sendMessage(message);
                }
            }
        });
    }

    private void clickSubmit() {
        showWaitingDlg();
        String nameStr = name.getText().toString().trim();
        String idStr = id.getText().toString().trim();
        Map<String, String> mapParams = new LinkedHashMap<>();
        mapParams.put("idCard", idStr);
        mapParams.put("metaInfo", ZIMFacade.getMetaInfos(this));
        mapParams.put("name", nameStr);
        Map<String, String> headParams = new LinkedHashMap<>();
        headParams.put("token", SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("token"));
        OkHttp3_0Utils.getInstance().asyncBodyOkHttp(Common.ALIYUNFACEINIT, mapParams, headParams, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                dismissWaitingDlg();
                Message message = Message.obtain();
                message.what = INIT_FAILED;
                message.obj = "获取实名认证信息失败，请稍后再试";
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
                    message.obj = "获取实名认证信息失败，请稍后再试";
                    mHandler.sendMessage(message);
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                clickSubmit();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActivity(this);
    }
}
