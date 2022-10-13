/***************************************************************************************************
 * 单位：北京红云融通技术有限公司
 * 日期：2016-12-14
 * 版本：1.0.0
 * 版权：All rights reserved.
 **************************************************************************************************/
package com.pingmo.chengyan.utils;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

public class BaseHandler extends Handler {

    private WeakReference<Object> innerObj;
    private HandleMsgCallback callback;

    public BaseHandler(Object o, HandleMsgCallback callback) {
        innerObj = new WeakReference<>(o);
        this.callback = callback;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (callback != null && innerObj.get() != null) {
            callback.handleMessage(msg);
        }
    }

    public interface HandleMsgCallback {
        void handleMessage(Message msg);
    }
}
