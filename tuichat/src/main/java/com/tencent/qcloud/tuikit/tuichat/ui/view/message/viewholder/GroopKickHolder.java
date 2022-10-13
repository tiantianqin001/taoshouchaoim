package com.tencent.qcloud.tuikit.tuichat.ui.view.message.viewholder;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.tencent.qcloud.tuikit.tuichat.R;
import com.tencent.qcloud.tuikit.tuichat.TUIChatConstants;
import com.tencent.qcloud.tuikit.tuichat.TUIChatService;
import com.tencent.qcloud.tuikit.tuichat.bean.message.GroupkickBean;
import com.tencent.qcloud.tuikit.tuichat.bean.message.TUIMessageBean;

public class GroopKickHolder extends MessageContentHolder{
    public static final String TAG = "GroopKickHolder";
    protected TextView mChatTipsTv;
    protected TextView mReEditText;

    public GroopKickHolder(View itemView) {
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
        if (msg instanceof GroupkickBean){
            msgArea.setBackground(null);
            msgArea.setPadding(0, 0, 0, 0);
            usernameText.setVisibility(View.GONE);
            leftUserIcon.setVisibility(View.GONE);
            rightUserIcon.setVisibility(View.GONE);
            message_top_group_info_tv.setVisibility(View.VISIBLE);
            Log.i(TAG, "layoutVariableViews: "+msg);
            String operationName = ((GroupkickBean) msg).operationName;
            String content = ((GroupkickBean) msg).content;

           // mChatTipsTv.setText(""+content+""+"被踢出群组");
          //  message_top_group_info_tv.setText("\""+content+"\""+"被踢出群组");
            message_top_group_info_tv.setText("\""+operationName+"\""+"将"+content+"踢出群聊");

        }
    }
}
