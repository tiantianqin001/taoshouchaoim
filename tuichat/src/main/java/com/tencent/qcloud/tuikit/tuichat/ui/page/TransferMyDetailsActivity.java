package com.tencent.qcloud.tuikit.tuichat.ui.page;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.gyf.immersionbar.ImmersionBar;
import com.tencent.qcloud.tuikit.tuichat.R;

public class TransferMyDetailsActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置沉静状态栏
        ImmersionBar.with(this)
                .transparentBar()
                .init();
        setContentView(R.layout.transfer_pack_detail_layout);
      TextView tv_recipient_red=(TextView) findViewById(R.id.tv_recipient_red);
      TextView tv_into_red_envelope=(TextView) findViewById(R.id.tv_into_red_envelope);
      TextView tv_transfer_name=(TextView) findViewById(R.id.tv_transfer_name);
      TextView tv_transfer_time=(TextView) findViewById(R.id.tv_transfer_time);

        boolean isRecipient = getIntent().getBooleanExtra("isRecipient", false);
        if (isRecipient){
            tv_recipient_red.setVisibility(View.GONE);
        }
        boolean isSelf = getIntent().getBooleanExtra("isSelf", false);
        //如果是自己就不显示收款按钮
        if (isSelf){
            tv_into_red_envelope.setVisibility(View.INVISIBLE);
        }
        //获取昵称
        String nickName = getIntent().getStringExtra("nickName");
        tv_transfer_name.setText("待"+nickName+"收款");
        //转账时间
        String currentTime = getIntent().getStringExtra("currentTime");
        tv_transfer_time.setText(currentTime);

    }
}
