package com.tencent.qcloud.tuikit.tuichat.ui.page;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tencent.qcloud.tuikit.tuichat.R;
import com.tencent.qcloud.tuikit.tuichat.adapter.CardAdapter;
import com.tencent.qcloud.tuikit.tuichat.ui.interfaces.IPhoneCode;
import com.tencent.qcloud.tuikit.tuichat.ui.view.PhoneCode;

public class PaymentMethodActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "PaymentMethodActivity";

    private LinearLayout ll_envelope_del;
    private RecyclerView rv_get_back_card;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_method_layout);

        ll_envelope_del = (LinearLayout) findViewById(R.id.ll_envelope_del);

        //显示银行卡
        rv_get_back_card = (RecyclerView) findViewById(R.id.rv_get_back_card);

        rv_get_back_card.setLayoutManager(new LinearLayoutManager(this));
        CardAdapter adapter = new CardAdapter(this);
        rv_get_back_card.setAdapter(adapter);
        initListener();
    }
    private void initListener() {
        ll_envelope_del.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_envelope_del) {
            finish();
        }
    }
}
