package com.pingmo.chengyan.activity.shop.common;


public class SealTalkUrl {
    public static final String DOMAIN = "http://api.njpingmo.com.cn";
    public static final String LOGIN = "/api/user/login";
    public static final String GET_TOKEN = "/api/user/getToken";

    public static final String SEND_CODE = "/api/user/sendCode";

    public static final String VERIFY_CODE = "user/verify_code_yp";

    public static final String REGISTER = "/api/user/register";
    /**
     * 注销
     */
    public static final String cancellation = "api/user/cancellation";

    public static final String REGION_LIST = "user/regionlist";

    public static final String CHECK_PHONE_AVAILABLE = "user/check_phone_available";

    public static final String RESET_PASSWORD = "user/reset_password";
    public static final String RESET_PASSWORD2 = "api/user/forgetPassword";

    public static final String GET_IMAGE_UPLOAD_TOKEN = "user/get_image_token";
    public static final String GET_CONFIG = "api/user/config/{key}";

    /**
     * 上传文件
     */
    public static final String COMMON_UPLOAD = "/common/api/upload";
    /**
     * 获取协议值
     */
    public static final String AGREEMENT_KEY = "/common/agreement/{key}";


    public static final String GROUP_CREATE = "api/group/creat";

    public static final String GROUP_ADD_MEMBER = "api/group/add";

    public static final String GROUP_JOIN = "api/group/addCode";

    public static final String GROUP_KICK_MEMBER = "api/group/kick";

    public static final String GROUP_QUIT = "api/group/quit/{id}";

    public static final String GROUP_DISMISS = "api/group/dismiss/{id}";

    //转让群主
    public static final String GROUP_TRANSFER = "api/group/setCreator";

    public static final String GROUP_RENAME = "api/group/rename";

    public static final String GROUP_SET_REGULAR_CLEAR = "api/group/setClearStatus";

    public static final String GROUP_GET_REGULAR_CLEAR_STATE = "group/get_regular_clear";

    public static final String GROUP_SET_BULLETIN = "api/group/setBulletin";

    public static final String GROUP_GET_BULLETIN = "group/get_bulletin";

    public static final String GROUP_SET_PORTRAIT_URL = "api/group/setPortraitUri";

    public static final String GROUP_SET_DISPLAY_NAME = "group/set_display_name";

    public static final String GROUP_GET_INFO = "/api/group";

    public static final String GROUP_GET_MEMBER_INFO = "/api/group/members";

    public static final String GROUP_SAVE_TO_CONTACT = "api/group/favGroups/{groupId}";
    public static final String GROUP_DELETE_TO_CONTACT = "api/group/delFavGroups/{groupId}";

    public static final String GROUP_GET_ALL_IN_CONTACT = "api/group/favGroups";

    public static final String GROUP_GET_NOTICE_INFO = "api/group/noticeInfo";

    public static final String GROUP_SET_NOTICE_STATUS = "api/group/agree";

    public static final String GROUP_CLEAR_NOTICE = "/api/group/clearNoticeInfo";

    public static final String GROUP_COPY = "group/copy_group";

    public static final String GROUP_GET_EXITED = "group/exited_list";

    public static final String GROUP_GET_MEMBER_INFO_DES = "group/get_member_info";

    public static final String GROUP_SET_MEMBER_INFO_DES = "api/group/updateGroupNickName";

    //先用好友信息接口
//    public static final String GET_USER_INFO = "/api/userFriend/friendInfo/{friendId}";
    public static final String GET_USER_INFO = "/api/userFriend/friendInfo";
    public static final String GET_FRIEND_ALL = "/api/userFriend/list";
    public static final String APPLY_LIST = "/api/userFriend/apilyList";
    public static final String CLEAR_NEW_FRIENDS = "/api/userFriend/clearApplyList";

    /**
     * 获取黑名单列表
     */
    public static final String GET_BLACK_LIST = "/api/userFriend/blackList";

    /**
     * 添加到黑名单
     */
    public static final String ADD_BLACK_LIST = "/api/userFriend/addBlack/{friendId}";

    /**
     * 移除黑名单
     */
    public static final String REMOVE_BLACK_LIST = "/api/userFriend/refuseBlack/{friendId}";

    /**
     * 更新登录者昵称
     */
    public static final String SET_NICK_NAME = "/api/user/setNickName";

    /**
     * 设置微呗号
     */
    public static final String SET_ST_ACCOUNT = "api/user/setCloudId";

