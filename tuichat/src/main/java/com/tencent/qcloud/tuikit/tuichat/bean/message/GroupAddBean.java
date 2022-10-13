package com.tencent.qcloud.tuikit.tuichat.bean.message;

import android.util.Log;

import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.qcloud.tuikit.tuichat.TUIChatConstants;

import org.json.JSONException;
import org.json.JSONObject;

public class GroupAddBean extends TUIMessageBean{
    public String businessID = TUIChatConstants.USER_GROUP_INVITATION;

    public String content;
    public String invitationName;
    public String description;

    @Override
    public String onGetDisplayString() {
        return invitationName+"邀请"+content+"进入群聊";
    }

    @Override
    public void onProcessMessage(V2TIMMessage v2TIMMessage) {
        String data = new String(v2TIMMessage.getCustomElem().getData());
        Log.i("", "onProcessMessage: "+data);
        try {
            JSONObject jsonObject = new JSONObject(data);
            String content = jsonObject.optString("content");
            String invitationName = jsonObject.optString("invitationName");
            String description = jsonObject.optString("description");

            this.content = content;
            this.invitationName = invitationName;
            this.description = description;

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
