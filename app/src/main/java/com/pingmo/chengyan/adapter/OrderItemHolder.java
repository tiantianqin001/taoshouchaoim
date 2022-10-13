package com.pingmo.chengyan.adapter;

import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.butel.easyrecyclerview.adapter.BaseViewHolder;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.bean.OrderItemBean;

public class OrderItemHolder extends BaseViewHolder {
    private ImageView mImg;
    private TextView mName;
    private TextView mCharge;
    private TextView mCharge2;
    private TextView mNum;
    private TextView mDate;
    private TextView mType;

    public OrderItemHolder(View itemView) {
        super(itemView);
        mImg = itemView.findViewById(R.id.img);
        mName = itemView.findViewById(R.id.name);
        mCharge = itemView.findViewById(R.id.charge);
        mCharge2 = itemView.findViewById(R.id.charge2);
        mCharge2.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        mNum = itemView.findViewById(R.id.num);
        mDate = itemView.findViewById(R.id.date);
        mType = itemView.findViewById(R.id.type);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void onBind(OrderItemBean bean) {

    }
}
