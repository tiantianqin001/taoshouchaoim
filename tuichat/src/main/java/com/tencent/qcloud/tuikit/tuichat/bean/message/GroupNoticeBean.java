package com.tencent.qcloud.tuikit.tuichat.bean.message;

import android.util.Log;

import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.qcloud.tuikit.tuichat.TUIChatConstants;

import org.json.JSONException;
import org.json.JSONObject;

public class GroupNoticeBean extends TUIMessageBean{
    public String businessID = TUIChatConstants.USER_GROUP_NOTICE;

    public String content;
    public String operationName;
    public String members;
    @Override
    public String onGetDisplayString() {
        return "邀请通知";
    }

    @Override
    public void onProcessMessage(V2TIMMessage v2TIMMessage) {

    }
}
