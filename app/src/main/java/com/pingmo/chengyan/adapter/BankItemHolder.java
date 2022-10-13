package com.pingmo.chengyan.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.butel.easyrecyclerview.adapter.BaseViewHolder;
import com.pingmo.chengyan.R;

import java.util.Random;

public class BankItemHolder extends BaseViewHolder {
    public ImageView mCheckBtn;
    public TextView mName;
    public TextView mNum;
    public View mRootView;

    public BankItemHolder(View itemView) {
        super(itemView);
        mCheckBtn = itemView.findViewById(R.id.check_btn);
        mName = itemView.findViewById(R.id.name);
        mNum = itemView.findViewById(R.id.num);
        mRootView = itemView.findViewById(R.id.item_view);
        Random random = new Random();
        switch (random.nextInt(3)) {
            case 0:
                mRootView.setBackgroundResource(R.mipmap.bank_bg_1);
                break;
            case 1:
                mRootView.setBackgroundResource(R.mipmap.bank_bg_2);
                break;
            case 2:
                mRootView.setBackgroundResource(R.mipmap.bank_bg_3);
                break;
        }
    }
}
