package com.pingmo.chengyan.activity.shop.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Outline;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.BaseAdapter;

import com.alibaba.fastjson.JSON;
import com.gyf.immersionbar.ImmersionBar;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.shop.adapter.TypeImageAdapter;
import com.pingmo.chengyan.activity.shop.adapter.WeChatMallNewAdapter;
import com.pingmo.chengyan.activity.shop.bean.IconBean;
import com.pingmo.chengyan.activity.shop.bean.ShopTypeBean;
import com.pingmo.chengyan.activity.shop.common.Status;
import com.pingmo.chengyan.activity.shop.model.entity.BannerEntity;
import com.pingmo.chengyan.activity.shop.model.entity.ShopEntity;
import com.pingmo.chengyan.activity.shop.utils.GlideBannerEntityImageLoader;
import com.pingmo.chengyan.activity.shop.viewmodel.WeChatMallViewModel;
import com.pingmo.chengyan.net.Common;
import com.pingmo.chengyan.utils.OkHttp3_0Utils;
import com.pingmo.chengyan.utils.QZXTools;
import com.pingmo.chengyan.utils.SharedPreferenceUtil;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 微呗商城 ProductDetails
 */
public class WeChatMallNewActivity extends TitleC47DEDBaseActivity {
    private static final String TAG = "";
    private XRecyclerView mRecyclerView;
    private Banner mBanner;

    private List<ShopEntity> mList = new ArrayList<>();
    private List<BannerEntity> mBannerEntities = new ArrayList<>();
    private WeChatMallViewModel mViewModel;
    private RecyclerView grid_photo;
    private BaseAdapter mAdapter = null;
    private ArrayList<IconBean> mData = null;

    private Handler handler = new Handler();
    private WeChatMallNewAdapter weChatMallNewAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mList.clear();
        //设置沉静状态栏
        ImmersionBar.with(this)
                .transparentBar()
                .statusBarDarkFont(true)
                .init();

        setContentView(R.layout.activity_we_chat_mall_new);
    }
    private  int page = 1;
    @Override
    protected void initView() {
        setTitle("城言商城");
        mRecyclerView = findViewById(R.id.recyclerview_mall);

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        weChatMallNewAdapter = new WeChatMallNewAdapter(this,mList);
        mRecyclerView.setAdapter(weChatMallNewAdapter);

        mBanner = findViewById(R.id.banner);
        mBanner.setImageLoader(new GlideBannerEntityImageLoader());
        mBanner.setDelayTime(4500);
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        mBanner.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 30);
            }
        });
        mBanner.setClipToOutline(true);
        mBanner.setOnBannerListener(position -> {

        });
        grid_photo = ( findViewById(R.id.grid_type));




        //获取商品的类别
        String url = Common.SHOP_TYPE;
        Map<String , String> mapHeaders = new HashMap<>();
        String token = SharedPreferenceUtil.getInstance(this).getString("token");
        mapHeaders.put("token",token);
        OkHttp3_0Utils.getInstance().asyncGetOkHttpHadHeader(url, mapHeaders, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Message message = Message.obtain();
                message.what = Common.Interface_err;
                message.obj = "当前服务请求失败";
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String content = response.body().string();
                QZXTools.logE("onResponse: " + content, null);

                JSONObject  jsonObject = null;
                try {
                    jsonObject = new JSONObject(content);
                    int code = jsonObject.optInt("code");
                    String msg = jsonObject.optString("msg");
                    if (code == 200){
                        ShopTypeBean shopTypeBean = JSON.parseObject(content, ShopTypeBean.class);
                        List<ShopTypeBean.DataDTO> data = shopTypeBean.data;
                        if (data!=null){

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    TypeImageAdapter typeImageAdapter = new TypeImageAdapter(WeChatMallNewActivity.this,data);

                                    GridLayoutManager gridLayoutManager =    new GridLayoutManager(WeChatMallNewActivity.this,4) {
                                        @Override
                                        public boolean canScrollVertically() {
                                            return false;
                                        }
                                    };
                                    grid_photo.setLayoutManager(gridLayoutManager);
                                    grid_photo.setAdapter(typeImageAdapter);
//                                    mAdapter = new MyAdapter<ShopTypeBean.DataDTO>(data, R.layout.item_grid_icon) {
//                                        @Override
//                                        public void bindView(ViewHolder holder, IconBean obj) {
//                                            holder.setImageResource(R.id.img_icon, obj.getiId());
//                                            holder.setText(R.id.txt_icon, obj.getiName());
//                                        }
//                                    };
//
//                                    grid_photo.setAdapter(mAdapter);
//
//                                    grid_photo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                                        @Override
//                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                            Intent intent = new Intent(WeChatMallActivity.this, MallTypeActivity.class);
//                                            intent.putExtra(Constant.TYPE, position);
//                                            startActivity(intent);
//                                        }
//                                    });

                                }
                            });

                        }
                    }else if (code == 403){
                        Intent intent = new Intent();
                        intent.setAction("cn.twle.android.sendbroadcast.Setup.Token");
                        intent.setComponent(new ComponentName("com.pingmo.chengyan",
                                "com.pingmo.chengyan.receiver.TokenFileBroadcastReceiver"));
                        sendBroadcast(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        mRecyclerView.setPullRefreshEnabled(false);
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
//                page = 1;
//                mList.clear();
//                weChatMallNewAdapter.notifyDataSetChanged();
//                refresh(page);
            }

            @Override
            public void onLoadMore() {
                //加载更多
                page++;
                refresh(page);

            }
        });
    }

    @Override
    protected void initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(WeChatMallViewModel.class);
        mViewModel.getShopList().observe(this, listResource -> {
            if (listResource != null) {
                if (listResource.status == Status.SUCCESS) {
                    mList.addAll(listResource.data);
                    weChatMallNewAdapter.notifyDataSetChanged();
                    mRecyclerView.loadMoreComplete();
                    mRecyclerView.refreshComplete();


                }
            }
        });
        refresh(1);
        mViewModel.getBannerList().observe(this, listResource -> {
            if (listResource != null) {
                if (listResource.status == Status.SUCCESS) {
                    mBannerEntities.clear();
                    mBannerEntities.addAll(listResource.data);
                    mBanner.setImages(mBannerEntities);
                    mBanner.start();
                }
            }
        });
        mViewModel.setBannerList();
    }

    public void refresh(int pageNum) {
        mViewModel.setShopList( pageNum);
    }
}
