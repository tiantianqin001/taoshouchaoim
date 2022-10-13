package com.pingmo.chengyan.activity.persion;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gyf.immersionbar.ImmersionBar;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.manager.BaseActivity;
import com.pingmo.chengyan.bean.BankItemBean;
import com.pingmo.chengyan.net.Common;

import androidx.annotation.Nullable;

public class CashOutActivity extends BaseActivity implements View.OnClickListener {

    public static final int CASH_OUT_REQUEST_CODE = 0x1111;

    private EditText mCashOut;
    private TextView mCash;
    private TextView mBank;

    private BankItemBean mSelectedBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashout_layout);
        initView();
        addActivity(this);
    }

    private void initView() {
        getTitleBar().setTitle("提现");
        getTitleBar().enableBack();
        ImmersionBar.setTitleBar(this, getTitleBar().getTitle());
        mCashOut = findViewById(R.id.cash);
        mCash = findViewById(R.id.yue);
        mBank = findViewById(R.id.bank);
        findViewById(R.id.tip).setOnClickListener(this);
        findViewById(R.id.cash_list).setOnClickListener(this);
        findViewById(R.id.submit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tip:
                String url = Common.SEALTALK_SERVER + "app/agreement/withdraw_explain";
                Intent intentTip = new Intent(this, CommonWebviewActivity.class);
                intentTip.putExtra(CommonWebviewActivity.PAGE_TITLE, "提现说明");
                intentTip.putExtra(CommonWebviewActivity.PAGE_URL, url);
                startActivity(intentTip);
                break;
            case R.id.cash_list:
                Intent intentSelected = new Intent(this, BankCardNewActivity.class);
                intentSelected.putExtra(BankCardNewActivity.IS_SELECTED_TYPE, true);
                startActivityForResult(intentSelected, CASH_OUT_REQUEST_CODE);
                break;
            case R.id.submit:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CASH_OUT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            mSelectedBean = (BankItemBean) data.getSerializableExtra(BankCardNewActivity.SELECTED_BEAN);
            if (mSelectedBean != null) {
                mBank.setText(mSelectedBean.getBackName());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActivity(this);
    }
}
