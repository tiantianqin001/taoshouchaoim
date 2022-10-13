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

import androidx.recyclerview.widget.RecyclerView;

import com.tencent.imsdk.v2.V2TIMUserStatus;
import com.tencent.qcloud.tuicore.TUIThemeManager;
import com.tencent.qcloud.tuicore.component.gatherimage.ShadeImageView;
import com.tencent.qcloud.tuicore.component.imageEngine.impl.GlideEngine;
import com.tencent.qcloud.tuicore.component.interfaces.IUIKitCallback;
import com.tencent.qcloud.tuicore.util.ImageUtil;
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

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private Context context;
    protected List<ContactItemBean> mData;
    private ContactListView.OnSelectChangedListener mOnSelectChangedListener;
    private ContactListView.OnItemClickListener mOnClickListener;

    private int mPreSelectedPosition;
    private boolean isSingleSelectMode;
    private ContactPresenter presenter;
    private ContactsBean contactsBean;
    private boolean isGroupList = false;
    private int dataSourceType = ContactListView.DataSource.UNKNOWN;

    private int chartType  = 0;
    private boolean isFistLoad = true;


    public ContactAdapter(Context context,List<ContactItemBean> data) {
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
    public ContactAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContactAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_selecable_adapter_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ContactAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final ContactItemBean contactBean = mData.get(position);
        holder.setIsRecyclable(false);
        holder.avatar.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(contactBean.getRemark())) {
            holder.tvName.setText(contactBean.getRemark());
        } else if (!TextUtils.isEmpty(contactBean.getNickName())) {
            holder.tvName.setText(contactBean.getNickName());
        } else {
            holder.tvName.setText(contactBean.getId());
        }

        setIsGroupList(contactBean.isGroup());
        if (mOnSelectChangedListener != null) {
            holder.ccSelect.setVisibility(View.VISIBLE);
            holder.ccSelect.setChecked(contactBean.isSelected());
        }

        if (selectDatas!=null && selectDatas.size()>0){
            //已经选中的好友要高亮
            for (GroupMemberInfo selectData : selectDatas) {
                String id = selectData.getAccount();
                if (id.equals(contactBean.getId())){
                    holder.ccSelect.setChecked(true);
                }
            }
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
                contactBean.setSelected(holder.ccSelect.isChecked());
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
        if (TextUtils.equals(TUIContactService.getAppContext().getResources().getString(R.string.new_friend), contactBean.getId())) {
           // holder.avatar.setImageResource(TUIThemeManager.getAttrResId(holder.itemView.getContext(), R.attr.contact_new_friend_icon));
            //新的好友
            final float scale = context.getResources().getDisplayMetrics().density;
            int left=  (int) (45 * scale + 0.5f);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)
                    holder.rl_middle_count.getLayoutParams();

            layoutParams.setMargins(-left,0,0,0);
            holder.rl_middle_count.setLayoutParams(layoutParams);
            holder.avatar.setVisibility(View.GONE);

            presenter.getFriendApplicationUnreadCount(new IUIKitCallback<Integer>() {
                @Override
                public void onSuccess(Integer data) {
                    if (data == 0) {
                        holder.unreadText.setVisibility(View.GONE);
                    } else {
                        holder.unreadText.setVisibility(View.VISIBLE);
                        holder.unreadText.setText("" + data);
                    }
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    ToastUtil.toastShortMessage("Error code = " + errCode + ", desc = " + errMsg);
                }
            });

        } else if (TextUtils.equals(TUIContactService.getAppContext().getResources().getString(R.string.blacklist), contactBean.getId())) {
            //黑名单 已实现
            final float scale = context.getResources().getDisplayMetrics().density;
          int left=  (int) (45 * scale + 0.5f);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)
                    holder.rl_middle_count.getLayoutParams();

            layoutParams.setMargins(-left,0,0,0);
            holder.rl_middle_count.setLayoutParams(layoutParams);
            holder.avatar.setVisibility(View.INVISIBLE);
           // holder.avatar.setImageResource(TUIThemeManager.getAttrResId(holder.itemView.getContext(), R.attr.contact_group_list_icon));
        } else if (TextUtils.equals(TUIContactService.getAppContext().getResources().getString(R.string.contact_my_group), contactBean.getId())) {
            //我的群聊
            final float scale = context.getResources().getDisplayMetrics().density;
            int left=  (int) (45 * scale + 0.5f);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)
                    holder.rl_middle_count.getLayoutParams();

            layoutParams.setMargins(-left,0,0,0);
            holder.rl_middle_count.setLayoutParams(layoutParams);
            holder.avatar.setVisibility(View.INVISIBLE);
          //  holder.avatar.setImageResource(TUIThemeManager.getAttrResId(holder.itemView.getContext(), R.attr.contact_black_list_icon));
            //holder.avatar.setVisibility(View.GONE);
        }else if (TextUtils.equals(TUIContactService.getAppContext().getResources().getString(R.string.group_invitation), contactBean.getId())){
            //群聊邀请
            final float scale = context.getResources().getDisplayMetrics().density;
            int left=  (int) (45 * scale + 0.5f);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)
                    holder.rl_middle_count.getLayoutParams();

            layoutParams.setMargins(-left,0,0,0);
            holder.rl_middle_count.setLayoutParams(layoutParams);
            holder.avatar.setVisibility(View.INVISIBLE);
        } else if (TextUtils.equals(TUIContactService.getAppContext().getResources().getString(R.string.my_server), contactBean.getId())) {
            //我的客服
            final float scale = context.getResources().getDisplayMetrics().density;
            int left=  (int) (45 * scale + 0.5f);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)
                    holder.rl_middle_count.getLayoutParams();

            layoutParams.setMargins(-left,0,0,0);
            holder.rl_middle_count.setLayoutParams(layoutParams);
           // holder.avatar.setImageResource(TUIThemeManager.getAttrResId(holder.itemView.getContext(), R.attr.contact_black_list_icon));
           if (chartType == 0){
               holder.ll_base_friend.setVisibility(View.VISIBLE);
               holder.tv_contacts_group_chat.setBackground(context.getResources().getDrawable(R.drawable.save_firent_nomal_bg));
               holder.tv_contacts_group_chat.setTextColor(context.getResources().getColor(R.color.goup_info));

               holder.tv_contacts_friends.setBackground(context.getResources().getDrawable(R.drawable.save_firent_bg));
               holder.tv_contacts_friends.setTextColor(context.getResources().getColor(R.color.white));

           }else if (chartType == 1){
               holder.ll_base_friend.setVisibility(View.VISIBLE);
               holder.tv_contacts_friends.setBackground(context.getResources().getDrawable(R.drawable.save_firent_nomal_bg));
               holder.tv_contacts_friends.setTextColor(context.getResources().getColor(R.color.goup_info));

               holder.tv_contacts_group_chat.setBackground(context.getResources().getDrawable(R.drawable.save_firent_bg));
               holder.tv_contacts_group_chat.setTextColor(context.getResources().getColor(R.color.white));
           }

           if (isFistLoad){
               ContactsBean contactsBean = new ContactsBean();
               contactsBean.setName("contacts_friends");
               EventBus.getDefault().post(contactsBean);
               isFistLoad = false;
           }
            //好友的点击事件
            holder.ll_contacts_friends.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chartType = 0;
                    ContactsBean contactsBean = new ContactsBean();
                    contactsBean.setName("contacts_friends");
                    EventBus.getDefault().post(contactsBean);
                }
            });
            // 群聊ll_contacts_group_chat
            holder.ll_contacts_group_chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chartType = 1;
                    ContactsBean contactsBean = new ContactsBean();
                    contactsBean.setName("group_chat");
                    EventBus.getDefault().post(contactsBean);
                }
            });
        } else {
            //我的好友

            holder.avatar.setVisibility(View.VISIBLE);
            int radius = holder.itemView.getResources().getDimensionPixelSize(R.dimen.contact_profile_face_radius);
            if (isGroupList) {
                //群聊头像
                String avatarUrl = contactBean.getAvatarUrl();
                if (TextUtils.isEmpty(avatarUrl)){
                    String savedIcon = ImageUtil.getGroupConversationAvatar("group_"+contactBean.getId());
                    GlideEngine.loadUserIcon(holder.avatar, savedIcon, TUIThemeManager.getAttrResId(holder.avatar.getContext(),
                            R.attr.core_default_group_icon), radius);
                }else {
                    GlideEngine.loadUserIcon(holder.avatar, avatarUrl, TUIThemeManager.getAttrResId(holder.avatar.getContext(),
                            R.attr.core_default_group_icon), radius);
                }

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

    }

    @Override
    public void onViewRecycled(ContactAdapter.ViewHolder holder) {
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

    public void setOnSelectChangedListener(ContactListView.OnSelectChangedListener selectListener) {
        mOnSelectChangedListener = selectListener;
    }

    public void setOnItemClickListener(ContactListView.OnItemClickListener listener) {
        mOnClickListener = listener;
    }



    public void setDataSourceType(int dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView unreadText;
        ShadeImageView avatar;
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
