package com.pingmo.chengyan.activity.shop.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@lombok.NoArgsConstructor
@lombok.Data
public class ShopTypeBean {

    @JsonProperty("code")
    public Integer code;
    @JsonProperty("msg")
    public String msg;
    @JsonProperty("data")
    public List<DataDTO> data;

    @lombok.NoArgsConstructor
    @lombok.Data
    public static class DataDTO {
        @JsonProperty("value")
        public Integer value;
        @JsonProperty("label")
        public String label;
        @JsonProperty("icon")
        public String icon;
        @JsonProperty("children")
        public Object children;
    }
}
