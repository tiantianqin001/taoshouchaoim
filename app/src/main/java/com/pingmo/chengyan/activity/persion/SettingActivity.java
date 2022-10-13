package com.pingmo.chengyan.activity.persion;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;

import com.gyf.immersionbar.ImmersionBar;
import com.pingmo.chengyan.MyAPP;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.login.LoginActivity;
import com.pingmo.chengyan.activity.manager.BaseActivity;
import com.pingmo.chengyan.customview.CommonDialog;
import com.pingmo.chengyan.net.Common;
import com.pingmo.chengyan.utils.OkHttp3_0Utils;
import com.tencent.qcloud.tuicore.TUILogin;
import com.tencent.qcloud.tuicore.interfaces.TUICallback;
import com.pingmo.chengyan.utils.QZXTools;
import com.pingmo.chengyan.utils.SharedPreferenceUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_layout);
        initView();
    }

    private void initView() {
        getTitleBar().setTitle("设置");
        getTitleBar().enableBack();
        ImmersionBar.setTitleBar(this, getTitleBar().getTitle());
        findViewById(R.id.about).setOnClickListener(this);
        findViewById(R.id.writeoff).setOnClickListener(this);
        findViewById(R.id.loginout).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.about:
                Intent intentAbout = new Intent(this, AboutActivity.class);
                startActivity(intentAbout);
                break;
            case R.id.writeoff:
                writeoffClick();
                break;
            case R.id.loginout:
                loginOut();
                break;
        }
    }

    private void writeoffClick() {
        CommonDialog commonDialog = new CommonDialog(this);
        commonDialog.setMessage("确定注销吗？");
        commonDialog.setPositiveButton(new CommonDialog.BtnClickedListener() {
            @Override
            public void onBtnClicked() {
                doWriteOff();
            }
        });
        commonDialog.setCancelButton(new CommonDialog.BtnClickedListener() {
            @Override
            public void onBtnClicked() {

            }
        });
        commonDialog.showDialog();
    }

    private void doWriteOff() {
        String url = Common.CANCELLATION;
        Map<String, String> mapParams = new LinkedHashMap<>();
        Map<String, String> headParams = new LinkedHashMap<>();
        headParams.put("token", SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("token"));
        OkHttp3_0Utils.getInstance().asyncBodyOkHttp(url, mapParams, headParams, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                QZXTools.logD(e.getMessage());
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
                        LoginActivity.onLoginOut(SettingActivity.this);
                    } else {
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    private void loginOut() {
        String url = Common.LOGINOUT;
        Map<String, String> mapParams = new LinkedHashMap<>();
        Map<String, String> headParams = new LinkedHashMap<>();
        headParams.put("token", SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("token"));
        OkHttp3_0Utils.getInstance().asyncBodyOkHttp(url, mapParams, headParams, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String content = response.body().string();
                QZXTools.logE("onResponse: " + content, null);
            }
        });

        //腾讯的也退出
        TUILogin.logout(new TUICallback() {
            @Override
            public void onSuccess() {
                QZXTools.logE("腾讯退出成功", null);

            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                QZXTools.logE(errorMessage, null);
            }
        });

        LoginActivity.onLoginOut(SettingActivity.this);
      finish();


    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActivity(this);
    }
}
