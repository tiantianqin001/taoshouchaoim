package com.tencent.qcloud.tuikit.tuichat.bean.message;

import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.qcloud.tuikit.tuichat.TUIChatConstants;

public class SystemAnnouncementBean extends TUIMessageBean{
    public String businessID = TUIChatConstants.USER_SYSTEM_NOTICE;
    @Override
    public String onGetDisplayString() {
        return "系统公告";
    }

    @Override
    public void onProcessMessage(V2TIMMessage v2TIMMessage) {
        String data = new String(v2TIMMessage.getCustomElem().getData());
    }
}
