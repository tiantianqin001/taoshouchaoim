package com.tencent.qcloud.tuikit.tuichat.bean.message;

import android.util.Log;

import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.qcloud.tuikit.tuichat.TUIChatConstants;

import org.json.JSONException;
import org.json.JSONObject;

public class GroupCreateBean extends TUIMessageBean{
    public String businessID = TUIChatConstants.USER_GROUP_CREATE;

    public String content;
    public String opUser;

    @Override
    public String onGetDisplayString() {
        return opUser+content;
    }

    @Override
    public void onProcessMessage(V2TIMMessage v2TIMMessage) {
        String data = new String(v2TIMMessage.getCustomElem().getData());
        Log.i("", "onProcessMessage: "+data);
        try {
            JSONObject jsonObject = new JSONObject(data);
            String content = jsonObject.optString("content");
            String operationName = jsonObject.optString("opUser");

            this.content = content;
            this.opUser = operationName;


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
