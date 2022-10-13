package com.tencent.qcloud.tuikit.tuichat.ui.view.message.viewholder;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.tencent.qcloud.tuikit.tuichat.R;
import com.tencent.qcloud.tuikit.tuichat.bean.message.GroupAddBean;
import com.tencent.qcloud.tuikit.tuichat.bean.message.GroupCreateBean;
import com.tencent.qcloud.tuikit.tuichat.bean.message.TUIMessageBean;

public class GroopAddHolder extends MessageContentHolder{
    public static final String TAG = "GroopKickHolder";
    protected TextView mChatTipsTv;
    protected TextView mReEditText;

    public GroopAddHolder(View itemView) {
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
        if (msg instanceof GroupAddBean){
            msgArea.setBackground(null);
            msgArea.setPadding(0, 0, 0, 0);
            usernameText.setVisibility(View.GONE);
            leftUserIcon.setVisibility(View.GONE);
            rightUserIcon.setVisibility(View.GONE);
            message_top_group_info_tv.setVisibility(View.VISIBLE);
            Log.i(TAG, "layoutVariableViews: "+msg);
            String invitationName = ((GroupAddBean) msg).invitationName;
            String content = ((GroupAddBean) msg).content;
            String description = ((GroupAddBean) msg).description;
            if (!TextUtils.isEmpty(invitationName) && !TextUtils.isEmpty(content)){
               message_top_group_info_tv.setText("\""+invitationName+"\""+"邀请"+content+"进入群聊");

            }


        }
    }
}
