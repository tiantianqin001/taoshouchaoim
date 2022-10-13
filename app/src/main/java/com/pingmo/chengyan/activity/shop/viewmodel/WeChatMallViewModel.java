package com.pingmo.chengyan.activity.shop.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;


import com.pingmo.chengyan.activity.shop.common.Resource;
import com.pingmo.chengyan.activity.shop.model.entity.AddressEntity;
import com.pingmo.chengyan.activity.shop.model.entity.BannerEntity;
import com.pingmo.chengyan.activity.shop.model.entity.OrderEntity;
import com.pingmo.chengyan.activity.shop.model.entity.ShopEntity;
import com.pingmo.chengyan.activity.shop.task.WeChatMallTask;

import java.util.List;

public class WeChatMallViewModel extends AndroidViewModel {
    private WeChatMallTask mWeChatMallTask;

    /*
    商品列表
     */
    private SingleSourceLiveData<Resource<List<ShopEntity>>> shopList = new SingleSourceLiveData<>();
    private SingleSourceLiveData<Resource<List<ShopEntity>>> shopListType = new SingleSourceLiveData<>();
    private SingleSourceLiveData<Resource<List<BannerEntity>>> bannerList = new SingleSourceLiveData<>();
    /*
    商品详情
     */
    private SingleSourceLiveData<Resource<ShopEntity>> shopDetail = new SingleSourceLiveData<>();
    /*
    订单列表
     */
    private SingleSourceLiveData<Resource<List<OrderEntity>>> orderList = new SingleSourceLiveData<>();

    /*
    购买商品结果
     */
    private SingleSourceLiveData<Resource<Object>> buyGoods = new SingleSourceLiveData<>();

    /*
    添加地址结果
     */
    private SingleSourceLiveData<Resource<Object>> addAddress = new SingleSourceLiveData<>();
    /*
    删除地址结果
     */
    private SingleSourceLiveData<Resource<Object>> delAddress = new SingleSourceLiveData<>();

    /*
    更新地址结果
     */
    private SingleSourceLiveData<Resource<Object>> updateAddress = new SingleSourceLiveData<>();

    /*
    地址列表
     */
    private SingleSourceLiveData<Resource<List<AddressEntity>>> addressList = new SingleSourceLiveData<>();

    public WeChatMallViewModel(@NonNull Application application) {
        super(application);
        mWeChatMallTask = new WeChatMallTask(application);
    }

    public LiveData<Resource<List<ShopEntity>>> getShopList() {
        return shopList;
    }

    public void setShopList(int pageNum) {
        shopList.setSource(mWeChatMallTask.getShopList( pageNum));
    }

    public SingleSourceLiveData<Resource<List<ShopEntity>>> getShopListType() {
        return shopListType;
    }

    public void setShopListType(int shopClassify) {
        shopListType.setSource(mWeChatMallTask.getShopListType(shopClassify));
    }

    public LiveData<Resource<List<BannerEntity>>> getBannerList() {
        return bannerList;
    }

    public void setBannerList() {
        bannerList.setSource(mWeChatMallTask.getBannerList());
    }

    public LiveData<Resource<ShopEntity>> getShopDetail() {
        return shopDetail;
    }

    public void setShopDetail(String id) {
        shopDetail.setSource(mWeChatMallTask.getShopEntity(id));
    }

    public LiveData<Resource<List<OrderEntity>>> getOrderList() {
        return orderList;
    }

    public void setOrderList(int pageNum, int pageSize) {
        orderList.setSource(mWeChatMallTask.orderList(pageNum, pageSize));
    }

    public LiveData<Resource<Object>> getBuyGoods() {
        return buyGoods;
    }

    public void setBuyGoods(String address, String addressee, String number, String password, String phone, String region, String shopId) {
        buyGoods.setSource(mWeChatMallTask.getBuyGoods(address, addressee, number, password, phone, region, shopId));
    }

    public LiveData<Resource<Object>> getAddAddress() {
        return addAddress;
    }

    public void setAddAddress( String address, String addressee, String isDefault, String phone, String region,String userId) {
        addAddress.setSource(mWeChatMallTask.addAddress(address, addressee, isDefault, phone, region,userId));
    }

    public LiveData<Resource<Object>> getDelAddress(){
        return delAddress;
    }
    public void setDelAddress(String id){
        delAddress.setSource(mWeChatMallTask.delAddress(id));
    }

    public LiveData<Resource<List<AddressEntity>>> getAddressList(){
        return addressList;
    }
    public void setAddressList(){
        addressList.setSource(mWeChatMallTask.addressList());
    }

    public LiveData<Resource<Object>> getUpdateAddress(){
        return updateAddress;
    }
    public void setUpdateAddress(AddressEntity address){
        updateAddress.setSource(mWeChatMallTask.updateAddress(address));
    }
}
