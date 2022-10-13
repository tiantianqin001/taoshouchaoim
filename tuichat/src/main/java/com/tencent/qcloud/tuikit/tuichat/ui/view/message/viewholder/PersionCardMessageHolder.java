package com.tencent.qcloud.tuikit.tuichat.ui.view.message.viewholder;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hjq.toast.ToastUtils;
import com.tencent.imsdk.v2.V2TIMFriendInfo;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMUserFullInfo;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.tencent.qcloud.tuicore.TUILogin;
import com.tencent.qcloud.tuicore.TUIThemeManager;
import com.tencent.qcloud.tuicore.component.imageEngine.impl.GlideEngine;
import com.tencent.qcloud.tuicore.util.ErrorMessageConverter;
import com.tencent.qcloud.tuikit.tuichat.R;
import com.tencent.qcloud.tuikit.tuichat.TUIChatService;
import com.tencent.qcloud.tuikit.tuichat.bean.PersionerCardBean;
import com.tencent.qcloud.tuikit.tuichat.bean.message.GroupTabooBean;
import com.tencent.qcloud.tuikit.tuichat.bean.message.LocationMessageBean;
import com.tencent.qcloud.tuikit.tuichat.bean.message.TUIMessageBean;
import com.tencent.qcloud.tuikit.tuichat.ui.page.GeographicMapStaileActivity;
import com.tencent.qcloud.tuikit.tuicontact.TUIContactConstants;
import com.tencent.qcloud.tuikit.tuicontact.TUIContactService;
import com.tencent.qcloud.tuikit.tuicontact.bean.ContactItemBean;
import com.tencent.qcloud.tuikit.tuicontact.ui.pages.FriendProfileActivity;
import com.tencent.qcloud.tuikit.tuicontact.util.TUIContactLog;

import java.util.ArrayList;
import java.util.List;


public class PersionCardMessageHolder extends MessageContentHolder {

    private ImageView iv_avatar;
    private TextView tv_nink_name;
    private RelativeLayout rl_base;

    private static final String TAG = "Persion";
    public PersionCardMessageHolder(View itemView) {
        super(itemView);

        msgArea.setBackground(null);
        msgArea.setPadding(0, 0, 0, 0);
        iv_avatar = itemView.findViewById(R.id.iv_avatar);
        tv_nink_name = itemView.findViewById(R.id.tv_nink_name);
        rl_base = itemView.findViewById(R.id.rl_base);
    }

    @Override
    public int getVariableLayout() {
        return R.layout.chat_persion_layout;
    }

    @Override
    public void layoutVariableViews(TUIMessageBean msg, int position) {
      if (msg instanceof PersionerCardBean){
          msgArea.setBackground(null);
          msgArea.setPadding(0, 0, 0, 0);
          String ninkName = ((PersionerCardBean) msg).ninkName;
          tv_nink_name.setText(ninkName);
          String headImage = ((PersionerCardBean) msg).headImage;

          Glide.with(TUILogin.getAppContext())
                  .asBitmap()
                  .diskCacheStrategy(DiskCacheStrategy.ALL)
                  .load(headImage)
                  .apply(new RequestOptions().circleCropTransform())
                  .apply(new RequestOptions().error(itemView.getContext().getDrawable(R.drawable.defult_avater)))
                  .into(iv_avatar);

          String userId = ((PersionerCardBean) msg).userId;

          rl_base.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {

                  List<String> userIdList = new ArrayList<>();
                  userIdList.add(userId);
                  String finalResult = userId;
                  V2TIMManager.getInstance().getUsersInfo(userIdList,
                          new V2TIMValueCallback<List<V2TIMUserFullInfo>>() {


                              @Override
                      public void onSuccess(List<V2TIMUserFullInfo> v2TIMUserFullInfos) {
                          if (v2TIMUserFullInfos.isEmpty()) {
                              Log.e(TAG, "get logined userInfo failed. list is empty");

                              ToastUtils.show("当前用户不存在");
                              return;
                          }
                          V2TIMUserFullInfo userFullInfo = v2TIMUserFullInfos.get(0);
                          ContactItemBean contact = new ContactItemBean();
                          contact.setId(userFullInfo.getUserID());
                          contact.setNickName(userFullInfo.getNickName());
                          contact.setAvatarUrl(userFullInfo.getFaceUrl());
                          contact.setEnable(true);

                          contact.setGroup(false);


                          V2TIMManager.getFriendshipManager().getFriendList(new V2TIMValueCallback<List<V2TIMFriendInfo>>() {
                              @Override
                              public void onError(int code, String desc) {
                                  TUIContactLog.e(TAG, "getFriendList err code = " + code + ", desc = " + ErrorMessageConverter.convertIMError(code, desc));

                              }

                              @Override
                              public void onSuccess(List<V2TIMFriendInfo> v2TIMFriendInfos) {
                                  if (v2TIMFriendInfos != null && v2TIMFriendInfos.size() > 0) {
                                      for (V2TIMFriendInfo friendInfo : v2TIMFriendInfos) {

                                          String userID = friendInfo.getUserID();
                                          if (userID.equals(finalResult)){
                                              //说明已经是好友了
                                              contact.setFriend(true);
                                              break;
                                          }else {
                                              contact.setFriend(false);
                                          }
                                      }

                                      Intent intent = new Intent(TUIContactService.getAppContext(), FriendProfileActivity.class);
                                      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                      intent.putExtra(TUIContactConstants.ProfileType.CONTENT, contact);
                                      TUIContactService.getAppContext().startActivity(intent);

                                  }
                              }
                          });
                      }

                      @Override
                      public void onError(int code, String desc) {
                          Log.e(TAG, "get logined userInfo failed. code : " + code + " desc : " + ErrorMessageConverter.convertIMError(code, desc));

                          ToastUtils.show("当前用户不存在");
                      }
                  });
              }
          });

      }


    }




}
