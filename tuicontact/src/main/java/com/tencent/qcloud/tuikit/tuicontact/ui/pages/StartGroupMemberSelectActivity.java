package com.tencent.qcloud.tuikit.tuicontact.ui.pages;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;


import com.tencent.qcloud.tuicore.component.TitleBarLayout;
import com.tencent.qcloud.tuicore.component.activities.BaseLightActivity;
import com.tencent.qcloud.tuicore.component.interfaces.ITitleBarLayout;
import com.tencent.qcloud.tuicore.util.ToastUtil;
import com.tencent.qcloud.tuikit.tuicontact.TUIContactConstants;
import com.tencent.qcloud.tuikit.tuicontact.bean.ContactItemBean;

import com.tencent.qcloud.tuikit.tuicontact.bean.GroupMemberInfo;
import com.tencent.qcloud.tuikit.tuicontact.presenter.ContactPresenter;
import com.tencent.qcloud.tuikit.tuicontact.ui.view.ContactAdapter;
import com.tencent.qcloud.tuikit.tuicontact.ui.view.ContactCustomerAdapter;
import com.tencent.qcloud.tuikit.tuicontact.ui.view.ContactCustomerListView;
import com.tencent.qcloud.tuikit.tuicontact.ui.view.ContactListView;
import com.tencent.qcloud.tuikit.tuicontact.R;
import com.tencent.qcloud.tuikit.tuigroup.bean.GroupInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StartGroupMemberSelectActivity extends BaseLightActivity {

    private static final String TAG = StartGroupMemberSelectActivity.class.getSimpleName();

    private TitleBarLayout mTitleBar;
    private ContactCustomerListView mContactListView;
    private ArrayList<GroupMemberInfo> mMembers = new ArrayList<>();
    private ArrayList<GroupMemberInfo> lastMembers = new ArrayList<>();

    private ContactPresenter presenter;
    private String groupId;
    private GroupInfo info;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_start_group_select_two_activity);

        init();
    }

    private ArrayList<String> getMembersNameCard(){
        if (mMembers.size() == 0) {
            return new ArrayList<>();
        }

        ArrayList<String> nameCards = new ArrayList<>();
        for(int i = 0; i < mMembers.size(); i++){
            nameCards.add(mMembers.get(i).getNameCard());
        }
        return nameCards;
    }

    private ArrayList<String> getMembersUserId(){
        if (mMembers.size() == 0) {
            return new ArrayList<>();
        }

        ArrayList<String> userIds = new ArrayList<>();
        for(int i = 0; i < mMembers.size(); i++){
            userIds.add(mMembers.get(i).getAccount());
        }
        return userIds;
    }
    private void init() {
        mMembers.clear();
        lastMembers.clear();
        groupId = getIntent().getStringExtra(TUIContactConstants.Group.GROUP_ID);
        //添加朋友
        boolean isSelectFriends = getIntent().getBooleanExtra(TUIContactConstants.Selection.SELECT_FRIENDS, false);
        //删除朋友
        boolean isSelectForCall = getIntent().getBooleanExtra(TUIContactConstants.Selection.SELECT_FOR_CALL, false);
        int limit = getIntent().getIntExtra(TUIContactConstants.Selection.LIMIT, Integer.MAX_VALUE);

        mTitleBar = findViewById(R.id.group_create_title_bar);
        mTitleBar.setBackgroundColor(getResources().getColor(R.color.white));
        RelativeLayout rootView = mTitleBar.getRootView();
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) rootView.getLayoutParams();
        params.setMargins(0,0,0,0);
        rootView.setLayoutParams(params);

        mTitleBar.getLeftIcon().setBackground(getResources().getDrawable(R.drawable.back));
        mTitleBar.setTitle(getResources().getString(R.string.sure), ITitleBarLayout.Position.RIGHT);
        mTitleBar.getRightIcon().setVisibility(View.GONE);
        mTitleBar.setOnRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMembers.size() > limit) {
                    String overLimitTip = getString(R.string.contact_over_limit_tip, limit);
                    ToastUtil.toastShortMessage(overLimitTip);
                    return;
                }
                //要移除上次已经选中的
                for (GroupMemberInfo lastMember : lastMembers) {
                    if (mMembers.contains(lastMember)){
                        mMembers.remove(lastMember);
                    }
                }

                Intent i = new Intent();
                List<String> friendIdList = new ArrayList<>();
                for (GroupMemberInfo memberInfo : mMembers) {
                    friendIdList.add(memberInfo.getAccount());
                }
                i.putExtra(TUIContactConstants.Selection.LIST, (Serializable) friendIdList);
                i.putStringArrayListExtra(TUIContactConstants.Selection.USER_NAMECARD_SELECT, getMembersNameCard());
                i.putStringArrayListExtra(TUIContactConstants.Selection.USER_ID_SELECT, getMembersUserId());
                i.putExtras(getIntent());
                setResult(3, i);

                finish();
            }
        });
        mTitleBar.setOnLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mContactListView = findViewById(R.id.group_create_member_list);


        //获得已经选中的好友，要显示被选中  只有在添加好友的时候才执行 ,移除好友不需要
        if(isSelectFriends){
            info = (GroupInfo) getIntent().getSerializableExtra("ADD_FRIENDS");
            Log.i(TAG, "init: "+ info);
            if (info !=null){
                List<com.tencent.qcloud.tuikit.tuigroup.bean.GroupMemberInfo> memberDetails = info.getMemberDetails();
                if (memberDetails!=null && memberDetails.size()>0){
                    for (com.tencent.qcloud.tuikit.tuigroup.bean.GroupMemberInfo memberDetail : memberDetails) {
                        GroupMemberInfo memberInfo = new GroupMemberInfo();
                        memberInfo.setAccount(memberDetail.getAccount());
                        memberInfo.setNameCard(TextUtils.isEmpty(memberDetail.getNickName()) ? memberDetail.getAccount() : memberDetail.getNickName());
                        mMembers.add(memberInfo);
                        lastMembers.add(memberInfo);

                    }
                    ContactCustomerAdapter mAdapter = mContactListView.getAdapter();
                    mAdapter.setSelectSource(mMembers);

                }
            }

        }
        presenter = new ContactPresenter(null);
        presenter.setFriendListListener();
        presenter.setIsForCall(isSelectForCall);
        mContactListView.setPresenter(presenter);
        presenter.setContactListView(mContactListView);

        mContactListView.setGroupId(groupId);
        if (isSelectFriends) {
            mTitleBar.setTitle(getString(R.string.add_group_member), TitleBarLayout.Position.MIDDLE);
            mContactListView.loadDataSource(ContactListView.DataSource.FRIEND_LIST);
        } else {
            //根据类型获取数据
            mContactListView.setAddType("remove");
            mContactListView.loadDataSource(ContactListView.DataSource.GROUP_MEMBER_LIST);
        }

        if (!isSelectForCall && !isSelectFriends) {
            mContactListView.setOnItemClickListener(new ContactCustomerListView.OnItemClickListener() {
                @Override
                public void onItemClick(int position, ContactItemBean contact) {
                    if (position == 0) {
                        mMembers.clear();

                        Intent i = new Intent();
                        i.putExtra("groupId",groupId);
                        i.putStringArrayListExtra(TUIContactConstants.Selection.USER_NAMECARD_SELECT, new ArrayList<String>(Arrays.asList(getString(R.string.at_all))));
                        i.putStringArrayListExtra(TUIContactConstants.Selection.USER_ID_SELECT, new ArrayList<String>(Arrays.asList(TUIContactConstants.Selection.SELECT_ALL)));
                        setResult(3, i);

                        finish();
                    }
                }
            });
        }
        mContactListView.setOnSelectChangeListener(new ContactCustomerListView.OnSelectChangedListener() {
            @Override
            public void onSelectChanged(ContactItemBean contact, boolean selected) {
                if (selected) {
                    GroupMemberInfo memberInfo = new GroupMemberInfo();
                    memberInfo.setAccount(contact.getId());
                    memberInfo.setNameCard(TextUtils.isEmpty(contact.getNickName()) ? contact.getId() : contact.getNickName());
                    mMembers.add(memberInfo);
                } else {
                    for (int i = mMembers.size() - 1; i >= 0; i--) {
                        if (mMembers.get(i).getAccount().equals(contact.getId())) {
                            mMembers.remove(i);
                        }
                    }
                }
            }
        });
    }
}
