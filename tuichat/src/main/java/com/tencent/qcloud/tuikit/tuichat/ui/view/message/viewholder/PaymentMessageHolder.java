package com.tencent.qcloud.tuikit.tuichat.ui.view.message.viewholder;

import android.util.Log;
import android.view.View;

import com.tencent.qcloud.tuikit.tuichat.bean.message.TUIMessageBean;

public class PaymentMessageHolder  extends MessageContentHolder{
   public PaymentMessageHolder(View itemView) {
      super(itemView);
   }

   @Override
   public int getVariableLayout() {
      return 0;
   }

   @Override
   public void layoutVariableViews(TUIMessageBean msg, int position) {
      msgArea.setBackground(null);
      msgArea.setPadding(0, 0, 0, 0);
      Log.i("", "layoutVariableViews: "+msg);

   }
}
