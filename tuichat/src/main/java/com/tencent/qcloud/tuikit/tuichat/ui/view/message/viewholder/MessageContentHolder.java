package com.tencent.qcloud.tuikit.tuichat.ui.view.message.viewholder;

import static com.tencent.qcloud.tuikit.tuichat.ui.view.message.viewholder.CustomLinkMessageHolder.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.tencent.imsdk.v2.V2TIMGroupInfo;
import com.tencent.imsdk.v2.V2TIMGroupInfoResult;
import com.tencent.imsdk.v2.V2TIMGroupMemberFullInfo;
import com.tencent.imsdk.v2.V2TIMGroupMemberInfoResult;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.tencent.qcloud.tuicore.TUICore;
import com.tencent.qcloud.tuicore.TUILogin;
import com.tencent.qcloud.tuicore.TUIThemeManager;
import com.tencent.qcloud.tuicore.component.gatherimage.UserIconView;
import com.tencent.qcloud.tuicore.util.DateTimeUtil;
import com.tencent.qcloud.tuicore.util.ScreenUtil;
import com.tencent.qcloud.tuikit.tuichat.R;
import com.tencent.qcloud.tuikit.tuichat.TUIChatConstants;
import com.tencent.qcloud.tuikit.tuichat.TUIChatService;
import com.tencent.qcloud.tuikit.tuichat.bean.LoginBean;
import com.tencent.qcloud.tuikit.tuichat.bean.MessageReactBean;
import com.tencent.qcloud.tuikit.tuichat.bean.MessageRepliesBean;
import com.tencent.qcloud.tuikit.tuichat.bean.message.GroupkickBean;
import com.tencent.qcloud.tuikit.tuichat.bean.message.TUIMessageBean;
import com.tencent.qcloud.tuikit.tuichat.bean.message.TextMessageBean;
import com.tencent.qcloud.tuikit.tuichat.config.TUIChatConfigs;
import com.tencent.qcloud.tuikit.tuichat.presenter.ChatPresenter;
import com.tencent.qcloud.tuikit.tuichat.ui.view.CenterTextView;
import com.tencent.qcloud.tuikit.tuichat.ui.view.message.SelectTextHelper;
import com.tencent.qcloud.tuikit.tuichat.ui.view.message.reply.ChatFlowReactView;
import com.tencent.qcloud.tuikit.tuichat.util.EncryptUtil;
import com.tencent.qcloud.tuikit.tuichat.util.TUIChatLog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class MessageContentHolder extends MessageBaseHolder {

    public   TextView message_top_time_tv;
    public TextView message_top_group_info_tv;
    public UserIconView leftUserIcon;
    public UserIconView rightUserIcon;
    public TextView usernameText;
    public LinearLayout msgContentLinear;
    public ProgressBar sendingProgress;
    public ImageView statusImage;
    public TextView isReadText;
    public TextView unreadAudioText;
    public TextView messageDetailsTimeTv;

    public boolean isForwardMode = false;
    public boolean isReplyDetailMode = false;
    public boolean isMultiSelectMode = false;

    private List<TUIMessageBean> mDataSource = new ArrayList<>();
    protected SelectTextHelper selectableTextHelper;

    protected ChatPresenter presenter;

    private SharedPreferences sp;
    private static String SP_NAME = "huimiaomiao_share_date";
    private final LoginBean.DataDTO user;

    private  long  mLastTime ,mCurTime =0;
    private final ImageView iv_logo_item;

    public MessageContentHolder(View itemView) {
        super(itemView);


        sp = itemView.getContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
     String   user_info = sp.getString("user_info", "");

        user_info = EncryptUtil.getInstance(itemView.getContext()).decrypt(user_info);
        user = JSON.parseObject(user_info, LoginBean.DataDTO.class);
        leftUserIcon = itemView.findViewById(R.id.left_user_icon_view);
        rightUserIcon = itemView.findViewById(R.id.right_user_icon_view);
        usernameText = itemView.findViewById(R.id.user_name_tv);
        msgContentLinear = itemView.findViewById(R.id.msg_content_ll);
        statusImage = itemView.findViewById(R.id.message_status_iv);
        sendingProgress = itemView.findViewById(R.id.message_sending_pb);
        isReadText = itemView.findViewById(R.id.is_read_tv);
        unreadAudioText = itemView.findViewById(R.id.audio_unread);
        message_top_time_tv = itemView.findViewById(R.id.message_top_time_tv);
        messageDetailsTimeTv = itemView.findViewById(R.id.msg_detail_time_tv);
        message_top_group_info_tv = itemView.findViewById(R.id.message_top_group_info_tv);

        //左边的标志
        iv_logo_item = itemView.findViewById(R.id.iv_logo_item);
    }

    public void setPresenter(ChatPresenter chatPresenter) {
        this.presenter = chatPresenter;
    }

    public void setDataSource(List<TUIMessageBean> dataSource) {
        if (dataSource == null || dataSource.isEmpty()) {
            mDataSource = null;
        }

        List<TUIMessageBean> mediaSource = new ArrayList<>();
        for(TUIMessageBean messageBean : dataSource) {
            int type = messageBean.getMsgType();
            if (type == V2TIMMessage.V2TIM_ELEM_TYPE_IMAGE || type == V2TIMMessage.V2TIM_ELEM_TYPE_VIDEO) {
                mediaSource.add(messageBean);
            }
        }
        mDataSource = mediaSource;
    }

    public List<TUIMessageBean> getDataSource() {
        return mDataSource;
    }

    public void resetSelectableText() {
        if (selectableTextHelper != null) {
            selectableTextHelper.reset();
        }
    }
    private int count = 0;//点击次数
    private long firstClick = 0;//第一次点击时间
    private long secondClick = 0;//第二次点击时间
    private Handler handler = new Handler();
    /**
     * 两次点击时间间隔，单位毫秒
     */
    private final int totalTime = 800;
    @SuppressLint("WrongConstant")
    @Override
    public void layoutViews(final TUIMessageBean msg, final int position) {
        super.layoutViews(msg, position);

        if (isForwardMode || isReplyDetailMode) {
            leftUserIcon.setVisibility(View.VISIBLE);
            rightUserIcon.setVisibility(View.GONE);
        } else {
            //// 头像设置
            if (msg.isSelf()) {
                leftUserIcon.setVisibility(View.GONE);
                rightUserIcon.setVisibility(View.VISIBLE);
            } else {
                leftUserIcon.setVisibility(View.VISIBLE);
                rightUserIcon.setVisibility(View.GONE);
            }
        }
        if (properties.getAvatar() != 0) {
            leftUserIcon.setDefaultImageResId(properties.getAvatar());
            rightUserIcon.setDefaultImageResId(properties.getAvatar());
        } else {
            leftUserIcon.setDefaultImageResId(TUIThemeManager.getAttrResId(leftUserIcon.getContext(), R.attr.core_default_user_icon));
            rightUserIcon.setDefaultImageResId(TUIThemeManager.getAttrResId(rightUserIcon.getContext(), R.attr.core_default_user_icon));
        }
        if (properties.getAvatarRadius() != 0) {
            leftUserIcon.setRadius(properties.getAvatarRadius());
            rightUserIcon.setRadius(properties.getAvatarRadius());
        } else {
            int radius = ScreenUtil.dip2px(4);
            leftUserIcon.setRadius(radius);
            rightUserIcon.setRadius(radius);
        }
        if (properties.getAvatarSize() != null && properties.getAvatarSize().length == 2) {
            ViewGroup.LayoutParams params = leftUserIcon.getLayoutParams();
            params.width = properties.getAvatarSize()[0];
            params.height = properties.getAvatarSize()[1];
            leftUserIcon.setLayoutParams(params);

            params = rightUserIcon.getLayoutParams();
            params.width = properties.getAvatarSize()[0];
            params.height = properties.getAvatarSize()[1];
            rightUserIcon.setLayoutParams(params);
        }

        // 转发模式下显示用户名
        if (isForwardMode || isReplyDetailMode) {
            usernameText.setVisibility(View.VISIBLE);
        } else {
            //// 用户昵称设置
            if (msg.isSelf()) { // 默认不显示自己的昵称
                if (properties.getRightNameVisibility() == 0) {
                    usernameText.setVisibility(View.GONE);
                } else {
                    usernameText.setVisibility(properties.getRightNameVisibility());
                }
            } else {
                if (properties.getLeftNameVisibility() == 0) {
                    if (msg.isGroup()) { // 群聊默认显示对方的昵称
                        usernameText.setVisibility(View.VISIBLE);
                    } else { // 单聊默认不显示对方昵称
                        usernameText.setVisibility(View.GONE);
                    }
                } else {
                    usernameText.setVisibility(properties.getLeftNameVisibility());
                }
            }
        }
        if (properties.getNameFontColor() != 0) {
            usernameText.setTextColor(properties.getNameFontColor());
        }
        if (properties.getNameFontSize() != 0) {
            usernameText.setTextSize(properties.getNameFontSize());
        }
        // 聊天界面设置头像和昵称
        if (!TextUtils.isEmpty(msg.getNameCard())) {
            usernameText.setText(msg.getNameCard());
        } else if (!TextUtils.isEmpty(msg.getFriendRemark())) {
            usernameText.setText(msg.getFriendRemark());
        } else if (!TextUtils.isEmpty(msg.getNickName())) {
            usernameText.setText(msg.getNickName());
        } else {
          if (msg instanceof GroupkickBean){
              String operationName = ((GroupkickBean) msg).operationName;
              usernameText.setText(operationName);
          }else {
              usernameText.setText(msg.getSender());
          }

        }

        //判断有没有设置头像
        if (user!=null){
            String headImage = user.getHeadImage();
            if (TextUtils.isEmpty(headImage)){

                if (msg.isSelf()) {
                    rightUserIcon.setBackgroundResource(TUIThemeManager.getAttrResId(TUILogin.getAppContext(), com.tencent.qcloud.tuicore.R.drawable.defult_avater));
                    if (msg.isGroup()){

                        //判断是群主
                        List<String> groupList = new ArrayList<>();
                        groupList.add(presenter.getChatInfo().getId());
                        V2TIMManager.getGroupManager().getGroupsInfo(groupList, new V2TIMValueCallback<List<V2TIMGroupInfoResult>>() {
                            @Override
                            public void onSuccess(List<V2TIMGroupInfoResult> v2TIMGroupInfoResults) {
                                Log.i(TAG, "onSuccess: "+v2TIMGroupInfoResults);
                                if (v2TIMGroupInfoResults!=null){
                                    V2TIMGroupInfoResult v2TIMGroupInfoResult = v2TIMGroupInfoResults.get(0);
                                    if (v2TIMGroupInfoResult!=null){
                                        V2TIMGroupInfo groupInfo = v2TIMGroupInfoResult.getGroupInfo();
                                        if (groupInfo!=null){
                                            String owner = groupInfo.getOwner();
                                            Log.i(TAG, "onSuccess: "+owner);

                                            if (owner.equals(msg.getSender())){
                                                //我是群主
                                               // rightUserIcon.setLogo(1);

                                                iv_logo_item.setBackground(null);
                                            }else {
                                              //  rightUserIcon.setLogo(3);
                                                iv_logo_item.setBackground(null);
                                                //获取群管理员
                                                int filter = V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_FILTER_ADMIN;
                                                V2TIMManager.getGroupManager().getGroupMemberList(presenter.getChatInfo().getId(), filter, 0,
                                                        new V2TIMValueCallback<V2TIMGroupMemberInfoResult>(){
                                                            @Override
                                                            public void onSuccess(V2TIMGroupMemberInfoResult v2TIMGroupMemberInfoResult) {
                                                                List<V2TIMGroupMemberFullInfo> infoList = v2TIMGroupMemberInfoResult.getMemberInfoList();
                                                                for (V2TIMGroupMemberFullInfo fullInfo : infoList) {
                                                                    Log.i(TAG, "onSuccess: "+fullInfo);
                                                                    String userID = fullInfo.getUserID();
                                                                    if (userID.equals(msg.getSender())){
                                                                        //当前设置了群管理员
                                                                       // rightUserIcon.setLogo(2);
                                                                        iv_logo_item.setBackground(null);

                                                                        break;
                                                                    }else {
                                                                        //rightUserIcon.setLogo(3);
                                                                        iv_logo_item.setBackground(null);

                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onError(int i, String s) {

                                                            }
                                                        });
                                            }
                                        }
                                    }
                                }

                            }

                            @Override
                            public void onError(int i, String s) {

                            }
                        });
                    }
                } else {
                    leftUserIcon.setBackgroundResource(TUIThemeManager.getAttrResId(TUILogin.getAppContext(), com.tencent.qcloud.tuicore.R.drawable.defult_avater));
                    if (msg.isGroup()){

                        //判断是群主
                        List<String> groupList = new ArrayList<>();
                        groupList.add(presenter.getChatInfo().getId());
                        V2TIMManager.getGroupManager().getGroupsInfo(groupList,
                                new V2TIMValueCallback<List<V2TIMGroupInfoResult>>() {
                            @Override
                            public void onSuccess(List<V2TIMGroupInfoResult> v2TIMGroupInfoResults) {
                                Log.i(TAG, "onSuccess: "+v2TIMGroupInfoResults);
                                if (v2TIMGroupInfoResults!=null){
                                    V2TIMGroupInfoResult v2TIMGroupInfoResult = v2TIMGroupInfoResults.get(0);
                                    if (v2TIMGroupInfoResult!=null){
                                        V2TIMGroupInfo groupInfo = v2TIMGroupInfoResult.getGroupInfo();
                                        if (groupInfo!=null){
                                            String owner = groupInfo.getOwner();
                                            Log.i(TAG, "onSuccess: "+owner);

                                            if (owner.equals(msg.getSender())){
                                                //我是群主
                                               // leftUserIcon.setLogo(1);
                                                iv_logo_item.setBackground(itemView.getResources()
                                                        .getDrawable(com.tencent.qcloud.tuicore.R.drawable.group_admin));

                                            }else {
                                              //  leftUserIcon.setLogo(3);
                                                iv_logo_item.setBackground(null);

                                                //获取群管理员
                                                int filter = V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_FILTER_ADMIN;
                                                V2TIMManager.getGroupManager().getGroupMemberList(presenter.getChatInfo().getId(), filter, 0,
                                                        new V2TIMValueCallback<V2TIMGroupMemberInfoResult>(){
                                                            @Override
                                                            public void onSuccess(V2TIMGroupMemberInfoResult v2TIMGroupMemberInfoResult) {
                                                                List<V2TIMGroupMemberFullInfo> infoList = v2TIMGroupMemberInfoResult.getMemberInfoList();
                                                                for (V2TIMGroupMemberFullInfo fullInfo : infoList) {
                                                                    Log.i(TAG, "onSuccess: "+fullInfo);
                                                                    String userID = fullInfo.getUserID();
                                                                    if (userID.equals(msg.getSender())){
                                                                        //当前设置了群管理员
                                                                     //   leftUserIcon.setLogo(2);

                                                                        iv_logo_item.setBackground(itemView.getResources()
                                                                                .getDrawable(com.tencent.qcloud.tuicore.R.drawable.group_admin));

                                                                        break;
                                                                    }else {
                                                                      //  leftUserIcon.setLogo(3);
                                                                        iv_logo_item.setBackground(null);
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onError(int i, String s) {

                                                            }
                                                        });
                                            }
                                        }
                                    }
                                }

                            }

                            @Override
                            public void onError(int i, String s) {

                            }
                        });
                    }
                }

            }else {
                if (!TextUtils.isEmpty(msg.getFaceUrl())) {
                    List<Object> urllist = new ArrayList<>();
                    urllist.add(msg.getFaceUrl());
                    if (isForwardMode || isReplyDetailMode) {
                        leftUserIcon.setIconUrls(urllist);
                    } else {
                        if (msg.isSelf()) {
                            //判断是不是群
                            if (msg.isGroup()){
                                //判断是群主
                                List<String> groupList = new ArrayList<>();
                                groupList.add(presenter.getChatInfo().getId());
                                V2TIMManager.getGroupManager().getGroupsInfo(groupList, new V2TIMValueCallback<List<V2TIMGroupInfoResult>>() {
                                    @Override
                                    public void onSuccess(List<V2TIMGroupInfoResult> v2TIMGroupInfoResults) {
                                        Log.i(TAG, "onSuccess: "+v2TIMGroupInfoResults);
                                        if (v2TIMGroupInfoResults!=null){
                                            V2TIMGroupInfoResult v2TIMGroupInfoResult = v2TIMGroupInfoResults.get(0);
                                            if (v2TIMGroupInfoResult!=null){
                                                V2TIMGroupInfo groupInfo = v2TIMGroupInfoResult.getGroupInfo();
                                                if (groupInfo!=null){
                                                    String owner = groupInfo.getOwner();
                                                    Log.i(TAG, "onSuccess: "+owner);
                                                    if (owner.equals(msg.getSender())){
                                                        //我是群主
                                                       // rightUserIcon.setLogo(1);
                                                        iv_logo_item.setBackground(null);
                                                    }else {
                                                       // rightUserIcon.setLogo(3);
                                                        iv_logo_item.setBackground(null);
                                                        //获取群管理员 只有不是群主 才能获取是不是管理员
                                                        int filter = V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_FILTER_ADMIN;
                                                        V2TIMManager.getGroupManager().getGroupMemberList(presenter.getChatInfo().getId(), filter, 0,
                                                                new V2TIMValueCallback<V2TIMGroupMemberInfoResult>(){
                                                                    @Override
                                                                    public void onSuccess(V2TIMGroupMemberInfoResult v2TIMGroupMemberInfoResult) {
                                                                        List<V2TIMGroupMemberFullInfo> infoList = v2TIMGroupMemberInfoResult.getMemberInfoList();
                                                                        for (V2TIMGroupMemberFullInfo fullInfo : infoList) {
                                                                            Log.i(TAG, "onSuccess: "+fullInfo);
                                                                            String userID = fullInfo.getUserID();
                                                                            if (userID.equals(msg.getSender())){
                                                                                //当前设置了群管理员
                                                                               // rightUserIcon.setLogo(2);
                                                                                iv_logo_item.setBackground(null);
                                                                                break;
                                                                            }else {
                                                                             //   rightUserIcon.setLogo(3);
                                                                                iv_logo_item.setBackground(null);
                                                                            }
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onError(int i, String s) {

                                                                    }
                                                                });
                                                    }
                                                }
                                            }
                                        }

                                    }

                                    @Override
                                    public void onError(int i, String s) {

                                    }
                                });
                            }
                            rightUserIcon.setIconUrls(urllist);


                        } else {
                            if (msg.isGroup()){

                                //判断是群主
                                List<String> groupList = new ArrayList<>();
                                groupList.add(presenter.getChatInfo().getId());
                                V2TIMManager.getGroupManager().getGroupsInfo(groupList,
                                        new V2TIMValueCallback<List<V2TIMGroupInfoResult>>() {
                                    @Override
                                    public void onSuccess(List<V2TIMGroupInfoResult> v2TIMGroupInfoResults) {
                                        Log.i(TAG, "onSuccess: "+v2TIMGroupInfoResults);
                                        if (v2TIMGroupInfoResults!=null){
                                            V2TIMGroupInfoResult v2TIMGroupInfoResult = v2TIMGroupInfoResults.get(0);
                                            if (v2TIMGroupInfoResult!=null){
                                                V2TIMGroupInfo groupInfo = v2TIMGroupInfoResult.getGroupInfo();
                                                if (groupInfo!=null){
                                                    String owner = groupInfo.getOwner();
                                                    Log.i(TAG, "onSuccess: "+owner);

                                                    if (owner.equals(msg.getSender())){
                                                        //我是群主
                                                       // leftUserIcon.setLogo(1);

                                                        iv_logo_item.setBackground(itemView.getResources()
                                                                .getDrawable(com.tencent.qcloud.tuicore.R.drawable.group_admin));
                                                    }else {
                                                       // leftUserIcon.setLogo(3);


                                                        iv_logo_item.setBackground(null);
                                                        //获取群管理员
                                                        int filter = V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_FILTER_ADMIN;
                                                        V2TIMManager.getGroupManager().getGroupMemberList(presenter.getChatInfo().getId(), filter, 0,
                                                                new V2TIMValueCallback<V2TIMGroupMemberInfoResult>(){
                                                                    @Override
                                                                    public void onSuccess(V2TIMGroupMemberInfoResult v2TIMGroupMemberInfoResult) {
                                                                        List<V2TIMGroupMemberFullInfo> infoList = v2TIMGroupMemberInfoResult.getMemberInfoList();
                                                                        for (V2TIMGroupMemberFullInfo fullInfo : infoList) {
                                                                            Log.i(TAG, "onSuccess: "+fullInfo);
                                                                            String userID = fullInfo.getUserID();
                                                                            if (userID.equals(msg.getSender())){
                                                                                //当前设置了群管理员
                                                                                //leftUserIcon.setLogo(2);
                                                                                iv_logo_item.setBackground(itemView.getResources()
                                                                                        .getDrawable(com.tencent.qcloud.tuicore.R.drawable.group_manage));
                                                                                break;
                                                                            }else {
                                                                               // leftUserIcon.setLogo(3);
                                                                                iv_logo_item.setBackground(null);
                                                                            }
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onError(int i, String s) {

                                                                    }
                                                                });
                                                    }
                                                }
                                            }
                                        }

                                    }

                                    @Override
                                    public void onError(int i, String s) {

                                    }
                                });

                            }
                            leftUserIcon.setIconUrls(urllist);

                        }
                    }
                } else {
                    rightUserIcon.setIconUrls(null);
                    leftUserIcon.setIconUrls(null);
                }
            }
        }



        if (isForwardMode || isReplyDetailMode) {
            sendingProgress.setVisibility(View.GONE);
        } else {
            if (msg.isSelf()) {
                if (msg.getStatus() == TUIMessageBean.MSG_STATUS_SEND_FAIL
                        || msg.getStatus() == TUIMessageBean.MSG_STATUS_SEND_SUCCESS
                        || msg.isPeerRead()) {
                    sendingProgress.setVisibility(View.GONE);
                } else {
                    sendingProgress.setVisibility(View.VISIBLE);
                }
            } else {
                sendingProgress.setVisibility(View.GONE);
            }
        }

        if (isForwardMode || isReplyDetailMode) {
            msgArea.setBackgroundResource(TUIThemeManager.getAttrResId(itemView.getContext(), R.attr.chat_bubble_other_bg));
            statusImage.setVisibility(View.GONE);
        } else {
            //// 聊天气泡设置
            if (msg.isSelf()) {
                if (properties.getRightBubble() != null && properties.getRightBubble().getConstantState() != null) {
                    msgArea.setBackground(properties.getRightBubble().getConstantState().newDrawable());
                } else {
                    msgArea.setBackgroundResource(TUIThemeManager.getAttrResId(itemView.getContext(), R.attr.chat_bubble_self_bg));
                }
            } else {
                if (properties.getLeftBubble() != null && properties.getLeftBubble().getConstantState() != null) {
                    msgArea.setBackground(properties.getLeftBubble().getConstantState().newDrawable());
                } else {
                    msgArea.setBackgroundResource(TUIThemeManager.getAttrResId(itemView.getContext(), R.attr.chat_bubble_other_bg));
                }
            }

            //// 聊天气泡的点击事件处理
            if (onItemClickListener != null) {
                msgContentFrame.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        onItemClickListener.onMessageLongClick(v, position, msg);
                        return true;
                    }
                });

                leftUserIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        count++;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //单击
                                if (count >1){
                                    //双击事件
                                    //左边头像长安是发专属红包 必须是群红包
                                    boolean group = msg.isGroup();
                                    if (group){
                                        onItemClickListener.onMessageDoubleClick(leftUserIcon, position, msg);
                                    }
                                    count = 0;
                                    //防止handler引起的内存泄漏
                                    handler.removeCallbacksAndMessages(null);
                                } else if (count == 1){
                                    onItemClickListener.onUserIconClick(leftUserIcon, position, msg);
                                    count = 0;
                                }

                            }
                        },500);
                    }
                });

//                leftUserIcon.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        if (MotionEvent.ACTION_DOWN == event.getAction()) {//按下
//                            count++;
//                            if (1 == count) {
//                                firstClick = System.currentTimeMillis();//记录第一次点击时间
//                            } else if (2 == count) {
//                                secondClick = System.currentTimeMillis();//记录第二次点击时间
//                                if (secondClick - firstClick < totalTime) {//判断二次点击时间间隔是否在设定的间隔时间之内
//
//                                } else {
//                                    firstClick = secondClick;
//                                    count = 1;
//                                }
//                                secondClick = 0;
//                            }else {
//
//
//
//                            }
//                        }
//                        return true;
//                    }
//                });
//
//
//                leftUserIcon.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        mLastTime = mCurTime;
//                        mCurTime = System.currentTimeMillis();
//
//                        if (mCurTime - mLastTime < 500) {
//
//                        }
//                    }
//                });

                leftUserIcon.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                      //这个是@
                        boolean group = msg.isGroup();
                        if (group){
                            onItemClickListener.onUserIconLongClick(view, position, msg);
                        }

                        return true;
                    }
                });
                rightUserIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onItemClickListener.onUserIconClick(view, position, msg);
                    }
                });
            }

            //// 发送状态的设置
            if (msg.getStatus() == TUIMessageBean.MSG_STATUS_SEND_FAIL) {
                statusImage.setVisibility(View.VISIBLE);
                msgContentFrame.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onMessageLongClick(msgContentFrame, position, msg);
                        }
                    }
                });
                statusImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onSendFailBtnClick(statusImage, position, msg);
                        }
                    }
                });
            } else {
                msgContentFrame.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onMessageClick(msgContentFrame, position, msg);
                        }
                    }
                });
                statusImage.setVisibility(View.GONE);
            }
        }

        if (isForwardMode || isReplyDetailMode) {
            setGravity(true);
            msgContentLinear.removeView(msgAreaAndReply);
            msgContentLinear.addView(msgAreaAndReply);
        } else {
            // 左右边的消息需要调整一下内容的位置，使已读标签位置显示正确
            if (msg.isSelf()) {
                setGravity(false);
                msgContentLinear.removeView(msgAreaAndReply);
                msgContentLinear.addView(msgAreaAndReply);
            } else {
                setGravity(true);
                msgContentLinear.removeView(msgAreaAndReply);
                msgContentLinear.addView(msgAreaAndReply, 0);
            }
        }

        if (rightGroupLayout != null) {
            rightGroupLayout.setVisibility(View.VISIBLE);
        }
        msgContentLinear.setVisibility(View.VISIBLE);

        // clear isReadText status
        isReadText.setTextColor(isReadText.getResources().getColor(R.color.text_gray1));
        isReadText.setOnClickListener(null);

        if (isForwardMode || isReplyDetailMode) {
            isReadText.setVisibility(View.GONE);
            unreadAudioText.setVisibility(View.GONE);
        } else {
            //// 对方已读标识的设置
            if (TUIChatConfigs.getConfigs().getGeneralConfig().isShowRead()) {
                if (msg.isSelf() && TUIMessageBean.MSG_STATUS_SEND_SUCCESS == msg.getStatus()) {
                    if (!msg.isNeedReadReceipt()) {
                        isReadText.setVisibility(View.GONE);
                    } else {
                        showReadText(msg);
                    }
                } else {
                    isReadText.setVisibility(View.GONE);
                }
            }

            //// 音频已读
            unreadAudioText.setVisibility(View.GONE);

        }

        if (isReplyDetailMode) {
            chatTimeText.setVisibility(View.GONE);
        }

        setReplyContent(msg);
        setReactContent(msg);

        setMessageAreaPadding();

        //// 由子类设置指定消息类型的views
        layoutVariableViews(msg, position);
    }

    protected void setMessageAreaPadding() {
        // after setting background, the padding will be reset
        int paddingHorizontal = itemView.getResources().getDimensionPixelSize(R.dimen.chat_message_area_padding_left_right);
        int paddingVertical = itemView.getResources().getDimensionPixelSize(R.dimen.chat_message_area_padding_top_bottom);;
        msgArea.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical);
    }

    protected void setGravity(boolean isStart) {
        int gravity = isStart ? Gravity.START : Gravity.END;
        msgAreaAndReply.setGravity(gravity);
        ViewGroup.LayoutParams layoutParams = msgContentFrame.getLayoutParams();
        if (layoutParams instanceof FrameLayout.LayoutParams) {
            ((FrameLayout.LayoutParams) layoutParams).gravity = gravity;
        } else if (layoutParams instanceof LinearLayout.LayoutParams) {
            ((LinearLayout.LayoutParams) layoutParams).gravity = gravity;
        }
        msgContentFrame.setLayoutParams(layoutParams);
    }

    private void setReplyContent(TUIMessageBean messageBean) {
        MessageRepliesBean messageRepliesBean = messageBean.getMessageRepliesBean();
        if (messageRepliesBean != null && messageRepliesBean.getRepliesSize() > 0) {
            TextView replyNumText = msgReplyDetailLayout.findViewById(R.id.reply_num);
            replyNumText.setText(replyNumText.getResources().getString(R.string.chat_reply_num, messageRepliesBean.getRepliesSize()));
            msgReplyDetailLayout.setVisibility(View.VISIBLE);
            msgReplyDetailLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onReplyDetailClick(messageBean);
                    }
                }
            });
        } else {
            msgReplyDetailLayout.setVisibility(View.GONE);
            msgReplyDetailLayout.setOnClickListener(null);
        }
        if (!isReplyDetailMode) {
            messageDetailsTimeTv.setVisibility(View.GONE);
        } else {
            messageDetailsTimeTv.setText(DateTimeUtil.getTimeFormatText(new Date(messageBean.getMessageTime() * 1000)));
            messageDetailsTimeTv.setVisibility(View.VISIBLE);
            msgReplyDetailLayout.setVisibility(View.GONE);
        }
    }

    private void setReactContent(TUIMessageBean messageBean) {
        MessageReactBean messageReactBean = messageBean.getMessageReactBean();
        if (messageReactBean != null && messageReactBean.getReactSize() > 0) {
            reactView.setVisibility(View.VISIBLE);
            reactView.setData(messageReactBean);
            reactView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onMessageLongClick(v, 0, messageBean);
                    }
                    return true;
                }
            });
            if (!isForwardMode) {
                reactView.setReactOnClickListener(new ChatFlowReactView.ReactOnClickListener() {
                    @Override
                    public void onClick(String emojiId) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onReactOnClick(emojiId, messageBean);
                        }
                    }
                });
            } else {
                reactView.setOnLongClickListener(null);
            }
        } else {
            reactView.setVisibility(View.GONE);
            reactView.setOnLongClickListener(null);
        }
        if (!messageBean.isSelf() || isForwardMode || isReplyDetailMode) {
            reactView.setThemeColorId(TUIThemeManager.getAttrResId(reactView.getContext(), R.attr.chat_react_other_text_color));
        } else {
            reactView.setThemeColorId(0);
        }
    }

    private void showReadText(TUIMessageBean msg) {
        if (msg.isGroup()) {
            isReadText.setVisibility(View.VISIBLE);
            if (msg.isAllRead()) {
                isReadText.setText(R.string.has_all_read);
            } else if (msg.isUnread()) {
                isReadText.setTextColor(isReadText.getResources().getColor(TUIThemeManager.getAttrResId(isReadText.getContext(), R.attr.chat_read_receipt_text_color)));
                isReadText.setText(R.string.unread);
                isReadText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startShowUnread(msg);
                    }
                });
            } else {
                long readCount = msg.getReadCount();
                if (readCount > 0) {
                    isReadText.setText(isReadText.getResources().getString(R.string.someone_has_read, readCount));
                    isReadText.setTextColor(isReadText.getResources().getColor(TUIThemeManager.getAttrResId(isReadText.getContext(), R.attr.chat_read_receipt_text_color)));
                    isReadText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startShowUnread(msg);
                        }
                    });
                }
            }
        } else {
            isReadText.setVisibility(View.VISIBLE);
            if (msg.isPeerRead()) {
                isReadText.setText(R.string.has_read);
            } else {
                isReadText.setText(R.string.unread);
                isReadText.setTextColor(isReadText.getResources().getColor(TUIThemeManager.getAttrResId(isReadText.getContext(), R.attr.chat_read_receipt_text_color)));
                isReadText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startShowUnread(msg);
                    }
                });
            }
        }
    }

    public abstract void layoutVariableViews(final TUIMessageBean msg, final int position);

    public void onRecycled() {}

    public void startShowUnread(TUIMessageBean messageBean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TUIChatConstants.MESSAGE_BEAN, messageBean);
        bundle.putSerializable(TUIChatConstants.CHAT_INFO, presenter.getChatInfo());
        TUICore.startActivity("MessageReceiptDetailActivity", bundle);
    }

    protected void setSelectableTextHelper(TUIMessageBean msg, TextView textView, int position, boolean isEmoji) {
        selectableTextHelper = new SelectTextHelper
                .Builder(textView)// 放你的textView到这里！！
                .setCursorHandleColor(TUIChatService.getAppContext().getResources().getColor(R.color.font_blue))// 游标颜色
                .setCursorHandleSizeInDp(18)// 游标大小 单位dp
                .setSelectedColor(TUIChatService.getAppContext().getResources().getColor(R.color.test_blue))// 选中文本的颜色
                .setSelectAll(true)// 初次选中是否全选 default true
                .setIsEmoji(isEmoji)
                .setScrollShow(false)// 滚动时是否继续显示 default true
                .setSelectedAllNoPop(true)// 已经全选无弹窗，设置了监听会回调 onSelectAllShowCustomPop 方法
                .setMagnifierShow(false)// 放大镜 default true
                .build();

        selectableTextHelper.setSelectListener(new SelectTextHelper.OnSelectListener() {
            /**
             * 点击回调
             */
            @Override
            public void onClick(View v) {
            }

            /**
             * 长按回调
             */
            @Override
            public void onLongClick(View v) {
            }

            /**
             * 选中文本回调
             */
            @Override
            public void onTextSelected(CharSequence content) {
                String selectedText = content.toString();
                msg.setSelectText(selectedText);
                TUIChatLog.d("TextMessageHolder", "onTextSelected selectedText = " + selectedText);
                if (onItemClickListener != null) {
                    onItemClickListener.onTextSelected(msgContentFrame, position, msg);
                }
            }

            /**
             * 弹窗关闭回调
             */
            @Override
            public void onDismiss() {
                msg.setSelectText(null);
                msg.setSelectText(msg.getExtra());
            }

            /**
             * 点击TextView里的url回调
             *
             * 已被下面重写
             * textView.setMovementMethod(new LinkMovementMethodInterceptor());
             */
            @Override
            public void onClickUrl(String url) {
            }

            /**
             * 全选显示自定义弹窗回调
             */
            @Override
            public void onSelectAllShowCustomPop() {
            }

            /**
             * 重置回调
             */
            @Override
            public void onReset() {
                msg.setSelectText(null);
                msg.setSelectText(msg.getExtra());
            }

            /**
             * 解除自定义弹窗回调
             */
            @Override
            public void onDismissCustomPop() {
            }

            /**
             * 是否正在滚动回调
             */
            @Override
            public void onScrolling() {
            }
        });
    }

}
