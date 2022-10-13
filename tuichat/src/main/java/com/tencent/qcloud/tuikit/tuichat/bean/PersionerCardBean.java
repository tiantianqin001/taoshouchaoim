package com.tencent.qcloud.tuikit.tuichat.bean;

import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.qcloud.tuikit.tuichat.TUIChatConstants;
import com.tencent.qcloud.tuikit.tuichat.bean.message.TUIMessageBean;

import org.json.JSONException;
import org.json.JSONObject;

public class PersionerCardBean extends TUIMessageBean {

    public String businessID = TUIChatConstants.USER_PERSION_CARD;
    public String headImage;
    public String ninkName;
    public String userId;
    @Override
    public String onGetDisplayString() {
        return "个人名片";
    }

    @Override
    public void onProcessMessage(V2TIMMessage v2TIMMessage) {
        String data = new String(v2TIMMessage.getCustomElem().getData());
        try {
            JSONObject jsonObject = new JSONObject(data);
            String headImage = jsonObject.optString("headImage");
            if(!headImage.contains("http")){
                headImage = "http://api.njpingmo.com.cn"+headImage;
            }


            String ninkName = jsonObject.optString("nickName");
            ninkName = ninkName.replaceAll("\r|\n", "");
            String userId = jsonObject.optString("userId");

            this.headImage = headImage;
            this.ninkName = ninkName;
            this.userId = userId;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
