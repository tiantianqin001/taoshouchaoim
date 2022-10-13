package com.tencent.qcloud.tuikit.tuisearch.net;

public class Common {
    public static final int Interface_success = 77;
    public static final int Interface_err= 78;
    public static final int Interface_success_law= 82;
    //调接口网络错误
    public static final int Interface_net_err = 79;
    //删除数据
    public static final int Delete_data_success = 80;
    public static final int Delete_data_err = 81;



    //根地址
   public static final String BaseUrl = "http://api.njpingmo.com.cn";

    //解散群 是自己建的就是解散群
    public static final String DISBAND_GROUP = BaseUrl + "/api/group/destroy";

    //获取好友信息
    public static final String GET_FRIEND_INFORMATION = BaseUrl + "/api/friend/search";




}
