package com.tencent.qcloud.tuikit.tuichat.bean;




public class GoinGroupBean  {

    private String id;
    private String groupId;
    private String userID;
    //0 取消个人禁言   1 添加个人禁言 2 全员禁言 3 取消全员禁言
    private int type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
