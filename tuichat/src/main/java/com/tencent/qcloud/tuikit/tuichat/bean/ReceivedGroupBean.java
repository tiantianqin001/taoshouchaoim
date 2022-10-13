package com.tencent.qcloud.tuikit.tuichat.bean;

import java.io.Serializable;

public class ReceivedGroupBean implements Serializable {
    private String headImage;
    private String nickName;
    private String robTime;
    private String money;

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getRobTime() {
        return robTime;
    }

    public void setRobTime(String robTime) {
        this.robTime = robTime;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    @Override
    public String toString() {
        return "ReceivedGroupBean{" +
                "headImage='" + headImage + '\'' +
                ", nickName='" + nickName + '\'' +
                ", robTime='" + robTime + '\'' +
                ", money='" + money + '\'' +
                '}';
    }
}
