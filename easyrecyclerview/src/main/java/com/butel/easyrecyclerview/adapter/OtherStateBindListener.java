package com.butel.easyrecyclerview.adapter;

import android.view.View;

/**
 * 描述：其他页面状态监听
 * 类名：OtherStateBindListener
 * 作者：xihao
 * 日期：2018/7/19
 */
public interface OtherStateBindListener {
    void onBindView(OtherStateViewHolder holder, int currentState);

    boolean clickable();

    void onItemClick(View v, int mViewState);
}
