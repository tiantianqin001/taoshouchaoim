package com.tencent.qcloud.tuikit.tuicontact.ui.pages;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.gyf.immersionbar.ImmersionBar;
import com.tencent.qcloud.tuicore.component.TitleBarLayout;
import com.tencent.qcloud.tuicore.component.activities.BaseLightActivity;
import com.tencent.qcloud.tuicore.component.interfaces.ITitleBarLayout;
import com.tencent.qcloud.tuicore.component.interfaces.IUIKitCallback;
import com.tencent.qcloud.tuikit.tuicontact.bean.FriendApplicationBean;
import com.tencent.qcloud.tuikit.tuicontact.presenter.NewFriendPresenter;
import com.tencent.qcloud.tuikit.tuicontact.ui.interfaces.INewFriendActivity;
import com.tencent.qcloud.tuikit.tuicontact.ui.view.NewFriendListAdapter;
import com.tencent.qcloud.tuikit.tuicontact.R;
import com.tencent.qcloud.tuikit.tuicontact.util.TUIContactLog;

import java.util.List;

public class NewFriendActivity extends BaseLightActivity implements INewFriendActivity {

    private static final String TAG = NewFriendActivity.class.getSimpleName();

    private TitleBarLayout mTitleBar;
    private ListView mNewFriendLv;
    private NewFriendListAdapter mAdapter;
    private TextView notFoundTip;

    private NewFriendPresenter presenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置沉静状态栏
        ImmersionBar.with(this)
                .transparentBar()
                .init();
        setContentView(R.layout.contact_new_friend_activity);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initPendency();
    }

    private void init() {
        mTitleBar = findViewById(R.id.new_friend_titlebar);
        mTitleBar.setBackgroundColor(getResources().getColor(R.color.white));
        mTitleBar.getLeftIcon().setBackground(getResources().getDrawable(R.drawable.back));
        mTitleBar.setTitle(getResources().getString(R.string.new_friend), ITitleBarLayout.Position.MIDDLE);
        mTitleBar.getMiddleTitle().setTextColor(getResources().getColor(R.color.split_lint_friend));
        mTitleBar.getMiddleTitle().setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mTitleBar.setOnLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitleBar.getRightIcon().setVisibility(View.GONE);
        presenter = new NewFriendPresenter();
        presenter.setFriendActivity(this);
        presenter.setFriendApplicationListAllRead(new IUIKitCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                Log.i(TAG, "onSuccess: "+data);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {

            }
        });
        mNewFriendLv = findViewById(R.id.new_friend_list);
        notFoundTip = findViewById(R.id.not_found_tip);
    }

    private void initPendency() {
        presenter.loadFriendApplicationList();
    }

    @Override
    public void onDataSourceChanged(List<FriendApplicationBean> dataSource) {
        TUIContactLog.i(TAG, "getFriendApplicationList success");
        if (dataSource == null || dataSource.isEmpty()) {
            notFoundTip.setVisibility(View.VISIBLE);
        } else {
            notFoundTip.setVisibility(View.GONE);
        }
        mNewFriendLv.setVisibility(View.VISIBLE);
        mAdapter = new NewFriendListAdapter(NewFriendActivity.this, R.layout.contact_new_friend_item, dataSource);
        mAdapter.setPresenter(presenter);
        mNewFriendLv.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void finish() {
        super.finish();
    }

}
