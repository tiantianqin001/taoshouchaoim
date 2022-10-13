package com.pingmo.chengyan.activity.shop.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.hjq.toast.ToastUtils;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.shop.adapter.WeChatMallAdapter;
import com.pingmo.chengyan.activity.shop.common.Constant;
import com.pingmo.chengyan.activity.shop.common.Status;
import com.pingmo.chengyan.activity.shop.model.entity.ShopEntity;
import com.pingmo.chengyan.activity.shop.viewmodel.WeChatMallViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 微呗商城 ProductDetails
 */
public class MallTypeActivity extends TitleC47DEDBaseActivity {
    private RecyclerView mRecyclerView;
    private WeChatMallAdapter mWeChatMallAdapter;
    private List<ShopEntity> mList = new ArrayList<>();
    private WeChatMallViewModel mViewModel;
    private int type;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mall_type);
    }

    @Override
    protected void initView() {
        type = getIntent().getIntExtra(Constant.TYPE,0);
        setTitle("商品分类");
        mRecyclerView = findViewById(R.id.recyclerview_mall);
//        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setAdapter(mWeChatMallAdapter = new WeChatMallAdapter(R.layout.item_we_chat_mall, mList));
        mWeChatMallAdapter.addChildClickViewIds(R.id.cl_item);
        mWeChatMallAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            Intent intent = new Intent(MallTypeActivity.this, ProductDetailsActivity.class);
            ShopEntity shopEntity = mList.get(position);
            if (shopEntity != null) {
                intent.putExtra(Constant.PUBLIC_PARAMETER, shopEntity.id);
                startActivity(intent);
            } else {
                ToastUtils.show("商品已下架");
            }
        });

    }

    @Override
    protected void initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(WeChatMallViewModel.class);
        mViewModel.getShopListType().observe(this, listResource -> {
            if (listResource != null) {
                if (listResource.status == Status.SUCCESS) {
                    mList.clear();
                    mList.addAll(listResource.data);
                    mWeChatMallAdapter.notifyDataSetChanged();
                }
            }
        });
        refresh();

    }

    public void refresh() {
        mViewModel.setShopListType(type);
    }
}
