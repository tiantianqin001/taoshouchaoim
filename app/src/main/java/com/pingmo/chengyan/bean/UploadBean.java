package com.pingmo.chengyan.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UploadBean {
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
        @JsonProperty("fileName")
        private String fileName;
        @JsonProperty("newFileName")
        private String newFileName;
        @JsonProperty("url")
        private String url;
        @JsonProperty("originalFilename")
        private String originalFilename;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getNewFileName() {
            return newFileName;
        }

        public void setNewFileName(String newFileName) {
            this.newFileName = newFileName;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getOriginalFilename() {
            return originalFilename;
        }

        public void setOriginalFilename(String originalFilename) {
            this.originalFilename = originalFilename;
        }
    }
}
