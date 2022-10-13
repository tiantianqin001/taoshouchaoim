package com.tencent.qcloud.tuikit.tuichat.bean;

import java.util.List;

public class RedPacketDetailBean  {

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
        @com.fasterxml.jackson.annotation.JsonProperty("userId")
        private int userId;
        @com.fasterxml.jackson.annotation.JsonProperty("nickName")
        private String nickName;
        @com.fasterxml.jackson.annotation.JsonProperty("headImage")
        private String headImage;
        @com.fasterxml.jackson.annotation.JsonProperty("money")
        private String money;
        @com.fasterxml.jackson.annotation.JsonProperty("notes")
        private String notes;
        @com.fasterxml.jackson.annotation.JsonProperty("status")
        private int status;
        @com.fasterxml.jackson.annotation.JsonProperty("createTime")
        private String createTime;
        @com.fasterxml.jackson.annotation.JsonProperty("members")
        private List<MembersDTO> members;

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

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
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

        public List<MembersDTO> getMembers() {
            return members;
        }

        public void setMembers(List<MembersDTO> members) {
            this.members = members;
        }

        public static class MembersDTO {
            @com.fasterxml.jackson.annotation.JsonProperty("userId")
            private int userId;
            @com.fasterxml.jackson.annotation.JsonProperty("nickName")
            private String nickName;
            @com.fasterxml.jackson.annotation.JsonProperty("headImage")
            private String headImage;
            @com.fasterxml.jackson.annotation.JsonProperty("money")
            private double money;
            @com.fasterxml.jackson.annotation.JsonProperty("robTime")
            private String robTime;

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

            public double getMoney() {
                return money;
            }

            public void setMoney(double money) {
                this.money = money;
            }

            public String getRobTime() {
                return robTime;
            }

            public void setRobTime(String robTime) {
                this.robTime = robTime;
            }
        }
    }
}
