package com.tencent.qcloud.tuikit.tuiconversation.ui.page;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gyf.immersionbar.ImmersionBar;
import com.tencent.qcloud.tuikit.tuiconversation.R;
import com.tencent.qcloud.tuikit.tuiconversation.adapter.SystemSetAdapter;
import com.tencent.qcloud.tuikit.tuiconversation.bean.SystemMessageBean;
import com.tencent.qcloud.tuikit.tuiconversation.nets.Common;
import com.tencent.qcloud.tuikit.tuiconversation.nets.OkHttp3_0Utils;


import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SystemSetMessageActivity extends Activity {

    private static final String TAG = "SystemSetMessageActivity";
    private RecyclerView rv_system_message;
    private TextView tv_system_title;
    private JSONArray rows;
    private static final int sucess = 100;
    private List<SystemMessageBean> mSystemMessageBeans = new ArrayList<>();

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case sucess:
                    List<SystemMessageBean> systemMessageBeanList = (List<SystemMessageBean>) msg.obj;
                    if (systemMessageBeanList!=null && systemMessageBeanList.size()>0){
                        rv_system_message.setLayoutManager(new LinearLayoutManager(SystemSetMessageActivity.this));

                        SystemSetAdapter systemSetAdapter = new SystemSetAdapter(systemMessageBeanList);
                        rv_system_message.setAdapter(systemSetAdapter);
                    }
                    break;
            }

        }
    };

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置沉静状态栏
        ImmersionBar.with(this)
                .transparentBar()
                .init();
        setContentView(R.layout.system_set_layout);

       TextView tv_system_title =  findViewById(R.id.tv_system_title);
        tv_system_title.setText("系统消息");
        initView();
    }

    private void initView() {
        rv_system_message = findViewById(R.id.rv_system_message);
        LinearLayout ll_system_back = findViewById(R.id.ll_system_back);


        ll_system_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSystemMessageBeans.clear();
        setSystenInfo();
    }

    @SuppressLint("LongLogTag")
    public void setSystenInfo() {
        String url = Common.SYSTEMNOTIFICATION;
        Map<String, String> mapParams = new LinkedHashMap<>();
        mapParams.put("pageNum", "1");
        mapParams.put("pageSize", "100");

        OkHttp3_0Utils.getInstance().asyncGetOkHttp(url, mapParams, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String content = response.body().string();
                Log.i(TAG, "onResponse: "+content);
                try {
                    JSONObject jsonObject = new JSONObject(content);
                    int code = jsonObject.optInt("code");
                    if (code == 200){
                        JSONObject data = jsonObject.optJSONObject("data");
                        rows = data.optJSONArray("rows");
                        if (rows !=null && rows.length() >0){
                            mSystemMessageBeans.clear();
                            for (int i = 0; i < rows.length(); i++) {
                                JSONObject jsonObject1 = rows.optJSONObject(i);
                                String createTime = jsonObject1.optString("createTime");
                                String title = jsonObject1.optString("title");
                                String contents = jsonObject1.optString("content");
                                SystemMessageBean systemMessageBean = new SystemMessageBean();
                                systemMessageBean.setContent(contents);
                                systemMessageBean.setCreateTime(createTime);
                                systemMessageBean.setTitle(title);
                                mSystemMessageBeans.add(systemMessageBean);
                            }
                        }
                        Message message = Message.obtain();
                        message.what = sucess;
                        message.obj = mSystemMessageBeans;
                        handler.sendMessage(message);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }
}
