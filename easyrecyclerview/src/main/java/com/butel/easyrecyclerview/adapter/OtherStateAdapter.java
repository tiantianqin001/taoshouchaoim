package com.butel.easyrecyclerview.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 描述：页面状态adapter(加载中，加载失败，无数据)
 * 类名：OtherStateAdapter
 * 作者：xihao
 * 日期：2018/7/19
 */
public abstract class OtherStateAdapter extends RecyclerView.Adapter<OtherStateViewHolder> {

    @Override
    public OtherStateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OtherStateViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
    }

    @Override
    public void onBindViewHolder(final OtherStateViewHolder holder, final int position) {

        if (clickable()) {
            holder.getConvertView().setClickable(true);
            holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(v, position);
                }
            });
        }
        onBindView(holder, holder.getAdapterPosition());
    }

    public abstract void onBindView(OtherStateViewHolder holder, int position);

    @Override
    public int getItemViewType(int position) {
        return getLayoutID(position);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public abstract int getLayoutID(int position);

    public abstract boolean clickable();

    public void onItemClick(View v, int position) {
    }

}
