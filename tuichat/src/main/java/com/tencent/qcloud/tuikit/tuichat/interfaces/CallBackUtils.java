package com.tencent.qcloud.tuikit.tuichat.interfaces;

public class CallBackUtils {
    public static CallBackStatus mCallBack;
    public static void setmCallBack(CallBackStatus callBack){
        mCallBack = callBack;
    }

    public static void getStatus(String redPacketId){
        if (mCallBack!=null){
            mCallBack.getStatus(redPacketId);
        }

    }
}
