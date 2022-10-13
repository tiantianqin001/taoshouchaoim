package com.pingmo.chengyan.activity.persion;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.toast.ToastUtils;
import com.pingmo.chengyan.MyAPP;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.manager.BaseActivity;
import com.pingmo.chengyan.bean.LoginBean;
import com.pingmo.chengyan.eventbus.EventBus;
import com.pingmo.chengyan.net.Common;
import com.pingmo.chengyan.utils.Constants;
import com.pingmo.chengyan.utils.DemoLog;
import com.pingmo.chengyan.utils.OkHttp3_0Utils;
import com.pingmo.chengyan.utils.QZXTools;
import com.pingmo.chengyan.utils.SharedPreferenceUtil;
import com.tencent.qcloud.tuicore.TUILogin;
import com.tencent.qcloud.tuicore.interfaces.TUICallback;
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

public class NickActivity extends BaseActivity implements View.OnClickListener {

    private EditText mName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nick_layout);
        initView();
        addActivity(this);
    }

    private void initView() {
        getTitleBar().setTitle("昵称");
        getTitleBar().enableBack();
        ImmersionBar.setTitleBar(this, getTitleBar().getTitle());
        mName = findViewById(R.id.name);
        findViewById(R.id.clear).setOnClickListener(this);
        findViewById(R.id.submit).setOnClickListener(this);
        String userInfo = SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("user_info");
        if (!TextUtils.isEmpty(userInfo)) {
            LoginBean.DataDTO user = JSON.parseObject(userInfo, LoginBean.DataDTO.class);
            mName.setText(user.getNickName());

            SharedPreferenceUtil.getInstance(MyAPP.getInstance()).setString("nickName",user.getNickName());
        }
    }

    private void submit() {
        showWaitingDlg();
        String deviceName = QZXTools.getSystemModel();
        String deviceId = QZXTools.getDeviceId(this);
        String url = Common.UPDATE_DATA;
        Map<String, String> mapParams = new LinkedHashMap<>();
        mapParams.put("nickName", mName.getText().toString());
        Map<String, String> headParams = new LinkedHashMap<>();
        headParams.put("token", SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("token"));
        OkHttp3_0Utils.getInstance().asyncBodyOkHttp(url, mapParams, headParams, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Message message = Message.obtain();
                message.what = Common.Interface_err;
                message.obj = "修改昵称失败，请稍后重试";
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
                        Message message = Message.obtain();
                        message.what = Common.Interface_success;
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
                    message.obj = "修改昵称失败，请稍后重试";
                    mHandler.sendMessage(message);
                }


            }
        });
    }

    @Override
    protected void handleActMessage(Message msg) {
        super.handleActMessage(msg);
        switch (msg.what) {
            case Common.Interface_err:
                String failedInfo = (String) msg.obj;
                ToastUtils.show(failedInfo);
                dismissWaitingDlg();
                break;
            case Common.Interface_success:
                dismissWaitingDlg();
                String successInfo = (String) msg.obj;
                ToastUtils.show(successInfo);
                String userInfo = SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("user_info");
                LoginBean.DataDTO user;
                if (!TextUtils.isEmpty(userInfo)) {
                    user = JSON.parseObject(userInfo, LoginBean.DataDTO.class);
                } else {
                    user = new LoginBean.DataDTO();
                }
                user.setNickName(mName.getText().toString());
                SharedPreferenceUtil.getInstance(MyAPP.getInstance()).setString("user_info", JSON.toJSONString(user));
                EventBus.getDefault().post(new UserRefresh());


                String userId = SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("userId");
                String userSig = SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("userSig");
                String headImage = SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("headImage");
                String nickName = user.getNickName();
                //开始初始化 imsdk

                if (!TextUtils.isEmpty(userId)){
                    TUILogin.login(MyAPP.getInstance(), Constants.SDKAPPID, userId + "", userSig, new TUICallback() {
                        @Override
                        public void onSuccess() {
                            QZXTools.logE("成功", null);
                            //腾讯设置名称和头像
                            TUILogin.setUserInfo(userId,nickName,headImage);

                        }

                        @Override
                        public void onError(int errorCode, String errorMessage) {
                            DemoLog.i("", "imLogin errorCode = " + errorCode + ", errorInfo = " + errorMessage);

                        }
                    });
                }


                finish();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear:
                mName.setText("");
                break;
            case R.id.submit:
                if (!TextUtils.isEmpty(mName.getText())) {
                    submit();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActivity(this);
    }
}
