package com.tencent.qcloud.tuikit.tuichat.bean.message;

import android.util.Log;

import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.qcloud.tuikit.tuichat.TUIChatConstants;

import org.json.JSONException;
import org.json.JSONObject;

public class GroupTabooBean extends TUIMessageBean{
    public String businessID = TUIChatConstants.USER_GROUP_GROUP_MUTE_ALL;

    public boolean muteStatus;


    @Override
    public String onGetDisplayString() {
        if (muteStatus){
            return "取消全员禁言";
        }
        return "全员禁言";
    }

    @Override
    public void onProcessMessage(V2TIMMessage v2TIMMessage) {
        String data = new String(v2TIMMessage.getCustomElem().getData());
        Log.i("", "onProcessMessage: "+data);
        try {
            JSONObject jsonObject = new JSONObject(data);
            boolean muteStatus = jsonObject.optBoolean("muteStatus");

            this.muteStatus = muteStatus;


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
