package com.tencent.qcloud.tuikit.tuichat.ui.view.message.viewholder;

import android.view.View;

import com.tencent.qcloud.tuikit.tuichat.bean.message.TUIMessageBean;

public class SystemAnnouncentHolder extends MessageContentHolder{
   public SystemAnnouncentHolder(View itemView) {
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