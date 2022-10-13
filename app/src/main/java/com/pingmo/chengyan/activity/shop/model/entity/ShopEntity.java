package com.pingmo.chengyan.activity.shop.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;

/**
 * 商品列表实体
 */
public class ShopEntity implements Parcelable {

    /**
     * searchValue : null
     * createBy : null
     * createTime : 2021-04-08 14:57:01
     * updateBy : null
     * updateTime : 2021-04-08 16:35:10
     * remark :
     * id :
     * title : 商品
     * coverImg : /profile/upload/2021/04/08/78bafc8f351e6b6f2acdf4d34497a309.jpg
     * bannerImgs : /profile/upload/2021/04/08/78bafc8f351e6b6f2acdf4d34497a309.jpg,/profile/upload/2021/04/08/7dd156db4314e57b13cdc3c1f8f7fe13.jpg,/profile/upload/2021/04/08/235984c688339320e79145bbae0779fd.jpg,/profile/upload/2021/04/08/f7a190b02a797708836520f9866e1013.jpg
     * basePrice :
     * price :
     * integral :积分
     * number :
     * shopDetails :
     * delStatus : 1
     */

    public String searchValue;
    public String createBy;
    public String createTime;
    public String updateBy;
    public String updateTime;
    public String remark;
    public String id;
    public String title;
    public String coverImg;
    public String bannerImgs;
    public String basePrice;
    public String price;
    public BigDecimal integral;
    public String number;
    public String soldNumber;;
    public String shopDetails;
    public int delStatus;


    protected ShopEntity(Parcel in) {
        searchValue = in.readString();
        createBy = in.readString();
        createTime = in.readString();
        updateBy = in.readString();
        updateTime = in.readString();
        remark = in.readString();
        id = in.readString();
        title = in.readString();
        coverImg = in.readString();
        bannerImgs = in.readString();
        basePrice = in.readString();
        price = in.readString();
        integral = (BigDecimal) in.readSerializable();
        number = in.readString();
        soldNumber = in.readString();
        shopDetails = in.readString();
        delStatus = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(searchValue);
        dest.writeString(createBy);
        dest.writeString(createTime);
        dest.writeString(updateBy);
        dest.writeString(updateTime);
        dest.writeString(remark);
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(coverImg);
        dest.writeString(bannerImgs);
        dest.writeString(basePrice);
        dest.writeString(price);
        dest.writeSerializable(integral);
        dest.writeString(number);
        dest.writeString(soldNumber);
        dest.writeString(shopDetails);
        dest.writeInt(delStatus);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ShopEntity> CREATOR = new Creator<ShopEntity>() {
        @Override
        public ShopEntity createFromParcel(Parcel in) {
            return new ShopEntity(in);
        }

        @Override
        public ShopEntity[] newArray(int size) {
            return new ShopEntity[size];
        }
    };
}
