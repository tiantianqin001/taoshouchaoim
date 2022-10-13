package com.pingmo.chengyan.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CashItemBean {
    @JsonProperty("createTime")
    private String createTime;
    @JsonProperty("id")
    private int id;
    @JsonProperty("money")
    private double money;
    @JsonProperty("notes")
    private String notes;
    @JsonProperty("objectId")
    private int objectId;
    @JsonProperty("objectType")
    private int objectType;
    @JsonProperty("type")
    private int type;
    @JsonProperty("userId")
    private int userId;

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

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    public int getObjectType() {
        return objectType;
    }

    public void setObjectType(int objectType) {
        this.objectType = objectType;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
