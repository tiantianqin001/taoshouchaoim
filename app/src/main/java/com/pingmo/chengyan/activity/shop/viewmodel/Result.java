package com.pingmo.chengyan.activity.shop.viewmodel;

import com.pingmo.chengyan.activity.shop.common.ErrorCode;
import com.pingmo.chengyan.activity.shop.common.NetConstant;

/**
 * 网络请求结果基础类
 * @param <T> 请求结果的实体类
 */
public class Result<T> {
    public int code;
//    public T result;
    public T data;
    public String msg;

    public Result(){
    }

    public Result(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getResult() {
        return data;
    }

    public void setResult(T result) {
        this.data = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isSuccess(){
        return code == NetConstant.REQUEST_SUCCESS_CODE;
    }

    public String getErrorMessage(){
        return ErrorCode.fromCode(code).getMessage();
    }
}
