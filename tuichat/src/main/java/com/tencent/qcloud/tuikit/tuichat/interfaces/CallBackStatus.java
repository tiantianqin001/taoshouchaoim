package com.tencent.qcloud.tuikit.tuichat.interfaces;

public interface CallBackStatus {

    //1是单人红包   2 是群红包  3群红包已经领完
    void getStatus(String redPacketId);
}