    public static final String SET_GENDER = "api/user/setGender";
    /**
     * 获取登陆设备信息
     */
    public static final String DEVICE_INFO = "api/user/getDeviceInfos";
    public static final String DEL_DEVICE_INFO = "api/user/delDeviceInfo/{id}";
    public static final String LOGIN_VERIFICATION = "api/user/loginVerification";
    /**
     * 更新图像
     */
    public static final String SET_PORTRAIT = "/api/user/setHeadImg";

    public static final String APPLY_USER = "/api/userFriend/apilyUser";

    public static final String REFUSE_USER = "/api/userFriend/refuseUser/{id}";

    public static final String GET_FRIEND_PROFILE = GET_USER_INFO;

    public static final String SET_DISPLAY_NAME = "friendship/set_display_name";

    //    public static final String INVITE_FRIEND = "/api/userFriend/addFriend";
    public static final String INVITE_FRIEND = "/api/userFriend/addFriendDecrypt";

    public static final String DELETE_FREIND = "/api/userFriend/del/{friendId}";

    public static final String GET_CONTACTS_INFO = "friendship/get_contacts_info";

    public static final String CLIENT_VERSION = "api/apiVersion/{device}";

    public static final String CHANGE_PASSWORD = "api/user/changePassword";

    public static final String GET_DISCOVERY_CHAT_ROOM = "misc/demo_square";

    public static final String FIND_FRIEND = "/api/userFriend/searchFriend/{search}";

    public static final String GROUP_REMOVE_MANAGER = "api/group/removeManagers";

    public static final String GROUP_ADD_MANAGER = "api/group/setManagers";

    public static final String GROUP_MUTE_ALL = "api/group/muteAll";

    public static final String GROUP_MEMBER_PROTECTION = "api/group/setCertification";

    public static final String GROUP_SET_CERTIFICATION = "api/group/setCertification";

    public static final String SET_PRIVACY = "/api/user/setPrivacy";

    public static final String GET_PRIVACY = "/api/user/getPrivacy";

    public static final String GET_SCREEN_CAPTURE = "misc/get_screen_capture";

    public static final String SET_SCREEN_CAPTURE = "misc/set_screen_capture";

    public static final String SEND_SC_MSG = "misc/send_sc_msg";

    public static final String SET_RECEIVE_POKE_MESSAGE_STATUS = "user/set_poke";

    public static final String GET_RECEIVE_POKE_MESSAGE_STATUS = "user/get_poke";

    public static final String SET_FRIEND_DESCRIPTION = "/api/userFriend/friendInfo/update";

    public static final String GET_FRIEND_DESCRIPTION = "friendship/get_friend_description";

    public static final String MULTI_DELETE_FRIEND = "friendship/batch_delete";

    public static final String GET_ROB_NO = "api/group/getRobNo/{groupId}";
    public static final String GET_MANAGERS = "api/group/managers";
    /**
     * 批量设置抢包状态
     */
    public static final String SET_ROB_STATUS = "api/group/setRobStatus";
    /**
     * 设置地区
     */
    public static final String setRegion = "api/user/setRegion";

    /*
    ----------------------------零钱包 start----------------------
     */
    public static final String user_wallet = "api/userWallet";
    public static final String update_pay_password = "api/userWallet/updatePayPassword";
    public static final String FORGET_PAY_PASSWORD = "api/userWallet/forgetPayPassword";
    public static final String wallet_record = "api/userWallet/walletRecord";
    public static final String wallet_record2 = "api/userWallet/walletRecord";
    public static final String add_ali_pay = "api/userWallet/addAliPay";
    public static final String del_ali_pay = "api/userWallet/delAliPay";
    public static final String select_descriptions = "api/userWallet/selectDescriptions";
    //账单详情
    public static final String bill_info = "api/userWallet/walletRecord/{id}";
    /*
    ----------------------------零钱包 end----------------------
     */


    /*
    ---------------------------红包 start------------------------------
     */
    public static final String get_red_packet = "api/redPacket/getRedPacket/{redPacketId}";
    public static final String rob_user_packet = "api/redPacket/robUserPacket/{redPacketId}";
    public static final String rob_group_packet = "api/redPacket/robGroupPacket/{redPacketId}";
    public static final String send_user_packet = "api/redPacket/sendUserPacket";
    public static final String send_group_packet = "api/redPacket/sendGroupPacket";
    /*
    ---------------------------红包 end------------------------------
     */

