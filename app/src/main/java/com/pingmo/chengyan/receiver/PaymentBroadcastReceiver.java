package com.pingmo.chengyan.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.pingmo.chengyan.activity.persion.PayMentSetActivity;

public class PaymentBroadcastReceiver extends BroadcastReceiver {
    private final String ACTION_BOOT = "cn.twle.android.sendbroadcast.Setup.payment.password";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("", "onReceive: ");

        if( ACTION_BOOT.equals(intent.getAction())){
            Intent intent1 = new Intent(context, PayMentSetActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        }

    }



}
