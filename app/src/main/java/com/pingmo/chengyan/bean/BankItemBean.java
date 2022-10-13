package com.pingmo.chengyan.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class BankItemBean implements Serializable {
    @JsonProperty("backCode")
    private String backCode;
    @JsonProperty("backName")
    private String backName;
    @JsonProperty("cardNumber")
    private String cardNumber;
    @JsonProperty("createBy")
    private String createBy;
    @JsonProperty("createTime")
    private String createTime;
    @JsonProperty("id")
    private int id;
    @JsonProperty("mainBackCode")
    private String mainBackCode;
    @JsonProperty("mainBackName")
    private String mainBackName;

    public String getBackCode() {
        return backCode;
    }

    public void setBackCode(String backCode) {
        this.backCode = backCode;
    }

    public String getBackName() {
        return backName;
    }

    public void setBackName(String backName) {
        this.backName = backName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMainBackCode() {
        return mainBackCode;
    }

    public void setMainBackCode(String mainBackCode) {
        this.mainBackCode = mainBackCode;
    }

    public String getMainBackName() {
        return mainBackName;
    }

    public void setMainBackName(String mainBackName) {
        this.mainBackName = mainBackName;
    }
}
