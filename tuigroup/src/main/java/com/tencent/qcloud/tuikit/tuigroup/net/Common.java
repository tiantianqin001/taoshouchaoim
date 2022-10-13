package com.tencent.qcloud.tuikit.tuigroup.net;

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
    //不是自己建的就退出群聊
    public static final String QUIT_GROUP_CHAT = BaseUrl + "/api/group/quit";
    //全员禁言
    public static final String ALL_SPEAK = BaseUrl + "/api/group/estoppelAll";
    //设置管理员
    public static final String SET_ADMINISTRATOR = BaseUrl + "/api/group/setManagers";
    //邀请入群
    public static final String INVITE_GROUP = BaseUrl + "/api/group/invitation";
    //移除群聊
    public static final String MOVE_GROUP = BaseUrl + "/api/group/kicking";
    //修改群信息
    public static final String UPDATA_GROUP = BaseUrl + "/api/group/update";
    //获取群消息
    public static final String GETGROUPINFO = BaseUrl + "/api/group/info";
    //转账群主
    public static final String TRANSFER_GROUP_OWNER = BaseUrl + "/api/group/changeGroupOwner";


    //上传文件
    public static final String UPLOAD = BaseUrl + "/api/user/upload";



}
