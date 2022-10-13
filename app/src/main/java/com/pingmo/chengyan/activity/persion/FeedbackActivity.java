package com.pingmo.chengyan.activity.persion;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.toast.ToastUtils;
import com.pingmo.chengyan.MyAPP;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.manager.BaseActivity;
import com.pingmo.chengyan.bean.OrderListBean;
import com.pingmo.chengyan.net.Common;
import com.pingmo.chengyan.utils.OkHttp3_0Utils;
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

public class FeedbackActivity extends BaseActivity implements View.OnClickListener {

    TextView mTvNum;
    EditText mEtDetail;
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initView();
        addActivity(this);
    }

    private void initView() {
        getTitleBar().setTitle("意见反馈");
        getTitleBar().enableBack();
        ImmersionBar.setTitleBar(this, getTitleBar().getTitle());
        mTvNum = findViewById(R.id.tv_num);
        mEtDetail = findViewById(R.id.et_detail);

        findViewById(R.id.btn_to_save).setOnClickListener(this);
        mEtDetail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 250) {
                    mTvNum.setTextColor(Color.parseColor("#ffff4444"));
                } else if (s.length() > 250) {
                    mEtDetail.setError("输入超长");
                    mTvNum.setTextColor(Color.parseColor("#ffff4444"));
                    return;
                } else {
                    mTvNum.setTextColor(Color.parseColor("#666666"));
                }
                mTvNum.setText(String.format("%d/250", s.length()));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_to_save:
                if (mEtDetail.getText() == null || TextUtils.isEmpty(mEtDetail.getText().toString())) {
                    ToastUtils.show("请填写反馈内容！");
                    return;
                }
                if (mEtDetail.getText().toString().length() > 250) {
                    ToastUtils.show("反馈内容过长");
                    return;
                }
              //  mViewModel.setFeedbackResult(mEtDetail.getText().toString());
                feedInfo(mEtDetail.getText().toString());

                break;
            default:
        }
    }

    private void feedInfo(String content) {
        String url = Common.FEEDBACK;
        Map<String, String> mapParams = new LinkedHashMap<>();
        mapParams.put("title", "意见反馈");
        mapParams.put("content", content);
        Map<String, String> headerParams = new LinkedHashMap<>();
        headerParams.put("token", SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("token"));
        OkHttp3_0Utils.getInstance().asyncBodyOkHttp(url, mapParams, headerParams, new Callback() {
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
                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               finish();
                               ToastUtils.show("提交成功");
                           }
                       });

                    }else if (code == 403){
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
                    message.obj = "获取数据失败";
                    mHandler.sendMessage(message);
                }
            }
        });
    }
}
