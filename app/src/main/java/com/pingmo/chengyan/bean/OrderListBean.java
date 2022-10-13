package com.pingmo.chengyan.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class OrderListBean {
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
        @JsonProperty("total")
        private int total;
        @JsonProperty("rows")
        private List<OrderItemBean> rows;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<OrderItemBean> getRows() {
            return rows;
        }

        public void setRows(List<OrderItemBean> rows) {
            this.rows = rows;
        }
    }
}
