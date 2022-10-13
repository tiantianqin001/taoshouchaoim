package com.tencent.qcloud.tuikit.tuichat.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.qcloud.tuikit.tuichat.R;


/**
 * 自定义键盘
 */
public class CustomerKeyboardPassWord extends LinearLayout implements View.OnClickListener {
    private int layoutId = R.layout.keyboard_password;
    private CustomerKeyboardClickListener mListener;

    public CustomerKeyboardPassWord(Context context) {
        this(context, null);
    }

    public CustomerKeyboardPassWord(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomerKeyboardPassWord(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs, defStyleAttr);
        setChildViewOnclick(this);
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomerKeyboard);
        layoutId = typedArray.getResourceId(R.styleable.CustomerKeyboard_customerKeyboardLayout, layoutId);
        inflate(context, layoutId, this);

    }

    /**
     * 设置键盘子View的点击事件
     */
    private void setChildViewOnclick(ViewGroup parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            // 不断的递归设置点击事件
            View view = parent.getChildAt(i);
            if (view instanceof ViewGroup) {
                setChildViewOnclick((ViewGroup) view);
                continue;
            }
            view.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        View clickView = v;


        if (clickView instanceof TextView) {
            // 如果点击的是TextView
            String number = ((TextView) clickView).getText().toString();
            if (!TextUtils.isEmpty(number)) {

                if (mListener != null) {
                    // 回调
                    mListener.click(number);
                }
            }

        } else if (clickView instanceof ImageView) {
            // 如果是图片那肯定点击的是删除
            if (mListener != null) {
                mListener.delete();
            }
        }
    }

    /**
     * 设置键盘的点击回调监听
     */
    public void setOnCustomerKeyboardClickListener(CustomerKeyboardClickListener listener) {
        this.mListener = listener;
    }

    /**
     * 点击键盘的回调监听
     */
    public interface CustomerKeyboardClickListener {
        public void click(String number);

        public void delete();
    }
}