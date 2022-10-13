package com.pingmo.chengyan.activity.shop.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.hjq.toast.ToastUtils;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.shop.adapter.AddressRVAdapter;
import com.pingmo.chengyan.activity.shop.common.CommonDialog;
import com.pingmo.chengyan.activity.shop.common.Constant;
import com.pingmo.chengyan.activity.shop.common.Resource;
import com.pingmo.chengyan.activity.shop.common.Status;
import com.pingmo.chengyan.activity.shop.model.entity.AddressEntity;
import com.pingmo.chengyan.activity.shop.viewmodel.WeChatMallViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 地址列表 选择 新增
 */
public class AddressListActivity extends TitleWhiteBaseActivity implements View.OnClickListener {
    private SmartRefreshLayout mSmartRefreshLayout;
    private RecyclerView mRecyclerView;
    private AddressRVAdapter mAddressRVAdapter;
    private List<AddressEntity> mList = new ArrayList<>();
    private WeChatMallViewModel mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_smart_recyclerview);
    }

    @Override
    protected void initView() {
        setTitle("地址选择");
        setTitleRight("新增");
        getTvTitleRight().setOnClickListener(this);
        mSmartRefreshLayout = findViewById(R.id.smartRefreshLayout);
        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAddressRVAdapter = new AddressRVAdapter(mList));
        mAddressRVAdapter.addChildClickViewIds(R.id.tv_edit, R.id.tv_del, R.id.cb_default);
        mAddressRVAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                AddressEntity addressEntity = mList.get(position);
                if (addressEntity == null) {
                    ToastUtils.show("数据有误");
                    return;
                }
                switch (view.getId()) {
                    case R.id.tv_edit:
                        Intent intent = new Intent(AddressListActivity.this, AddressDetailsActivity.class);
                        intent.putExtra(Constant.PUBLIC_PARAMETER, addressEntity);
                        startActivity(intent);
                        break;
                    case R.id.tv_del:
                        showDelDialog(mList.get(position).id);
                        break;
                    case R.id.cb_default:
                        addressEntity.setIsDefault("1");
                        mViewModel.setUpdateAddress(addressEntity);
                        break;
                }
            }
        });
    }
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onResume() {
        super.onPause();
        initViewModel();
    }

    @Override
    protected void initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(WeChatMallViewModel.class);
        mViewModel.getAddressList().observe(this, new Observer<Resource<List<AddressEntity>>>() {
            @Override
            public void onChanged(Resource<List<AddressEntity>> listResource) {
                if (listResource.status == Status.SUCCESS) {
                    mList.clear();
                    mList.addAll(listResource.data);
                    mAddressRVAdapter.notifyDataSetChanged();
                }
            }
        });
        mViewModel.setAddressList();
        mViewModel.getDelAddress().observe(this, new Observer<Resource<Object>>() {
            @Override
            public void onChanged(Resource<Object> objectResource) {
                if (objectResource.status == Status.SUCCESS) {
                    mViewModel.setAddressList();
                }
            }
        });
        mViewModel.getUpdateAddress().observe(this, new Observer<Resource<Object>>() {
            @Override
            public void onChanged(Resource<Object> objectResource) {
                if (objectResource.status == Status.SUCCESS) {
                    mViewModel.setAddressList();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_title_right:
                Intent intent = new Intent(this, AddressDetailsActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void showDelDialog(String id) {
        CommonDialog.Builder builder = new CommonDialog.Builder();
        builder.setContentMessage("是否删除？");
        builder.setDialogButtonClickListener(new CommonDialog.OnDialogButtonClickListener() {
            @Override
            public void onPositiveClick(View v, Bundle bundle) {
                mViewModel.setDelAddress(id);
            }

            @Override
            public void onNegativeClick(View v, Bundle bundle) {

            }
        });
        CommonDialog dialog = builder.build();
        dialog.show(getSupportFragmentManager(), "del_address");
    }
}
