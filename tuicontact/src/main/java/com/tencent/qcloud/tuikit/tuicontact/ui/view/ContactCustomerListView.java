package com.tencent.qcloud.tuikit.tuicontact.ui.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tencent.imsdk.v2.V2TIMGroupMemberFullInfo;
import com.tencent.qcloud.tuicore.component.CustomLinearLayoutManager;
import com.tencent.qcloud.tuikit.tuicontact.R;
import com.tencent.qcloud.tuikit.tuicontact.bean.ContactItemBean;
import com.tencent.qcloud.tuikit.tuicontact.component.indexlib.IndexBar.widget.IndexBar;
import com.tencent.qcloud.tuikit.tuicontact.component.indexlib.suspension.SuspensionDecoration;
import com.tencent.qcloud.tuikit.tuicontact.presenter.ContactPresenter;
import com.tencent.qcloud.tuikit.tuicontact.ui.interfaces.IContactListView;

import java.util.ArrayList;
import java.util.List;


public class ContactCustomerListView extends LinearLayout implements IContactListView {

    private static final String TAG = ContactCustomerListView.class.getSimpleName();

    private static final String INDEX_STRING_TOP = "↑";
    private RecyclerView mRv;
    private ContactCustomerAdapter mAdapter;
    private CustomLinearLayoutManager mManager;
    private List<ContactItemBean> mData = new ArrayList<>();
    private List<ContactItemBean> mNewData = new ArrayList<>();
    private SuspensionDecoration mDecoration;
    private ProgressBar mContactLoadingBar;
    private String groupId;
    private boolean isGroupList = false;

    private TextView notFoundTip;
    /**
     * 右侧边栏导航区域
     */
    private IndexBar mIndexBar;

    /**
     * 显示指示器DialogText
     */
    private TextView mTvSideBarHint;

    private ContactPresenter presenter;

    private int dataSourceType = DataSource.UNKNOWN;
    private String type;

    public ContactCustomerListView(Context context) {
        super(context);
        init();
    }

    public ContactCustomerListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ContactCustomerListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setPresenter(ContactPresenter presenter) {
        this.presenter = presenter;
        if (mAdapter != null) {
            mAdapter.setPresenter(presenter);
            mAdapter.setIsGroupList(isGroupList);
        }
    }
    public void setIsGroupList(boolean isGroupList) {
        this.isGroupList = isGroupList;
    }

    private void init() {
        inflate(getContext(), R.layout.contact_list, this);
        mRv = findViewById(R.id.contact_member_list);
        notFoundTip = findViewById(R.id.not_found_tip);
        mManager = new CustomLinearLayoutManager(getContext());
        mRv.setLayoutManager(mManager);

        mAdapter = new ContactCustomerAdapter(getContext(),mData);
        mRv.setAdapter(mAdapter);
        mRv.addItemDecoration(mDecoration = new SuspensionDecoration(getContext(), mData));
        mRv.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int lastCompletelyVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                //TUILiveLog.i(TAG, "lastCompletelyVisibleItemPosition: "+lastCompletelyVisibleItemPosition);
                if(lastCompletelyVisibleItemPosition==layoutManager.getItemCount()-1) {
                    if (presenter.getNextSeq() > 0) {
                        presenter.loadGroupMemberList(groupId);
                    }
                }
            }
        });
        mTvSideBarHint = findViewById(R.id.contact_tvSideBarHint);
        mIndexBar = findViewById(R.id.contact_indexBar);
        mIndexBar.setPressedShowTextView(mTvSideBarHint)
                .setNeedRealIndex(false)
                .setLayoutManager(mManager);
        mContactLoadingBar = findViewById(R.id.contact_loading_bar);
    }

    public ContactCustomerAdapter getAdapter() {
        return mAdapter;
    }

    public void setNotFoundTip(String text) {
        notFoundTip.setText(text);
    }

    @Override
    public void onDataSourceChanged(List<ContactItemBean> data) {
        if (data == null || data.isEmpty()) {
            if (!TextUtils.isEmpty(notFoundTip.getText())) {
                notFoundTip.setVisibility(VISIBLE);
            }
        } else {
            notFoundTip.setVisibility(GONE);
        }
        mContactLoadingBar.setVisibility(GONE);
        //去掉客服的聊天
        mNewData.clear();
        if (!TextUtils.isEmpty(type) && type.equals("remove")){
            for (ContactItemBean datum : data) {
                //移除客服
                if (!datum.getId().equals("13532325")) {
                  //根据role 测试是不是群主和管理员  如果是减号就要移除群主和管理员
                    int role = datum.getRole();
                    if (role != V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_ROLE_OWNER) {
                        mNewData.add(datum);
                    }
                }

//            //群管理员
//            if (role == V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_ROLE_ADMIN) {
//
//                //群主
//            } else if (role == V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_ROLE_OWNER) {
//              //普通成员
//            } else if (role == V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_ROLE_MEMBER) {
//                mNewData.add(datum);
//            }
            }


        }else {
            for (ContactItemBean datum : data) {
                //移除客服
                if (!datum.getId().equals("13532325")) {
                    mNewData.add(datum);
                }
            }
        }





        this.mData = mNewData;
        mAdapter.setDataSource(mNewData);
        mIndexBar.setSourceDatas(mNewData).invalidate();
        mDecoration.setDatas(mNewData);
    }

    @Override
    public void onFriendApplicationChanged() {
        if (dataSourceType == DataSource.CONTACT_LIST) {
            mAdapter.notifyItemChanged(0);
        }
    }

    public void setSingleSelectMode(boolean mode) {
        mAdapter.setSingleSelectMode(mode);
    }

    public void setOnSelectChangeListener(OnSelectChangedListener selectChangeListener) {
        mAdapter.setOnSelectChangedListener(selectChangeListener);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mAdapter.setOnItemClickListener(listener);
    }

    public void loadDataSource(int dataSource) {
        this.dataSourceType = dataSource;
        if (presenter == null) {
            return;
        }
        if (mAdapter != null) {
            mAdapter.setDataSourceType(dataSource);
        }
        mContactLoadingBar.setVisibility(VISIBLE);
        if (dataSource == ContactCustomerListView.DataSource.GROUP_MEMBER_LIST) {
            presenter.loadGroupMemberList(groupId);
        } else {
            presenter.loadDataSource(dataSource);
        }
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setAddType(String type){

        this.type = type;
    }


    public interface OnSelectChangedListener {
        void onSelectChanged(ContactItemBean contact, boolean selected);
    }

    public interface OnItemClickListener {
        void onItemClick(int position, ContactItemBean contact);
    }

    public static class DataSource {
        public static final int UNKNOWN = -1;
        public static final int FRIEND_LIST = 1;
        public static final int BLACK_LIST = 2;
        public static final int GROUP_LIST = 3;
        public static final int CONTACT_LIST = 4;
        public static final int GROUP_MEMBER_LIST = 5;
    }
}
