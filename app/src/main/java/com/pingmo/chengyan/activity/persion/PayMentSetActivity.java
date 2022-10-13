package com.pingmo.chengyan.activity.persion;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.toast.ToastUtils;
import com.pingmo.chengyan.MyAPP;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.manager.BaseActivity;
import com.pingmo.chengyan.bean.LoginBean;
import com.pingmo.chengyan.utils.SharedPreferenceUtil;

import androidx.annotation.Nullable;

public class PayMentSetActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_set_layout);
        getTitleBar().setTitle("支付设置");
        getTitleBar().enableBack();
        ImmersionBar.setTitleBar(this, getTitleBar().getTitle());
        //忘记密码
        findViewById(R.id.rl_forgot_password).setOnClickListener(this);
        //修改密码
        findViewById(R.id.rl_change_password).setOnClickListener(this);
        addActivity(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            //设置密码
            case R.id.rl_forgot_password:
                if (check()) {
                    Intent intentSet = new Intent(this, ResetPayPasswordActivity.class);
                    intentSet.putExtra("isSet", true);
                    startActivity(intentSet);
                } else {
                    ToastUtils.show("您还未实名认证，请先实名认证通过后再设置支付密码");
                    Intent intentId = new Intent(this, IdentificationActivity.class);
                    startActivity(intentId);
                }
                break;
            //修改密码
            case R.id.rl_change_password:
                if (check()) {
                    Intent intentChange = new Intent(this, ResetPayPasswordActivity.class);
                    intentChange.putExtra("isSet", false);
                    startActivity(intentChange);
                } else {
                    ToastUtils.show("您还未实名认证，请先实名认证通过后再修改支付密码");
                    Intent intentId = new Intent(this, IdentificationActivity.class);
                    startActivity(intentId);
                }
                break;
        }
    }

    private boolean check() {
        String userInfo = SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("user_info");
        if (!TextUtils.isEmpty(userInfo)) {
            LoginBean.DataDTO user = JSON.parseObject(userInfo, LoginBean.DataDTO.class);
            return user.getRealStatus() == 2;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActivity(this);
    }
}
