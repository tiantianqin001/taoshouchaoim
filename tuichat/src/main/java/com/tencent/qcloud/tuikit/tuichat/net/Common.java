package com.tencent.qcloud.tuikit.tuichat.net;

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

    //发送单人红包
    public static final String SendPersonalEnvelope = BaseUrl + "/api/redpacket/user/send";
    //领取单人红包
    public static final String RECEIVE_SINGLE_RED_ENVELOPE = BaseUrl + "/api/redpacket/user/rob";
    //单人红包信息
    public static final String SINGLE_RED_PACKET_INFORMATION = BaseUrl + "/api/redpacket/user/info";
    //群红包的消息
    public static final String GROUP_RED_ENVELOPE_NEWS = BaseUrl + "/api/redpacket/group/info";
    //发群红包
    public static final String FAQUN_RED_ENVELOPE  = BaseUrl + "/api/redpacket/group/send";
    //抢红包
    public static final String GRAB_RED_ENVELOPE = BaseUrl + "/api/redpacket/group/rob";
    //转账
    public static final String PERSION_TO_TRANSFER = BaseUrl + "/api/userWallet/transfer";

    //发送群名片
    public static final String PERSION_CARD = BaseUrl + "/api/group/sendBusinessCardMessage";
    //发送好友模块的名片
    public static final String FRIEND_CARD = BaseUrl + "/api/friend/sendBusinessCardMessage";
    //获取群消息
    public static final String GETGROUPINFO = BaseUrl + "/api/group/info";



}
