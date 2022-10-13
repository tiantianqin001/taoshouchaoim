package com.pingmo.chengyan.activity.shop.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.Nullable;

import com.pingmo.chengyan.R;


/**
 * #417DED 背景的toolbar
 */
public abstract class TitleC47DEDBaseActivity extends BaseActivity {
    private ViewFlipper contentContainer;
    private ImageView mIvBack;
    private TextView mTvTitle, mTvTitleRight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base_title_c417ded);
        contentContainer = findViewById(R.id.layout_container);
        mIvBack = findViewById(R.id.iv_back);
        mTvTitle = findViewById(R.id.tv_title);
        mTvTitleRight = findViewById(R.id.tv_title_right);
        mIvBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void initStatusBar() {
//        StatusBarUtils.setColor(this, getResources().getColor(R.color.color_417ded)); //Color.parseColor("#F5F6F9")
//        StatusBarUtils.setTextDark(this, false);
    }

    @Override
    public void setContentView(View view) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        contentContainer.addView(view, lp);
    }

    @Override
    public void setContentView(int layoutResID) {
        View view = LayoutInflater.from(this).inflate(layoutResID, null);
        setContentView(view);
        try {
            initParams();
        } catch (Exception e) {
            e.printStackTrace();
        }
        initView();
        initViewModel();

        initData();
    }

    protected void initData() {

    }

    public TextView getTvTitle() {
        return mTvTitle;
    }

    public TextView getTvTitleRight() {
        return mTvTitleRight;
    }

    public ImageView getIvBack() {
        return mIvBack;
    }

    public void hideBack() {
        mIvBack.setVisibility(View.GONE);
    }

    public void setTitle(String title) {
        mTvTitle.setText(title);
    }

    public void setTitleRight(String title) {
        if (TextUtils.isEmpty(title)) {
            mTvTitleRight.setVisibility(View.GONE);
        } else {
            mTvTitleRight.setText(title);
            mTvTitleRight.setVisibility(View.VISIBLE);
        }
    }

    public void setTitle(int titleId) {
        mTvTitle.setText(titleId);
    }

    public void setTitleRight(int titleId) {
        mTvTitleRight.setText(titleId);
    }

    protected abstract void initView();

    protected void initParams() {
    }

    protected abstract void initViewModel();

    @Override
    public void finish() {
        super.finish();
        hideInputKeyboard();
    }
}