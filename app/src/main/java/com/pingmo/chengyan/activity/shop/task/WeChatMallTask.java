package com.pingmo.chengyan.activity.shop.task;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;


import com.pingmo.chengyan.activity.shop.common.Resource;
import com.pingmo.chengyan.activity.shop.model.entity.AddressEntity;
import com.pingmo.chengyan.activity.shop.model.entity.BannerEntity;
import com.pingmo.chengyan.activity.shop.model.entity.ListResult;
import com.pingmo.chengyan.activity.shop.model.entity.OrderEntity;
import com.pingmo.chengyan.activity.shop.model.entity.ShopEntity;
import com.pingmo.chengyan.activity.shop.net.HttpClientManager;
import com.pingmo.chengyan.activity.shop.net.NetworkOnlyResource;
import com.pingmo.chengyan.activity.shop.service.WeChatMallService;
import com.pingmo.chengyan.activity.shop.utils.RetrofitUtil;
import com.pingmo.chengyan.activity.shop.viewmodel.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WeChatMallTask {
    private WeChatMallService mWeChatMallService;
    private Context mContext;

    public WeChatMallTask(Context context) {
        mContext = context;
        mWeChatMallService = HttpClientManager.getInstance(context).getClient().createService(WeChatMallService.class);
    }

    /**
     * 商品列表
     *
     * @return
     */
    public LiveData<Resource<List<ShopEntity>>> getShopList(int pageNum) {
        return new NetworkOnlyResource<List<ShopEntity>, Result<ListResult<ShopEntity>>>() {
            @NonNull
            @Override
            protected LiveData<Result<ListResult<ShopEntity>>> createCall() {
                return mWeChatMallService.getShopList(pageNum,30);
            }

            @Override
            protected List<ShopEntity> transformRequestType(Result<ListResult<ShopEntity>> response) {
                List<ShopEntity> rows = response.getResult().rows;
                return rows;
            }
        }.asLiveData();
    }

    /**
     * 商品列表分类
     *
     * @return
     */
    public LiveData<Resource<List<ShopEntity>>> getShopListType(int shopClassify) {
        return new NetworkOnlyResource<List<ShopEntity>, Result<ListResult<ShopEntity>>>() {
            @NonNull
            @Override
            protected LiveData<Result<ListResult<ShopEntity>>> createCall() {
                return mWeChatMallService.getShopListType(shopClassify);
            }

            @Override
            protected List<ShopEntity> transformRequestType(Result<ListResult<ShopEntity>> response) {
                Log.i("", "transformRequestType: "+response);
                List<ShopEntity> rows = response.getResult().rows;
                return rows;
            }
        }.asLiveData();
    }


    /**
     * 商品轮播图
     *
     * @return
     */
    public LiveData<Resource<List<BannerEntity>>> getBannerList() {
        return new NetworkOnlyResource<List<BannerEntity>, Result<List<BannerEntity>>>() {
            @NonNull
            @Override
            protected LiveData<Result<List<BannerEntity>>> createCall() {
                return mWeChatMallService.getBannerList();
            }
        }.asLiveData();
    }

    /**
     * 商品详情
     *
     * @param id
     * @return
     */
    public LiveData<Resource<ShopEntity>> getShopEntity(String id) {
        return new NetworkOnlyResource<ShopEntity, Result<ShopEntity>>() {
            @NonNull
            @Override
            protected LiveData<Result<ShopEntity>> createCall() {
                return mWeChatMallService.getShopDetail(id);
            }
        }.asLiveData();
    }

    /**
     * 购买商品
     */
    public LiveData<Resource<Object>> getBuyGoods(String address, String addressee, String number, String password, String phone, String region, String shopId) {
        return new NetworkOnlyResource<Object, Result<Object>>() {

            @NonNull
            @Override
            protected LiveData<Result<Object>> createCall() {
                HashMap<String, Object> map = new HashMap<>();
                map.put("address", address);
                map.put("addressee", addressee);
                map.put("number", number);
                map.put("password", password);
                map.put("phone", phone);
                map.put("region", region);
                map.put("shopId", shopId);
                return mWeChatMallService.getBuyGoods(RetrofitUtil.createJsonRequest(map));
            }
        }.asLiveData();
    }

    /**
     * 添加地址
     */
    public LiveData<Resource<Object>> addAddress( String address, String addressee, String isDefault, String phone, String region,String userId) {
        return new NetworkOnlyResource<Object, Result<Object>>() {

            @NonNull
            @Override
            protected LiveData<Result<Object>> createCall() {
                HashMap<String, Object> map = new HashMap<>();
                map.put("address", address);
                map.put("addressee", addressee);
                map.put("isDefault", isDefault);
                map.put("phone", phone);
                map.put("region", region);
                map.put("userId", userId);
                return mWeChatMallService.addAddress(RetrofitUtil.createJsonRequest(map));
            }
        }.asLiveData();
    }

    /**
     * 更新地址
     */
    public LiveData<Resource<Object>> updateAddress(AddressEntity addressEntity) {
        return new NetworkOnlyResource<Object, Result<Object>>() {
            @NonNull
            @Override
            protected LiveData<Result<Object>> createCall() {
                HashMap<String, Object> map = new HashMap<>();
                map.put("id", addressEntity.id);
                map.put("address", addressEntity.address);
                map.put("addressee", addressEntity.addressee);
                map.put("isDefault", addressEntity.getIsDefault());
                map.put("phone", addressEntity.phone);
                map.put("region", addressEntity.region);
                map.put("userId", addressEntity.userId);
                return mWeChatMallService.updateAddress(RetrofitUtil.createJsonRequest(map));
            }
        }.asLiveData();
    }

    /**
     * 删除地址
     */
    public LiveData<Resource<Object>> delAddress(String id) {
        return new NetworkOnlyResource<Object, Result<Object>>() {
            @NonNull
            @Override
            protected LiveData<Result<Object>> createCall() {
                HashMap<String, Object> map = new HashMap<>();
//                map.put("id", id);
//                return mWeChatMallService.delAddress(RetrofitUtil.createJsonRequest(map));
                return mWeChatMallService.delAddress(id);
            }
        }.asLiveData();
    }



    /**
     * 获取地址列表
     */
    public LiveData<Resource<List<AddressEntity>>> addressList() {
        return new NetworkOnlyResource<List<AddressEntity>, Result<List<AddressEntity>>>() {

            @NonNull
            @Override
            protected LiveData<Result<List<AddressEntity>>> createCall() {
                return mWeChatMallService.addressList();
            }
//
//            @Override
//            protected List<AddressEntity> transformRequestType(Result<ListResult<AddressEntity>> response) {
//                return super.transformRequestType(response);
//            }
        }.asLiveData();
    }

    /**
     * 订单列表
     */
    public LiveData<Resource<List<OrderEntity>>> orderList(int pageNum, int pageSize) {
        return new NetworkOnlyResource<List<OrderEntity>, Result<ListResult<OrderEntity>>>() {
            @NonNull
            @Override
            protected LiveData<Result<ListResult<OrderEntity>>> createCall() {
                return mWeChatMallService.shopOrder(pageNum, pageSize);
            }

            @Override
            protected List<OrderEntity> transformRequestType(Result<ListResult<OrderEntity>> response) {
                if (response != null && response.data != null) {
                    return response.data.rows;
                } else {
                    return new ArrayList<>();
                }
            }
        }.asLiveData();
    }


}
