package com.tencent.qcloud.tuikit.tuicontact.ui.pages;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.imsdk.v2.V2TIMConversation;
import com.tencent.qcloud.tuicore.TUIConstants;
import com.tencent.qcloud.tuicore.TUICore;
import com.tencent.qcloud.tuicore.component.fragments.BaseFragment;
import com.tencent.qcloud.tuikit.tuicontact.TUIContactService;
import com.tencent.qcloud.tuikit.tuicontact.bean.ContactItemBean;
import com.tencent.qcloud.tuikit.tuicontact.model.ContactsBean;
import com.tencent.qcloud.tuikit.tuicontact.presenter.ContactPresenter;
import com.tencent.qcloud.tuikit.tuicontact.ui.view.ContactLayout;
import com.tencent.qcloud.tuikit.tuicontact.ui.view.ContactListView;
import com.tencent.qcloud.tuikit.tuicontact.R;
import com.tencent.qcloud.tuikit.tuicontact.TUIContactConstants;
import com.tencent.qcloud.tuikit.tuicontact.util.ContactUtils;
import com.tencent.qcloud.tuikit.tuicontact.util.TUIContactLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;

public class TUIContactFragment extends BaseFragment {

    private static final String TAG = TUIContactFragment.class.getSimpleName();
    private ContactLayout mContactLayout;

    private ContactPresenter presenter;
    private View baseView;
    private ContactsBean contactsBean;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        baseView = inflater.inflate(R.layout.contact_fragment, container, false);
        initViews(baseView, null);
        EventBus.getDefault().register(this);

        return baseView;
    }
    private void initViews(View view, ContactsBean contactsBean) {
        // 从布局文件中获取通讯录面板
        mContactLayout = view.findViewById(R.id.contact_layout);

        presenter = new ContactPresenter(contactsBean);
        presenter.setFriendListListener();
        mContactLayout.setPresenter(presenter, contactsBean);
        //获取好友列表信息
        mContactLayout.initDefault();

        mContactLayout.getContactListView().setOnItemClickListener(new ContactListView.OnItemClickListener() {
            @Override
            public void onItemClick(int position, ContactItemBean contact) {
               // 新的好友
                if (position == 0) {
                    Intent intent = new Intent(TUIContactService.getAppContext(), NewFriendActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    TUIContactService.getAppContext().startActivity(intent);
                } else if (position == 1) {
                    //我的群聊
                    Intent intent = new Intent(TUIContactService.getAppContext(), GroupListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    TUIContactService.getAppContext().startActivity(intent);


                } else if (position == 2) {
                    //黑名单列表
                    Intent intent = new Intent(TUIContactService.getAppContext(),MyBlackListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    TUIContactService.getAppContext().startActivity(intent);

                } else if (position == 3) {
                    //我的客服

                    Intent intent = new Intent(TUIContactService.getAppContext(),MyServerActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    TUIContactService.getAppContext().startActivity(intent);


                    //发起群聊
//                    Intent intent = new Intent(TUIContactService.getAppContext(), StartGroupChatActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("type", 1);
//                    bundle.putString("titleName","选择联系人");
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.putExtras(bundle);
//                    TUIContactService.getAppContext().startActivity(intent);
                } else {
                    //判断是好友还是群聊
                    if(contactsBean == null){
                        Intent intent = new Intent(TUIContactService.getAppContext(), FriendProfileActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(TUIContactConstants.ProfileType.CONTENT, contact);
                        TUIContactService.getAppContext().startActivity(intent);
                        return;
                    }
                    String name = contactsBean.getName();
                    if (name.equals("contacts_friends")){
//                        Intent intent = new Intent(TUIContactService.getAppContext(), FriendProfileActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        intent.putExtra(TUIContactConstants.ProfileType.CONTENT, contact);
//                        TUIContactService.getAppContext().startActivity(intent);


                        ContactUtils.startChatActivity(contact.getId(), ContactItemBean.TYPE_C2C,contact.getNickName(), "");
                    }else {


                        Bundle param = new Bundle();
                        param.putInt(TUIConstants.TUIChat.CHAT_TYPE, contact.isGroup() ? V2TIMConversation.V2TIM_GROUP : V2TIMConversation.V2TIM_C2C);
                        param.putString(TUIConstants.TUIChat.CHAT_ID, contact.getId());
                        param.putString(TUIConstants.TUIChat.CHAT_NAME, contact.getRemark());

                        param.putBoolean(TUIConstants.TUIChat.IS_TOP_CHAT, contact.isTop());
                        param.putString(TUIConstants.TUIChat.GROUP_TYPE, contact.getGroupType());
                        TUICore.startActivity(TUIConstants.TUIChat.GROUP_CHAT_ACTIVITY_NAME, param);
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        TUIContactLog.i(TAG, "onResume");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ContactsBean contactsBean) {
        this.contactsBean = contactsBean;
        Log.i(TAG, "onMessageEvent: " + contactsBean);
        initViews(baseView, contactsBean);
    }
}
