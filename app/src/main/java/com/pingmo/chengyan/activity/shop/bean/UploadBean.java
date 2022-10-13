package com.pingmo.chengyan.activity.shop.bean;

public class UploadBean {

    /**
     * msg : 操作成功
     * fileName : /profile/upload/2021/02/26/8a911412db9add8e16790a40aea3e9c8.png
     * code : 200
     * url : http://chengxin.ijidoo.cn/profile/upload/2021/02/26/8a911412db9add8e16790a40aea3e9c8.png
     */

    private String msg;
    private String fileName;
    private int code;
    private String url;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
