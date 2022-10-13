package com.pingmo.chengyan.activity.shop.service;

import androidx.lifecycle.LiveData;


import com.pingmo.chengyan.activity.shop.common.SealTalkUrl;
import com.pingmo.chengyan.activity.shop.model.entity.AddressEntity;
import com.pingmo.chengyan.activity.shop.model.entity.BannerEntity;
import com.pingmo.chengyan.activity.shop.model.entity.ListResult;
import com.pingmo.chengyan.activity.shop.model.entity.OrderEntity;
import com.pingmo.chengyan.activity.shop.model.entity.ShopEntity;
import com.pingmo.chengyan.activity.shop.viewmodel.Result;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WeChatMallService {

    /**
     * 获取商品
     */
    @GET(SealTalkUrl.shop)
    LiveData<Result<ListResult<ShopEntity>>> getShopList(@Query("pageNum") int pageNum, @Query("pageSize") int pageSize);

    /**
     * 获取商品分类的详情
     */
    @GET(SealTalkUrl.shop)
    LiveData<Result<ListResult<ShopEntity>>> getShopListType(@Query("shopClassification")int shopClassify);


    /**
     * 商品轮播图
     *
     * @return
     */
    @GET(SealTalkUrl.shop_banners)
    LiveData<Result<List<BannerEntity>>> getBannerList();

    /**
     * 商品详情
     *
     * @param id
     * @return
     */
    @GET(SealTalkUrl.shop_detail)
    LiveData<Result<ShopEntity>> getShopDetail(@Path("id") String id);

    /**
     * 购买商品
     *
     * @param body
     * @return
     */
    @POST(SealTalkUrl.shopOrder)
    LiveData<Result<Object>> getBuyGoods(@Body RequestBody body);

    /**
     * 订单列表
     */
    @GET(SealTalkUrl.shopOrder)
    LiveData<Result<ListResult<OrderEntity>>> shopOrder(@Query("pageNum") int pageNum, @Query("pageSize") int pageSize);

    /**
     * 添加地址
     */
    @POST(SealTalkUrl.shopAddress)
    LiveData<Result<Object>> addAddress(@Body RequestBody body);

    /**
     * 删除地址
     */
    @DELETE(SealTalkUrl.shopAddress)
    LiveData<Result<Object>> delAddress(@Query("id") String id);

    @PUT(SealTalkUrl.shopAddress)
    LiveData<Result<Object>> updateAddress(@Body RequestBody body);

    /**
     * 获取地址列表
     */
    @GET(SealTalkUrl.shopAddress)
    LiveData<Result<List<AddressEntity>>> addressList();


}
