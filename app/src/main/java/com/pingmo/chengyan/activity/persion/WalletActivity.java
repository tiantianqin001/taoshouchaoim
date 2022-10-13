package com.pingmo.chengyan.activity.persion;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.gyf.immersionbar.ImmersionBar;
import com.pingmo.chengyan.MyAPP;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.manager.BaseActivity;
import com.pingmo.chengyan.bean.LoginBean;
import com.pingmo.chengyan.bean.WalletStailBean;
import com.pingmo.chengyan.net.Common;
import com.pingmo.chengyan.utils.OkHttp3_0Utils;
import com.pingmo.chengyan.utils.QZXTools;
import com.pingmo.chengyan.utils.SharedPreferenceUtil;
import com.tencent.qcloud.tuicore.component.imageEngine.impl.GlideEngine;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WalletActivity extends BaseActivity implements View.OnClickListener {

    private TextView mCash;
    private double money;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet_layout);
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(false).init();
        ImmersionBar.setTitleBar(this, findViewById(R.id.title));
        mCash = findViewById(R.id.tv_my_balance);
        findViewById(R.id.ll_base_back).setOnClickListener(this);
        findViewById(R.id.recharge).setOnClickListener(this);
        findViewById(R.id.cashout).setOnClickListener(this);
        findViewById(R.id.rl_bank_card).setOnClickListener(this);
        findViewById(R.id.order_list).setOnClickListener(this);
        //支付密码
        findViewById(R.id.rl_payment_password).setOnClickListener(this);
        //实名认证
        findViewById(R.id.rl_identification).setOnClickListener(this);
        //修改支付密码
        findViewById(R.id.rl_modify).setOnClickListener(this);

        addActivity(this);
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String userInfo = SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("user_info");
        if (!TextUtils.isEmpty(userInfo)) {
            LoginBean.DataDTO mUser = JSON.parseObject(userInfo, LoginBean.DataDTO.class);
            findViewById(R.id.realStatus).setVisibility(mUser.getRealStatus() == 2 ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    protected void handleActMessage(Message msg) {
        super.handleActMessage(msg);
        switch (msg.what) {
            case Common.Interface_success:
                WalletStailBean walletStailBean = (WalletStailBean) msg.obj;
                WalletStailBean.DataDTO data = walletStailBean.getData();
                if (data != null) {
                    money = data.getMoney();
                    mCash.setText(String.valueOf(money));
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_base_back:
                finish();
                break;
            case R.id.recharge:
                Intent intentRecharge = new Intent(this, RechargeActivity.class);
                startActivity(intentRecharge);
                break;
            case R.id.cashout:
                Intent intentCashout = new Intent(this, CashOutActivity.class);
                startActivity(intentCashout);
                break;
            case R.id.rl_bank_card:
                //打开银行卡
                Intent intentBank = new Intent(this, BankCardNewActivity.class);
                startActivity(intentBank);
                break;
            case R.id.order_list:
                //打开账单明细
                Intent intentCashList = new Intent(this, CashListActivity.class);
                intentCashList.putExtra("total_money", money);
                startActivity(intentCashList);
                break;
            case R.id.rl_payment_password:
                //支付设置
                Intent intentPayment = new Intent(this, PayMentSetActivity.class);
                startActivity(intentPayment);
                break;
            //实名认证
            case R.id.rl_identification:
                Intent intentId = new Intent(this, IdentificationActivity.class);
                startActivity(intentId);
                break;
            //修改支付密码
            case R.id.rl_modify:
                Intent intentModify = new Intent(this, ModifyActivity.class);
                startActivity(intentModify);
                break;
        }
    }


    private void initData() {
        String url = Common.WALLET_INFORMATION;
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
                        WalletStailBean walletStailBean = JSON.parseObject(content, WalletStailBean.class);
                        Message message = Message.obtain();
                        message.what = Common.Interface_success;
                        message.obj = walletStailBean;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActivity(this);
    }
}
