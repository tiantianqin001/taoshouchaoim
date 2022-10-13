package com.pingmo.chengyan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.butel.easyrecyclerview.adapter.BaseViewHolder;
import com.butel.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.bean.CashItemBean;

import java.util.List;

public class CashListAdapter extends RecyclerArrayAdapter<CashItemBean> {

    private double totalMoney;

    public CashListAdapter(Context context, double totalMoney) {
        super(context);
        this.totalMoney = totalMoney;
    }

    public CashListAdapter(Context context, CashItemBean[] objects) {
        super(context, objects);
    }

    public CashListAdapter(Context context, List<CashItemBean> objects) {
        super(context, objects);
    }

    private View inflate(ViewGroup parent, int layoutRes) {
        return LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflate(parent, R.layout.view_cash_item);
        return new CashItemHolder(itemView);
    }

    @Override
    public void OnBindViewHolder(BaseViewHolder holder, int position) {
        ((CashItemHolder) holder).onBind(getItem(position), totalMoney);
    }
}
