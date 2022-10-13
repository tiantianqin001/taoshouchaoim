package com.tencent.qcloud.tuikit.tuichat.ui.page;

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
import com.tencent.qcloud.tuikit.tuichat.R;
import com.tencent.qcloud.tuikit.tuicontact.TUIContactConstants;
import com.tencent.qcloud.tuikit.tuicontact.bean.ContactItemBean;
import com.tencent.qcloud.tuikit.tuicontact.bean.GroupMemberInfo;
import com.tencent.qcloud.tuikit.tuicontact.presenter.ContactPresenter;
import com.tencent.qcloud.tuikit.tuicontact.ui.view.ContactCustomerListView;
import com.tencent.qcloud.tuikit.tuicontact.ui.view.ContactListView;
import com.tencent.qcloud.tuikit.tuicontact.ui.view.ContactOriginListView;

import java.util.ArrayList;
import java.util.List;

public class OrigintationActivity extends BaseLightActivity {
    private static final String TAG = OrigintationActivity.class.getSimpleName();

    private TitleBarLayout mTitleBar;
    private ContactOriginListView mContactListView;
    private ArrayList<GroupMemberInfo> mMembers = new ArrayList<>();

    private ContactPresenter presenter;
    private String groupId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_start_group_origin_activity);

        init();
    }

    private void init() {
        mMembers.clear();
        groupId = getIntent().getStringExtra(TUIContactConstants.Group.GROUP_ID);


        mTitleBar = findViewById(R.id.group_create_title_bar);
        mTitleBar.setBackgroundColor(getResources().getColor(R.color.white));
        RelativeLayout rootView = mTitleBar.getRootView();
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) rootView.getLayoutParams();
        params.setMargins(0,0,0,0);
        rootView.setLayoutParams(params);

        mTitleBar.getLeftIcon().setBackground(getResources().getDrawable(R.drawable.back));
        mTitleBar.setTitle(getResources().getString(R.string.sure), ITitleBarLayout.Position.RIGHT);
        mTitleBar.getRightIcon().setVisibility(View.GONE);

        mTitleBar.setOnLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mContactListView = findViewById(R.id.group_create_member_list);



        presenter = new ContactPresenter(null);
        presenter.setFriendListListener();

        mContactListView.setPresenter(presenter);
        presenter.setContactListView(mContactListView);

        mContactListView.setGroupId(groupId);
        mTitleBar.setTitle("选择领取的人", TitleBarLayout.Position.MIDDLE);
        mTitleBar.getRightTitle().setVisibility(View.GONE);
        mContactListView.loadDataSource(ContactListView.DataSource.FRIEND_LIST);

        mContactListView.setOnItemClickListener(new ContactOriginListView.OnItemClickListener() {
            @Override
            public void onItemClick(int position, ContactItemBean contact) {
                Log.i(TAG, "onItemClick: "+contact);
                Intent intent = new Intent();
                intent.putExtra("photo",contact.getAvatarUrl());
                intent.putExtra("ninkName",contact.getNickName());
                intent.putExtra("userId",contact.getId());
                OrigintationActivity.this.setResult(100,intent);
                OrigintationActivity.this.finish();

            }
        });

    }
}
