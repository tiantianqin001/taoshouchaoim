package com.pingmo.chengyan.activity.persion;

import android.os.Bundle;

import com.flyco.tablayout.SlidingTabLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.manager.BaseActivity;
import com.pingmo.chengyan.adapter.OrderPageAdapter;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class OrderActivity extends BaseActivity {

    private SlidingTabLayout mTabLayout;
    private ViewPager mViewpager;
    private OrderPageAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_layout);
        initView();
        initData();
        addActivity(this);
    }

    private void initView() {
        getTitleBar().setTitle("订单信息");
        getTitleBar().enableBack();
        ImmersionBar.setTitleBar(this, getTitleBar().getTitle());
        mTabLayout = findViewById(R.id.tabLayout);
        mViewpager = findViewById(R.id.viewPager);


    }

    private void initData() {
        mAdapter = new OrderPageAdapter(getSupportFragmentManager());
        mViewpager.setAdapter(mAdapter);
        mTabLayout.setViewPager(mViewpager);

//        //获取订单数据
//        String url = Common.ORIDER_LIST;
//        Map<String, String> mapParams = new LinkedHashMap<>();
//        String token = SharedPreferenceUtil.getInstance(this).getString("token");
//        mapParams.put("token", token);
//
//        OkHttp3_0Utils.getInstance().asyncGetOkHttpHadHeader(url, mapParams, new Callback() {
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                Message message = Message.obtain();
//                message.what = Common.Interface_err;
//                message.obj = "当前服务获取失败";
//                mHandler.sendMessage(message);
//            }
//
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                String content = response.body().string();
//                QZXTools.logE("onResponse: " + content, null);
//
//
//            }
//        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActivity(this);
    }
}
