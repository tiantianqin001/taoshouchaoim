package com.pingmo.chengyan.activity.persion;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener;
import com.pingmo.chengyan.MyAPP;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.manager.BaseActivity;
import com.pingmo.chengyan.adapter.DeviceManageAdapter;
import com.pingmo.chengyan.bean.DeviceMessageBean;
import com.pingmo.chengyan.bean.WalletStailBean;
import com.pingmo.chengyan.net.Common;
import com.pingmo.chengyan.utils.OkHttp3_0Utils;
import com.pingmo.chengyan.utils.QZXTools;
import com.pingmo.chengyan.utils.SharedPreferenceUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DeviceManagementActivity extends BaseActivity implements DeviceManageAdapter.OnClickListener {

    private RecyclerView rv_device_manage;
    private TextView tv_no_data;
    public static final String TAG = "DeviceManagementActivity";
    private List<DeviceMessageBean.DataDTO> data;

    @Override
    protected void handleActMessage(Message msg) {
        super.handleActMessage(msg);
        switch (msg.what) {
            case Common.Interface_success:
                DeviceMessageBean deviceMessageBean = (DeviceMessageBean) msg.obj;
                data = deviceMessageBean.data;
                if (data != null && data.size() > 0) {
                    rv_device_manage.setLayoutManager(new LinearLayoutManager(this));
                    DeviceManageAdapter deviceManageAdapter = new DeviceManageAdapter(this, data);
                    deviceManageAdapter.setOnClickListener(this);

                    rv_device_manage.setAdapter(deviceManageAdapter);
                } else {
                    //没有数据
                    tv_no_data.setVisibility(View.VISIBLE);
                    rv_device_manage.setVisibility(View.GONE);
                }
                break;
            case Common.Interface_success_law:
                //删除设备成功
                if (data != null && data.size() > 0) {
                    rv_device_manage.setLayoutManager(new LinearLayoutManager(this));
                    DeviceManageAdapter deviceManageAdapter = new DeviceManageAdapter(this, data);
                    deviceManageAdapter.setOnClickListener(this);

                    rv_device_manage.setAdapter(deviceManageAdapter);
                } else {
                    //没有数据
                    tv_no_data.setVisibility(View.VISIBLE);
                    rv_device_manage.setVisibility(View.GONE);
                }
                break;
        }
    }


    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_manage_layout);

        rv_device_manage = findViewById(R.id.rv_device_manage);
        tv_no_data = findViewById(R.id.tv_no_data);
        TextView title = findViewById(R.id.title);
        title.setText("登录设备管理");
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initData();

    }

    private void initData() {
        String url = Common.LOGIN_DEVICE_LIST;
        Map<String, String> headerParams = new LinkedHashMap<>();
        headerParams.put("token", SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("token"));
        OkHttp3_0Utils.getInstance().asyncGetOkHttpHadHeader(url, headerParams, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Message message = Message.obtain();
                message.what = Common.Interface_err;
                message.obj = "获取数据失败";
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
                        DeviceMessageBean deviceMessageBean = JSON.parseObject(content, DeviceMessageBean.class);
                        Message message = Message.obtain();
                        message.what = Common.Interface_success;
                        message.obj = deviceMessageBean;
                        mHandler.sendMessage(message);

                    } else if (code == 403) {
                        Intent intent = new Intent();
                        intent.setAction("cn.twle.android.sendbroadcast.Setup.Token");
                        intent.setComponent(new ComponentName("com.pingmo.chengyan",
                                "com.pingmo.chengyan.receiver.TokenFileBroadcastReceiver"));
                        sendBroadcast(intent);
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
    @SuppressLint("LongLogTag")
    @Override
    public void onClickListener(int pos) {
        //点击了删除
        Log.i(TAG, "onClickListener: ");
        MessageDialog.show("删除", "确定要删除么？", "确定","取消").setOkButton(new OnDialogButtonClickListener<MessageDialog>() {
            @Override
            public boolean onClick(MessageDialog baseDialog, View v) {
                deleItem(pos);
                return false;
            }
        });



    }

    private void deleItem(int pos) {
        if (data!=null){
            DeviceMessageBean.DataDTO dataDTO = data.get(pos);
            Integer id = dataDTO.id;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id",id);
                RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8")
                        , jsonObject.toString());

                String url = Common.LOGIN_DEVICE_LIST_DEL+"?id="+id;
                Map<String, String> headerParams = new LinkedHashMap<>();
                headerParams.put("token", SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("token"));
                OkHttp3_0Utils.getInstance().asyncDeleteOkHttp(url, headerParams, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Message message = Message.obtain();
                        message.what = Common.Interface_err;
                        message.obj = "获取数据失败";
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
                                data.remove(pos);
                                Message message = Message.obtain();
                                message.what = Common.Interface_success_law;
                                mHandler.sendMessage(message);

                            } else if (code == 403) {
                                Intent intent = new Intent();
                                intent.setAction("cn.twle.android.sendbroadcast.Setup.Token");
                                intent.setComponent(new ComponentName("com.pingmo.chengyan",
                                        "com.pingmo.chengyan.receiver.TokenFileBroadcastReceiver"));
                                sendBroadcast(intent);
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
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}
