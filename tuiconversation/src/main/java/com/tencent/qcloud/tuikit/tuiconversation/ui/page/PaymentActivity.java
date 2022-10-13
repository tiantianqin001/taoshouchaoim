package com.tencent.qcloud.tuikit.tuiconversation.ui.page;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gyf.immersionbar.ImmersionBar;
import com.tencent.imsdk.common.IMCallback;
import com.tencent.imsdk.conversation.ConversationManager;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.imsdk.v2.V2TIMMessageListGetOption;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.tencent.qcloud.tuicore.util.ErrorMessageConverter;
import com.tencent.qcloud.tuikit.tuiconversation.R;
import com.tencent.qcloud.tuikit.tuiconversation.adapter.PaymentAdapter;
import com.tencent.qcloud.tuikit.tuiconversation.adapter.SystemSetAdapter;

import java.util.List;

public class PaymentActivity extends Activity {
    private static final String TAG = "PaymentActivity";
    private String message;
    private RecyclerView rv_system_message;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置沉静状态栏
        ImmersionBar.with(this)
                .transparentBar()
                .init();
        setContentView(R.layout.system_set_layout);

        String id = getIntent().getStringExtra("id");
        initView();

        //更具id 查历史消息
        getHistory(id);


    }



    private void initView() {
        rv_system_message = findViewById(R.id.rv_system_message);

        LinearLayout ll_system_back = findViewById(R.id.ll_system_back);
        rv_system_message.setLayoutManager(new LinearLayoutManager(this));



        ll_system_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void getHistory(String id) {
        V2TIMMessageListGetOption optionBackward = new V2TIMMessageListGetOption();
        optionBackward.setCount(20);
        optionBackward.setGetType(V2TIMMessageListGetOption.V2TIM_GET_CLOUD_OLDER_MSG);
        optionBackward.setUserID(id);
        optionBackward.setLastMsg(null);
       // optionBackward.setGroupID(chatId);


        ConversationManager.getInstance().clearUnreadMessage(true, true, new IMCallback(new V2TIMValueCallback() {
            @Override
            public void onSuccess(Object o) {

            }

            @Override
            public void onError(int i, String s) {

            }
        }) {
            @Override
            public void success(Object data) {
                super.success(data);
            }

            @Override
            public void fail(int code, String errorMessage) {
                super.fail(code, errorMessage);
            }
        });


        V2TIMManager.getMessageManager().getHistoryMessageList(optionBackward, new V2TIMValueCallback<List<V2TIMMessage>>() {
            @Override
            public void onError(int code, String desc) {

                Log.e(TAG, "loadChatMessages getHistoryMessageList optionBackward failed, code = " + code + ", desc = " + ErrorMessageConverter.convertIMError(code, desc));
            }

            @Override
            public void onSuccess(List<V2TIMMessage> v2TIMMessages) {
                Log.i(TAG, "onSuccess: "+v2TIMMessages);

                if (v2TIMMessages!=null && v2TIMMessages.size()>0){
                    PaymentAdapter paymentAdapter = new PaymentAdapter(v2TIMMessages);
                    rv_system_message.setAdapter(paymentAdapter);
                }




            }
        });
    }
}
