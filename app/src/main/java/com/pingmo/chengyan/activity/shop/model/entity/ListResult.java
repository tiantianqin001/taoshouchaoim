package com.pingmo.chengyan.activity.shop.model.entity;

import java.util.List;

public class ListResult<T> {
    public Integer total;
    public List<T> rows;
    public String code;
    public String msg;
}
