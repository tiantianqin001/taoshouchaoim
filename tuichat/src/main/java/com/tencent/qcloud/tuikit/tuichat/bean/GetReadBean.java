package com.tencent.qcloud.tuikit.tuichat.bean;

import com.google.gson.Gson;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.qcloud.tuikit.tuichat.TUIChatConstants;
import com.tencent.qcloud.tuikit.tuichat.bean.message.RedenvelopeBean;
import com.tencent.qcloud.tuikit.tuichat.bean.message.TUIMessageBean;

import org.json.JSONException;
import org.json.JSONObject;

public class GetReadBean extends TUIMessageBean {
    public String businessID = TUIChatConstants.USER_TYPING_RED_GET;
    public String robNickName;
    @Override
    public String onGetDisplayString() {
        return robNickName+"领取了红包";
    }

    @Override
    public void onProcessMessage(V2TIMMessage v2TIMMessage) {
        String data = new String(v2TIMMessage.getCustomElem().getData());
        try {
            JSONObject jsonObject = new JSONObject(data);
            String robNickName = jsonObject.optString("robNickName");
            this.robNickName = robNickName;
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
