package com.tencent.qcloud.tuikit.tuicontact.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.imsdk.v2.V2TIMUserStatus;
import com.tencent.qcloud.tuicore.TUIThemeManager;
import com.tencent.qcloud.tuicore.component.imageEngine.impl.GlideEngine;
import com.tencent.qcloud.tuicore.component.interfaces.IUIKitCallback;
import com.tencent.qcloud.tuicore.util.ToastUtil;
import com.tencent.qcloud.tuikit.tuicontact.R;
import com.tencent.qcloud.tuikit.tuicontact.TUIContactService;
import com.tencent.qcloud.tuikit.tuicontact.bean.ContactItemBean;
import com.tencent.qcloud.tuikit.tuicontact.bean.GroupMemberInfo;
import com.tencent.qcloud.tuikit.tuicontact.config.TUIContactConfig;
import com.tencent.qcloud.tuikit.tuicontact.model.ContactsBean;
import com.tencent.qcloud.tuikit.tuicontact.presenter.ContactPresenter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class PersionCustomerAdapter extends RecyclerView.Adapter<PersionCustomerAdapter.ViewHolder> {

    private Context context;
    protected List<ContactItemBean> mData;
    private PersionCustomerListView.OnSelectChangedListener mOnSelectChangedListener;
    private PersionCustomerListView.OnItemClickListener mOnClickListener;

    private int mPreSelectedPosition;
    private boolean isSingleSelectMode;
    private ContactPresenter presenter;
    private ContactsBean contactsBean;
    private boolean isGroupList = false;
    private int dataSourceType = ContactCustomerListView.DataSource.UNKNOWN;

    private int chartType  = 0;
    private boolean isFistLoad = true;


    public PersionCustomerAdapter(Context context, List<ContactItemBean> data) {
        this.context = context;
        this.mData = data;
    }

    public void setPresenter(ContactPresenter presenter) {
        this.presenter = presenter;

    }

    public void setIsGroupList(boolean groupList) {
        isGroupList = groupList;
    }

    @Override
    public PersionCustomerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PersionCustomerAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_selecable_adapter_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final PersionCustomerAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final ContactItemBean contactBean = mData.get(position);
        holder.setIsRecyclable(false);
        holder.avatar.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(contactBean.getRemark())) {
            holder.tvName.setText(contactBean.getRemark());
        } else if (!TextUtils.isEmpty(contactBean.getNickName())) {
            holder.tvName.setText(contactBean.getNickName());
        } else {
            holder.tvName.setText(contactBean.getId());
        }

        holder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!contactBean.isEnable()) {
                    return;
                }


                holder.ccSelect.setChecked(!holder.ccSelect.isChecked());
                if (mOnSelectChangedListener != null) {
                    mOnSelectChangedListener.onSelectChanged(getItem(position), holder.ccSelect.isChecked());
                }

                if (mOnClickListener != null) {
                    mOnClickListener.onItemClick(position, contactBean);
                }
                if (isSingleSelectMode && position != mPreSelectedPosition && contactBean.isSelected()) {
                    //单选模式的prePos处理
                    mData.get(mPreSelectedPosition).setSelected(false);
                    notifyItemChanged(mPreSelectedPosition);
                }
                mPreSelectedPosition = position;
            }
        });
        holder.unreadText.setVisibility(View.GONE);
        holder.userStatusView.setVisibility(View.GONE);
        holder.ll_base_friend.setVisibility(View.GONE);
        //我的好友

        holder.avatar.setVisibility(View.VISIBLE);
        int radius = holder.itemView.getResources().getDimensionPixelSize(R.dimen.contact_profile_face_radius);
        if (isGroupList) {
            //群聊头像
            GlideEngine.loadUserIcon(holder.avatar, contactBean.getAvatarUrl(), TUIThemeManager.getAttrResId(holder.avatar.getContext(), R.attr.core_default_group_icon), radius);
        } else {
            GlideEngine.loadUserIcon(holder.avatar, contactBean.getAvatarUrl(), radius);
        }


        if (dataSourceType == ContactListView.DataSource.CONTACT_LIST && TUIContactConfig.getInstance().isShowUserStatus()) {
            holder.userStatusView.setVisibility(View.VISIBLE);
            if (contactBean.getStatusType() == V2TIMUserStatus.V2TIM_USER_STATUS_ONLINE) {
                holder.userStatusView.setBackgroundResource(TUIThemeManager.getAttrResId(TUIContactService.getAppContext(), com.tencent.qcloud.tuicore.R.attr.user_status_online));
            } else {
                holder.userStatusView.setBackgroundResource(TUIThemeManager.getAttrResId(TUIContactService.getAppContext(), com.tencent.qcloud.tuicore.R.attr.user_status_offline));
            }
        } else {
            holder.userStatusView.setVisibility(View.GONE);

        }

    }

    @Override
    public void onViewRecycled(PersionCustomerAdapter.ViewHolder holder) {
        if (holder != null) {
            GlideEngine.clear(holder.avatar);
            holder.avatar.setImageResource(0);

        }
        super.onViewRecycled(holder);
    }

    private ContactItemBean getItem(int position) {
        if (position < mData.size()) {
            return mData.get(position);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public void setDataSource(List<ContactItemBean> datas) {
        this.mData = datas;
        notifyDataSetChanged();
    }
    public ArrayList<GroupMemberInfo> selectDatas;
    public void setSelectSource(ArrayList<GroupMemberInfo> datas){
        this.selectDatas = datas;
        notifyDataSetChanged();
    }

    public void setSingleSelectMode(boolean mode) {
        isSingleSelectMode = mode;
    }

    public void setOnSelectChangedListener(PersionCustomerListView.OnSelectChangedListener selectListener) {
        mOnSelectChangedListener = selectListener;
    }

    public void setOnItemClickListener(PersionCustomerListView.OnItemClickListener listener) {
        mOnClickListener = listener;
    }



    public void setDataSourceType(int dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView unreadText;
        ImageView avatar;
        CheckBox ccSelect;
        View content;
        View line;
        View userStatusView;
        RelativeLayout rl_middle_count;
       LinearLayout ll_base_friend;
        LinearLayout  ll_contacts_friends;
        TextView  tv_contacts_friends;
        LinearLayout  ll_contacts_group_chat;
        TextView  tv_contacts_group_chat;
        RelativeLayout rl_ivAvatar;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCity);
            unreadText = itemView.findViewById(R.id.conversation_unread);
            unreadText.setVisibility(View.GONE);
            avatar = itemView.findViewById(R.id.ivAvatar);
            rl_ivAvatar = itemView.findViewById(R.id.rl_ivAvatar);
            ccSelect = itemView.findViewById(R.id.contact_check_box);
            content = itemView.findViewById(R.id.selectable_contact_item);
            line = itemView.findViewById(R.id.view_line);
            userStatusView = itemView.findViewById(R.id.user_status);
            //设置属性
            rl_middle_count = itemView.findViewById(R.id.rl_middle_count);

            //显示好友  ll_base_friend
            ll_base_friend = itemView.findViewById(R.id.ll_base_friend);
            //好友
              ll_contacts_friends =itemView.findViewById(R.id.ll_contacts_friends);
              tv_contacts_friends =itemView.findViewById(R.id.tv_contacts_friends);
            //群聊
              ll_contacts_group_chat =itemView.findViewById(R.id.ll_contacts_group_chat);
              tv_contacts_group_chat =itemView.findViewById(R.id.tv_contacts_group_chat);
        }
    }
}
