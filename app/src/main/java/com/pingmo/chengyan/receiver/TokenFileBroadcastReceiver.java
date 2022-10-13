package com.pingmo.chengyan.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.pingmo.chengyan.MyAPP;
import com.pingmo.chengyan.activity.login.LoginActivity;
import com.pingmo.chengyan.utils.QZXTools;
import com.pingmo.chengyan.utils.SharedPreferenceUtil;
import com.tencent.qcloud.tuicore.TUILogin;
import com.tencent.qcloud.tuicore.interfaces.TUICallback;

public class TokenFileBroadcastReceiver extends BroadcastReceiver {
    private final String ACTION_BOOT = "cn.twle.android.sendbroadcast.Setup.Token";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("", "onReceive: ");

        if( ACTION_BOOT.equals(intent.getAction())){
            SharedPreferenceUtil.getInstance(MyAPP.getInstance()).setString("token", "");
            SharedPreferenceUtil.getInstance(MyAPP.getInstance()).setString("userId", "");
            SharedPreferenceUtil.getInstance(MyAPP.getInstance()).setString("userSig", "");
            // SharedPreferenceUtil.getInstance(MyAPP.getInstance()).setString("phone", "");
            SharedPreferenceUtil.getInstance(MyAPP.getInstance()).setString("password", "");
            SharedPreferenceUtil.getInstance(MyAPP.getInstance()).setString("user_info", "");

            Intent intent1 = new Intent(context, LoginActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);

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



        }

    }



}
