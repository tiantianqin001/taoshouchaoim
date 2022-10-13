package com.pingmo.chengyan.activity.shop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.hjq.toast.ToastUtils;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.shop.common.CommonDialog;
import com.pingmo.chengyan.activity.shop.common.Constant;
import com.pingmo.chengyan.activity.shop.common.Status;
import com.pingmo.chengyan.activity.shop.model.entity.AddressEntity;
import com.pingmo.chengyan.activity.shop.model.entity.ShopEntity;
import com.pingmo.chengyan.activity.shop.utils.GlideUtils;
import com.pingmo.chengyan.activity.shop.utils.PayUtils;
import com.pingmo.chengyan.activity.shop.viewmodel.WeChatMallViewModel;

import java.math.BigDecimal;
import java.math.RoundingMode;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.lifecycle.ViewModelProviders;

import static com.pingmo.chengyan.activity.shop.common.SealTalkUrlCode.WRONG_PASSWORD_MAX;


/**
 * 订单确认
 */
public class OrderConfirmationActivity extends TitleWhiteBaseActivity implements View.OnClickListener {
    private WeChatMallViewModel mViewModel;
   // private UserDetailViewModel mUserDetailViewModel;
    private ShopEntity mShopEntity;
    private ImageView mIvOrderLog;
    private TextView mTvTitleOlder, mTvMoneyOrder, mTvNum, mTvMoney, tv_money_integral, mTvAddAddress;
    private TextView mTvNameAddress, mTvPhoneAddress, mTvAddress;
    private Group mGroupAddress;
    private String mNum;
    private AddressEntity mAddressEntity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);
    }

    @Override
    protected void initView() {
        setTitle("订单确认");
        mShopEntity = getIntent().getParcelableExtra(Constant.PUBLIC_PARAMETER);
        mNum = getIntent().getStringExtra("num");
        findViewById(R.id.view_1).setOnClickListener(this);
        mIvOrderLog = findViewById(R.id.iv_order_log);
        mTvTitleOlder = findViewById(R.id.tv_title_older);
        mTvMoneyOrder = findViewById(R.id.tv_money_order);
        mTvNum = findViewById(R.id.tv_num);
        mTvMoney = findViewById(R.id.tv_money);
        tv_money_integral = findViewById(R.id.tv_money_integral);
        mTvNameAddress = findViewById(R.id.tv_name_address);
        mTvPhoneAddress = findViewById(R.id.tv_phone_address);
        mTvAddress = findViewById(R.id.tv_address);
        mGroupAddress = findViewById(R.id.group_address);
        mTvAddAddress = findViewById(R.id.tv_add_address);
        findViewById(R.id.btn_settle_accounts).setOnClickListener(this);
        mTvAddAddress.setOnClickListener(this);
        if (mShopEntity == null) {
            ToastUtils.show("商品参数有误");
            return;
        }
        GlideUtils.getLoad(mShopEntity.coverImg, mIvOrderLog).into(mIvOrderLog);
        mTvTitleOlder.setText(mShopEntity.title);
        mTvMoneyOrder.setText(mShopEntity.price);
        mTvNum.setText(mNum);
        mTvMoney.setText(strSum(mNum, mShopEntity.price));
//        tv_money_integral.setText(String.format("+%s积分", strSum(mNum, mShopEntity.integral.toString())));
    }

    @Override
    protected void initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(WeChatMallViewModel.class);
       // mUserDetailViewModel = ViewModelProviders.of(this, new UserDetailViewModel.Factory(getApplication(), IMManager.getInstance().getCurrentId())).get(UserDetailViewModel.class);
        mViewModel.getBuyGoods().observe(this, objectResource -> {
            if (objectResource != null) {
                if (objectResource.status == Status.SUCCESS) {
                    ToastUtils.show("购买成功");
//                    finish();
                    Intent intent = new Intent(OrderConfirmationActivity.this, WeChatMallActivity.class);
                    OrderConfirmationActivity.this.startActivity(intent);
                } else if (objectResource.status == Status.ERROR) {
                    if (WRONG_PASSWORD_MAX == objectResource.code) {
                        //您已多次输错密码，是否忘记密码？
                        PayUtils.showChangePassDialog(getSupportFragmentManager(), new CommonDialog.OnDialogButtonClickListener() {
                            @Override
                            public void onPositiveClick(View v, Bundle bundle) {
//                                Intent intentResetPay = new Intent(OrderConfirmationActivity.this, ResetPayPasswordActivity.class);
//                                startActivity(intentResetPay);
                            }

                            @Override
                            public void onNegativeClick(View v, Bundle bundle) {

                            }
                        });




                    } else {
                        ToastUtils.show("购买失败：" + objectResource.message);
                    }
                }
            }
        });
        mViewModel.getAddressList().observe(this, listResource -> {
            if (listResource != null) {
                if (listResource.status == Status.SUCCESS) {
                    if (listResource.data != null && listResource.data.size() > 0) {
                        mGroupAddress.setVisibility(View.VISIBLE);
                        mTvAddAddress.setVisibility(View.GONE);
                        mAddressEntity = listResource.data.get(0);
                        mTvNameAddress.setText(mAddressEntity.addressee);
                        mTvPhoneAddress.setText(mAddressEntity.phone);
                        mTvAddress.setText(mAddressEntity.address);
                    } else {
                        mGroupAddress.setVisibility(View.INVISIBLE);
                        mTvAddAddress.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
//        mUserDetailViewModel.getUpdatePayPass().observe(this, resource -> {
//            if (resource != null) {
//                if (resource.status == Status.SUCCESS) {
//                    ToastUtils.showToast("设置成功");
//                    SpUtils.getInstance().encode(SpConstant.isPay, true);
//                } else if (resource.status == Status.ERROR) {
//                    ToastUtils.showToast("设置失败");
//                }
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewModel.setAddressList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_1:
                //地址
                Intent intent = new Intent(this, AddressListActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_settle_accounts:
//                if (mAddressEntity == null) {
//                    ToastUtils.show("请选择地址");
//                    return;
//                }
                PayUtils.payPassword(this,"订单确认",mTvMoney.getText().toString(), (password, isPay) -> {
                    if (isPay) {
                        mViewModel.setBuyGoods(mAddressEntity.address, mAddressEntity.addressee,
                                mNum, password, mAddressEntity.phone, mAddressEntity.region, mShopEntity.id);
                    } else {
                       // mUserDetailViewModel.setUpdatePayPass(password, null);
                    }
                });
                break;
            case R.id.tv_add_address:
                Intent intent2 = new Intent(this, AddressDetailsActivity.class);
                startActivity(intent2);
                break;
            default:
        }
    }

    private String strSum(String str, String str2) {
        try {
//            double v = Double.parseDouble(str);
//            double v1 = Double.parseDouble(str2);
//            DecimalFormat df = new DecimalFormat("######0.00");
//            return String.valueOf(df.format(v * v1));
            return new BigDecimal(str).multiply(new BigDecimal(str2)).setScale(2, RoundingMode.HALF_UP).toString();
        } catch (Exception e) {
            return "0";
        }
    }
}
