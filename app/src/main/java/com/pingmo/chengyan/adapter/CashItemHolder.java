package com.pingmo.chengyan.adapter;

import android.view.View;
import android.widget.TextView;

import com.butel.easyrecyclerview.adapter.BaseViewHolder;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.bean.CashItemBean;
import com.pingmo.chengyan.utils.DateUtil;

public class CashItemHolder extends BaseViewHolder {
    private TextView mType;
    private TextView mCash;
    private TextView mDate;
    private TextView mYue;

    public CashItemHolder(View itemView) {
        super(itemView);
        mType = itemView.findViewById(R.id.type);
        mCash = itemView.findViewById(R.id.cash);
        mDate = itemView.findViewById(R.id.date);
        mYue = itemView.findViewById(R.id.yue);
    }

    public void onBind(CashItemBean bean, double totalMoney) {
        //类型
       // mType.setText(getType(bean.getObjectType()));
        mType.setText(bean.getNotes());
        //金额
        mCash.setText((bean.getType() == 1 ? "+" : "-") + bean.getMoney());
        //日期
        mDate.setText(DateUtil.getMonthAndDay(bean.getCreateTime()));
        //余额
        mYue.setText(String.valueOf(totalMoney));
    }

    private String getType(int type) {
        switch (type) {
            case 6://退回单人红包
            case 3://退回群红包
                return "退款";
            case 2://抢群红包
            case 5://领取单人红包
                return "抢红包";
            case 1://发群红包
            case 4://发单人红包
                return "发红包";
            case 7://发起转账
            case 8://接收转账
                return "转账";
            default:
                return "";
        }
    }
}
