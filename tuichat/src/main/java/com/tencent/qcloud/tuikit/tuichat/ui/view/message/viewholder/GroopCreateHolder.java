package com.tencent.qcloud.tuikit.tuichat.ui.view.message.viewholder;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.tencent.qcloud.tuikit.tuichat.R;
import com.tencent.qcloud.tuikit.tuichat.bean.message.GroupCreateBean;
import com.tencent.qcloud.tuikit.tuichat.bean.message.GroupkickBean;
import com.tencent.qcloud.tuikit.tuichat.bean.message.TUIMessageBean;
import com.tencent.qcloud.tuikit.tuichat.util.EncryptUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class GroopCreateHolder extends MessageContentHolder {
    public static final String TAG = "GroopKickHolder";
    protected TextView mChatTipsTv;
    protected TextView mReEditText;


    public GroopCreateHolder(View itemView) {
        super(itemView);



        mChatTipsTv = itemView.findViewById(R.id.chat_tips_tv);
        mReEditText = itemView.findViewById(R.id.re_edit);
    }

    @Override
    public int getVariableLayout() {
        return R.layout.message_adapter_content_tips;
    }


    @Override
    public void layoutVariableViews(TUIMessageBean msg, int position) {
        if (msg instanceof GroupCreateBean) {
            msgArea.setBackground(null);
            msgArea.setPadding(0, 0, 0, 0);
            usernameText.setVisibility(View.GONE);
            leftUserIcon.setVisibility(View.GONE);
            rightUserIcon.setVisibility(View.GONE);
            message_top_group_info_tv.setVisibility(View.VISIBLE);
            Log.i(TAG, "layoutVariableViews: " + msg);
            String opUser = ((GroupCreateBean) msg).opUser;
            String content = ((GroupCreateBean) msg).content;

            message_top_group_info_tv.setText("\"" + opUser + "\"" + content);



        }
    }
}
