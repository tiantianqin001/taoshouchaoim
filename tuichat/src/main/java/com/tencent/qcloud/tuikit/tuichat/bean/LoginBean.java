package com.tencent.qcloud.tuikit.tuichat.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginBean {
    @JsonProperty("msg")
    private String msg;
    @JsonProperty("code")
    private int code;
    @JsonProperty("data")
    private DataDTO data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }

    public static class DataDTO {
        @JsonProperty("userId")
        private int userId;
        @JsonProperty("phone")
        private String phone;
        @JsonProperty("nickName")
        private String nickName;
        @JsonProperty("headImage")
        private String headImage;
        @JsonProperty("region")
        private Object region;
        @JsonProperty("birthday")
        private Object birthday;
        @JsonProperty("gender")
        private int gender;
        @JsonProperty("device")
        private String device;
        @JsonProperty("token")
        private String token;
        @JsonProperty("userSig")
        private String userSig;
        @JsonProperty("time")
        private String time;
        @JsonProperty("groupVerify")
        private int groupVerify;
        @JsonProperty("idSearchVerify")
        private int idSearchVerify;
        @JsonProperty("phoneVerify")
        private int phoneVerify;
        @JsonProperty("friVerify")
        private int friVerify;
        @JsonProperty("payPassword")
        private boolean payPassword;
        @JsonProperty("realStatus")
        private int realStatus;

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getHeadImage() {
            return headImage;
        }

        public void setHeadImage(String headImage) {
            this.headImage = headImage;
        }

        public Object getRegion() {
            return region;
        }

        public void setRegion(Object region) {
            this.region = region;
        }

        public Object getBirthday() {
            return birthday;
        }

        public void setBirthday(Object birthday) {
            this.birthday = birthday;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public String getDevice() {
            return device;
        }

        public void setDevice(String device) {
            this.device = device;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getUserSig() {
            return userSig;
        }

        public void setUserSig(String userSig) {
            this.userSig = userSig;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public int getGroupVerify() {
            return groupVerify;
        }

        public void setGroupVerify(int groupVerify) {
            this.groupVerify = groupVerify;
        }

        public int getIdSearchVerify() {
            return idSearchVerify;
        }

        public void setIdSearchVerify(int idSearchVerify) {
            this.idSearchVerify = idSearchVerify;
        }

        public int getPhoneVerify() {
            return phoneVerify;
        }

        public void setPhoneVerify(int phoneVerify) {
            this.phoneVerify = phoneVerify;
        }

        public int getFriVerify() {
            return friVerify;
        }

        public void setFriVerify(int friVerify) {
            this.friVerify = friVerify;
        }

        public boolean isPayPassword() {
            return payPassword;
        }

        public void setPayPassword(boolean payPassword) {
            this.payPassword = payPassword;
        }

        public int getRealStatus() {
            return realStatus;
        }

        public void setRealStatus(int realStatus) {
            this.realStatus = realStatus;
        }
    }
}
