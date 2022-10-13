package com.pingmo.chengyan.activity.shop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;


import com.hjq.toast.ToastUtils;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.shop.common.Constant;
import com.pingmo.chengyan.activity.shop.common.Status;
import com.pingmo.chengyan.activity.shop.model.entity.ShopEntity;
import com.pingmo.chengyan.activity.shop.utils.GlideBannerStringImageLoader;
import com.pingmo.chengyan.activity.shop.view.AmountView;
import com.pingmo.chengyan.activity.shop.viewmodel.WeChatMallViewModel;
import com.pingmo.chengyan.net.Common;
import com.youth.banner.Banner;

import java.util.Arrays;

/**
 * 商品详情
 */
public class ProductDetailsActivity extends TitleWhiteBaseActivity implements View.OnClickListener {
    private AmountView mAmountView;
    private WebView mWebView;
    private TextView mTvMoney, mTvMoneyOld, mTvProductDesc, mTvSold, mTvStock, mTvBuy, tv_money_integral;
    private Banner mBanner;
    //    private NestedScrollView mNestedScrollView;
    private WeChatMallViewModel mViewModel;
    private ShopEntity mShopEntity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
    }

    @Override
    protected void initView() {
        setTitle("详情");
        mBanner = findViewById(R.id.banner_commodity);
        mAmountView = findViewById(R.id.amount_view);
        mWebView = findViewById(R.id.webview_product_details);
        mTvMoney = findViewById(R.id.tv_money);
        tv_money_integral = findViewById(R.id.tv_money_integral);
        mTvMoneyOld = findViewById(R.id.tv_money_old);
        mTvProductDesc = findViewById(R.id.tv_product_desc);
        mTvSold = findViewById(R.id.tv_sold);
        mTvStock = findViewById(R.id.tv_stock);
        mTvBuy = findViewById(R.id.tv_buy);
//        mNestedScrollView = findViewById(R.id.nestedScrollView);
        mTvBuy.setOnClickListener(this);
        mBanner.setImageLoader(new GlideBannerStringImageLoader());
        mBanner.setDelayTime(4500);
        mAmountView.setOnAmountChangeListener((view, amount) -> {
            if (amount > 0) {
                mTvBuy.setEnabled(true);
            } else {
                mTvBuy.setEnabled(false);
            }
        });
    }

    @Override
    protected void initViewModel() {
        String id = getIntent().getStringExtra(Constant.PUBLIC_PARAMETER);
        mViewModel = ViewModelProviders.of(this).get(WeChatMallViewModel.class);
        mViewModel.getShopDetail().observe(this, resource -> {
            if (resource != null) {
                if (resource.status == Status.SUCCESS) {
                    mShopEntity = resource.data;
                    if (mShopEntity == null) {
                        finish();
                        return;
                    }
                    String[] split = mShopEntity.bannerImgs.split(",");
                    mBanner.setImages(Arrays.asList(split));
                    mBanner.start();
                    mTvProductDesc.setText(mShopEntity.title);
                    mTvMoney.setText(mShopEntity.price);
                   // tv_money_integral.setText(String.format("+%s积分", mShopEntity.integral.toBigInteger()));
                    mTvMoneyOld.setText(String.format("%s%s", Constant.money, mShopEntity.basePrice));
                    mTvSold.setText(String.format("已售%s件", mShopEntity.soldNumber));
                    mTvStock.setText(String.format("库存%s件", mShopEntity.number));
                    mAmountView.setGoods_storage(string2int(mShopEntity.number));
                    mWebView.getSettings().setLoadWithOverviewMode(true);
                    mWebView.getSettings().setDomStorageEnabled(true);
//                    mWebView.getSettings().setJavaScriptEnabled(true);
//                    mWebView.setWebViewClient(mClient);

                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("<HTML><HEAD><LINK href=\"quill.snow.css\" type=\"text/css\" rel=\"stylesheet\"/></HEAD><body><div class=\"ql-editor\">");
                    stringBuilder.append(mShopEntity.shopDetails);
                    stringBuilder.append("</div></body></HTML>");
//                    http://api.hefeixufei.com/css/quill.snow.css
                    mWebView.loadDataWithBaseURL(Common.BaseUrl + "/css/", stringBuilder.toString(), "text/html", "UTF-8", null);

//                    mWebView.loadData(mShopEntity.shopDetails, "text/html", "UTF-8");
                } else if (resource.status == Status.ERROR) {
                    ToastUtils.show("商品已下架");
                    finish();
                }
            }
        });
        mViewModel.setShopDetail(id);
    }

    private int string2int(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_buy:
               Intent intent = new Intent(this, OrderConfirmationActivity.class);
                intent.putExtra(Constant.PUBLIC_PARAMETER, mShopEntity);
                intent.putExtra("num", mAmountView.getText());
                startActivity(intent);
                break;
            default:
        }
    }

//    private WebViewClient mClient = new WebViewClient() {
//        @Override
//        public void onPageFinished(WebView view, String url) {
//            resetHeight();
//        }
//    };

//    @SuppressLint("JavascriptInterface")
//    @JavascriptInterface
//    private void resetHeight() {
//        //重新调整webview高度
//        mWebView.post(() -> {
//            mWebView.measure(0, 0);
//            int measuredHeight = mWebView.getMeasuredHeight();
//        });
//    }

}
