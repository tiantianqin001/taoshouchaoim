package com.pingmo.chengyan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.butel.easyrecyclerview.adapter.BaseViewHolder;
import com.butel.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.bean.OrderItemBean;

import java.util.List;

public class OrderListAdapter extends RecyclerArrayAdapter<OrderItemBean> {

    public OrderListAdapter(Context context) {
        super(context);
    }

    public OrderListAdapter(Context context, OrderItemBean[] objects) {
        super(context, objects);
    }

    public OrderListAdapter(Context context, List<OrderItemBean> objects) {
        super(context, objects);
    }

    private View inflate(ViewGroup parent, int layoutRes) {
        return LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflate(parent, R.layout.view_order_item);
        return new OrderItemHolder(itemView);
    }

    @Override
    public void OnBindViewHolder(BaseViewHolder holder, int position) {
        super.OnBindViewHolder(holder, position);
        ((OrderItemHolder) holder).onBind(getItem(position));
    }
}
