package com.pingmo.chengyan.activity.persion;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.gyf.immersionbar.ImmersionBar;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.manager.BaseActivity;

import androidx.annotation.Nullable;

public class CashOutTipActivity extends BaseActivity implements View.OnClickListener {

    private TextView mHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper_layout);
        initView();
        addActivity(this);
    }

    private void initView() {
        getTitleBar().setTitle("提现说明");
        getTitleBar().enableBack();
        ImmersionBar.setTitleBar(this, getTitleBar().getTitle());
        mHelper = findViewById(R.id.content);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActivity(this);
    }
}
