package com.tencent.qcloud.tuikit.tuichat.bean.message;

import com.google.gson.Gson;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.qcloud.tuikit.tuichat.TUIChatConstants;

import org.json.JSONException;
import org.json.JSONObject;

public class StartTransferBean extends TUIMessageBean {
    public String businessID = TUIChatConstants.USER_TYPING_TRANSFER;

    public String money;
    public String notes;
    public String transferTime;
    public String collectionTime;
    private V2TIMMessage v2TIMMessage;

    @Override
    public String onGetDisplayString() {
        return "[转账]"+notes;
    }

    @Override
    public void onProcessMessage(V2TIMMessage v2TIMMessage) {
        this.v2TIMMessage = v2TIMMessage;
        String data = new String(v2TIMMessage.getCustomElem().getData());
        try {
            JSONObject jsonObject = new JSONObject(data);
            String notes = jsonObject.optString("notes");
            String money = jsonObject.optString("money");
            this.money = money;
            this.notes = notes;
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        this.money = startTransferBean.money;
//        this.notes = startTransferBean.notes;

    }
}
