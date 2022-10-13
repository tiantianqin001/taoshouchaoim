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

public class RechargeActivity extends BaseActivity implements View.OnClickListener {

    public static final int RECHARGE_REQUEST_CODE = 0x2222;

    private EditText mCash;
    private TextView mCash1, mCash2, mCash3, mCash4, mCash5, mCash6;

    private TextView mCurCash;

    private TextView mBank;

    private BankItemBean mSelectedBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_layout);
        initView();
        addActivity(this);
    }

    private void initView() {
        getTitleBar().setTitle("充值");
        getTitleBar().enableBack();
        ImmersionBar.setTitleBar(this, getTitleBar().getTitle());
        mCash = findViewById(R.id.cash);
        mCash1 = findViewById(R.id.cash1);
        mCash2 = findViewById(R.id.cash2);
        mCash3 = findViewById(R.id.cash3);
        mCash4 = findViewById(R.id.cash4);
        mCash5 = findViewById(R.id.cash5);
        mCash6 = findViewById(R.id.cash6);
        mBank = findViewById(R.id.bank);
        mCash1.setOnClickListener(this);
        mCash2.setOnClickListener(this);
        mCash3.setOnClickListener(this);
        mCash4.setOnClickListener(this);
        mCash5.setOnClickListener(this);
        mCash6.setOnClickListener(this);
        findViewById(R.id.cash_list).setOnClickListener(this);
        findViewById(R.id.tip).setOnClickListener(this);
        findViewById(R.id.submit).setOnClickListener(this);
    }

    private void clickCash(TextView cashTv, String cash) {
        if (mCurCash != cashTv) {
            if (mCurCash != null) {
                mCurCash.setTextColor(getResources().getColor(R.color.regist_erification_code));
                mCurCash.setBackgroundResource(R.drawable.recharge_unselected_item_bg);
            }
            mCurCash = cashTv;
            mCurCash.setTextColor(getResources().getColor(R.color.white));
            mCurCash.setBackgroundResource(R.drawable.recharge_selected_item_bg);
            mCash.setText(cash);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cash1:
                clickCash(mCash1, "10");
                break;
            case R.id.cash2:
                clickCash(mCash2, "50");
                break;
            case R.id.cash3:
                clickCash(mCash3, "100");
                break;
            case R.id.cash4:
                clickCash(mCash4, "200");
                break;
            case R.id.cash5:
                clickCash(mCash5, "500");
                break;
            case R.id.cash6:
                clickCash(mCash6, "1000");
                break;
            case R.id.tip:
                String url = Common.SEALTALK_SERVER + "app/agreement/recharge_explain";
                Intent intentTip = new Intent(this, CommonWebviewActivity.class);
                intentTip.putExtra(CommonWebviewActivity.PAGE_TITLE, "充值说明");
                intentTip.putExtra(CommonWebviewActivity.PAGE_URL, url);
                startActivity(intentTip);
                break;
            case R.id.cash_list:
                Intent intentSelected = new Intent(this, BankCardNewActivity.class);
                intentSelected.putExtra(BankCardNewActivity.IS_SELECTED_TYPE, true);
                startActivityForResult(intentSelected, RECHARGE_REQUEST_CODE);
                break;
            case R.id.submit:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RECHARGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
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
