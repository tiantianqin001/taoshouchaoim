package com.pingmo.chengyan.activity.persion;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import com.gyf.immersionbar.ImmersionBar;
import com.hjq.toast.ToastUtils;
import com.pingmo.chengyan.MyAPP;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.manager.BaseActivity;
import com.pingmo.chengyan.net.Common;
import com.pingmo.chengyan.utils.OkHttp3_0Utils;
import com.pingmo.chengyan.utils.QZXTools;
import com.pingmo.chengyan.utils.SharedPreferenceUtil;
import com.tencent.qcloud.tuicore.TUILogin;
import com.tencent.qcloud.tuicore.component.LineControllerView;
import com.tencent.qcloud.tuikit.tuicontact.TUIContactService;
import com.tencent.qcloud.tuikit.tuicontact.ui.pages.MyBlackListActivity;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SafeActivity extends BaseActivity implements View.OnClickListener {

    private LineControllerView siv_friend_verify;
    public static final String TAG = "SafeActivity";
    private LineControllerView siv_search_phone;
    private Handler handler = new Handler();
    private LineControllerView siv_login_device_management;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_layout);
        initView();
        addActivity(this);
    }

    private void initView() {
        getTitleBar().setTitle("安全隐私");
        getTitleBar().enableBack();
        ImmersionBar.setTitleBar(this, getTitleBar().getTitle());
        //手机号的搜索
        siv_search_phone = findViewById(R.id.siv_search_phone);
        int phoneVerify = SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getInt("phoneVerify", 1);
        if (phoneVerify == 1){
            siv_search_phone.setChecked(true);
        }else {
            siv_search_phone.setChecked(false);
        }

        siv_search_phone.setCheckListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                String url = Common.MODIFY_PRIVACY;
                Map<String, String> mapParams = new LinkedHashMap<>();
                mapParams.put("groupVerify", 1+"");
                mapParams.put("idSearchVerify", 1+"");
                if (isChecked){
                    mapParams.put("phoneVerify", 1+"");
                }else {
                    mapParams.put("phoneVerify", 0+"");
                }
                Map<String, String> headParams = new LinkedHashMap<>();
                headParams.put("token", SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("token"));
                OkHttp3_0Utils.getInstance().asyncBodyOkHttp(url, mapParams, headParams, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        QZXTools.logE("onResponse: " + e.getMessage(), null);

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
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (isChecked){
                                                ToastUtils.show("允许手机号搜索");
                                                SharedPreferenceUtil.getInstance(MyAPP.getInstance()).setInt("phoneVerify",1);
                                            }else {
                                                ToastUtils.show("不允许手机号搜索");
                                                SharedPreferenceUtil.getInstance(MyAPP.getInstance()).setInt("phoneVerify",0);
                                            }

                                        }
                                    });

                            } else if (code == 403) {
                                Intent intent = new Intent();
                                intent.setAction("cn.twle.android.sendbroadcast.Setup.Token");
                                intent.setComponent(new ComponentName("com.pingmo.chengyan",
                                        "com.pingmo.chengyan.receiver.TokenFileBroadcastReceiver"));
                                sendBroadcast(intent);
                            }
                        } catch (Exception e) {

                        }

                    }
                });
            }
        });



        //添加好友验证
        siv_friend_verify = findViewById(R.id.siv_friend_verify);
        boolean friend_verify = SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getBoolean("siv_friend_verify", true);
        this.siv_friend_verify.setChecked(friend_verify);
        this.siv_friend_verify.setCheckListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i(TAG, "onCheckedChanged: " + isChecked);

                SharedPreferenceUtil.getInstance(MyAPP.getInstance()).setBoolean("siv_friend_verify", isChecked);
                if (isChecked) {
                    //开启好友验证
                    TUILogin.updateProfile(1);
                } else {
                    //关闭嘞好友验证
                    TUILogin.updateProfile(0);
                }
            }
        });
        //黑名单的点击事件
        LineControllerView siv_blacklist = findViewById(R.id.siv_blacklist);
        siv_blacklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TUIContactService.getAppContext(), MyBlackListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                TUIContactService.getAppContext().startActivity(intent);
            }
        });
        //todo 修改密码的点击事件
        LineControllerView siv_modify_login_password = findViewById(R.id.siv_modify_login_password);
        siv_modify_login_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentModify = new Intent(SafeActivity.this, ModifyActivity.class);
                startActivity(intentModify);
            }
        });

        //登录设备管理
        siv_login_device_management = findViewById(R.id.siv_login_device_management);
        siv_login_device_management.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentModify = new Intent(SafeActivity.this, DeviceManagementActivity.class);
                startActivity(intentModify);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActivity(this);
    }
}
