package com.tencent.qcloud.tuikit.tuicontact.bean;

import java.util.List;

public class GroupNewLoadBean {

    @com.fasterxml.jackson.annotation.JsonProperty("code")
    private int code;
    @com.fasterxml.jackson.annotation.JsonProperty("msg")
    private String msg;
    @com.fasterxml.jackson.annotation.JsonProperty("data")
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

    public static class DataDTO {
        @com.fasterxml.jackson.annotation.JsonProperty("total")
        private int total;
        @com.fasterxml.jackson.annotation.JsonProperty("rows")
        private List<RowsDTO> rows;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<RowsDTO> getRows() {
            return rows;
        }

        public void setRows(List<RowsDTO> rows) {
            this.rows = rows;
        }

        public static class RowsDTO {
            @com.fasterxml.jackson.annotation.JsonProperty("id")
            private int id;
            @com.fasterxml.jackson.annotation.JsonProperty("groupId")
            private int groupId;
            @com.fasterxml.jackson.annotation.JsonProperty("groupName")
            private String groupName;
            @com.fasterxml.jackson.annotation.JsonProperty("groupImage")
            private Object groupImage;
            @com.fasterxml.jackson.annotation.JsonProperty("userId")
            private Object userId;
            @com.fasterxml.jackson.annotation.JsonProperty("userNickName")
            private String userNickName;
            @com.fasterxml.jackson.annotation.JsonProperty("inviterId")
            private Object inviterId;
            @com.fasterxml.jackson.annotation.JsonProperty("inviterName")
            private String inviterName;
            @com.fasterxml.jackson.annotation.JsonProperty("operatorName")
            private String operatorName;
            @com.fasterxml.jackson.annotation.JsonProperty("status")
            private int status;
            @com.fasterxml.jackson.annotation.JsonProperty("createTime")
            private String createTime;
            @com.fasterxml.jackson.annotation.JsonProperty("updateTime")
            private Object updateTime;

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

            public String getGroupName() {
                return groupName;
            }

            public void setGroupName(String groupName) {
                this.groupName = groupName;
            }

            public Object getGroupImage() {
                return groupImage;
            }

            public void setGroupImage(Object groupImage) {
                this.groupImage = groupImage;
            }

            public Object getUserId() {
                return userId;
            }

            public void setUserId(Object userId) {
                this.userId = userId;
            }

            public String getUserNickName() {
                return userNickName;
            }

            public void setUserNickName(String userNickName) {
                this.userNickName = userNickName;
            }

            public Object getInviterId() {
                return inviterId;
            }

            public void setInviterId(Object inviterId) {
                this.inviterId = inviterId;
            }

            public String getInviterName() {
                return inviterName;
            }

            public void setInviterName(String inviterName) {
                this.inviterName = inviterName;
            }

            public String getOperatorName() {
                return operatorName;
            }

            public void setOperatorName(String operatorName) {
                this.operatorName = operatorName;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public String getCreateTime() {
                return createTime;
            }

            public void setCreateTime(String createTime) {
                this.createTime = createTime;
            }

            public Object getUpdateTime() {
                return updateTime;
            }

            public void setUpdateTime(Object updateTime) {
                this.updateTime = updateTime;
            }
        }
    }
}
