package com.tencent.qcloud.tuikit.tuichat.ui.page;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.tencent.qcloud.tuicore.component.TitleBarLayout;
import com.tencent.qcloud.tuicore.component.activities.BaseLightActivity;
import com.tencent.qcloud.tuicore.component.interfaces.ITitleBarLayout;
import com.tencent.qcloud.tuicore.component.interfaces.IUIKitCallback;
import com.tencent.qcloud.tuicore.util.ToastUtil;
import com.tencent.qcloud.tuikit.tuicontact.R;
import com.tencent.qcloud.tuikit.tuicontact.TUIContactConstants;
import com.tencent.qcloud.tuikit.tuicontact.bean.ContactItemBean;
import com.tencent.qcloud.tuikit.tuicontact.bean.GroupMemberInfo;
import com.tencent.qcloud.tuikit.tuicontact.bean.PersionCardInfoBean;
import com.tencent.qcloud.tuikit.tuicontact.presenter.ContactPresenter;
import com.tencent.qcloud.tuikit.tuicontact.ui.view.CommonPersionDialog;
import com.tencent.qcloud.tuikit.tuicontact.ui.view.ContactCustomerAdapter;
import com.tencent.qcloud.tuikit.tuicontact.ui.view.ContactCustomerListView;
import com.tencent.qcloud.tuikit.tuicontact.ui.view.ContactListView;
import com.tencent.qcloud.tuikit.tuicontact.ui.view.PersionCustomerListView;
import com.tencent.qcloud.tuikit.tuigroup.bean.GroupInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.Nullable;

public class PersionMemberSelectActivity extends BaseLightActivity {

    private static final String TAG = PersionMemberSelectActivity.class.getSimpleName();

    private TitleBarLayout mTitleBar;
    private PersionCustomerListView mContactListView;
    private ArrayList<GroupMemberInfo> mMembers = new ArrayList<>();
    private ArrayList<GroupMemberInfo> lastMembers = new ArrayList<>();

    private ContactPresenter presenter;
    private String groupId;
    private GroupInfo info;
    private String headImage;
    private String ninkName;

    public static IUIKitCallback mCallBack;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_start_group_select_activity);
        headImage = getIntent().getStringExtra("headImage");
        ninkName = getIntent().getStringExtra("ninkName");

        init();
    }


    private void init() {


        mTitleBar = findViewById(R.id.group_create_title_bar);
        mTitleBar.setBackgroundColor(getResources().getColor(R.color.white));
        RelativeLayout rootView = mTitleBar.getRootView();
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) rootView.getLayoutParams();
        params.setMargins(0,0,0,0);
        rootView.setLayoutParams(params);

        mTitleBar.getLeftIcon().setBackground(getResources().getDrawable(R.drawable.back));

        mTitleBar.setTitle(getResources().getString(R.string.change_friend), ITitleBarLayout.Position.MIDDLE);

        mTitleBar.getRightIcon().setVisibility(View.GONE);
        mTitleBar.getRightTitle().setVisibility(View.GONE);

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
       // mTitleBar.setTitle(getString(R.string.add_group_member), TitleBarLayout.Position.MIDDLE);
        mContactListView.loadDataSource(ContactListView.DataSource.FRIEND_LIST);

        mContactListView.setOnItemClickListener(new PersionCustomerListView.OnItemClickListener() {
            @Override
            public void onItemClick(int position, ContactItemBean contact) {
                PersionCardInfoBean persionCardInfoBean = new PersionCardInfoBean();
                persionCardInfoBean.sendToHeadImage = headImage;
                persionCardInfoBean.sendToNinkName = ninkName;


                Log.i(TAG, "onItemClick: "+contact);
                String cardName = contact.getNickName();
                CommonPersionDialog commonPersionDialog = new CommonPersionDialog(PersionMemberSelectActivity.this);
                commonPersionDialog.setNiniName(headImage,ninkName,cardName);
                commonPersionDialog.showDialog();
                commonPersionDialog.setOnClickListener(new CommonPersionDialog.onClickListener() {
                    @Override
                    public void sendInfo() {
                        //发送群消息
                      if (mCallBack!=null){
                          mCallBack.onSuccess(contact);
                          finish();
                      }

                    }
                });


            }
        });

    }



}
