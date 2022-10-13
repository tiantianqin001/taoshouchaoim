package com.tencent.qcloud.tuicore.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.qcloud.tuicore.R;
import com.tencent.qcloud.tuicore.TUIThemeManager;
import com.tencent.qcloud.tuicore.component.interfaces.ITitleBarLayout;
import com.tencent.qcloud.tuicore.util.ScreenUtil;

import androidx.annotation.Nullable;

public class TitleBarLayout extends LinearLayout implements ITitleBarLayout {

    private LinearLayout mLeftGroup;
    private LinearLayout mRightGroup;
    private TextView mLeftTitle;
    private TextView mCenterTitle;
    private TextView mRightTitle;
    private ImageView mLeftIcon;
    private ImageView mRightIcon;
    private RelativeLayout mTitleLayout;
    private UnreadCountTextView unreadCountTextView;
    private ImageView mRightSearIcon;
    private LinearLayout mRightSercherGroup;

    public TitleBarLayout(Context context) {
        super(context);
        init(context);
    }

    public TitleBarLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TitleBarLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
      // View view = LayoutInflater.from(context).inflate(R.layout.title_bar_layout, this, true);
        View view = inflate(context, R.layout.title_bar_layout, this);
        mTitleLayout = view.findViewById(R.id.page_title_layout);

        mLeftGroup = view.findViewById(R.id.page_title_left_group);
        mRightGroup = view.findViewById(R.id.page_title_right_group);
        //搜索
        mRightSercherGroup = view.findViewById(R.id.ll_title_right_search);
        mLeftTitle = view.findViewById(R.id.page_title_left_text);
        mRightTitle = view.findViewById(R.id.page_title_right_text);
        mCenterTitle = view.findViewById(R.id.page_title);
        mLeftIcon = view.findViewById(R.id.page_title_left_icon);
        mRightIcon = view.findViewById(R.id.page_title_right_icon);
        //搜索
        mRightSearIcon = view.findViewById(R.id.page_title_right_search);
        unreadCountTextView = view.findViewById(R.id.new_message_total_unread);

        LayoutParams params = (LayoutParams) mTitleLayout.getLayoutParams();
        params.height = ScreenUtil.getPxByDp(50);
        mTitleLayout.setLayoutParams(params);
        setBackgroundResource(TUIThemeManager.getAttrResId(getContext(), R.attr.core_title_bar_bg));

        int iconSize = ScreenUtil.dip2px(30);
        ViewGroup.LayoutParams iconParams = mLeftIcon.getLayoutParams();
        iconParams.width = iconSize;
        iconParams.height = iconSize;
        mLeftIcon.setLayoutParams(iconParams);
        iconParams = mRightIcon.getLayoutParams();
        iconParams.width = iconSize;
        iconParams.height = iconSize;

        mRightIcon.setLayoutParams(iconParams);

    }

    @Override
    public void setOnLeftClickListener(OnClickListener listener) {
        mLeftGroup.setOnClickListener(listener);
    }

    @Override
    public void setOnRightClickListener(OnClickListener listener) {
        mRightGroup.setOnClickListener(listener);
    }

    @Override
    public void setOnRightSearchClickListener(OnClickListener listener) {
        mRightSercherGroup.setOnClickListener(listener);
    }

    @Override
    public void setTitle(String title, Position position) {
        switch (position) {
            case LEFT:
                mLeftTitle.setText(title);
                break;
            case RIGHT:
                mRightTitle.setText(title);
                break;
            case MIDDLE:
                mCenterTitle.setText(title);
                break;
        }
    }

    @Override
    public LinearLayout getLeftGroup() {
        return mLeftGroup;
    }

    @Override
    public LinearLayout getRightGroup() {
        return mRightGroup;
    }

    @Override
    public ImageView getLeftIcon() {
        return mLeftIcon;
    }

    @Override
    public void setLeftIcon(int resId) {
        mLeftIcon.setBackgroundResource(resId);
    }

    @Override
    public ImageView getRightIcon() {
        return mRightIcon;
    }

    @Override
    public void setRightIcon(int resId) {
        mRightIcon.setBackgroundResource(resId);
    }

    @Override
    public void setRightSearchIcon(int resId) {
        mRightSearIcon.setBackgroundResource(resId);
    }




    @Override
    public TextView getLeftTitle() {
        return mLeftTitle;
    }

    @Override
    public TextView getMiddleTitle() {
        return mCenterTitle;
    }

    @Override
    public TextView getRightTitle() {
        return mRightTitle;
    }
    @Override
    public RelativeLayout getRootView() {
        return mTitleLayout;
    }
    @Override
    public ImageView getRightSearchIcon() {
        return mRightSearIcon;
    }

    public UnreadCountTextView getUnreadCountTextView() {
        return unreadCountTextView;
    }
}
