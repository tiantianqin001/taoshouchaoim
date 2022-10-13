package com.tencent.qcloud.tuikit.tuichat.ui.view.message.viewholder;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.qcloud.tuikit.tuichat.R;
import com.tencent.qcloud.tuikit.tuichat.bean.GetReadBean;
import com.tencent.qcloud.tuikit.tuichat.bean.message.TUIMessageBean;
import com.tencent.qcloud.tuikit.tuichat.interfaces.CallBackUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.jar.JarEntry;

public class GetRedHolder extends MessageContentHolder {


    private final LinearLayout ll_red_tips;
    private final TextView re_edit;
    private final TextView message_top_time_tv;

    public GetRedHolder(View itemView) {
        super(itemView);
        ll_red_tips = itemView.findViewById(R.id.ll_red_tips);
        re_edit = itemView.findViewById(R.id.re_edit);
        message_top_time_tv = itemView.findViewById(R.id.message_top_time_tv);
        ll_red_tips.setVisibility(View.VISIBLE);

    }

    @Override
    public void layoutVariableViews(TUIMessageBean msg, int position) {
        msgArea.setBackground(null);
        msgArea.setPadding(0, 0, 0, 0);
        if (msg instanceof GetReadBean){
            usernameText.setVisibility(View.GONE);
            leftUserIcon.setVisibility(View.GONE);
            rightUserIcon.setVisibility(View.GONE);
            message_top_time_tv.setVisibility(View.GONE);
            String robNickName = ((GetReadBean) msg).robNickName;
           message_top_group_info_tv.setText(""+robNickName+""+"领取了红包");

            String nickName = msg.getNickName();
            if (robNickName.equals(nickName)){
                re_edit.setText(""+robNickName+""+"领取了我的");

            }else {
                re_edit.setText("我领取了"+ nickName+"的");
            }

            byte[] data = msg.getV2TIMMessage().getCustomElem().getData();
            String info = new String(data);
            if (!TextUtils.isEmpty(info)){
                try {
                    JSONObject jsonObject = new JSONObject(info);
                    int redPacketId = jsonObject.optInt("redPacketId");
                    CallBackUtils.getStatus(String.valueOf(redPacketId));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            Log.i("", "layoutVariableViews: "+info);
            //发送红包消息状态的回调

        }

    }

    @Override
    public int getVariableLayout() {
        return 0;
    }
}
