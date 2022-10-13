package com.tencent.qcloud.tuikit.tuichat.ui.page;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.tencent.qcloud.tuikit.tuichat.R;
import com.tencent.qcloud.tuikit.tuichat.bean.CardBean;
import com.tencent.qcloud.tuikit.tuichat.ui.interfaces.IPhoneCode;
import com.tencent.qcloud.tuikit.tuichat.ui.view.PhoneCode;

import org.greenrobot.eventbus.EventBus;

public class IntoenvelopeActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "IntoenvelopeActivity";
    private PhoneCode pc_code;
    private LinearLayout ll_envelope_del;
    private RelativeLayout rl_payment_method;
    private String mony;
    private String recipientId;
    private String note;
    private String number;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.into_envelope_layout);
        pc_code = (PhoneCode) findViewById(R.id.pc_code);
        ll_envelope_del = (LinearLayout) findViewById(R.id.ll_envelope_del);
        rl_payment_method = (RelativeLayout) findViewById(R.id.rl_payment_method);

       TextView tv_send_money = findViewById(R.id.tv_send_money);
        String chatName = getIntent().getStringExtra("chatName");

        //单人红包要传的值
        mony = getIntent().getStringExtra("mony");

        tv_send_money.setText("¥"+mony);
        recipientId = getIntent().getStringExtra("recipientId");
        note = getIntent().getStringExtra("note");
        if (TextUtils.isEmpty(note)){
            note = "你发起了一笔转账";
        }
        number = getIntent().getStringExtra("number");


        initListener();
    }
    private void initListener() {
        ll_envelope_del.setOnClickListener(this);
        //支付方式
        rl_payment_method.setOnClickListener(this);
        pc_code.setOnVCodeCompleteListener(new IPhoneCode.OnVCodeInputListener() {
            @Override
            public void vCodeComplete(String verificationCode) {
                Log.i(TAG, "vCodeComplete: "+verificationCode);
                Intent intent = getIntent();
                intent.putExtra("cardPassword", verificationCode);
                intent.putExtra("money", mony);
                intent.putExtra("note", note);
                intent.putExtra("recipientId", recipientId);
                intent.putExtra("number", number);
                setResult(RESULT_OK,intent);


                CardBean cardBean = new CardBean();
                cardBean.cardPassword = verificationCode;
                cardBean.money = mony;
                cardBean.note = note;
                cardBean.recipientId = recipientId;
                EventBus.getDefault().post(cardBean);
                finish();
            }

            @Override
            public void vCodeIncomplete(String verificationCode) {

            }
        });
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_envelope_del) {

            Intent intent = getIntent();
            intent.putExtra("result","error");
            setResult(RESULT_OK,intent);
            finish();
        }
        //支付方式
      /*  if(v.getId() == R.id.rl_payment_method){
            Intent intent = new Intent(this,PaymentMethodActivity.class);
            startActivity(intent);
        }*/

    }
}
