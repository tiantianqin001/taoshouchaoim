package com.pingmo.chengyan.net;

import com.fasterxml.jackson.databind.JsonSerializable;

public class Common {
    public static final int Interface_success = 77;
    public static final int Interface_err = 78;
    public static final int Interface_success_law = 82;
    //调接口网络错误
    public static final int Interface_net_err = 79;
    //删除数据
    public static final int Delete_data_success = 80;
    public static final int Delete_data_err = 81;

    // Field from default config.
    public static final String SEALTALK_SERVER = "http://api.njpingmo.com.cn";
    //根地址
    public static final String BaseUrl = "http://api.njpingmo.com.cn";


    //登录的地址
    public static final String LOGIN = BaseUrl + "/api/user/login";
    //退出登录的地址
    public static final String LOGINOUT = BaseUrl + "/api/user/loginOut";
    //账号注销的地址
    public static final String CANCELLATION = BaseUrl + "/api/user/cancellation";
    //获取验证码
    public static final String SENDCODE = BaseUrl + "/api/user/sendCode";
    //修改密码
    public static final String UPDATEPWD = BaseUrl + "/api/user/updatePassword";
    //注册的地址
    public static final String REGIST = BaseUrl + "/api/user/register";
    //忘记密码
    public static final String FORGETPASSWORD = BaseUrl + "/api/user/forgetPassword";
    //修改资料
    public static final String UPDATE_DATA = BaseUrl + "/api/user/updateData";
    //上传文件
    public static final String UPLOAD = BaseUrl + "/api/user/upload";

    //订单列表
    public static final String SHOP_ORDER = BaseUrl + "/api/shopOrder";
    //app软件信息
    public static final String SOFTWARE_PACKAGE = BaseUrl + "/api/softwarePackage";


    //系统公告  也就是通知消息
    public static final String SYSTEMNOTIFICATION = BaseUrl + "/api/notice/list";
    //实人认证获取certifyId
    public static final String ALIYUNFACEINIT = BaseUrl + "/api/aliyunFaceVerify/init";
    //实人认证查询结果
    public static final String ALIYUNFACERESULT = BaseUrl + "/api/aliyunFaceVerify/describe";

    //修改支付密码
    public static final String PAYMENT_PASSWORD = BaseUrl + "/api/userWallet/updatePassword";

    //钱包信息
    public static final String WALLET_INFORMATION = BaseUrl + "/api/userWallet/walletInfo";
    //钱包流水信息
    public static final String WALLET_RECORD = BaseUrl + "/api/userWallet/walletRecord";
    //获取商品的类别
    public static final String SHOP_TYPE = BaseUrl + "/api/shop/classificationCascadeOptions";
    //用户的银行卡列表
    public static final String BANK_LIST = BaseUrl + "/api/userCard/backList";
    //添加银行卡
    public static final String ADD_BANK = BaseUrl + "/api/userCard/addBack";
    //删除银行卡
    public static final String DELETE_BANK = BaseUrl + "/api/userCard/deleteBack";
    //意见反馈
    public static final String FEEDBACK = BaseUrl + "/api/feedback";
    //修改隐私
    public static final String MODIFY_PRIVACY = BaseUrl + "/api/user/updatePrivate";
    //登录设备模块 列表
    public static final String LOGIN_DEVICE_LIST= BaseUrl + "/api/userDevice";
    //登录设备模块 列表的移除
    public static final String LOGIN_DEVICE_LIST_DEL = BaseUrl + "/api/userDevice";



}
