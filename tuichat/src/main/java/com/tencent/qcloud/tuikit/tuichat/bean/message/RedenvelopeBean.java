package com.tencent.qcloud.tuikit.tuichat.bean.message;

import com.google.gson.Gson;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.qcloud.tuikit.tuichat.TUIChatConstants;

public class RedenvelopeBean extends TUIMessageBean {
    public String businessID = TUIChatConstants.USER_TYPING_RED;
    public String money;
    public String notes;
    public long redPacketId;

    @Override
    public String onGetDisplayString() {
        return "红包";
    }

    @Override
    public void onProcessMessage(V2TIMMessage v2TIMMessage) {
        String data = new String(v2TIMMessage.getCustomElem().getData());
        RedenvelopeBean redenvelopeBean = new Gson().fromJson(data, RedenvelopeBean.class);

        this.notes = redenvelopeBean.notes;
        this.money = redenvelopeBean.money;
        this.redPacketId = redenvelopeBean.redPacketId;

    }
}
