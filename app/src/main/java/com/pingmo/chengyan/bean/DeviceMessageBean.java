package com.pingmo.chengyan.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DeviceMessageBean {

    @JsonProperty("code")
    public Integer code;
    @JsonProperty("msg")
    public String msg;
    @JsonProperty("data")
    public List<DataDTO> data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @JsonProperty("id")
        public Integer id;
        @JsonProperty("userId")
        public Integer userId;
        @JsonProperty("deviceId")
        public String deviceId;
        @JsonProperty("deviceName")
        public String deviceName;
        @JsonProperty("deviceBy")
        public String deviceBy;
        @JsonProperty("loginTime")
        public String loginTime;
    }
}
