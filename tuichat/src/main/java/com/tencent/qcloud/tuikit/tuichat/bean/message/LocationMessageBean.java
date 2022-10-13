package com.tencent.qcloud.tuikit.tuichat.bean.message;

import com.google.gson.Gson;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.qcloud.tuikit.tuichat.TUIChatConstants;
import com.tencent.qcloud.tuikit.tuichat.bean.message.reply.LocationReplyQuoteBean;
import com.tencent.qcloud.tuikit.tuichat.bean.message.reply.TUIReplyQuoteBean;

/**
 * 定位消息
 */
public class LocationMessageBean extends TUIMessageBean {
    public String businessID = TUIChatConstants.USER_TYPING_LOCATION;
    public String desc;
    public double latitude;
    public double longitude;


    @Override
    public String onGetDisplayString() {
        return "[位置]"+desc;
    }

    @Override
    public void onProcessMessage(V2TIMMessage v2TIMMessage) {
        String data = new String(v2TIMMessage.getCustomElem().getData());
        LocationMessageBean locationMessageBean = new Gson().fromJson(data, LocationMessageBean.class);
        this.desc = locationMessageBean.desc;
        this.longitude = locationMessageBean.longitude;
        this.latitude = locationMessageBean.latitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public Class<? extends TUIReplyQuoteBean> getReplyQuoteBeanClass() {
        return LocationReplyQuoteBean.class;
    }
}
