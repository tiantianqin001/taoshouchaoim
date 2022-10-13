package com.pingmo.chengyan.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.pingmo.chengyan.MyAPP;
import com.pingmo.chengyan.activity.zxing.ScanGeneratectivity;
import com.pingmo.chengyan.bean.LoginBean;
import com.pingmo.chengyan.utils.SharedPreferenceUtil;

public class GetGeneratecBroadcastReceiver extends BroadcastReceiver {
    private final String ACTION_BOOT = "cn.twle.android.sendbroadcast.GetGeneratec";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("", "onReceive: ");

        if( ACTION_BOOT.equals(intent.getAction())){
            String userInfo = SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("user_info");
            if (!TextUtils.isEmpty(userInfo)) {
                LoginBean.DataDTO user = JSON.parseObject(userInfo, LoginBean.DataDTO.class);
                String headImage = user.getHeadImage();
                int userId = user.getUserId();
                String nickName = user.getNickName();
                Intent intent1 = new Intent(context, ScanGeneratectivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.putExtra("userId",userId+"");
                intent1.putExtra("photo", headImage);
                intent1.putExtra("nameTxt", nickName);
                context.startActivity(intent1);
            }




        }

    }



}