    /*
    ---------------------------会员 start-----------------------------
     */
    //查询vip开通规则
    public static final String vip_get_rules = "api/vip/getRules";
    public static final String vip_open_vip = "api/vip/openVip";
    /*
    ---------------------------会员 end-------------------------------
     */

    //提交反馈
    public static final String feedBack = "api/feedBack";

    /*
    --------------------------微呗商城 start-------------------
     */
    public static final String shop = "api/shop";
    public static final String shop_detail = "api/shop/{id}";
    public static final String shop_banners = "api/shop/banners";
    public static final String shop_types = "api/shop/classificationCascadeOptions";

    //    获取订单列表
    public static final String shopOrder = "api/shopOrder";
    //    api/shopOrder 购买商品
//    api/shopOrder/{id} 获取订单信息
    public static final String getShopOrderInfo = "api/shopOrder";

    //    api/shopAddress 获取地址列表
    public static final String shopAddress = "api/shopAddress";

    /*
    --------------------------微呗商城 end-------------------
     */

    //转账
    public static final String transfer = "api/userWallet/transfer";
    public static final String transfer_open = "api/userWallet/transferGet";
    public static final String transfer_return = "api/userWallet/transfer/return/{id}";
    public static final String transfer_info = "api/userWallet/transfer/selectById/{id}";

    /*
    --------------------------首信支付 start-------------------
    --------------------------2021-9-17改为新生支付 start-------------------
    */
    //实名认证
//    public static final String verified = "api/weipay/wallet/creat";
    public static final String verified = "api/hnapay/wallet/realAuth";
    //获取token
    public static final String creatToken = "api/hnapay/wallet/creatToken";
    //购买 支付（购买）预下单
    public static final String recharge = "api/hnapay/wallet/onlinePayOrder";
    //支付确认
    public static final String receiptPayment = "api/hnapay/wallet/receiptPayment";
    //提现
    public static final String without = "api/hnapay/wallet/without";
    //提现费率
    public static final String WITHOUT_RATE = "api/hnapay/wallet/withoutRate";
    //提现确认
    public static final String receiptWithout = "api/hnapay/wallet/without";
    //绑卡
    public static final String bindCard = "api/hnapay/wallet/bindCard";
    //绑卡确认
    public static final String bindCardConfirm = "api/hnapay/wallet/bindCardConfirm";

    //解绑 api/weipay/wallet/bindCardConfirm/{bindCardId}
    public static final String bindCardUnbind = "api/hnapay/wallet/bindCardUnbind/{bindCardId}";
    //银行卡列表
    public static final String cardList = "api/hnapay/wallet/list";
    /*
    --------------------------首信支付 end-------------------
    */

    /*
    --------------------------阿里云认证 start-------------------
    */
    public static final String RPBioDescribeVerifyToken = "api/aliyunFaceVerify/RPBioDescribeVerifyToken";
    public static final String RPBioDescribeVerifyResult = "api/aliyunFaceVerify/RPBioDescribeVerifyResult/{bizId}";
    public static final String FVBioDescribeVerifyToken = "api/aliyunFaceVerify/FVBioDescribeVerifyToken";
    public static final String FVBioDescribeVerifyResult = "api/aliyunFaceVerify/FVBioDescribeVerifyResult/{bizId}";
    /*
     _________________________阿里云认证 end_______________________________
     */
        /*
    --------------------------阿里云认证金融级 start-------------------
    */
    /**
     * 实人认证获取certifyId
     */
    public static final String getCertifyId = "api/aliyunFaceVerify/init";
    /**
     * 实人认证成功
     */
    public static final String getCertifyOK = "api/aliyunFaceVerify/describe";
        /*
     _________________________阿里云认证 end_______________________________
     */
    /*
     _________________________ 积分 start _______________________________
     */
    /**
     * 签到信息
     */
    public static final String selectSignWeek = "api/UserIntegral/selectSignInfo";
    /**
     * 签到
     */
    public static final String sign = "api/UserIntegral/sign";
    /**
     * 签到列表
     */
    public static final String signList = "api/UserIntegral/signList";
    /*
     _________________________ 积分 end _______________________________
     */

    /*
  _________________________ 盲盒 start _______________________________
  */
    public static final String blindBox = "api/blindBox";
    public static final String blindBoxBuy = "api/blindBox/buy";
    public static final String blindBoxList = "api/blindBox/{id}";
    /*
     _________________________ 盲盒 end _______________________________
     */

    /**
     * 未领完红包列表
     */
    public static final String groupPacketList = "api/redPacket/getGroupPacketNo/{groupId}";
}
