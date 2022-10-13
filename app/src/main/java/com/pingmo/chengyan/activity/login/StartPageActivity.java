package com.pingmo.chengyan.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.pingmo.chengyan.MainActivity;
import com.pingmo.chengyan.MyAPP;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.manager.BaseActivity;
import com.pingmo.chengyan.utils.Constants;
import com.pingmo.chengyan.utils.DemoLog;
import com.pingmo.chengyan.utils.QZXTools;
import com.pingmo.chengyan.utils.SharedPreferenceUtil;
import com.tencent.imsdk.v2.V2TIMFriendAddApplication;
import com.tencent.imsdk.v2.V2TIMFriendOperationResult;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.tencent.qcloud.tuicore.TUILogin;
import com.tencent.qcloud.tuicore.interfaces.TUICallback;
import com.tencent.qcloud.tuicore.util.ErrorMessageConverter;
import com.tencent.qcloud.tuikit.tuicontact.util.TUIContactLog;


public class StartPageActivity extends BaseActivity {

    private static final String TAG = "";
    private Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_page_layout);
        addActivity(this);
        String token = SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("token");
        if (!TextUtils.isEmpty(token)){
            initTengSdkLogin();
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(StartPageActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        },2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActivity(this);
    }


    private void initTengSdkLogin() {
        String userId = SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("userId");
        String userSig = SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("userSig");
//        String headImage = SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("headImage");
//        String nickName = SharedPreferenceUtil.getInstance(MyAPP.getInstance()).getString("nickName");
        //开始初始化 imsdk
        TUILogin.login(MyAPP.getInstance(), Constants.SDKAPPID, userId + "", userSig, new TUICallback() {
            @Override
            public void onSuccess() {
                QZXTools.logE("成功", null);
//                Intent intent = new Intent(MainActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
                //加我验证方式
                TUILogin.updateProfile(1);

                //添加好友
                V2TIMFriendAddApplication v2TIMFriendAddApplication = new V2TIMFriendAddApplication("13532325");

                v2TIMFriendAddApplication.setAddSource("android");

                V2TIMManager.getFriendshipManager().addFriend(v2TIMFriendAddApplication, new V2TIMValueCallback<V2TIMFriendOperationResult>() {
                    @Override
                    public void onError(int code, String desc) {
                        TUIContactLog.e(TAG, "addFriend err code = " + code + ", desc = " + ErrorMessageConverter.convertIMError(code, desc));
                        // ContactUtils.callbackOnError(callback, TAG, code, desc);
                    }

                    @Override
                    public void onSuccess(V2TIMFriendOperationResult v2TIMFriendOperationResult) {
                        TUIContactLog.i(TAG, "addFriend success");
                        //  ContactUtils.callbackOnSuccess(callback, new Pair<>(v2TIMFriendOperationResult.getResultCode(), v2TIMFriendOperationResult.getResultInfo()));
                    }
                });
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                DemoLog.i(TAG, "imLogin errorCode = " + errorCode + ", errorInfo = " + errorMessage);

            }
        });
    }
}
