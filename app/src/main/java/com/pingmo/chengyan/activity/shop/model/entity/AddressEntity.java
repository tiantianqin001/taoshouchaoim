package com.pingmo.chengyan.activity.shop.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class AddressEntity implements Parcelable {

    /**
     * id : 1
     * userId : 22
     * addressee : ren
     * phone : 18888888888
     * region : 北京市,北京市,东城区
     * address : vbj
     * isDefault : 1
     * createTime : 2021-04-12T18:10:12.000+0800
     * updateTime : null
     */

    public String id;
    public String userId;
    public String addressee;
    public String phone;
    public String region;
    public String address;
    private String isDefault;
    public String createTime;
    public String updateTime;

    public String getIsDefault() {
        return isDefault;
    }

    public boolean getIsDefaultBoolean(){
        return "1".equals(getIsDefault());
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.userId);
        dest.writeString(this.addressee);
        dest.writeString(this.phone);
        dest.writeString(this.region);
        dest.writeString(this.address);
        dest.writeString(this.isDefault);
        dest.writeString(this.createTime);
        dest.writeString(this.updateTime);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.userId = source.readString();
        this.addressee = source.readString();
        this.phone = source.readString();
        this.region = source.readString();
        this.address = source.readString();
        this.isDefault = source.readString();
        this.createTime = source.readString();
        this.updateTime = source.readString();
    }

    public AddressEntity() {
    }

    protected AddressEntity(Parcel in) {
        this.id = in.readString();
        this.userId = in.readString();
        this.addressee = in.readString();
        this.phone = in.readString();
        this.region = in.readString();
        this.address = in.readString();
        this.isDefault = in.readString();
        this.createTime = in.readString();
        this.updateTime = in.readString();
    }

    public static final Creator<AddressEntity> CREATOR = new Creator<AddressEntity>() {
        @Override
        public AddressEntity createFromParcel(Parcel source) {
            return new AddressEntity(source);
        }

        @Override
        public AddressEntity[] newArray(int size) {
            return new AddressEntity[size];
        }
    };
}
