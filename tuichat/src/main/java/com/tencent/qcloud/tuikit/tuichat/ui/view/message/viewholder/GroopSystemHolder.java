package com.tencent.qcloud.tuikit.tuichat.ui.view.message.viewholder;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.tencent.qcloud.tuikit.tuichat.R;
import com.tencent.qcloud.tuikit.tuichat.bean.message.GroupkickBean;
import com.tencent.qcloud.tuikit.tuichat.bean.message.TUIMessageBean;

public class GroopSystemHolder extends MessageContentHolder{
    public static final String TAG = "GroopKickHolder";
    protected TextView mChatTipsTv;
    protected TextView mReEditText;

    public GroopSystemHolder(View itemView) {
        super(itemView);
     
    }

    @Override
    public int getVariableLayout() {
        return 0;
    }



    @Override
    public void layoutVariableViews(TUIMessageBean msg, int position) {

    }
}
