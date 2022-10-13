package com.tencent.qcloud.tuikit.tuichat.bean.message;

import android.util.Log;

import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.qcloud.tuikit.tuichat.TUIChatConstants;

import org.json.JSONException;
import org.json.JSONObject;

public class GroupkickBean extends TUIMessageBean{
    public String businessID = TUIChatConstants.USER_GROUP_KICKINGE;

    public String content;
    public String operationName;
    public String members;
    @Override
    public String onGetDisplayString() {
        return "踢人";
    }

    @Override
    public void onProcessMessage(V2TIMMessage v2TIMMessage) {
        String data = new String(v2TIMMessage.getCustomElem().getData());
        Log.i("", "onProcessMessage: "+data);
        try {
            JSONObject jsonObject = new JSONObject(data);
            String content = jsonObject.optString("content");
            String operationName = jsonObject.optString("operationName");
            String members = jsonObject.optString("members");

            this.content = content;
            this.operationName = operationName;
            this.members = members;

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
