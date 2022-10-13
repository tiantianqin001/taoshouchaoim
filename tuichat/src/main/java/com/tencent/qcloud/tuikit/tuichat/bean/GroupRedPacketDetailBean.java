package com.tencent.qcloud.tuikit.tuichat.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class GroupRedPacketDetailBean implements Serializable {


    @JsonProperty("code")
    private int code;
    @JsonProperty("msg")
    private String msg;
    @JsonProperty("data")
    private DataDTO data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }

    public static class DataDTO implements Serializable{
        @JsonProperty("searchValue")
        private Object searchValue;
        @JsonProperty("createBy")
        private Object createBy;
        @JsonProperty("createTime")
        private String createTime;
        @JsonProperty("updateBy")
        private Object updateBy;
        @JsonProperty("updateTime")
        private Object updateTime;
        @JsonProperty("remark")
        private Object remark;
        @JsonProperty("id")
        private int id;
        @JsonProperty("groupId")
        private int groupId;
        @JsonProperty("userId")
        private int userId;
        @JsonProperty("nickName")
        private String nickName;
        @JsonProperty("headImage")
        private String headImage;
        @JsonProperty("money")
        private String money;
        @JsonProperty("number")
        private int number;
        @JsonProperty("status")
        private int status;
        @JsonProperty("notes")
        private String notes;
        @JsonProperty("expireTime")
        private Object expireTime;
        @JsonProperty("members")
        private List<MembersDTO> members;

        public Object getSearchValue() {
            return searchValue;
        }

        public void setSearchValue(Object searchValue) {
            this.searchValue = searchValue;
        }

        public Object getCreateBy() {
            return createBy;
        }

        public void setCreateBy(Object createBy) {
            this.createBy = createBy;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public Object getUpdateBy() {
            return updateBy;
        }

        public void setUpdateBy(Object updateBy) {
            this.updateBy = updateBy;
        }

        public Object getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(Object updateTime) {
            this.updateTime = updateTime;
        }

        public Object getRemark() {
            return remark;
        }

        public void setRemark(Object remark) {
            this.remark = remark;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getGroupId() {
            return groupId;
        }

        public void setGroupId(int groupId) {
            this.groupId = groupId;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
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

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public Object getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(Object expireTime) {
            this.expireTime = expireTime;
        }

        public List<MembersDTO> getMembers() {
            return members;
        }

        public void setMembers(List<MembersDTO> members) {
            this.members = members;
        }

        public static class MembersDTO implements Serializable{
            @JsonProperty("searchValue")
            private Object searchValue;
            @JsonProperty("createBy")
            private Object createBy;
            @JsonProperty("createTime")
            private Object createTime;
            @JsonProperty("updateBy")
            private Object updateBy;
            @JsonProperty("updateTime")
            private Object updateTime;
            @JsonProperty("remark")
            private Object remark;
            @JsonProperty("params")
            private ParamsDTO params;
            @JsonProperty("nickName")
            private String nickName;
            @JsonProperty("headImage")
            private String headImage;
            @JsonProperty("id")
            private int id;
            @JsonProperty("redPacketId")
            private int redPacketId;
            @JsonProperty("groupId")
            private int groupId;
            @JsonProperty("userId")
            private int userId;
            @JsonProperty("money")
            private String money;
            @JsonProperty("robTime")
            private String robTime;

            public Object getSearchValue() {
                return searchValue;
            }

            public void setSearchValue(Object searchValue) {
                this.searchValue = searchValue;
            }

            public Object getCreateBy() {
                return createBy;
            }

            public void setCreateBy(Object createBy) {
                this.createBy = createBy;
            }

            public Object getCreateTime() {
                return createTime;
            }

            public void setCreateTime(Object createTime) {
                this.createTime = createTime;
            }

            public Object getUpdateBy() {
                return updateBy;
            }

            public void setUpdateBy(Object updateBy) {
                this.updateBy = updateBy;
            }

            public Object getUpdateTime() {
                return updateTime;
            }

            public void setUpdateTime(Object updateTime) {
                this.updateTime = updateTime;
            }

            public Object getRemark() {
                return remark;
            }

            public void setRemark(Object remark) {
                this.remark = remark;
            }

            public ParamsDTO getParams() {
                return params;
            }

            public void setParams(ParamsDTO params) {
                this.params = params;
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

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getRedPacketId() {
                return redPacketId;
            }

            public void setRedPacketId(int redPacketId) {
                this.redPacketId = redPacketId;
            }

            public int getGroupId() {
                return groupId;
            }

            public void setGroupId(int groupId) {
                this.groupId = groupId;
            }

            public int getUserId() {
                return userId;
            }

            public void setUserId(int userId) {
                this.userId = userId;
            }

            public String getMoney() {
                return money;
            }

            public void setMoney(String money) {
                this.money = money;
            }

            public String getRobTime() {
                return robTime;
            }

            public void setRobTime(String robTime) {
                this.robTime = robTime;
            }

            public static class ParamsDTO {
            }
        }
    }
}
