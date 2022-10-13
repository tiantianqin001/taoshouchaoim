package com.pingmo.chengyan.activity.persion;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gyf.immersionbar.ImmersionBar;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.manager.BaseActivity;

import androidx.annotation.Nullable;

public class BankAddActivity extends BaseActivity implements View.OnClickListener {

    private EditText name, id;
    private TextView btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_add_layout);
        initView();
        addActivity(this);
    }

    private void initView() {
        getTitleBar().setTitle("添加银行卡");
        getTitleBar().enableBack();
        ImmersionBar.setTitleBar(this, getTitleBar().getTitle());
        name = findViewById(R.id.name);
        id = findViewById(R.id.id);
        findViewById(R.id.submit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActivity(this);
    }
}
