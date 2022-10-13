package com.pingmo.chengyan.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IdentificatonBean {
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
        @JsonProperty("certifyId")
        private String certifyId;

        public String getCertifyId() {
            return certifyId;
        }

        public void setCertifyId(String certifyId) {
            this.certifyId = certifyId;
        }
    }
}
