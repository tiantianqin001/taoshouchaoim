package com.tencent.qcloud.tuikit.tuichat.ui.view.message.viewholder;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.qcloud.tuikit.tuichat.R;
import com.tencent.qcloud.tuikit.tuichat.bean.message.StartTransferBean;
import com.tencent.qcloud.tuikit.tuichat.bean.message.TUIMessageBean;
public class TransferMessageHolder extends MessageContentHolder {


    public String desc;
    public String greetings;
    public TransferMessageHolder(View itemView) {
        super(itemView);
    }

    @Override
    public int getVariableLayout() {
        return R.layout.chat_transfer_layout;
    }
    @Override
    public void layoutVariableViews(TUIMessageBean msg, int position) {
        msgArea.setBackground(null);
        msgArea.setPadding(0, 0, 0, 0);

        RelativeLayout  rl_base =  itemView.findViewById(R.id.rl_base);
        TextView tv_transfer_money =  itemView.findViewById(R.id.tv_transfer_money);
        if (msg instanceof StartTransferBean){
            //当前发送消息的userid
            String money = ((StartTransferBean) msg).money;
            tv_transfer_money.setText("￥ "+money);
            String nickName = msg.getNickName();

            if (msg.getV2TIMMessage().isSelf()){
              //是我自己发的消息;
              rl_base.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.transfer_background_right));
          }else {
              rl_base.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.transfer_background_left));
          }

//            rl_base.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                   if (msg.getV2TIMMessage().isSelf()){
//                       //如果是我发的转账
//                       Intent intent = new Intent(itemView.getContext(), TransferMyDetailsActivity.class);
//                       intent.putExtra("isSelf",true);
//                       intent.putExtra("nickName",nickName);
//                       intent.putExtra("currentTime", DeviceUtil.getNowTime());
//                       itemView.getContext().startActivity(intent);
//
//                   }else {
//                       //朋友发给我的 打开一个dialogeActivity
//                       Intent intent = new Intent(itemView.getContext(), RedPacketDetailsMeActivity.class);
//                       itemView.getContext().startActivity(intent);
//                   }
//                }
//            });
      }
    }
}
