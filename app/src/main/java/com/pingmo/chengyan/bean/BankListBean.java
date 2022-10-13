package com.pingmo.chengyan.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class BankListBean {
    @JsonProperty("msg")
    private String msg;
    @JsonProperty("code")
    private int code;
    @JsonProperty("data")
    private List<BankItemBean> data;

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

    public List<BankItemBean> getData() {
        return data;
    }

    public void setData(List<BankItemBean> data) {
        this.data = data;
    }
}
