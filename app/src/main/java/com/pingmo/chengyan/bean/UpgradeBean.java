package com.pingmo.chengyan.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpgradeBean {
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
        @JsonProperty("version")
        private String version;
        @JsonProperty("downloadHref")
        private String downloadHref;
        @JsonProperty("forceUpdate")
        private boolean forceUpdate;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getDownloadHref() {
            return downloadHref;
        }

        public void setDownloadHref(String downloadHref) {
            this.downloadHref = downloadHref;
        }

        public boolean isForceUpdate() {
            return forceUpdate;
        }

        public void setForceUpdate(boolean forceUpdate) {
            this.forceUpdate = forceUpdate;
        }
    }
}
