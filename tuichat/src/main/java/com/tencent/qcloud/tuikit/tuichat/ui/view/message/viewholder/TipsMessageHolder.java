package com.tencent.qcloud.tuikit.tuichat.ui.view.message.viewholder;

import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.tencent.imsdk.v2.V2TIMCustomElem;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.qcloud.tuikit.tuichat.R;
import com.tencent.qcloud.tuikit.tuichat.TUIChatConstants;
import com.tencent.qcloud.tuikit.tuichat.TUIChatService;
import com.tencent.qcloud.tuikit.tuichat.bean.message.TUIMessageBean;
import com.tencent.qcloud.tuikit.tuichat.bean.message.TipsMessageBean;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

public class TipsMessageHolder extends MessageContentHolder {



    public TipsMessageHolder(View itemView) {
        super(itemView);


    }

    @Override
    public int getVariableLayout() {
       // return R.layout.message_adapter_content_tips;
        return 0;
    }



    @Override
    public void layoutVariableViews(TUIMessageBean msg, int position) {
        if (msg instanceof TipsMessageBean) {
            msgArea.setBackground(null);
            msgArea.setPadding(0, 0, 0, 0);
            usernameText.setVisibility(View.GONE);
            leftUserIcon.setVisibility(View.GONE);
            rightUserIcon.setVisibility(View.GONE);
            message_top_group_info_tv.setVisibility(View.VISIBLE);


            String text = ((TipsMessageBean) msg).getText();
            boolean con = text.contains("禁言全员");
            if (con){
                //截取后6位
                String substring = text.substring(text.length() - 6);
                if (substring.equals("取消禁言全员")){
                    EventBus.getDefault().post("1");
                }else {
                    EventBus.getDefault().post("0");
                }

            }
            message_top_group_info_tv.setText(Html.fromHtml(text));
        }
    }

}
