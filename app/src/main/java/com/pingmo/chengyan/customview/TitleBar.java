package com.pingmo.chengyan.customview;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pingmo.chengyan.R;

public class TitleBar {

    private Activity mActivity = null;

    // 返回按钮
    private ImageView backBtn = null;
    // 自定义按钮
    private View rightBtn = null;
    private ImageView rightIv;
    private TextView rightTv;
    // 标题
    private TextView titleTxt = null;
    // 标题
    private LinearLayout titleLine = null;

    public TitleBar(Activity activity, View parent) {
        mActivity = activity;
        initWidget(parent);
    }

    public void setTitle(String title) {
        titleTxt.setText(title);
    }

    public void setBackground(int index) {
        if (titleLine != null) {
            titleLine.setBackgroundResource(index);
        }
    }

    public void enableBack() {
        backBtn.setVisibility(View.VISIBLE);
        // 默认返回事件，关闭当前activity
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });
    }

    public ImageView getBackBtn() {
        return backBtn;
    }

    public void enableBackBtn(View.OnClickListener clickListener) {
        backBtn.setVisibility(View.VISIBLE);
        if (clickListener != null) {
            // 用户自定义返回事件
            backBtn.setOnClickListener(clickListener);
        } else {
            // 默认返回事件，关闭当前activity
            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.finish();
                }
            });
        }
    }

    public void enableRightBtn(String txt, View.OnClickListener clickListener) {
        rightBtn.setVisibility(View.VISIBLE);
        rightTv.setVisibility(View.VISIBLE);
        rightIv.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(txt)) {
            rightTv.setText(txt);
        }
        if (clickListener != null) {
            // 用户自定义事件
            rightBtn.setOnClickListener(clickListener);
        }
    }

    public void enableRightBtn(int icon, View.OnClickListener clickListener) {
        rightBtn.setVisibility(View.VISIBLE);
        rightIv.setVisibility(View.VISIBLE);
        rightTv.setVisibility(View.GONE);
        if (icon > 0) {
            rightIv.setImageResource(icon);
        }
        if (clickListener != null) {
            // 用户自定义事件
            rightBtn.setOnClickListener(clickListener);
        }
    }

    public void enableCenterBtn(View.OnClickListener clickListener) {
        titleTxt.setVisibility(View.VISIBLE);
        if (clickListener != null) {
            // 用户自定义事件
            titleTxt.setOnClickListener(clickListener);
        }
    }

    public View getRightBtn() {
        return rightBtn;
    }

    public void setRightBtnVisibility(int visibility) {
        rightBtn.setVisibility(visibility);
    }

    public void addCustomTitleView(View view) {
        if (titleLine != null) {
            titleLine.addView(view);
        }
    }

    private void initWidget(View parent) {
        backBtn = parent.findViewById(R.id.back);
        rightBtn = parent.findViewById(R.id.right);
        rightIv = parent.findViewById(R.id.right_iv);
        rightTv = parent.findViewById(R.id.right_tv);
        titleTxt = parent.findViewById(R.id.title);
        titleLine = parent.findViewById(R.id.title_line);
    }

    public void enableLongCenterBtn(View.OnLongClickListener clickListener) {
        titleTxt.setVisibility(View.VISIBLE);
        // 设置页面长按标题,改变数据接口参数
        if (clickListener != null) {
            titleTxt.setOnLongClickListener(clickListener);
        }
    }

    public View getTitle() {
        return titleLine;
    }
}
